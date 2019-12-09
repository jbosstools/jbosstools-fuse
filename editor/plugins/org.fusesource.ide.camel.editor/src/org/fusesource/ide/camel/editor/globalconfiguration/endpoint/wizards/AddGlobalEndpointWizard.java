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
package org.fusesource.ide.camel.editor.globalconfiguration.endpoint.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.camel.editor.component.wizard.SelectComponentWizardPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author Aurelien Pupier
 *
 */
public class AddGlobalEndpointWizard extends Wizard implements GlobalConfigurationTypeWizard {

	private CamelModel model;
	private DataBindingContext dbc;
	private Element globalConfigurationNode;
	private SelectComponentWizardPage globalEndpointPage;
	private CamelFile camelFile;
	private Component component;

	public AddGlobalEndpointWizard(CamelFile camelFile, CamelModel model) {
		super();
		this.camelFile = camelFile;
		this.model = model;
		this.dbc = new DataBindingContext();
		setWindowTitle(UIMessages.addGlobalEndpointWizardWindowTitle);
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
		globalEndpointPage = new SelectComponentWizardPage(dbc, model, UIMessages.selectComponentWizardPagePageName,
				UIMessages.globalEndpointWizardPageGlobalEndpointTypeSelectionWizardpageDescription, camelFile);
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

	/**
	 * @return the component
	 */
	public Component getComponent() {
		return component;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		component = globalEndpointPage.getComponentSelected();
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		globalConfigurationNode = camelFile.createElement("endpoint", prefixNS); //$NON-NLS-1$
		globalConfigurationNode.setAttribute("uri", component.getSyntax()); //$NON-NLS-1$
		globalConfigurationNode.setAttribute("id", globalEndpointPage.getId()); //$NON-NLS-1$
		return true;
	}

}
