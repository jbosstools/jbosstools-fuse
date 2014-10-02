/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.propertysheet.model;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.connectors.Connector;
import org.fusesource.ide.camel.model.connectors.ConnectorModelFactory;
import org.fusesource.ide.camel.model.connectors.ConnectorProtocol;
import org.fusesource.ide.commons.util.JsonHelper;
import org.jboss.dmr.ModelNode;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
public final class CamelComponentUtils {

    private static final String CAMEL_COMPONENT_DESCRIPTOR_FILE_MASK = "META-INF/services/org/apache/camel/descriptors/%s.xml";
    private static HashMap<String, CamelComponent> knownComponents = new HashMap<String, CamelComponent>();
    
    /**
     * returns the properties model for a given protocol
     * 
     * @param protocol  the protocol to get the properties for
     * @return  the properties model or null if not available
     */
    public static CamelComponent getComponentModel(String protocol) {
        String componentClass = getComponentClass(protocol);
        if (knownComponents.containsKey(componentClass)) {
            return knownComponents.get(componentClass);
        }
        
        // it seems we miss a model for the given protocol...lets try creating one on the fly
        CamelComponent c = buildModelForComponent(protocol, componentClass);
        if (c != null) {
            knownComponents.put(componentClass, c);
            return getComponentModel(protocol);
        }

        return null;
    }
    
    
    
    public static boolean isBooleanProperty(CamelComponentUriParameter p) {
        return  p.getType().equalsIgnoreCase("boolean") || 
                p.getType().equalsIgnoreCase("java.lang.Boolean");
    }
    
    public static boolean isTextProperty(CamelComponentUriParameter p) {
        return  p.getType().equalsIgnoreCase("String") || 
                p.getType().equalsIgnoreCase("java.lang.String") || 
                p.getType().equalsIgnoreCase("java.net.URL") ||
                p.getType().equalsIgnoreCase("java.net.URI") || 
                p.getType().equalsIgnoreCase("Text");
    }
    
    public static boolean isNumberProperty(CamelComponentUriParameter p) {
        return  p.getType().equalsIgnoreCase("int") || 
                p.getType().equalsIgnoreCase("Integer") ||
                p.getType().equalsIgnoreCase("java.lang.Integer") || 
                p.getType().equalsIgnoreCase("long") || 
                p.getType().equalsIgnoreCase("java.lang.Long") || 
                p.getType().equalsIgnoreCase("double") || 
                p.getType().equalsIgnoreCase("java.lang.Double") ||
                p.getType().equalsIgnoreCase("float") || 
                p.getType().equalsIgnoreCase("java.lang.Float") || 
                p.getType().equalsIgnoreCase("Number");
    }
    
    public static boolean isChoiceProperty(CamelComponentUriParameter p) {
        return p.getType().toLowerCase().startsWith("choice[");
    }
    
    public static boolean isFileProperty(CamelComponentUriParameter p) {
        return  p.getType().equalsIgnoreCase("file") ||
                p.getType().equalsIgnoreCase("java.io.file");
    }
    
    public static boolean isFolderProperty(CamelComponentUriParameter p) {
        return  p.getType().equalsIgnoreCase("folder") ||
                p.getType().equalsIgnoreCase("path") || 
                p.getType().equalsIgnoreCase("directory");
    }
    
    public static boolean isExpressionProperty(CamelComponentUriParameter p) {
        return  p.getType().equalsIgnoreCase("expression") ||
                p.getType().equalsIgnoreCase("org.apache.camel.Expression");
    }
    
    public static boolean isListProperty(CamelComponentUriParameter p) {
        return p.getType().equalsIgnoreCase("java.util.list") ||
               p.getType().equalsIgnoreCase("java.util.collection"); 
    }
    
    public static String[] getChoices(CamelComponentUriParameter p) {
        String rawChoices = p.getType().substring(p.getType().indexOf('[')+1, p.getType().indexOf(']'));
        return rawChoices.split(",");
    }
    
    public static String buildChoice(Connector connector, String protocol) {
        String result = "choice[";
        
        if (connector != null) {
            boolean first = true;
            for (ConnectorProtocol p : connector.getProtocols()) {
                if (first) {
                    first = false;
                } else {
                    result += ",";
                }
                result += p.getPrefix();
            }        
        } else {
            result += protocol;
        }
        result += "]";
        
        return result;
    }
    
    /**
     * returns the component class for the given prefix
     * 
     * @param prefix
     * @return  the class or null if not found
     */
    protected static String getComponentClass(String prefix) {
        String compClass = null;
        
        ArrayList<Connector> connectors = ConnectorModelFactory.getModelForVersion(Activator.getDefault().getCamelVersion()).getSupportedConnectors();
        for (Connector c : connectors) {
            if (c.supportsProtocol(prefix)) {
                compClass = c.getComponentClass();
                break;
            }
        }
        
        return compClass;
    }
    
    protected static CamelComponent buildModelForComponent(String protocol, String clazz) {
        CamelComponent resModel = null;

        // 1. try to lookup the model inside the camel component jar file
        resModel = buildModelFromCamelComponentResource(protocol, clazz);
        
        // 2. try to lookup the model in this bundle's resources
        if (resModel == null) resModel = buildModelFromLocalResource(protocol, clazz);
        
        // 3. try to generate the model from json blob
        if (resModel == null) resModel = buildModelFromJSON(protocol, clazz);
        
        return resModel;        
    }
    
    /**
     * tries to locate the component model file inside the camel component jar
     * and build a model out of it
     * 
     * @param clazz
     * @return
     */
    protected static CamelComponent buildModelFromCamelComponentResource(String protocol, String clazz) {
        CamelComponent resModel = null;

        try {
            // 1. check for the correct dependency of the project which contains the camel component
            IMavenProjectFacade m2facade = MavenPlugin.getMavenProjectRegistry().create(Activator.getDiagramEditor().getCamelContextFile().getProject(), new NullProgressMonitor());
            Set<Artifact> deps = m2facade.getMavenProject(new NullProgressMonitor()).getArtifacts();
            ZipFile zf = null;
            for (Artifact dep : deps) {
                zf = new ZipFile(dep.getFile());
                if (zf.getEntry(String.format("META-INF/services/org/apache/camel/component/%s", protocol)) != null) {
                    break;
                }
                zf = null;
            }
            
            // 2. and then get the model file from that jar
            if (zf != null) {
                ZipEntry ze = zf.getEntry(String.format(CAMEL_COMPONENT_DESCRIPTOR_FILE_MASK, clazz));
                if (ze != null) {
                    // create JAXB context and instantiate marshaller
                    JAXBContext context = JAXBContext.newInstance(CamelComponent.class, CamelComponentDependency.class, CamelComponentModel.class, CamelComponentProperty.class, CamelComponentUriParameter.class, CamelComponentUriParameterKind.class);
                    Unmarshaller um = context.createUnmarshaller();
                    CamelComponent model = (CamelComponent) um.unmarshal(new InputSource(zf.getInputStream(ze)));
                    if (model != null) {
                        return model;
                    }
                }
            }
        } catch (Exception ex) {
            Activator.getLogger().error(ex);
        }
        
        return resModel;
    }
    
    /**
     * tries to locate the component model file inside this bundle
     * and build a model out of it
     * 
     * @param clazz
     * @return
     */
    protected static CamelComponent buildModelFromLocalResource(String protocol, String clazz) {
        CamelComponent resModel = null;

        URL modelFileURL = Activator.getDefault().getBundle().getEntry(String.format(CAMEL_COMPONENT_DESCRIPTOR_FILE_MASK, clazz));
        if (modelFileURL != null) {
            try {
                // create JAXB context and instantiate marshaller
                JAXBContext context = JAXBContext.newInstance(CamelComponent.class, CamelComponentDependency.class, CamelComponentModel.class, CamelComponentProperty.class, CamelComponentUriParameter.class, CamelComponentUriParameterKind.class);
                Unmarshaller um = context.createUnmarshaller();
                CamelComponent model = (CamelComponent) um.unmarshal(new InputSource(modelFileURL.openStream()));
                if (model != null) {
                    return model;
                }
            } catch (Exception ex) {
                Activator.getLogger().error(ex);
            }
        }
        
        return resModel;
    }
    
    /**
     * tries to build the model by querying the component config of the camel component
     * 
     * @param clazz the component class
     * @return
     */
    protected static CamelComponent buildModelFromJSON(String protocol, String clazz) {
        CamelComponent resModel = null;

        try {
            URLClassLoader child = getProjectClassLoader();
            Class classToLoad = child.loadClass(clazz);
            Method method = classToLoad.getMethod("createComponentConfiguration");
            Object instance = classToLoad.newInstance();
            Object compConf = method.invoke(instance);
            method = compConf.getClass().getMethod("createParameterJsonSchema");
            Object oJSONBlob = method.invoke(compConf);
            if (oJSONBlob != null && oJSONBlob instanceof String) {
                resModel = buildModelFromJSonBlob((String)oJSONBlob, clazz);  
                resModel.setPrefixes(Arrays.asList(protocol));
                saveModel(resModel);
            }
        } catch (Exception ex) {
            Activator.getLogger().error(ex);
        }
    
        return resModel;
    }
    
    private static void saveModel(CamelComponent component) {
        try {
            // create JAXB context and instantiate marshaller
            JAXBContext context = JAXBContext.newInstance(CamelComponent.class, CamelComponentDependency.class, CamelComponentProperty.class, CamelComponentUriParameter.class, CamelComponentUriParameterKind.class);
            Marshaller m = context.createMarshaller();
            m.marshal(component, new File("/var/tmp/model.xml"));
        } catch (Exception ex) {
            Activator.getLogger().error(ex);
        }
    }
    
    public static URLClassLoader getProjectClassLoader() {
        try {
            IProject project = Activator.getDiagramEditor().getCamelContextFile().getProject();
            IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
            IPackageFragmentRoot[] pfroots = javaProject.getAllPackageFragmentRoots();
            ArrayList<URL> urls = new ArrayList<URL>();
            for (IPackageFragmentRoot root : pfroots) {
                URL rUrl = root.getPath().toFile().toURI().toURL();
                urls.add(rUrl);
            }
            return new URLClassLoader(urls.toArray(new URL[urls.size()]), CamelComponentUtils.class.getClassLoader());
        } catch (Exception ex) {
            Activator.getLogger().error(ex);
        }
        return null;
    }
    
    /**
     * takes the json blob from camel configuration and makes a model from it
     * 
     * @param json
     * @return
     */
    protected static CamelComponent buildModelFromJSonBlob(String json, String clazz) {
        CamelComponent resModel = new CamelComponent();
        resModel.setComponentClass(clazz);
        
        try {
            ArrayList<CamelComponentUriParameter> uriParams = new ArrayList<CamelComponentUriParameter>();
            ModelNode model = JsonHelper.getModelNode(json);
            ModelNode propsNode = model.get("properties");
            Map<String, Object> props = JsonHelper.getAsMap(propsNode);    
            Iterator<String> it = props.keySet().iterator();
            
            while (it.hasNext()) {
                String propName = it.next();
                ModelNode valueNode = propsNode.get(propName);
                String type = null;
                if (valueNode.has("enum")) {
                    type = "choice[";
                    List<ModelNode> vals = JsonHelper.getAsList(valueNode, "enum");
                    for (ModelNode mn : vals) {
                        if (!type.equalsIgnoreCase("choice[")) {
                            type += ",";
                        }
                        type += mn.asString();
                    }
                    type += "]";
                } else {
                    type = valueNode.get("type").asString();
                }
                
                CamelComponentUriParameter param = new CamelComponentUriParameter(propName, type, null, CamelComponentUriParameterKind.BOTH);
                uriParams.add(param);
            }
            
            resModel.setUriParameters(uriParams);
        } catch(Exception ex) {
            Activator.getLogger().error(ex);
            resModel = null;
        }
        
        return resModel;
    }
}
