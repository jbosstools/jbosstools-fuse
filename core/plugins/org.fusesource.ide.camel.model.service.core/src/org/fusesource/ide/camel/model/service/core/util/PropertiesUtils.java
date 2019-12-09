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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.UriParameterKind;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentProperty;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.foundation.core.util.CamelUtils;

/**
 * @author lhein
 */
public class PropertiesUtils {

	public static final Pattern PATH_DELIMETER = Pattern.compile(":|/");

	public static Parameter getUriParam(String name, Component c) {
		return getUriParam(name, c.getParameters());
	}

	public static Parameter getUriParam(String name, List<Parameter> uriParams) {
		for (Parameter p : uriParams) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public static Component getComponentFor(AbstractCamelModelElement selectedEP) {
		if (selectedEP != null && selectedEP.getParameter("uri") != null) {
			int protocolSeparatorIdx = ((String) selectedEP.getParameter("uri")).indexOf(':');
			if (protocolSeparatorIdx != -1) {
				return CamelComponentUtils.getComponentModel(
						((String) selectedEP.getParameter("uri")).substring(0, protocolSeparatorIdx),
						selectedEP.getCamelFile());
			}
		}
		return null;
	}

	public static Eip getEipFor(AbstractCamelModelElement selectedEP) {
		if (selectedEP != null && selectedEP.getUnderlyingMetaModelObject() != null) {
			return selectedEP.getUnderlyingMetaModelObject();
		}
		return null;
	}

	public static List<Parameter> getPathProperties(AbstractCamelModelElement selectedEP) {
		ArrayList<Parameter> result = new ArrayList<>();

		if (selectedEP != null && selectedEP.getParameter("uri") != null) {
			int protocolSeparatorIdx = ((String) selectedEP.getParameter("uri")).indexOf(':');
			if (protocolSeparatorIdx != -1) {
				Component componentModel = CamelComponentUtils.getComponentModel(
						((String) selectedEP.getParameter("uri")).substring(0, protocolSeparatorIdx),
						selectedEP.getCamelFile());
				return getPathProperties(selectedEP, componentModel);
			}
		}

		return result;
	}

	public static List<Parameter> getPathProperties(AbstractCamelModelElement selectedEP, Component componentModel) {
		List<Parameter> result = new ArrayList<>();

		if (selectedEP != null && selectedEP.getParameter("uri") != null && componentModel != null) {
			int protocolSeparatorIdx = ((String) selectedEP.getParameter("uri")).indexOf(':');
			if (protocolSeparatorIdx != -1) {
				for (Parameter p : componentModel.getParameters()) {
					if ("path".equalsIgnoreCase(p.getKind())) {
						result.add(p);
					}
				}
			}
		}

		return result;
	}

	/**
	 * 
	 * @param kind
	 * @return
	 */
	public static List<Parameter> getPropertiesFor(AbstractCamelModelElement selectedEP, UriParameterKind kind) {
		if (selectedEP != null && selectedEP.getParameter("uri") != null) {
			int protocolSeparatorIdx = ((String) selectedEP.getParameter("uri")).indexOf(':');
			if (protocolSeparatorIdx != -1) {
				Component componentModel = CamelComponentUtils.getComponentModel(
						((String) selectedEP.getParameter("uri")).substring(0, protocolSeparatorIdx),
						selectedEP.getCamelFile());
				if (componentModel != null) {
					return getPropertiesFor(kind, componentModel);
				}
			}
		}

		return Collections.emptyList();
	}

	/**
	 * @param kind
	 * @param result
	 * @param componentModel
	 */
	public static List<Parameter> getPropertiesFor(UriParameterKind kind, Component componentModel) {
		List<Parameter> result = new ArrayList<>();
		for (Parameter p : componentModel.getParameters()) {
			if (kind == UriParameterKind.CONSUMER) {
				if (p.getLabel() != null && containsLabel("consumer", p)) {
					result.add(p);
				}
			} else if (kind == UriParameterKind.PRODUCER) {
				if (p.getLabel() != null && containsLabel("producer", p)) {
					result.add(p);
				}
			} else if (kind == UriParameterKind.BOTH) {
				if (p.getLabel() == null || p.getLabel().trim().length() < 1) {
					result.add(p);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param selectedEP
	 * @return
	 */
	public static List<Parameter> getComponentPropertiesFor(AbstractCamelModelElement selectedEP) {
		if (selectedEP != null && selectedEP.getParameter("uri") != null) {
			int protocolSeparatorIdx = ((String) selectedEP.getParameter("uri")).indexOf(':');
			if (protocolSeparatorIdx != -1) {
				Component componentModel = CamelComponentUtils.getComponentModel(
						((String) selectedEP.getParameter("uri")).substring(0, protocolSeparatorIdx),
						selectedEP.getCamelFile());
				if (componentModel != null) {
					return componentModel.getParameters();
				}
			}
		}
		return new ArrayList<>();
	}

	/**
	 * checks whether the parameter has the given label
	 * 
	 * @param label
	 *            the label to check for
	 * @param p
	 *            the parameter
	 * @return true if parameter has this label
	 */
	public static boolean containsLabel(String label, Parameter p) {
		if (p.getLabel() == null)
			return false;

		String pLabelString = p.getLabel();
		if (pLabelString.indexOf(',') != -1) {
			String[] labels = pLabelString.split(",");
			for (String lab : labels) {
				if (lab.trim().equalsIgnoreCase(label))
					return true;
			}
		} else {
			if (pLabelString.trim().equalsIgnoreCase(label))
				return true;
		}

		return false;
	}

	/**
	 * 
	 * @param kind
	 * @return
	 */
	public static List<Parameter> getPropertiesFor(AbstractCamelModelElement selectedEP) {
		List<Parameter> result = new ArrayList<>();

		if (selectedEP != null && selectedEP.getUnderlyingMetaModelObject() != null) {
			Eip eip = selectedEP.getUnderlyingMetaModelObject();
			result = eip.getParameters();
		}

		return result;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public static String getPropertyFromUri(AbstractCamelModelElement selectedEP, Parameter p, Component c) {
		// we need to distinguish between parameters in the uri part after the ?
		// char
		final String kind = p.getKind();
		final String uriParameterValue = (String) selectedEP.getParameter("uri");
		if ("parameter".equalsIgnoreCase(kind)) {
			int idx = uriParameterValue.indexOf(p.getName() + "=");
			if (idx != -1) {
				return uriParameterValue.substring(idx + (p.getName() + "=").length(),
						uriParameterValue.indexOf('&', idx + 1) != -1 ? uriParameterValue.indexOf('&', idx + 1)
								: uriParameterValue.length());
			} else {
				// no value defined....return the default
				if (p.getDefaultValue() != null && p.getDefaultValue().trim().length() > 0)
					return p.getDefaultValue();
			}
			// and those which are part of the part between the scheme: and the
			// ? char
		} else if ("path".equalsIgnoreCase(kind)) {
			// first get the delimiters
			String delimiters = getDelimitersAsString(c.getSyntax(), c.getParameters());
			
			// now get the uri without scheme and options
			String uri = uriParameterValue.substring(uriParameterValue.indexOf(':') + 1,
					uriParameterValue.lastIndexOf('?') != -1 ? uriParameterValue.lastIndexOf('?') : uriParameterValue.length());
			
			String pathParameters = findParameterOfTheUriPath(selectedEP, c, uriParameterValue);
			
			// sometimes there is only one field, so there are no delimiters
			if (delimiters.length() < 1) {
				if(pathParameters != null){
					return uri + "?" + pathParameters;
				} else {
					return uri;
				}
			} else {
				return getPathMap(selectedEP, c).get(p.getName());
			}
		}
		// all other cases are unsupported atm
		return null;
	}

	private static String findParameterOfTheUriPath(AbstractCamelModelElement selectedEP, Component c, final String uriParameterValue) {
		String pathParameters = null;
		if(uriParameterValue.lastIndexOf('?') != -1){
			String parameters = uriParameterValue.substring(uriParameterValue.lastIndexOf('?') +1, uriParameterValue.length());
			try {
				Map<String, Object> parseQuery = CamelServiceManagerUtil.getManagerService().parseQuery(parameters);
				for (Parameter componentParameter : c.getParameters()) {
					String componentParameterName = componentParameter.getName();
					if(parseQuery.containsKey(componentParameterName)){
						parseQuery.remove(componentParameterName);
					}
				}
				if(!parseQuery.isEmpty()) {
					pathParameters = CamelServiceManagerUtil.getManagerService().createQuery(parseQuery);
				}
			} catch (URISyntaxException e) {
				CamelModelServiceCoreActivator.pluginLog().logError(e);
			}
		}
		return pathParameters;
	}

	private static int getFieldIndex(String delimiters, String syntax, String fieldName) {
		int idx = -1;
		StringTokenizer syntaxTok = new StringTokenizer(syntax, delimiters);
		while (syntaxTok.hasMoreTokens()) {
			idx++;
			String fName = syntaxTok.nextToken();
			if (fName.equals(fieldName))
				break;
		}
		return idx;
	}

	/**
	 * strips off the scheme and field names from the syntax and then returns
	 * all remaining delimiters
	 * 
	 * @param syntax
	 * @param params
	 * @return
	 */
	private static String getDelimitersAsString(String syntax, List<Parameter> params) {
		String delimiterString = syntax;
		final String syntaxWithoutScheme = delimiterString.substring(delimiterString.indexOf(':') + 1);

		// first strip off the <scheme>:
		delimiterString = syntaxWithoutScheme;

		Collections.sort(params, (o1, o2) -> o2.getName().length() - o1.getName().length());

		// then strip off the remaining variable names
		for (Parameter p : params) {
			if (CamelComponentUtils.isUriPathParameter(p)) {
				delimiterString = delimiterString.replace(p.getName(), "");
			}
		}
		return delimiterString;
	}

	/**
	 * EXPERIMENTAL
	 * 
	 * @param selectedEP
	 * @param c
	 * @return
	 */
	private static Map<String, String> getPathMap(AbstractCamelModelElement selectedEP, Component c) {
		Map<String, String> retVal = new HashMap<>();

		// get all path params
		List<Parameter> pathParams = getPathProperties(selectedEP, c);
		// get all delimiters
		final String syntax = c.getSyntax();
		String delimiters = getDelimitersAsString(syntax, pathParams);
		// now get the uri without scheme and options
		String initialURIValue = (String) selectedEP.getParameter("uri");
		String uri = initialURIValue.substring(
				initialURIValue.indexOf(':') + 1,
				initialURIValue.indexOf('?') != -1 ? initialURIValue.indexOf('?') : initialURIValue.length());

		Map<Integer, Parameter> fieldMapping = new HashMap<>();
		for (Parameter param : pathParams) {
			int idx = getFieldIndex(delimiters, syntax.substring(syntax.indexOf(':') + 1), param.getName());
			fieldMapping.put(idx, param);
		}

		int lastPos = 0;
		int skippedDelimiters = 0;
		for (int field = 0; field < delimiters.length() + 1; field++) {
			Parameter uriParam = fieldMapping.get(field);
			boolean required = "true".equalsIgnoreCase(uriParam.getRequired());
			if (skippedDelimiters > 0 && !required){
				retVal.put(uriParam.getName(), null);
				skippedDelimiters--;
				continue;
			}
			int foundPos = -1;
			for (int delIdx = field + skippedDelimiters; delIdx < delimiters.length(); delIdx++) {
				char delim = delimiters.charAt(delIdx);
				int pos = uri.indexOf(delim, lastPos);
				if (pos != -1) {
					foundPos = pos;
					break;
				} else {
					skippedDelimiters++;
				}
			}
			if (foundPos != -1) {
				retVal.put(uriParam.getName(), uri.substring(lastPos, foundPos));
				lastPos = foundPos + 1;
			} else {
				// no delimiters found, so we have only one value
				String v = uri.substring(lastPos);
				String fieldName = null;
				while (fieldName == null && uriParam != null) {
					// this check is required if we start with an optional field
					// and its not in the uri
					if (field == 0
							&& (uriParam.getRequired() == null || uriParam.getRequired().equalsIgnoreCase("false"))) {
						uriParam = fieldMapping.get(++field);
					} else {
						fieldName = uriParam.getName();
					}
				}
				retVal.put(fieldName, v);
				break;
			}
		}

		return retVal;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public static Object getTypedPropertyFromUri(AbstractCamelModelElement selectedEP, Parameter p, Component c) {
		String val = getPropertyFromUri(selectedEP, p, c);

		if (CamelComponentUtils.isBooleanProperty(p)) {
			return Boolean.parseBoolean(val);
		}

		if (CamelComponentUtils.isTextProperty(p)) {
			return val;
		}
		
		if (CamelComponentUtils.isClassProperty(p) && !AbstractCamelModelElement.NODE_KIND_ELEMENT.equals(p.getKind()) ) {
			return val;
		}

		if (CamelComponentUtils.isNumberProperty(p)) {
			return val;
		}

		if (CamelComponentUtils.isCharProperty(p)){
			return val;
		}

		return null;
	}

	/**
	 * updates the uri for the changed value
	 * 
	 * @param p
	 * @param value
	 */
	public static void updateURIParams(AbstractCamelModelElement selectedEP, Parameter p, Object value, Component c, IObservableMap<?,?> modelMap) {
		if ("path".equalsIgnoreCase(p.getKind())) {
			// simply rebuild the uri

			// first build the path part
			String newUri = updatePathParams(selectedEP, c, c.getSyntax(), p, value, getPathProperties(selectedEP), modelMap) + "?";

			// now build the options
			for (Parameter uriParam : c.getParameters()) {
				if ("path".equalsIgnoreCase(uriParam.getKind()))
					continue;
				String pName = uriParam.getName();
				String pValue = getPropertyFromUri(selectedEP, uriParam, c);
				if (pValue == null || pValue.trim().length() < 1)
					continue;

				// remove values which equal the default
				if (uriParam.getDefaultValue() != null && uriParam.getDefaultValue().trim().length() > 0
						&& pValue.equals(uriParam.getDefaultValue()))
					continue;

				if (!newUri.endsWith("?")) {
					newUri += "&";
				}
				newUri += String.format("%s=%s", pName, pValue);
			}

			if (newUri.endsWith("?"))
				newUri = newUri.substring(0, newUri.length() - 1);

			selectedEP.setParameter("uri", newUri);
		} else {
			// normal uri options
			boolean equalsDefaultValue = value != null && p.getDefaultValue() != null && value.toString().equals(p.getDefaultValue());
			boolean valueDeleted = (value == null) || value.toString().trim().length() < 1 || (equalsDefaultValue && AbstractCamelModelElement.useOptimizedXML());
			String val = getPropertyFromUri(selectedEP, p, c);
			String key = String.format("%s=", p.getName());
			final String uri = (String) selectedEP.getParameter("uri");
			int idx = uri.indexOf(key);
			if (val != null && idx != -1) {
				// special replace logic needed as special expression chars can
				// break the replacement
				String newUri = "";
				boolean firstParam = idx == -1 || uri.charAt(idx - 1) == '?';
				if (valueDeleted) {
					newUri = uri.substring(0, firstParam ? idx : idx - 1);
				} else {
					newUri = uri.substring(0, idx + key.length());
					newUri += value.toString();	
				}				
				newUri += uri.substring(idx + key.length() + val.length());
				if (newUri.indexOf("?&") != -1)
					newUri = newUri.replace("?&", "?");
				if (newUri.endsWith("?"))
					newUri = newUri.substring(0, newUri.indexOf('?'));
				selectedEP.setParameter("uri", newUri);
			} else {
				String newUri = uri;
				if (!valueDeleted) {
					if (uri.indexOf('?') == -1) {
						newUri += "?";
					}
					if (uri.indexOf('=') != -1) {
						newUri += "&";
					}
					newUri += String.format("%s=%s", p.getName(), value.toString());
				}
				selectedEP.setParameter("uri", newUri);
			}
		}
	}

	/**
	 * Updates the path part of a given uri syntax
	 * @param selectedEP 
	 * @param component 
	 * 
	 * @param syntax
	 *            uri syntax
	 * @param param
	 *            parameter to be changed
	 * @param value
	 *            value
	 * @param pathParams
	 *            path parameters
	 * @param modelMap
	 *            current model map
	 * @return
	 */
	public static String updatePathParams(AbstractCamelModelElement selectedEP, Component component, String syntax, Parameter param, Object value, List<Parameter> pathParams, Map<?,?> modelMap) {
		String withoutScheme = syntax.substring(syntax.indexOf(':') + 1);
		for (Parameter pparam : pathParams) {
			String val;
			if (param.getName().equals(pparam.getName())) {
				val = value.toString();
			} else {
				Object storedValue = modelMap.get(pparam.getName());
				if(storedValue != null){
					val = storedValue.toString();
				} else {
					//modelMap is not initialized yet, use the model
					val = getPropertyFromUri(selectedEP, pparam, component);
				}
			}
			if (val == null || val.trim().length() < 1)
				val = pparam.getDefaultValue();

			if (val != null) {
				// sap components have some parameters with the same prefix (see
				// FUSETOOLS-1779)
				String newWithoutScheme = replaceParts(withoutScheme, pparam.getName(), val, PATH_DELIMETER);
				// if nothing happens then use the old logic
				if (withoutScheme.equals(newWithoutScheme)) {
					withoutScheme = withoutScheme.replace(pparam.getName(), val);
				} else {
					withoutScheme = newWithoutScheme;
				}
			}
		}
		return String.format("%s:%s", syntax.substring(0, syntax.indexOf(':')), withoutScheme);
	}

	/**
	 * Replaces either the whole part of a given text or nothing. The parts are
	 * determined by the specified delimiter.
	 * 
	 * @param text
	 *            text
	 * @param target
	 *            string to be replaced
	 * @param replacement
	 *            replacement string
	 * @param delimiter
	 *            delimiter
	 * @return text with replaced parts
	 */
	public static String replaceParts(String text, String target, String replacement, String delimiter) {
		return replaceParts(text, target, replacement, Pattern.compile(delimiter));
	}

	/**
	 * Replaces either the whole part of a given text or nothing. The parts are
	 * determined by the specified delimiter.
	 * 
	 * @param text
	 *            text
	 * @param target
	 *            string to be replaced
	 * @param replacement
	 *            replacement string
	 * @param delimiterPattern
	 *            delimiter pattern
	 * @return text with replaced parts
	 */
	public static String replaceParts(String text, String target, String replacement, Pattern delimiterPattern) {
		String[] parts = delimiterPattern.split(text);
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != null && parts[i].equals(target)) {
				parts[i] = replacement;
			}
		}

		Matcher matcher = delimiterPattern.matcher(text);

		StringBuilder result = new StringBuilder(parts[0]);
		int i = 1;
		while (matcher.find()) {
			result.append(matcher.group()).append(parts[i++]);
		}
		return result.toString();
	}

	public static String getUsedProtocol(AbstractCamelModelElement selectedEP) {
		return ((String) selectedEP.getParameter("uri")).substring(0,
				((String) selectedEP.getParameter("uri")).indexOf(':'));
	}

	/**
	 * Checks if the package field has to be pre-filled in this page and returns
	 * the package fragment to be used for that. The package fragment has the
	 * name of the project if the source folder does not contain any package and
	 * if the project name is a valid package name. If the source folder
	 * contains exactly one package then the name of that package is used as the
	 * package fragment's name. <code>null</code> is returned if none of the
	 * above is applicable.
	 * 
	 * @param javaProject
	 *            the containing Java project of the selection used to
	 *            initialize this page
	 * 
	 * @return the package fragment to be pre-filled in this page or
	 *         <code>null</code> if no suitable package can be suggested for the
	 *         given project
	 * 
	 * @since 3.9
	 */
	public static IPackageFragment getPackage(IJavaProject javaProject, final IPackageFragmentRoot pkgFragmentRoot) {
		String packName = null;
		IJavaElement[] packages = null;
		try {
			if (pkgFragmentRoot != null && pkgFragmentRoot.exists()) {
				packages = pkgFragmentRoot.getChildren();
				if (packages.length == 1) { // only default package -> use
											// Project name
					packName = javaProject.getElementName();
					// validate package name
					IStatus status = validatePackageName(packName, javaProject);
					if (status.getSeverity() == IStatus.OK) {
						return pkgFragmentRoot.getPackageFragment(packName);
					}
				} else {
					int noOfPackages = 0;
					IPackageFragment thePackage = null;
					for (final IJavaElement pack : packages) {
						IPackageFragment pkg = (IPackageFragment) pack;
						// ignoring empty parent packages and default package
						if ((!pkg.hasSubpackages() || pkg.hasChildren()) && !pkg.isDefaultPackage()) {
							noOfPackages++;
							thePackage = pkg;
							if (noOfPackages > 1) {
								return null;
							}
						}
					}
					if (noOfPackages == 1) { // use package name
						packName = thePackage.getElementName();
						return pkgFragmentRoot.getPackageFragment(packName);
					}
				}
			}
		} catch (JavaModelException e) {
			// fall through
		}
		return null;
	}

	public static IStatus validatePackageName(String text, IJavaProject project) {
		if (project == null || !project.exists()) {
			return JavaConventions.validatePackageName(text, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		}
		return JavaConventionsUtil.validatePackageName(text, project);
	}

	public static boolean isRequired(Parameter parameter) {
		return isParameterValueTrue(parameter.getRequired());
	}

	public static boolean isDeprecated(Parameter parameter) {
		return isParameterValueTrue(parameter.getDeprecated());
	}

	private static boolean isParameterValueTrue(String parameterValue) {
		return parameterValue != null && parameterValue.equalsIgnoreCase("true");
	}

	/**
	 * converts a given duration to milliseconds
	 * 
	 * @param value
	 * @return
	 */
	public static boolean validateDuration(String value) throws IllegalArgumentException {
		// try to convert to millis which will throw exception on error
		CamelServiceManagerUtil.getManagerService().durationToMillis(value);
		return true;
	}

	/**
	 * tries to figure out the used camel version of the currently opened
	 * diagram's project and if that fails it will return the latest supported
	 * camel version
	 * 
	 * @return
	 */
	public static String getCurrentProjectCamelVersion() {
		String camelVersion = CamelCatalogUtils.DEFAULT_CAMEL_VERSION;
		IProject wsProject = CamelUtils.getCurrentProject();
		if (wsProject != null) {
			camelVersion = new CamelMavenUtils().getCamelVersionFromMaven(wsProject);
		}
		return camelVersion;
	}
	
	/**
	 * method used to initialize the name field of each parameter as the name itself is only stored in the map as key
	 * 
	 * @param props
	 */
	public static void initializePropertyNames(Map<String, Parameter> props) {
		for(Map.Entry<String, Parameter> entry : props.entrySet()){
			Parameter parameter = entry.getValue();
			String name = entry.getKey();
			parameter.setName(name);
		}
	}
	
	/**
	 * method used to initialize the name field of each parameter as the name itself is only stored in the map as key
	 * 
	 * @param props
	 */
	public static void initializeComponentPropertyNames(Map<String, ComponentProperty> props) {
		for(Map.Entry<String, ComponentProperty> entry : props.entrySet()){
			ComponentProperty componentProperty = entry.getValue();
			String name = entry.getKey();
			componentProperty.setName(name);
		}
	}
}
