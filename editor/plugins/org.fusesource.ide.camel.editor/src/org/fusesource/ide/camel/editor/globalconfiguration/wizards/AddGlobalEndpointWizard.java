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
package org.fusesource.ide.camel.editor.globalconfiguration.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.camel.editor.globalconfiguration.wizards.pages.GlobalEndpointWizardPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author Aurelien Pupier
 *
 */
public class AddGlobalEndpointWizard extends Wizard implements GlobalConfigurationTypeWizard {

	private ComponentModel componentModel;
	private DataBindingContext dbc;
	private Element globalConfigurationNode;
	private GlobalEndpointWizardPage globalEndpointPage;
	private CamelFile camelFile;

	public AddGlobalEndpointWizard(CamelFile camelFile, ComponentModel componentModel) {
		super();
		this.camelFile = camelFile;
		this.componentModel = componentModel;
		this.dbc = new DataBindingContext();
		setWindowTitle(UIMessages.AddGlobalEndpointWizard_windowTitle);
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		globalEndpointPage = new GlobalEndpointWizardPage(dbc, componentModel);
		addPage(globalEndpointPage);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.editor.provider.ext.
	 * GlobalConfigurationTypeWizard#getGlobalConfigrationElementNode()
	 */
	@Override
	public Element getGlobalConfigurationElementNode() {
		return globalConfigurationNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.editor.provider.ext.
	 * GlobalConfigurationTypeWizard#setGlobalConfigrationElementNode(org.w3c.
	 * dom.Element)
	 */
	@Override
	public void setGlobalConfigurationElementNode(Element node) {
		this.globalConfigurationNode = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Component component = globalEndpointPage.getComponentSelected();
		globalConfigurationNode = camelFile.createElement("endpoint", camelFile.getCamelContext().getXmlNode().getPrefix()); //$NON-NLS-1$
		globalConfigurationNode.setAttribute("uri", component.getSyntax()); //$NON-NLS-1$
		globalConfigurationNode.setAttribute("id", globalEndpointPage.getId()); //$NON-NLS-1$
		globalConfigurationNode.setAttribute("description", globalEndpointPage.getDescriptionCreated()); //$NON-NLS-1$
		return true;
	}

}
