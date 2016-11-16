/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.genericEndpoint;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.features.create.ext.AbstractComponentBasedCreateFigurefeature;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

/**
 * @author Aurelien Pupier
 *
 */
public class GenericEndpointFigureFeature extends AbstractComponentBasedCreateFigurefeature {

	public GenericEndpointFigureFeature(IFeatureProvider fp) {
		super(fp, UIMessages.GenericEndpointFigureFeature_paletteName, UIMessages.GenericEndpointFigureFeature_paletteDescription);
	}

	@Override
	protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
		CamelFile camelFile = parent.getCamelFile();
		final String camelVersion = CamelModelFactory.getCamelVersion(camelFile.getResource().getProject());
		final CamelModel camelModel = CamelModelFactory.getModelForVersion(camelVersion);
		final ComponentModel componentModel = camelModel.getComponentModel();
		SelectEndpointWizard wizard = new SelectEndpointWizard(parent, componentModel);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		if (dialog.open() == IStatus.OK) {
			setComponent(wizard.getComponent());
			AbstractCamelModelElement newCamelElement = super.createNode(parent, createDOMNode);
			newCamelElement.setId(wizard.getId());
			return newCamelElement;
		} else {
			return null;
		}
	}

}
