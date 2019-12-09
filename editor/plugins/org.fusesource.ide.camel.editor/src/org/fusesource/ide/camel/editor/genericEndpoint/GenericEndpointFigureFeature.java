/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
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
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

/**
 * @author Aurelien Pupier
 *
 */
public class GenericEndpointFigureFeature extends AbstractComponentBasedCreateFigurefeature {

	public GenericEndpointFigureFeature(IFeatureProvider fp) {
		super(fp, UIMessages.genericEndpointFigureFeaturePaletteName, UIMessages.genericEndpointFigureFeaturePaletteDescription);
	}

	@Override
	protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
		CamelFile camelFile = parent.getCamelFile();
		final CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(camelFile.getResource().getProject());
		SelectEndpointWizard wizard = new SelectEndpointWizard(parent, camelModel);
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
