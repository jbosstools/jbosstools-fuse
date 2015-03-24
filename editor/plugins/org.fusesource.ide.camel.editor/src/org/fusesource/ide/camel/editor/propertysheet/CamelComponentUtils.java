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
package org.fusesource.ide.camel.editor.propertysheet;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.connectors.Component;
import org.fusesource.ide.camel.model.connectors.ComponentDependency;
import org.fusesource.ide.camel.model.connectors.ComponentModel;
import org.fusesource.ide.camel.model.connectors.ComponentModelFactory;
import org.fusesource.ide.camel.model.connectors.ComponentProperty;
import org.fusesource.ide.camel.model.connectors.ComponentScheme;
import org.fusesource.ide.camel.model.connectors.UriParameter;
import org.fusesource.ide.commons.util.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public final class CamelComponentUtils {

    private static final String CAMEL_COMPONENT_DESCRIPTOR_FILE_MASK = "META-INF/services/org/apache/camel/descriptors/%s.xml";
    private static HashMap<String, Component> knownComponents = new HashMap<String, Component>();
    
    /**
     * returns the properties model for a given protocol
     * 
     * @param protocol  the protocol to get the properties for
     * @return  the properties model or null if not available
     */
    public static Component getComponentModel(String protocol) {
        String componentClass = getComponentClass(protocol);
        if (knownComponents.containsKey(componentClass)) {
            return knownComponents.get(componentClass);
        }
        
        // it seems we miss a model for the given protocol...lets try creating one on the fly
        Component c = buildModelForComponent(protocol, componentClass);
        if (c != null) {
            knownComponents.put(componentClass, c);
            return getComponentModel(protocol);
        }

        return null;
    }
    
    
    
    public static boolean isBooleanProperty(UriParameter p) {
        return  p.getJavaType().equalsIgnoreCase("boolean") || 
                p.getJavaType().equalsIgnoreCase("java.lang.Boolean");
    }
    
    public static boolean isTextProperty(UriParameter p) {
        return  p.getChoice() == null && (
        		p.getJavaType().equalsIgnoreCase("String") || 
                p.getJavaType().equalsIgnoreCase("java.lang.String") || 
                p.getJavaType().equalsIgnoreCase("java.net.URL") ||
                p.getJavaType().equalsIgnoreCase("java.net.URI") || 
                p.getJavaType().equalsIgnoreCase("Text"));
    }
    
    public static boolean isNumberProperty(UriParameter p) {
        return  p.getChoice() == null && (
        		p.getJavaType().equalsIgnoreCase("int") || 
                p.getJavaType().equalsIgnoreCase("Integer") ||
                p.getJavaType().equalsIgnoreCase("java.lang.Integer") || 
                p.getJavaType().equalsIgnoreCase("long") || 
                p.getJavaType().equalsIgnoreCase("java.lang.Long") || 
                p.getJavaType().equalsIgnoreCase("double") || 
                p.getJavaType().equalsIgnoreCase("java.lang.Double") ||
                p.getJavaType().equalsIgnoreCase("float") || 
                p.getJavaType().equalsIgnoreCase("java.lang.Float") || 
                p.getJavaType().equalsIgnoreCase("Number"));
    }
    
    public static boolean isChoiceProperty(UriParameter p) {
        return p.getChoice() != null && p.getChoice().trim().length()>0;
    }
    
    public static boolean isFileProperty(UriParameter p) {
        return  p.getJavaType().equalsIgnoreCase("file") ||
                p.getJavaType().equalsIgnoreCase("java.io.file");
    }
    
    public static boolean isExpressionProperty(UriParameter p) {
        return  p.getJavaType().equalsIgnoreCase("expression") ||
                p.getJavaType().equalsIgnoreCase("org.apache.camel.Expression");
    }
    
    public static boolean isListProperty(UriParameter p) {
        return p.getJavaType().toLowerCase().startsWith("java.util.list") ||
               p.getJavaType().toLowerCase().startsWith("java.util.collection"); 
    }
    
    public static boolean isMapProperty(UriParameter p) {
        return p.getJavaType().toLowerCase().startsWith("java.util.map"); 
    }
    
    public static boolean isUriPathParameter(UriParameter p) {
    	return p.getKind() != null && p.getKind().equalsIgnoreCase("path");
    }
    
    public static boolean isUriOptionParameter(UriParameter p) {
    	return p.getKind() != null && p.getKind().equalsIgnoreCase("parameter");
    }
        
    public static boolean isUnsupportedProperty(UriParameter p) {
    	return isListProperty(p) ||
    		   isMapProperty(p) || 
    		   p.getJavaType().toLowerCase().startsWith("java.util.date");
    }
    
    public static String[] getChoices(UriParameter p) {
        String[] choices = p.getChoice().split(",");
        ArrayList<String> res = new ArrayList<String>();
        res.add(" "); // empty entry
        for (String choice : choices) res.add(choice);
        return res.toArray(new String[res.size()]);
    }
    
    public static String buildChoice(Component component, String protocol) {
        String result = "choice[";
        
        if (component != null) {
            boolean first = true;
            for (ComponentScheme p : component.getSchemes()) {
                if (first) {
                    first = false;
                } else {
                    result += ",";
                }
                result += p.getScheme();
            }        
        } else {
            result += protocol;
        }
        result += "]";
        
        return result;
    }
    
    /**
     * returns the component class for the given scheme
     * 
     * @param scheme
     * @return  the class or null if not found
     */
    protected static String getComponentClass(String scheme) {
        String compClass = null;
        
        ArrayList<Component> components = ComponentModelFactory.getModelForVersion(Activator.getDefault().getCamelVersion()).getSupportedComponents();
        for (Component c : components) {
            if (c.supportsScheme(scheme)) {
                compClass = c.getClazz();
                break;
            }
        }
        
        if (compClass == null) {
        	// seems this scheme has no model entry -> check dependency
        	try {
                IMavenProjectFacade m2facade = MavenPlugin.getMavenProjectRegistry().create(Activator.getDiagramEditor().getCamelContextFile().getProject(), new NullProgressMonitor());
                Set<Artifact> deps = m2facade.getMavenProject(new NullProgressMonitor()).getArtifacts();
                ZipFile zf = null;
                ZipEntry ze = null;
                for (Artifact dep : deps) {
                    zf = new ZipFile(dep.getFile());
                    ze = zf.getEntry(String.format("META-INF/services/org/apache/camel/component/%s", scheme));
                    if (ze != null) {
                        break;
                    }
                    ze = null;
                    zf = null;
                }
                
                if (ze != null) {
                	// try to generate a model entry for the component class
                	Properties p = new Properties();
                	p.load(zf.getInputStream(ze));
                	compClass = p.getProperty("class");
                }
        	} catch (Exception ex) {
        		Activator.getLogger().error(ex);
        		compClass = null;
        	}
        }
        
        return compClass;
    }
    
    protected static Component buildModelForComponent(String scheme, String clazz) {
        Component resModel = null;

        // 1. take what we have in our model xml
        resModel = ComponentModelFactory.getModelForVersion(Activator.getDefault().getCamelVersion()).getComponentForScheme(scheme);
        
        // 2. try to generate the model from json blob
        if (resModel == null) resModel = buildModelFromJSON(scheme, clazz);
        
        return resModel;        
    }
    
    /**
     * tries to build the model by querying the component config of the camel component
     * 
     * @param clazz the component class
     * @return
     */
    protected static Component buildModelFromJSON(String scheme, String clazz) {
        Component resModel = null;

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
                ComponentScheme cs = new ComponentScheme();
                cs.setScheme(scheme);
                ArrayList<ComponentScheme> csl = new ArrayList<ComponentScheme>();
                csl.add(cs);
                resModel.setSchemes(csl);
                saveModel(resModel);
            }
        } catch (Exception ex) {
            Activator.getLogger().error(ex);
        }
    
        return resModel;
    }
    
    private static void saveModel(Component component) {
        try {
            // create JAXB context and instantiate marshaller
        	JAXBContext context = JAXBContext.newInstance(ComponentModel.class, Component.class, ComponentDependency.class, ComponentScheme.class, ComponentProperty.class, UriParameter.class);
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
    protected static Component buildModelFromJSonBlob(String json, String clazz) {
        Component resModel = new Component();
        resModel.setClazz(clazz);
        
        try {
            ArrayList<UriParameter> uriParams = new ArrayList<UriParameter>();
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
                
                UriParameter param = new UriParameter();
                param.setName(propName);
                param.setJavaType(type);
                param.setLabel(null);
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
