/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.camel.editor.component.wizard.ComponentManager;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages.GlobalBeanEditWizardPage;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author bfitzpat
 *
 */
public class EditGlobalBeanWizard extends Wizard implements GlobalConfigurationTypeWizard {

	private DataBindingContext dbc;
	private Element inputNode;
	private CamelFile camelFile;
	private ComponentManager componentManager;

	public EditGlobalBeanWizard(CamelFile camelFile, ComponentModel componentModel) {
		super();
		this.camelFile = camelFile;
		this.dbc = new DataBindingContext();
		this.componentManager = new ComponentManager(componentModel);
		setWindowTitle("Edit Bean");
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
		final GlobalBeanEditWizardPage globalBeanPage = new GlobalBeanEditWizardPage(dbc, "Bean Definition",
				"Edit details for selected bean definition.", camelFile);
		globalBeanPage.setElement(inputNode);
		addPage(globalBeanPage);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.editor.provider.ext.
	 * GlobalConfigurationTypeWizard#getGlobalConfigrationElementNode()
	 */
	@Override
	public Element getGlobalConfigurationElementNode() {
		return inputNode;
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
		this.inputNode = node;
	}

	/**
	 * @return the component
	 */
	public Component getComponent() {
		Object component = componentManager.getComponentForTag(CamelBean.BEAN_NODE);
		if (component != null && component instanceof Component) {
			return (Component) component;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		setGlobalConfigurationElementNode(inputNode);
		return true;
	}

	/*
	 * Though this seems nonsensical at this point, it somehow resets the wizard to the point
	 * where the updated XML element is updated correctly in the model. 
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Edit Bean");
		setNeedsProgressMonitor(true);
		dbc = new DataBindingContext();
	}

}
