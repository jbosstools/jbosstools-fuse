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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
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

	private static Map<IMarker, AbstractCamelModelElement> markers = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.validation.ValidationSupport#validate(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public ValidationResult validate(AbstractCamelModelElement camelModelElement) {
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
		
		Set<IMarker> markersRelatedToElement = getMarkersFor(camelModelElement);
		
		createOrReuseMarkers(camelModelElement, result, markersRelatedToElement);

		for (IMarker markerToDelete : markersRelatedToElement) {
			try {
				markerToDelete.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @param camelModelElement
	 * @param result
	 * @param markersRelatedToElement
	 */
	private void createOrReuseMarkers(AbstractCamelModelElement camelModelElement, ValidationResult result, Set<IMarker> markersRelatedToElement) {
		final CamelFile camelFile = camelModelElement.getCamelFile();
		if (camelFile != null) {
			final IResource resource = camelFile.getResource();
			for (String error : result.getErrors()) {
				createOrReuseMarker(resource, camelModelElement, error, IMarker.SEVERITY_ERROR, markersRelatedToElement);
			}
			for (String warning : result.getWarnings()) {
				createOrReuseMarker(resource, camelModelElement, warning, IMarker.SEVERITY_WARNING, markersRelatedToElement);
			}
			for (String info : result.getInformations()) {
				createOrReuseMarker(resource, camelModelElement, info, IMarker.SEVERITY_INFO, markersRelatedToElement);
			}
		}
	}

	/**
	 * @param camelModelElement
	 * @return
	 */
	public Set<IMarker> getMarkersFor(AbstractCamelModelElement camelModelElement) {
		Set<IMarker> res = new HashSet<>();
		try {
			final CamelFile camelFile = camelModelElement.getCamelFile();
			if (camelFile != null) {
				final IResource resource = camelFile.getResource();
				if (resource != null) {
					for (IMarker marker : resource.findMarkers(IFuseMarker.MARKER_TYPE, true, IResource.DEPTH_INFINITE)) {
						if (camelModelElement.getId() != null && camelModelElement.getId().equals(marker.getAttribute(IFuseMarker.CAMEL_ID))) {
							res.add(marker);
						} else {
							final AbstractCamelModelElement cmeWithMarker = markers.get(marker);
							if (camelModelElement.equals(cmeWithMarker)) {
								// Used for id renaming
								res.add(marker);
							}

						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return res;
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
	private void validateDetailProperties(AbstractCamelModelElement selectedEP, ValidationResult result) {
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
							AbstractCamelModelElement cme = selectedEP.getCamelContext().findNode((String) selectedEP.getParameter("ref"));
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
						AbstractCamelModelElement cme = selectedEP.getCamelContext().findNode(refId);
						if (cme == null && selectedEP.getCamelFile().getGlobalDefinitions().containsKey(refId) == false) {
							// the ref doesn't exist
							result.addWarning("The entered reference does not exist in your context! Please check the properties view for more details.");
						} else {
							// the ref exists
							if (cme == null || cme.getParameter("uri") == null || ((String) cme.getParameter("uri")).trim().length() < 1) {
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
					if (value == null || (value instanceof String && value.toString().trim().isEmpty())) {
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
	protected boolean checkAllUniqueIDs(AbstractCamelModelElement nodeUnderValidation, List<AbstractCamelModelElement> nodes, ArrayList<String> processedNodeIDs) {
		boolean noDoubledIDs = true;
		for (AbstractCamelModelElement node : nodes) {
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

	private IMarker createOrReuseMarker(IResource resource, AbstractCamelModelElement cme, final String message, final int severity, Set<IMarker> markersRelatedToElement) {
		IMarker res = null;
		try {
			Map<String, Object> attributesForPosition = managePosition(resource, cme);
			res = searchForExistingSimilarMarker(message, severity, markersRelatedToElement, res, attributesForPosition);
			if (res == null) {
				res = createMarker(resource, message, severity, attributesForPosition);
			}
			markers.put(res, cme);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		markersRelatedToElement.remove(res);
		return res;
	}

	/**
	 * @param resource
	 * @param message
	 * @param severity
	 * @param attributesForPosition
	 * @return
	 * @throws CoreException
	 */
	private IMarker createMarker(IResource resource, final String message, final int severity, Map<String, Object> attributesForPosition) throws CoreException {
		IMarker res;
		res = resource.createMarker(IFuseMarker.MARKER_TYPE);
		res.setAttribute(IMarker.SEVERITY, severity);
		res.setAttribute(IMarker.MESSAGE, message);
		res.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);

		for (Entry<String, Object> attributeForPosition : attributesForPosition.entrySet()) {
			res.setAttribute(attributeForPosition.getKey(), attributeForPosition.getValue());
		}
		// marker.setAttribute(IMarker.CHAR_START, 0);
		// marker.setAttribute(IMarker.CHAR_END, 10);
		return res;
	}

	/**
	 * @param message
	 * @param severity
	 * @param markersRelatedToElement
	 * @param res
	 * @param attributesForPosition
	 * @return
	 * @throws CoreException
	 */
	private IMarker searchForExistingSimilarMarker(final String message, final int severity, Set<IMarker> markersRelatedToElement, IMarker res,
			Map<String, Object> attributesForPosition) throws CoreException {
		for (IMarker existingMarker : markersRelatedToElement) {
			if (severity == existingMarker.getAttribute(IMarker.SEVERITY, -1) && message.equals(existingMarker.getAttribute(IMarker.MESSAGE))) {
				boolean hasSamePositionAttributes = true;
				for (Entry<String, Object> attributeForPosition : attributesForPosition.entrySet()) {
					if (!attributeForPosition.getValue().equals(existingMarker.getAttribute(attributeForPosition.getKey()))) {
						hasSamePositionAttributes = false;
						break;
					}
				}
				if (hasSamePositionAttributes) {
					res = existingMarker;
					break;
				}
			}
		}
		return res;
	}

	/**
	 * @param resource
	 * @param cme
	 * @param marker
	 * @throws CoreException
	 */
	private Map<String, Object> managePosition(IResource resource, AbstractCamelModelElement cme) throws CoreException {
		Map<String, Object> attributesForPosition = new HashMap<>();
		Integer lineNumber = -1;
		if (cme.getId() != null) {
			List<Integer> foundIds = findLineNumbers("id=\"" + cme.getId() + "\"", cme.getCamelFile().getDocumentAsXML());
			if (foundIds.size() == 1) {
				lineNumber = foundIds.get(0);
				attributesForPosition.put(IMarker.LINE_NUMBER, lineNumber);
			}
		}
		if (lineNumber == -1) {
			attributesForPosition.put(IMarker.LOCATION, "/" + getCamelPath(cme));
		} else {
			attributesForPosition.put(IFuseMarker.PATH, "/" + getCamelPath(cme));
		}
		attributesForPosition.put(IFuseMarker.CAMEL_ID, cme.getId());
		return attributesForPosition;
	}

	public List<Integer> findLineNumbers(String word, String text) {
		List<Integer> results = new ArrayList<Integer>();
		LineNumberReader rdr;
		rdr = new LineNumberReader(new StringReader(text));
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
		return results;
	}

	/**
	 * @param cme
	 * @return
	 */
	private String getCamelPath(AbstractCamelModelElement cme) {
		String res = cme.getDisplayText();
		final AbstractCamelModelElement parent = cme.getParent();
		if (parent != null && !(parent instanceof CamelFile)) {
			res = getCamelPath(parent) + "/" + res;
		}
		return res;
	}

	/**
	 * @param cme
	 */
	public void clearMarkers(AbstractCamelModelElement cme) {
		for (IMarker marker : getMarkersFor(cme)) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
