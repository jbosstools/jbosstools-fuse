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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.camel.editor.component.wizard.SelectComponentWizardPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public class SelectEndpointWizard extends Wizard {

	private ComponentModel componentModel;
	private SelectComponentWizardPage page;
	private AbstractCamelModelElement parent;

	/**
	 * @param camelFile
	 * @param componentModel
	 */
	public SelectEndpointWizard(AbstractCamelModelElement parent, ComponentModel componentModel) {
		this.componentModel = componentModel;
		this.parent = parent;
		setWindowTitle(UIMessages.SelectEndpointWizard_windowTitle);
	}

	@Override
	public void addPages() {
		super.addPages();
		page = new SelectComponentWizardPage(new DataBindingContext(), componentModel, UIMessages.SelectEndpointWizard_pageSelectionComponentTitle,
				UIMessages.SelectEndpointWizard_pageSelectionComponentDescription, parent);
		addPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return true;
	}

	public Component getComponent() {
		return page.getComponentSelected();
	}

	public String getId() {
		return page.getId();
	}

}
