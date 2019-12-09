/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.validation.xml;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.validation.CamelValidationActivator;
import org.fusesource.ide.camel.validation.diagram.BasicNodeValidator;
import org.fusesource.ide.camel.validation.diagram.IFuseMarker;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;

public class XMLCamelRoutesValidator extends AbstractValidator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.validation.AbstractValidator#validate(org.eclipse.wst.
	 * validation.ValidationEvent, org.eclipse.wst.validation.ValidationState,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ValidationResult validate(ValidationEvent event, ValidationState state, IProgressMonitor monitor) {
		IResource resource = event.getResource();
		try {
			resource.deleteMarkers(IFuseMarker.MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			CamelValidationActivator.getDefault().getLog().log(StatusFactory.errorStatus(CamelValidationActivator.PLUGIN_ID, "Error deleting Fuse validation markers.", e)); //$NON-NLS-0$
		}

		ValidationResult validationResult = super.validate(event, state, monitor);
		if (validationResult == null) {
			validationResult = new ValidationResult();
		}
		CamelFile camelFile = loadCamelFile(monitor, resource);
		if (camelFile != null) {
			checkCamelFile(camelFile, validationResult, resource);
		}
		return validationResult;
	}

	/**
	 * /!\ Public for test purpose
	 * 
	 * @param monitor
	 * @param resource
	 * @return
	 */
	public CamelFile loadCamelFile(IProgressMonitor monitor, IResource resource) {
		return new CamelIOHandler().loadCamelModel(resource, monitor);
	}

	/**
	 * @param camelFile
	 * @param validationResult
	 * @param resource
	 */
	private void checkCamelFile(CamelFile camelFile, ValidationResult validationResult, IResource resource) {
		for (AbstractCamelModelElement cme : camelFile.getChildElements()) {
			checkCamelModelElement(cme, validationResult, resource);
		}
	}

	/**
	 * @param cme
	 * @param validationResult
	 * @param resource
	 * @param locator
	 */
	private void checkCamelModelElement(AbstractCamelModelElement cme, ValidationResult validationResult, IResource resource) {
		org.fusesource.ide.camel.validation.ValidationResult result = new BasicNodeValidator().validate(cme);
		validationResult.incrementError(result.getErrorCount());
		validationResult.incrementWarning(result.getWarningCount());
		validationResult.incrementInfo(result.getInformationCount());
		for (AbstractCamelModelElement cmeChild : cme.getChildElements()) {
			checkCamelModelElement(cmeChild, validationResult, resource);
		}
		if (cme instanceof CamelContextElement) {
			for (AbstractCamelModelElement globalEndpoint : ((CamelContextElement) cme).getEndpointDefinitions().values()) {
				checkCamelModelElement(globalEndpoint, validationResult, resource);
			}
			for (AbstractCamelModelElement globalEndpoint : ((CamelContextElement) cme).getDataformats().values()) {
				checkCamelModelElement(globalEndpoint, validationResult, resource);
			}
		}
	}

}
