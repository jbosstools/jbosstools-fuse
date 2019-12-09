/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.camel.editor.component.wizard.ComponentManager;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages.GlobalBeanEditWizardPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author brianf
 *
 */
public class EditGlobalBeanWizard extends Wizard implements GlobalConfigurationTypeWizard {

	private DataBindingContext dbc;
	private Element inputNode;
	private ComponentManager componentManager;
	private final CamelFile camelFile;
	private Element updatedNode = null;

	public EditGlobalBeanWizard(final CamelFile camelFile, CamelModel camelModel) {
		super();
		this.camelFile = camelFile;
		this.dbc = new DataBindingContext();
		this.componentManager = new ComponentManager(camelModel);
		setWindowTitle(UIMessages.editGlobalBeanWizardWindowTitle);
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
		final GlobalBeanEditWizardPage globalBeanPage = new GlobalBeanEditWizardPage(dbc, UIMessages.editGlobalBeanWizardBeanEditPageTitle,
				UIMessages.editGlobalBeanWizardBeanEditPageMessage, camelFile);
		globalBeanPage.setElement(updatedNode);
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
		return this.inputNode;
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
		if (updatedNode == null) {
			updatedNode = (Element) this.inputNode.cloneNode(true);
			this.inputNode.getOwnerDocument().adoptNode(updatedNode);
		}
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
		Element parentNode = (Element) inputNode.getParentNode();
		if (parentNode != null) {
			parentNode.replaceChild(updatedNode, inputNode);
		}
		setGlobalConfigurationElementNode(updatedNode);
		return true;
	}

	@Override
	public boolean performCancel() {
		setGlobalConfigurationElementNode(inputNode);
		return true;
	}

	public void init() {
		setWindowTitle(UIMessages.editGlobalBeanWizardWindowTitle);
		setNeedsProgressMonitor(true);
		dbc = new DataBindingContext();
	}
}
