/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.validation.diagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.ValidationResult;
import org.fusesource.ide.camel.validation.ValidationSupport;
import org.fusesource.ide.camel.validation.model.NumberValidator;
import org.fusesource.ide.camel.validation.model.RequiredPropertyValidator;
import org.fusesource.ide.foundation.core.util.Strings;


/**
 * @author lhein
 */
public class BasicNodeValidator implements ValidationSupport {

	private static Map<IMarker, CamelModelElement> markers = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.validation.ValidationSupport#validate(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public ValidationResult validate(CamelModelElement camelModelElement) {
		ValidationResult result = new ValidationResult();

		if (camelModelElement != null && camelModelElement.getCamelContext() != null) { // TODO: check why camel context can be null?!?
			// we check if all mandatory fields are filled
			validateDetailProperties(camelModelElement, result);
			final Component component = PropertiesUtils.getComponentFor(camelModelElement);
			for (Parameter prop : new ArrayList<>(PropertiesUtils.getComponentPropertiesFor(camelModelElement))) {
				Object value = PropertiesUtils.getPropertyFromUri(camelModelElement, prop, component);
				checkFor(result, prop, value, new RequiredPropertyValidator(prop));
				checkFor(result, prop, value, new NumberValidator(prop));
			}
		}

		clearMarkers(camelModelElement);
		createMarkers(camelModelElement, result);

		return result;
	}

	/**
	 * @param camelModelElement
	 * @param result
	 */
	private void createMarkers(CamelModelElement camelModelElement, ValidationResult result) {
		final CamelFile camelFile = camelModelElement.getCamelFile();
		if (camelFile != null) {
			final IResource resource = camelFile.getResource();
			for (String error : result.getErrors()) {
				createMarker(resource, camelModelElement, error, IMarker.SEVERITY_ERROR);
			}
			for (String warning : result.getWarnings()) {
				createMarker(resource, camelModelElement, warning, IMarker.SEVERITY_WARNING);
			}
			for (String info : result.getInformations()) {
				createMarker(resource, camelModelElement, info, IMarker.SEVERITY_INFO);
			}
		}
	}

	/**
	 * @param camelModelElement
	 * @return
	 */
	private void clearMarkers(CamelModelElement camelModelElement) {
		try {
			final CamelFile camelFile = camelModelElement.getCamelFile();
			if (camelFile != null) {
				final IResource resource = camelFile.getResource();
				if (resource != null) {
					for (IMarker marker : resource.findMarkers(IFuseMarker.MARKER_TYPE, true, IResource.DEPTH_INFINITE)) {
						final CamelModelElement cmeWithMarker = markers.get(marker);
						if (camelModelElement.equals(cmeWithMarker)
								// we are lucky still the same model in memory
								|| hasSameId(camelModelElement, cmeWithMarker)
						// maybe just a reloaded model, so finger-crossed two
						// elements
						// have the same id
						) {
							markers.remove(marker);
							marker.delete();
						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param camelModelElement
	 * @param obj
	 * @return
	 */
	private boolean hasSameId(CamelModelElement camelModelElement, final CamelModelElement obj) {
		return camelModelElement.getId() != null && obj != null && camelModelElement.getId().equals(obj.getId());
	}

	/**
	 * @param result
	 * @param prop
	 * @param value
	 * @param validator
	 */
	private void checkFor(ValidationResult result, Parameter prop, Object value, final IValidator validator) {
		IStatus status = validator.validate(value);
		if (!status.isOK()) {
			result.addError(status.getMessage());
		}
	}

	/**
	 * @param selectedEP
	 * @param result
	 */
	private void validateDetailProperties(CamelModelElement selectedEP, ValidationResult result) {
		for (Parameter prop : PropertiesUtils.getPropertiesFor(selectedEP)) {
			String property = prop.getName();
			if ((prop.getKind().equalsIgnoreCase("element") && prop.getType().equalsIgnoreCase("array")) || prop.getJavaType().equals("org.apache.camel.model.OtherwiseDefinition"))
				continue;

			Object value = selectedEP.getParameter(property);

			if (PropertiesUtils.isRequired(prop)) {

				if (prop.getName().equalsIgnoreCase("uri")) {
					// only enforce URI if there is no REF set
					if (selectedEP.getParameter("uri") == null || ((String) selectedEP.getParameter("uri")).trim().length() < 1) {
						// no URI set -> check for REF
						if (selectedEP.getParameter("ref") == null || ((String) selectedEP.getParameter("ref")).trim().length() < 1) {
							// there is no ref
							result.addError("One of Ref and Uri values have to be filled! Please check the properties view for more details.");
						} else {
							// ref found - now check if REF has URI defined
							CamelModelElement cme = selectedEP.getCamelContext().findNode((String) selectedEP.getParameter("ref"));
							if (cme == null || cme.getParameter("uri") == null || ((String) cme.getParameter("uri")).trim().length() < 1) {
								// no uri defined on ref
								result.addError("The referenced endpoint has no URI defined or does not exist. Please check the properties view for more details.");
							}
						}
					}

					// check for broken refs
					if (selectedEP.getParameter("uri") != null && ((String) selectedEP.getParameter("uri")).startsWith("ref:")) {
						String refId = ((String) selectedEP.getParameter("uri")).trim().length() > "ref:".length()
								? ((String) selectedEP.getParameter("uri")).substring("ref:".length()) : null;
						List<String> refs = Arrays.asList(CamelComponentUtils.getRefs(selectedEP.getCamelFile()));
						if (refId == null || refId.trim().length() < 1 || refs.contains(refId) == false) {
							result.addError("The entered reference does not exist in your context! Please check the properties view for more details.");
						}
					}

					// warn user if he set both ref and uri
					if (selectedEP.getParameter("uri") != null && ((String) selectedEP.getParameter("uri")).trim().length() > 0 && selectedEP.getParameter("ref") != null
							&& ((String) selectedEP.getParameter("ref")).trim().length() > 0) {
						result.addError("Please choose either URI or Ref but do not enter both values. Please check the properties view for more details.");
					}

				} else if (prop.getName().equalsIgnoreCase("ref")) {

					if (value != null && value instanceof String && value.toString().trim().length() > 0) {
						String refId = (String) value;
						CamelModelElement cme = selectedEP.getCamelContext().findNode(refId);
						if (cme == null && selectedEP.getCamelFile().getGlobalDefinitions().containsKey(refId) == false) {
							// the ref doesn't exist
							result.addWarning("The entered reference does not exist in your context! Please check the properties view for more details.");
						} else {
							// the ref exists
							if (cme.getParameter("uri") == null || ((String) cme.getParameter("uri")).trim().length() < 1) {
								// but has no URI defined
								result.addError("The referenced endpoint does not define a valid URI! Please check the properties view for more details.");
							}
						}
					}

					// warn user if he set both ref and uri
					if (selectedEP.getParameter("uri") != null && ((String) selectedEP.getParameter("uri")).trim().length() > 0 && selectedEP.getParameter("ref") != null
							&& ((String) selectedEP.getParameter("ref")).trim().length() > 0) {
						result.addWarning("Please choose only ONE of Uri and Ref. Please check the properties view for more details.");
					}

				} else if (prop.getName().equalsIgnoreCase("id")) {
					// check if ID is unique
					if (value == null || value instanceof String == false || value.toString().trim().length() < 1) {
						result.addError("Parameter " + prop.getName() + " is a mandatory field and cannot be empty. Please check the properties view for more details.");
					} else {
						if (selectedEP.getCamelContext().isIDUnique((String) value) == false) {
							result.addError("Parameter " + prop.getName() + " does not contain a unique value. Please check the properties view for more details.");
						}
					}

				} else if (prop.getName().equalsIgnoreCase("expression")) {

					// ignore for now - TODO: provide better validation for
					// expression properties

				} else {
					// by default we only check for a value != null and length >
					// 0
					if (value == null || value instanceof String == false || value.toString().trim().length() < 1) {
						result.addError("Parameter " + prop.getName() + " is a mandatory field and cannot be empty. Please check the properties view for more details.");
					}
				}

			}
		}
	}

	/**
	 * checks if the given node's id property is unique in the whole camel context
	 * 
	 * @param nodeUnderValidation
	 * @param nodes
	 * @param processedNodeIDs
	 * @return
	 */
	protected boolean checkAllUniqueIDs(CamelModelElement nodeUnderValidation, List<CamelModelElement> nodes, ArrayList<String> processedNodeIDs) {
		boolean noDoubledIDs = true;
		for (CamelModelElement node : nodes) {
			if (node.getChildElements() != null) {
				noDoubledIDs = checkAllUniqueIDs(nodeUnderValidation, node.getChildElements(), processedNodeIDs);
				if (noDoubledIDs == false) return false;
			}
			if (noDoubledIDs) {
				if (!Strings.isBlank(node.getId())) {
					if (processedNodeIDs.contains(node.getId()) && node.equals(nodeUnderValidation)) {
						return false;
					} else {
						processedNodeIDs.add(node.getId());
					}
				}
			}
		}
		return noDoubledIDs;
	}

	private void createMarker(IResource resource, CamelModelElement cme, final String message, final int severity) {
		try {
			IMarker marker = resource.createMarker(IFuseMarker.MARKER_TYPE);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			managePosition(resource, cme, marker);
			markers.put(marker, cme);
			// marker.setAttribute(IMarker.CHAR_START, 0);
			// marker.setAttribute(IMarker.CHAR_END, 10);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param resource
	 * @param cme
	 * @param marker
	 * @throws CoreException
	 */
	private void managePosition(IResource resource, CamelModelElement cme, IMarker marker) throws CoreException {
		Integer lineNumber = -1;
		if (cme.getId() != null) {
			List<Integer> foundIds = findLineNumbers("id=\"" + cme.getId() + "\"", resource.getRawLocation().toFile());
			if (foundIds.size() == 1) {
				lineNumber = foundIds.get(0);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			}
		}
		if (lineNumber == -1) {
			marker.setAttribute(IMarker.LOCATION, "/" + getCamelPath(cme));
		} else {
			marker.setAttribute(IFuseMarker.PATH, "/" + getCamelPath(cme));
		}
		marker.setAttribute(IFuseMarker.CAMEL_ID, cme.getId());
	}

	public List<Integer> findLineNumbers(String word, File text) {
		List<Integer> results = new ArrayList<Integer>();
		LineNumberReader rdr;
		try {
			rdr = new LineNumberReader(new FileReader(text));
			try {
				String line;
				while ((line = rdr.readLine()) != null) {
					if (line.indexOf(word) >= 0) {
						results.add(rdr.getLineNumber());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					rdr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * @param cme
	 * @return
	 */
	private String getCamelPath(CamelModelElement cme) {
		String res = cme.getDisplayText();
		final CamelModelElement parent = cme.getParent();
		if (parent != null && !(parent instanceof CamelFile)) {
			res = getCamelPath(parent) + "/" + res;
		}
		return res;
	}

}
