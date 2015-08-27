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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentProperty;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.JsonHelper;
import org.fusesource.ide.foundation.core.util.Strings;
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
    
    
    
    public static boolean isBooleanProperty(Parameter p) {
        return  p.getJavaType().equalsIgnoreCase("boolean") || 
                p.getJavaType().equalsIgnoreCase("java.lang.Boolean");
    }
    
    public static boolean isTextProperty(Parameter p) {
        return  p.getChoice() == null && (
        		p.getJavaType().equalsIgnoreCase("String") || 
                p.getJavaType().equalsIgnoreCase("java.lang.String") || 
                p.getJavaType().equalsIgnoreCase("java.net.URL") ||
                p.getJavaType().equalsIgnoreCase("java.net.URI") || 
                p.getJavaType().equalsIgnoreCase("Text"));
    }
    
    public static boolean isNumberProperty(Parameter p) {
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
    
    public static boolean isChoiceProperty(Parameter p) {
        return p.getChoice() != null && p.getChoice().trim().length()>0;
    }
    
    public static boolean isFileProperty(Parameter p) {
        return  p.getJavaType().equalsIgnoreCase("file") ||
                p.getJavaType().equalsIgnoreCase("java.io.file");
    }
    
    public static boolean isExpressionProperty(Parameter p) {
        return  p.getJavaType().equalsIgnoreCase("expression") ||
                p.getJavaType().equalsIgnoreCase("org.apache.camel.Expression");
    }
    
    public static boolean isListProperty(Parameter p) {
        return p.getJavaType().toLowerCase().startsWith("java.util.list") ||
               p.getJavaType().toLowerCase().startsWith("java.util.collection"); 
    }
    
    public static boolean isMapProperty(Parameter p) {
        return p.getJavaType().toLowerCase().startsWith("java.util.map"); 
    }
    
    public static boolean isUriPathParameter(Parameter p) {
    	return p.getKind() != null && p.getKind().equalsIgnoreCase("path");
    }
    
    public static boolean isUriOptionParameter(Parameter p) {
    	return p.getKind() != null && p.getKind().equalsIgnoreCase("parameter");
    }
        
    public static boolean isUnsupportedProperty(Parameter p) {
    	return isListProperty(p) ||
    		   isMapProperty(p) || 
    		   p.getJavaType().toLowerCase().startsWith("java.util.date");
    }
    
    public static String[] getChoices(Parameter p) {
        String[] choices = p.getChoice().split(",");
        ArrayList<String> res = new ArrayList<String>();
        res.add(" "); // empty entry
        for (String choice : choices) res.add(choice);
        return res.toArray(new String[res.size()]);
    }
    
    /**
     * returns the component class for the given scheme
     * 
     * @param scheme
     * @return  the class or null if not found
     */
    protected static String getComponentClass(String scheme) {
        String compClass = null;
        
        ArrayList<Component> components = CamelModelFactory.getModelForVersion(Activator.getDefault().getCamelVersion()).getComponentModel().getSupportedComponents();
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
    
    /**
     * returns the component class for the given scheme
     * 
     * @param scheme
     * @return  the class or null if not found
     */
    protected static String getComponentJSon(String scheme) {
        String json = null;

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
            	String compClass = p.getProperty("class");
            	String packageName = compClass.substring(0, compClass.lastIndexOf("."));
            	String folder = packageName.replaceAll("\\.", "/");
            	ze = zf.getEntry(String.format("%s/%s.json", folder, scheme));
                if (ze != null) {
                	json = IOUtils.loadText(zf.getInputStream(ze), null);
                }
            }
    	} catch (Exception ex) {
    		Activator.getLogger().error(ex);
    		json = null;
    	}
        
        return json;
    }
    
    protected static Component buildModelForComponent(String scheme, String clazz) {
        Component resModel = null;

        // 1. take what we have in our model xml
        resModel = CamelModelFactory.getModelForVersion(Activator.getDefault().getCamelVersion()).getComponentModel().getComponentForScheme(scheme);
        
        // 2. try to generate the model from json blob
        if (resModel == null) resModel = buildModelFromJSON(scheme, getComponentJSon(scheme), clazz);
        
        return resModel;        
    }
    
    /**
     * tries to build the model by querying the component config of the camel component
     * 
     * @param clazz the component class
     * @return
     */
    protected static Component buildModelFromJSON(String scheme, String oJSONBlob, String clazz) {
        Component resModel = null;

        try {
            if (oJSONBlob != null) {
                resModel = buildModelFromJSonBlob(oJSONBlob, clazz);  
                resModel.setScheme(scheme);
                saveModel(resModel);
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
            Activator.getLogger().error(ex);
        }
    
        return resModel;
    }
    
    private static void saveModel(Component component) {
    	knownComponents.put(component.getClazz(), component);
    	try {
            // create JAXB context and instantiate marshaller
//        	JAXBContext context = JAXBContext.newInstance(ComponentModel.class, Component.class, Dependency.class, ComponentProperty.class, Parameter.class);
//		    Marshaller m = context.createMarshaller();
//            m.marshal(component, new File("/var/tmp/model.xml"));
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
            ModelNode model = JsonHelper.getModelNode(json);
            
            ModelNode componentNode = model.get("component");
            Map<String, Object> props = JsonHelper.getAsMap(componentNode);    
            Iterator<String> it = props.keySet().iterator();

            String grpId = null, artId = null, ver = null;

            while (it.hasNext()) {
            	String propName = it.next();
                ModelNode valueNode = componentNode.get(propName);
                
                if (propName.equals("kind")) {
                	resModel.setKind(valueNode.asString());
                } else if (propName.equals("scheme")) {
                	resModel.setScheme(valueNode.asString());
                } else if (propName.equals("syntax")) {
                	resModel.setSyntax(valueNode.asString());
                } else if (propName.equals("title")) {
                	resModel.setTitle(valueNode.asString());
                } else if (propName.equals("description")) {
                	resModel.setDescription(valueNode.asString());
                } else if (propName.equals("label")) {
                	ArrayList<String> al = new ArrayList<String>();
                	al.addAll(Arrays.asList(valueNode.asString().split(",")));
                	resModel.setTags(al);
                } else if (propName.equals("consumerOnly")) {
                	resModel.setConsumerOnly(valueNode.asString());
                } else if (propName.equals("producerOnly")) {
                	resModel.setProducerOnly(valueNode.asString());
                } else if (propName.equals("javaType")) {
                	resModel.setClazz(valueNode.asString());
                } else if (propName.equals("groupId")) {
                	grpId = valueNode.asString();
                } else if (propName.equals("artifactId")) {
                	artId = valueNode.asString();
                } else if (propName.equals("version")) {
                	ver = valueNode.asString();
                } else {
                	// unknown property
                }
            }
            
            if (!Strings.isBlank(grpId) && !Strings.isBlank(artId) && !Strings.isBlank(ver)) {
            	ArrayList<Dependency> depList = new ArrayList<Dependency>();
            	Dependency dep = new Dependency();
            	dep.setGroupId(grpId);
            	dep.setArtifactId(artId);
            	dep.setVersion(ver);
            	depList.add(dep);
            	resModel.setDependencies(depList);
            }
                        
            ArrayList<ComponentProperty> cProps = new ArrayList<ComponentProperty>();

            ModelNode componentPropertiesNode = model.get("componentProperties");
            Map<String, Object> cprops = JsonHelper.getAsMap(componentPropertiesNode);    
            it = cprops.keySet().iterator();
            
            while (it.hasNext()) {
                ComponentProperty cp = new ComponentProperty();
            	String propName = it.next();
                ModelNode valueNode = componentNode.get(propName);
                
                if (propName.equals("defaultValue")) {
                	cp.setDefaultValue(valueNode.asString());
                } else if (propName.equals("deprecated")) {
                	cp.setDeprecated(valueNode.asString());
                } else  if (propName.equals("description")) {
                	cp.setDescription(valueNode.asString());
                } else  if (propName.equals("javaType")) {
                	cp.setJavaType(valueNode.asString());
                } else  if (propName.equals("kind")) {
                	cp.setKind(valueNode.asString());
                } else  if (propName.equals("name")) {
                	cp.setName(valueNode.asString());
                } else  if (propName.equals("type")) {
                	cp.setType(valueNode.asString());
                } else {
                	// unknown property
                }
                cProps.add(cp);
            }
            resModel.setComponentProperties(cProps);
            
            ModelNode propsNode = model.get("properties");
            props = JsonHelper.getAsMap(propsNode);    
            it = props.keySet().iterator();
            
            ArrayList<Parameter> uriParams = new ArrayList<Parameter>();
            
            while (it.hasNext()) {
                String propName = it.next();
                ModelNode valueNode = propsNode.get(propName);
                Parameter param = new Parameter();
                
                param.setName(propName);
                
                if (valueNode.hasDefined("choice")) {
                	param.setChoice(valueNode.get("choice").asString());
                }
//                if (valueNode.hasDefined("oneOf")) {
//                	param.setOneOf(valueNode.get("oneOf").asString());
//                } 
                if (valueNode.hasDefined("defaultValue")) {
                	param.setDefaultValue(valueNode.get("defaultValue").asString());
                } 
                if (valueNode.hasDefined("deprecated")) {
                	param.setDeprecated(valueNode.get("deprecated").asString());
                }
                if (valueNode.hasDefined("description")) {
                	param.setDescription(valueNode.get("description").asString());
                }
                if (valueNode.hasDefined("javaType")) {
                	param.setJavaType(valueNode.get("javaType").asString());
                }
                if (valueNode.hasDefined("kind")) {
                	param.setKind(valueNode.get("kind").asString());
                }
                if (valueNode.hasDefined("label")) {
                	param.setLabel(valueNode.get("label").asString());
                }
                if (valueNode.hasDefined("name")) {
                	param.setName(valueNode.get("name").asString());
                }
                if (valueNode.hasDefined("required")) {
                	param.setRequired(valueNode.get("required").asString());
                }
                if (valueNode.hasDefined("type")) {
                	param.setType(valueNode.get("type").asString());
                }
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
