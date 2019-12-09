/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 */
public final class CamelComponentUtils {

	private static Map<CamelModel, Map<String, Component>> knownComponentsForCamelModel = new HashMap<>();

	private CamelComponentUtils() {
		throw new IllegalAccessError("Utility class");
	}
	
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
		Map<String, Component> knownComponents = retrieveKnownComponents(camelModel);
		String componentClass = getComponentClass(protocol, camelFile, camelModel);
		if (knownComponents.containsKey(componentClass)) {
			return knownComponents.get(componentClass);
		} else {
			Component c = buildModelForComponent(protocol, componentClass, camelFile, camelModel);
			if (c != null) {
				knownComponents.put(componentClass, c);
				return getComponentModel(protocol, camelFile);
			}
			return null;
		}
	}

	private static Map<String, Component> retrieveKnownComponents(CamelModel camelModel) {
		Map<String, Component> knownComponents;
		if (knownComponentsForCamelModel.containsKey(camelModel)) {
			knownComponents = knownComponentsForCamelModel.get(camelModel);
		} else {
			knownComponents = new HashMap<>();
			knownComponentsForCamelModel.put(camelModel, knownComponents);
		}
		return knownComponents;
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
		return "ref".equalsIgnoreCase(p.getName()) && "string".equalsIgnoreCase(p.getType())
				&& "java.lang.String".equalsIgnoreCase(p.getJavaType()) && "attribute".equalsIgnoreCase(p.getKind());
	}

	public static boolean isBooleanProperty(Parameter p) {
		return "boolean".equalsIgnoreCase(p.getJavaType()) || "java.lang.Boolean".equalsIgnoreCase(p.getJavaType());
	}

	public static boolean isDescriptionProperty(Parameter p) {
		return AbstractCamelModelElement.NODE_KIND_ELEMENT.equals(p.getKind())
				&& "org.apache.camel.model.DescriptionDefinition".equalsIgnoreCase(p.getJavaType())
				&& "description".equals(p.getName());
	}

	public static boolean isTextProperty(Parameter p) {
		return p.getChoice() == null && !"ref".equalsIgnoreCase(p.getName())
				&& !"expression".equals(p.getKind())
				&& ("String".equalsIgnoreCase(p.getJavaType()) || "java.lang.String".equalsIgnoreCase(p.getJavaType())
						|| "java.net.URL".equalsIgnoreCase(p.getJavaType())
						|| "java.net.URI".equalsIgnoreCase(p.getJavaType())
						|| "Text".equalsIgnoreCase(p.getJavaType()));
	}
	
	public static boolean isCharProperty(Parameter p) {
		return "char".equalsIgnoreCase(p.getJavaType());
	}

	public static boolean isNumberProperty(Parameter p) {
		final String javaType = p.getJavaType();
		return p.getChoice() == null && (isIntegerTypeProperty(javaType)
				|| isLongTypeProperty(javaType)
				|| isDoubleTypeProperty(javaType)
				|| isFloatTypeProperty(javaType)
				|| "Number".equalsIgnoreCase(javaType));
	}

	public static boolean isLongTypeProperty(final String javaType) {
		return "long".equalsIgnoreCase(javaType) || "java.lang.Long".equalsIgnoreCase(javaType);
	}

	public static boolean isDoubleTypeProperty(final String javaType) {
		return "double".equalsIgnoreCase(javaType) || "java.lang.Double".equalsIgnoreCase(javaType);
	}

	public static boolean isFloatTypeProperty(final String javaType) {
		return "float".equalsIgnoreCase(javaType) || "java.lang.Float".equalsIgnoreCase(javaType);
	}

	public static boolean isIntegerTypeProperty(final String javaType) {
		return "int".equalsIgnoreCase(javaType) || "Integer".equalsIgnoreCase(javaType) || "java.lang.Integer".equalsIgnoreCase(javaType);
	}

	public static boolean isChoiceProperty(Parameter p) {
		return p.getChoice() != null && p.getChoice().length > 0;
	}

	public static boolean isFileProperty(Parameter p) {
		return "file".equalsIgnoreCase(p.getJavaType()) || "java.io.file".equalsIgnoreCase(p.getJavaType());
	}
	
	public static boolean isClassProperty(Parameter p) {
		return "object".equalsIgnoreCase(p.getType());
	}

	public static boolean isExpressionProperty(Parameter p) {
		return "expression".equalsIgnoreCase(p.getKind())
				|| "org.apache.camel.model.language.ExpressionDefinition".equalsIgnoreCase(p.getJavaType());
	}

	public static boolean isDataFormatProperty(Parameter p) {
		return AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind())
				&& "org.apache.camel.model.DataFormatDefinition".equalsIgnoreCase(p.getJavaType());
	}

	public static boolean isListProperty(Parameter p) {
		return p.getJavaType().toLowerCase().startsWith("java.util.list")
				|| p.getJavaType().toLowerCase().startsWith("java.util.collection");
	}

	public static boolean isMapProperty(Parameter p) {
		return p.getJavaType().toLowerCase().startsWith("java.util.map");
	}

	public static boolean isUriPathParameter(Parameter p) {
		return p.getKind() != null && "path".equalsIgnoreCase(p.getKind());
	}

	public static boolean isUriOptionParameter(Parameter p) {
		return p.getKind() != null && "parameter".equalsIgnoreCase(p.getKind());
	}

	public static boolean isUnsupportedProperty(Parameter p) {
		return isMapProperty(p) || p.getJavaType().toLowerCase().startsWith("java.util.date");
	}

	public static String[] getChoicesWithExtraEmptyEntry(Parameter p) {
		String[] choices = p.getChoice();
		List<String> res = new ArrayList<>();
		res.add(""); // empty entry
		for (String choice : choices) {
			res.add(choice);
		}
		return res.toArray(new String[res.size()]);
	}

	public static String[] getOneOfList(Parameter p) {
		return p.getOneOf();
	}

	public static String getComponentClassForScheme(String scheme, CamelModel camelModel) {
		String compClass = null;
		Collection<Component> components = camelModel.getComponents();
		for (Component c : components) {
			if (c.supportsScheme(scheme)) {
				compClass = c.getClazz();
				break;
			}
		}
		return compClass;
	}

	private static String getClassFromZipEntry(String scheme, File cpEntryFile) {
		if (isJarFile(cpEntryFile)) {
			try (ZipFile zf = new ZipFile(cpEntryFile)) {
				ZipEntry ze = zf.getEntry(String.format("META-INF/services/org/apache/camel/component/%s", scheme));
				if (ze != null) {
					// try to generate a model entry for the component class
					Properties p = new Properties();
					p.load(zf.getInputStream(ze));
					return p.getProperty("class");
				}
			} catch (IOException ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
			}
		}
		return null;
	}
	
	private static String getComponentClassFromJar(IJavaProject jpr, String scheme){
		try {
			for (IClasspathEntry e : jpr.getResolvedClasspath(true)) {
				File cpEntryFile = e.getPath().toFile();
				String compClass = getClassFromZipEntry(scheme, cpEntryFile);
				if (!Strings.isBlank(compClass)) {
					return compClass;
				}
			}
		} catch (JavaModelException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
	
	/**
	 * returns the component class for the given scheme
	 *
	 * @param scheme
	 * @param camelFile
	 * @param camelModel
	 * @return the class or null if not found
	 */
	protected static String getComponentClass(String scheme, CamelFile camelFile, CamelModel camelModel) {
		String compClass = getComponentClassForScheme(scheme, camelModel);
		if (compClass == null) {
			// seems this scheme has no model entry -> check dependency
			IProject project = camelFile.getResource().getProject();
			IJavaProject jpr = JavaCore.create(project);
			if (jpr.exists() && jpr.isOpen()) {
				compClass = getComponentClassFromJar(jpr, scheme);
			}
		}

		return compClass;
	}

	private static String getComponentJsonFromJar(File cpEntryFile, String scheme) {
		try (ZipFile zf = new ZipFile(cpEntryFile)) {
			ZipEntry ze = zf.getEntry(String.format("META-INF/services/org/apache/camel/component/%s", scheme));
			if (ze != null) {
				// try to generate a model entry for the component class
				Properties p = new Properties();
				p.load(zf.getInputStream(ze));
				String compClass = p.getProperty("class");
				String packageName = compClass.substring(0, compClass.lastIndexOf('.'));
				String folder = packageName.replaceAll("\\.", "/");
				ze = zf.getEntry(String.format("%s/%s.json", folder, scheme));
				if (ze != null) {
					return IOUtils.loadText(zf.getInputStream(ze), null);
				}
			}
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
	
	/**
	 * returns the component class for the given scheme
	 *
	 * @param scheme
	 * @param project
	 * @return the class or null if not found
	 */
	protected static String getComponentJSon(String scheme, IProject project) {
		IJavaProject jpr = JavaCore.create(project);
		
		if (jpr.exists() && jpr.isOpen()) {
			try {
				for (IClasspathEntry e : jpr.getResolvedClasspath(true)) {
					File cpEntryFile = e.getPath().toFile();
					if (!isJarFile(cpEntryFile)) {
						continue;
					}
					String compJSON = getComponentJsonFromJar(cpEntryFile, scheme);
					if (!Strings.isBlank(compJSON)) {
						return compJSON;
					}
				}
			} catch (JavaModelException ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
			}
		}
		return null;
	}

	private static boolean isJarFile(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith(".jar");
	}

	protected static Component buildModelForComponent(String scheme, String clazz, CamelFile camelFile, CamelModel camelModel) {
		// 1. take what we have in our model xml
		Component resModel = camelModel.getComponentForScheme(scheme);

		// 2. try to generate the model from json blob
		if (resModel == null) {
			resModel = buildModelFromJSON(scheme, getComponentJSon(scheme, camelFile.getResource().getProject()), camelModel);
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
	protected static Component buildModelFromJSON(String scheme, String oJSONBlob, CamelModel camelModel) {
		Component resModel = null;

		try {
			if (oJSONBlob != null) {
				resModel = buildModelFromJSonBlob(oJSONBlob);
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
			knownComponentsForCamelModel.put(camelModel, new HashMap<>());
		}
		knownComponentsForCamelModel.get(camelModel).put(component.getClazz(), component);
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
	protected static Component buildModelFromJSonBlob(String json) {
		return Component.getJSONFactoryInstance(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
	}
}
