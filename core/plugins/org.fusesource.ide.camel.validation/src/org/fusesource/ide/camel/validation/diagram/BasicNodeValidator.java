/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.validation.diagram;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.CamelValidationActivator;
import org.fusesource.ide.camel.validation.ValidationResult;
import org.fusesource.ide.camel.validation.ValidationSupport;
import org.fusesource.ide.camel.validation.model.EIPMandatoryChildValidator;
import org.fusesource.ide.camel.validation.model.NumberValidator;
import org.fusesource.ide.camel.validation.model.RefOrDataFormatUnicityChoiceValidator;
import org.fusesource.ide.camel.validation.model.RequiredPropertyValidator;
import org.fusesource.ide.camel.validation.model.TextParameterValidator;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;


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

		if (camelModelElement != null) {
			if(camelModelElement.getRouteContainer() != null) {
				// we check if all mandatory fields are filled
				validateDetailProperties(camelModelElement, result);
				final Component component = PropertiesUtils.getComponentFor(camelModelElement);
				for (Parameter prop : new ArrayList<>(PropertiesUtils.getComponentPropertiesFor(camelModelElement))) {
					Object value = PropertiesUtils.getPropertyFromUri(camelModelElement, prop, component);
					checkFor(result, value, new RequiredPropertyValidator(prop));
					checkFor(result, value, new NumberValidator(prop));
				}
				checkFor(result, camelModelElement, new EIPMandatoryChildValidator());
			}

      Set<IMarker> markersRelatedToElement = getMarkersFor(camelModelElement);
			
			createOrReuseMarkers(camelModelElement, result, markersRelatedToElement);

			for (IMarker markerToDelete : markersRelatedToElement) {
				try {
					markerToDelete.delete();
				} catch (CoreException e) {
					CamelValidationActivator.getDefault().getLog().log(StatusFactory.errorStatus(CamelValidationActivator.PLUGIN_ID, "Error while clearing validation marker.", e)); //$NON-NLS-0$
				}
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
		try {
			final CamelFile camelFile = camelModelElement.getCamelFile();
			if (camelFile != null) {
				final IResource resource = camelFile.getResource();
				if (resource != null && resource.exists()) {
					return getMarkersFor(camelModelElement, resource);
				}
			}
		} catch (CoreException e) {
			CamelValidationActivator.getDefault().getLog().log(StatusFactory.errorStatus(CamelValidationActivator.PLUGIN_ID, "Error while retrieving validation markers.", e)); //$NON-NLS-0$
		}
		return new HashSet<>();
	}

	protected Set<IMarker> getMarkersFor(AbstractCamelModelElement camelModelElement, final IResource resource) throws CoreException {
		Set<IMarker> res = new HashSet<>();
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
		return res;
	}

	/**
	 * @param result
	 * @param prop
	 * @param value
	 * @param validator
	 */
	private void checkFor(ValidationResult result, Object value, final IValidator validator) {
		IStatus status = validator.validate(value);
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			result.addError(status.getMessage());
			break;
		case IStatus.WARNING:
			result.addWarning(status.getMessage());
			break;
		case IStatus.INFO:
			result.addInfo(status.getMessage());
			break;
		default:
			break;
		}
	}

	/**
	 * @param selectedEP
	 * @param result
	 */
	private void validateDetailProperties(AbstractCamelModelElement selectedEP, ValidationResult result) {
		for (Parameter prop : PropertiesUtils.getPropertiesFor(selectedEP)) {
			String property = prop.getName();
			if ((AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(prop.getKind()) && "array".equalsIgnoreCase(prop.getType()))
					|| "org.apache.camel.model.OtherwiseDefinition".equals(prop.getJavaType()))
				continue;

			Object value = selectedEP.getParameter(property);

			if (PropertiesUtils.isRequired(prop) || "id".equals(prop.getName())) {
				checkFor(result, value, new TextParameterValidator(selectedEP, prop));
			}
			checkFor(result, value, new RefOrDataFormatUnicityChoiceValidator(selectedEP, prop));

		}
	}

	private IMarker createOrReuseMarker(IResource resource, AbstractCamelModelElement cme, final String message, final int severity, Set<IMarker> markersRelatedToElement) {
		IMarker res = null;
		try {
			Map<String, Object> attributesForPosition = managePosition(cme);
			res = searchForExistingSimilarMarker(message, severity, markersRelatedToElement, res, attributesForPosition);
			if (res == null) {
				res = createMarker(resource, message, severity, attributesForPosition);
			}
			markers.put(res, cme);
		} catch (CoreException e) {
			CamelValidationActivator.getDefault().getLog().log(StatusFactory.errorStatus(CamelValidationActivator.PLUGIN_ID, "Error creating validation marker.", e)); //$NON-NLS-0$
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
		IMarker marker = resource.createMarker(IFuseMarker.MARKER_TYPE);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);

		for (Entry<String, Object> attributeForPosition : attributesForPosition.entrySet()) {
			marker.setAttribute(attributeForPosition.getKey(), attributeForPosition.getValue());
		}
		// marker.setAttribute(IMarker.CHAR_START, 0);
		// marker.setAttribute(IMarker.CHAR_END, 10);
		return marker;
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
	private Map<String, Object> managePosition(AbstractCamelModelElement cme) {
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
		List<Integer> results = new ArrayList<>();
		try(LineNumberReader rdr = new LineNumberReader(new StringReader(text))) {
			String line;
			while ((line = rdr.readLine()) != null) {
				if (line.indexOf(word) >= 0) {
					results.add(rdr.getLineNumber());
				}
			}
		} catch (IOException e) {
			CamelValidationActivator.getDefault().getLog().log(StatusFactory.errorStatus(CamelValidationActivator.PLUGIN_ID, "Error while searching for line of validation marker.", e)); //$NON-NLS-0$
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
				CamelValidationActivator.getDefault().getLog().log(StatusFactory.errorStatus(CamelValidationActivator.PLUGIN_ID, "Error while deleting validation marker.", e)); //$NON-NLS-0$
			}
		}
	}

}
