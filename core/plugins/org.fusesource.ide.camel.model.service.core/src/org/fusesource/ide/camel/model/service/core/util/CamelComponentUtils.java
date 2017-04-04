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
package org.fusesource.ide.camel.model.service.core.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentProperty;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.JsonHelper;
import org.fusesource.ide.foundation.core.util.Strings;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public final class CamelComponentUtils {

	private static Map<CamelModel, Map<String, Component>> knownComponentsForCamelModel = new WeakHashMap<>();

	/**
	 * returns the properties model for a given protocol
	 *
	 * @param protocol
	 *            the protocol to get the properties for
	 * @param project
	 * @return the properties model or null if not available
	 */
	public static Component getComponentModel(String protocol, CamelFile camelFile) {
		CamelModel camelModel = camelFile.getCamelModel();
		Map<String, Component> knownComponents;
		if (knownComponentsForCamelModel.containsKey(camelModel)) {
			knownComponents = knownComponentsForCamelModel.get(camelModel);
		} else {
			knownComponents = new WeakHashMap<>();
			knownComponentsForCamelModel.put(camelModel, knownComponents);
		}
		String componentClass = getComponentClass(protocol, camelFile);
		if (knownComponents.containsKey(componentClass)) {
			return knownComponents.get(componentClass);
		} else {
			Component c = buildModelForComponent(protocol, componentClass, camelFile);
			if (c != null) {
				knownComponents.put(componentClass, c);
				return getComponentModel(protocol, camelFile);
			}
			return null;
		}
	}

	public static String[] getRefs(CamelFile cf) {
		List<String> refs = new ArrayList<>();

		refs.add("");
		Set<String> globalDefinitionIds = cf.getCamelFile().getGlobalDefinitions().keySet();
		refs.addAll(Arrays.asList(globalDefinitionIds.toArray(new String[globalDefinitionIds.size()])));
		if (cf.getRouteContainer() instanceof CamelContextElement) {
			final CamelContextElement camelContext = (CamelContextElement)cf.getRouteContainer();
			if(camelContext != null){
				Set<String> globalEndpointDefinitionIds = camelContext.getEndpointDefinitions().keySet();
				refs.addAll(Arrays.asList(globalEndpointDefinitionIds.toArray(new String[globalEndpointDefinitionIds.size()])));
				Set<String> globalDataformatIds = camelContext.getDataformats().keySet();
				refs.addAll(Arrays.asList(globalDataformatIds.toArray(new String[globalDataformatIds.size()])));
			}
		}
		
		return refs.toArray(new String[refs.size()]);
	}

	public static boolean isRefProperty(Parameter p) {
		return p.getName().equalsIgnoreCase("ref") && p.getType().equalsIgnoreCase("string")
				&& p.getJavaType().equalsIgnoreCase("java.lang.String") && p.getKind().equalsIgnoreCase("attribute");
	}

	public static boolean isBooleanProperty(Parameter p) {
		return p.getJavaType().equalsIgnoreCase("boolean") || p.getJavaType().equalsIgnoreCase("java.lang.Boolean");
	}

	public static boolean isDescriptionProperty(Parameter p) {
		return p.getKind().equals(AbstractCamelModelElement.NODE_KIND_ELEMENT)
				&& p.getJavaType().equalsIgnoreCase("org.apache.camel.model.DescriptionDefinition")
				&& p.getName().equals("description");
	}
	
	public static boolean isCharProperty(Parameter p) {
		return "char".equalsIgnoreCase(p.getJavaType());
	}

	public static boolean isTextProperty(Parameter p) {
		return p.getChoice() == null && p.getName().equalsIgnoreCase("ref") == false
				&& p.getKind().equals("expression") == false
				&& (p.getJavaType().equalsIgnoreCase("String") || p.getJavaType().equalsIgnoreCase("java.lang.String")
						|| p.getJavaType().equalsIgnoreCase("java.net.URL")
						|| p.getJavaType().equalsIgnoreCase("java.net.URI")
						|| p.getJavaType().equalsIgnoreCase("Text"));
	}

	public static boolean isNumberProperty(Parameter p) {
		final String javaType = p.getJavaType();
		return p.getChoice() == null && (javaType.equalsIgnoreCase("int") || javaType.equalsIgnoreCase("Integer")
				|| javaType.equalsIgnoreCase("java.lang.Integer") || javaType.equalsIgnoreCase("long")
				|| javaType.equalsIgnoreCase("java.lang.Long") || javaType.equalsIgnoreCase("double")
				|| javaType.equalsIgnoreCase("java.lang.Double") || javaType.equalsIgnoreCase("float")
				|| javaType.equalsIgnoreCase("java.lang.Float") || javaType.equalsIgnoreCase("Number"));
	}

	public static boolean isChoiceProperty(Parameter p) {
		return p.getChoice() != null && p.getChoice().trim().length() > 0;
	}

	public static boolean isFileProperty(Parameter p) {
		return p.getJavaType().equalsIgnoreCase("file") || p.getJavaType().equalsIgnoreCase("java.io.file");
	}
	
	public static boolean isClassProperty(Parameter p) {
		return p.getType().equalsIgnoreCase("object");
	}

	public static boolean isExpressionProperty(Parameter p) {
		return p.getKind().equalsIgnoreCase("expression")
				|| p.getJavaType().equalsIgnoreCase("org.apache.camel.model.language.ExpressionDefinition");
	}

	public static boolean isDataFormatProperty(Parameter p) {
		return p.getKind().equalsIgnoreCase(AbstractCamelModelElement.NODE_KIND_ELEMENT)
				&& p.getJavaType().equalsIgnoreCase("org.apache.camel.model.DataFormatDefinition");
	}

	public static boolean isListProperty(Parameter p) {
		return p.getJavaType().toLowerCase().startsWith("java.util.list")
				|| p.getJavaType().toLowerCase().startsWith("java.util.collection");
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
		return isMapProperty(p) || p.getJavaType().toLowerCase().startsWith("java.util.date");
	}

	public static String[] getChoices(Parameter p) {
		String[] choices = p.getChoice().split(",");
		List<String> res = new ArrayList<>();
		res.add(" "); // empty entry
		for (String choice : choices) {
			res.add(choice);
		}
		return res.toArray(new String[res.size()]);
	}

	public static String[] getOneOfList(Parameter p) {
		List<String> res = new ArrayList<>();
		res.add(" "); // empty entry
		for (String choice : p.getOneOf()) {
			res.add(choice);
		}
		return res.toArray(new String[res.size()]);
	}

	/**
	 * returns the component class for the given scheme
	 *
	 * @param scheme
	 * @return the class or null if not found
	 */
	protected static String getComponentClass(String scheme, CamelFile camelFile) {
		String compClass = null;
		IProject project = camelFile.getResource().getProject();
		Collection<Component> components = camelFile.getCamelModel().getComponents();
		for (Component c : components) {
			if (c.supportsScheme(scheme)) {
				compClass = c.getClazz();
				break;
			}
		}

		if (compClass == null) {
			// seems this scheme has no model entry -> check dependency
			try {
				ZipFile zf = null;
				ZipEntry ze = null;
				IJavaProject jpr = JavaCore.create(project);
				for (IClasspathEntry e : jpr.getResolvedClasspath(true)) {
					File cpEntryFile = e.getPath().toFile();
					if (isJarFile(cpEntryFile)) {
						zf = new ZipFile(cpEntryFile);
						ze = zf.getEntry(String.format("META-INF/services/org/apache/camel/component/%s", scheme));
						if (ze != null) {
							break;
						}
						ze = null;
						zf = null;
					}
				}

				if (ze != null) {
					// try to generate a model entry for the component class
					Properties p = new Properties();
					p.load(zf.getInputStream(ze));
					compClass = p.getProperty("class");
				}
			} catch (Exception ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
				compClass = null;
			}
		}

		return compClass;
	}

	/**
	 * returns the component class for the given scheme
	 *
	 * @param scheme
	 * @param project
	 * @return the class or null if not found
	 */
	protected static String getComponentJSon(String scheme, IProject project) {
		String json = null;

		try {
			ZipFile zf = null;
			ZipEntry ze = null;
			IJavaProject jpr = JavaCore.create(project);
			for (IClasspathEntry e : jpr.getResolvedClasspath(true)) {
				File cpEntryFile = e.getPath().toFile();
				if (isJarFile(cpEntryFile)) {
					zf = new ZipFile(cpEntryFile);
					ze = zf.getEntry(String.format("META-INF/services/org/apache/camel/component/%s", scheme));
					if (ze != null) {
						break;
					}
					ze = null;
					zf = null;
				}
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
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
			json = null;
		}

		return json;
	}

	private static boolean isJarFile(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith(".jar");
	}

	protected static Component buildModelForComponent(String scheme, String clazz, CamelFile camelFile) {
		// 1. take what we have in our model xml
		CamelModel camelModel = camelFile.getCamelModel();
		Component resModel = camelModel.getComponentForScheme(scheme);

		// 2. try to generate the model from json blob
		if (resModel == null) {
			resModel = buildModelFromJSON(scheme, getComponentJSon(scheme, camelFile.getResource().getProject()), clazz, camelModel);
		}

		// 3. handling special cases
		if (resModel == null) {
			// while the activemq component still has no own json file we simply
			// use the jms one for now
			if ("activemq".equalsIgnoreCase(scheme)) {
				return camelModel.getComponentForScheme("jms").duplicateFor(scheme, clazz);
			}
		}

		return resModel;
	}

	/**
	 * tries to build the model by querying the component config of the camel
	 * component
	 *
	 * @param clazz
	 *            the component class
	 * @param camelModel 
	 * @return
	 */
	protected static Component buildModelFromJSON(String scheme, String oJSONBlob, String clazz, CamelModel camelModel) {
		Component resModel = null;

		try {
			if (oJSONBlob != null) {
				resModel = buildModelFromJSonBlob(oJSONBlob, clazz);
				resModel.setScheme(scheme);
				saveModel(camelModel, resModel);
			}
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}

		return resModel;
	}

	private static void saveModel(CamelModel camelModel, Component component) {
		if(!knownComponentsForCamelModel.containsKey(camelModel)){
			knownComponentsForCamelModel.put(camelModel, new WeakHashMap<>());
		}
		knownComponentsForCamelModel.get(camelModel).put(component.getClazz(), component);
		
		try {
			// create JAXB context and instantiate marshaller
			// JAXBContext context =
			// JAXBContext.newInstance(ComponentModel.class, Component.class,
			// Dependency.class, ComponentProperty.class, Parameter.class);
			// Marshaller m = context.createMarshaller();
			// m.marshal(component, new File("/var/tmp/model.xml"));
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
	}

	public static URLClassLoader getProjectClassLoader(IProject project) {
		try {
			IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
			IPackageFragmentRoot[] pfroots = javaProject.getAllPackageFragmentRoots();
			List<URL> urls = new ArrayList<>();
			for (IPackageFragmentRoot root : pfroots) {
				URL rUrl = root.getPath().toFile().toURI().toURL();
				urls.add(rUrl);
			}
			return new URLClassLoader(urls.toArray(new URL[urls.size()]), CamelComponentUtils.class.getClassLoader());
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
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

				if ("kind".equals(propName)) {
					resModel.setKind(valueNode.asString());
				} else if ("scheme".equals(propName)) {
					resModel.setScheme(valueNode.asString());
				} else if ("syntax".equals(propName)) {
					resModel.setSyntax(valueNode.asString());
				} else if ("title".equals(propName)) {
					resModel.setTitle(valueNode.asString());
				} else if ("description".equals(propName)) {
					resModel.setDescription(valueNode.asString());
				} else if ("label".equals(propName)) {
					ArrayList<String> al = new ArrayList<String>();
					al.addAll(Arrays.asList(valueNode.asString().split(",")));
					resModel.setTags(al);
				} else if ("consumerOnly".equals(propName)) {
					resModel.setConsumerOnly(valueNode.asString());
				} else if ("producerOnly".equals(propName)) {
					resModel.setProducerOnly(valueNode.asString());
				} else if ("javaType".equals(propName)) {
					resModel.setClazz(valueNode.asString());
				} else if ("groupId".equals(propName)) {
					grpId = valueNode.asString();
				} else if ("artifactId".equals(propName)) {
					artId = valueNode.asString();
				} else if ("version".equals(propName)) {
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

			ArrayList<ComponentProperty> cProps = new ArrayList<>();

			ModelNode componentPropertiesNode = model.get("componentProperties");
			Map<String, Object> cprops = JsonHelper.getAsMap(componentPropertiesNode);
			it = cprops.keySet().iterator();

			while (it.hasNext()) {
				ComponentProperty cp = new ComponentProperty();
				String propName = it.next();
				ModelNode valueNode = componentNode.get(propName);

				if ("defaultValue".equals(propName)) {
					cp.setDefaultValue(valueNode.asString());
				} else if ("deprecated".equals(propName)) {
					cp.setDeprecated(valueNode.asString());
				} else if ("description".equals(propName)) {
					cp.setDescription(valueNode.asString());
				} else if ("javaType".equals(propName)) {
					cp.setJavaType(valueNode.asString());
				} else if ("kind".equals(propName)) {
					cp.setKind(valueNode.asString());
				} else if ("name".equals(propName)) {
					cp.setName(valueNode.asString());
				} else if ("type".equals(propName)) {
					cp.setType(valueNode.asString());
				} else {
					// unknown property
				}
				cProps.add(cp);
			}
			Map<String, ComponentProperty> cpropsMap = new HashMap<>();
			for (ComponentProperty cp : cProps) {
				cpropsMap.put(cp.getName(), cp);
			}
			resModel.setComponentProperties(cpropsMap);

			ModelNode propsNode = model.get("properties");
			props = JsonHelper.getAsMap(propsNode);
			it = props.keySet().iterator();

			ArrayList<Parameter> uriParams = new ArrayList<>();

			while (it.hasNext()) {
				String propName = it.next();
				ModelNode valueNode = propsNode.get(propName);
				Parameter param = new Parameter();

				param.setName(propName);

				if (valueNode.hasDefined("choice")) {
					param.setChoice(valueNode.get("choice").asString());
				}
				// if (valueNode.hasDefined("oneOf")) {
				// param.setOneOf(valueNode.get("oneOf").asString());
				// }
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
			Map<String, Parameter> uriParamsMap = new HashMap<>();
			for (Parameter p : uriParams) {
				uriParamsMap.put(p.getName(), p);
			}
			resModel.setProperties(uriParamsMap);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
			resModel = null;
		}

		return resModel;
	}
}
