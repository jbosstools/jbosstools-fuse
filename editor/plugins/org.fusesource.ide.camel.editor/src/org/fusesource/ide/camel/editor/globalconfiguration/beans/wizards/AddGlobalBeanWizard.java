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
import org.eclipse.jface.wizard.Wizard;
import org.fusesource.ide.camel.editor.component.wizard.ComponentManager;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages.GlobalBeanWizardPage;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author bfitzpat
 *
 */
public class AddGlobalBeanWizard extends Wizard implements GlobalConfigurationTypeWizard {

	private DataBindingContext dbc;
	private Element newBeanNode;
	private GlobalBeanWizardPage globalBeanPage;
	private CamelFile camelFile;
	private ComponentManager componentManager;
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();

	public AddGlobalBeanWizard(CamelFile camelFile, ComponentModel componentModel) {
		super();
		this.camelFile = camelFile;
		this.dbc = new DataBindingContext();
		this.componentManager = new ComponentManager(componentModel);
		setWindowTitle("Add Bean");
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
		globalBeanPage = new GlobalBeanWizardPage(dbc, "Bean Definition",
				"Specify details for the new bean definition.", camelFile);
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
		return newBeanNode;
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
		this.newBeanNode = node;
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
		newBeanNode = beanConfigUtil.createBeanNode(camelFile, globalBeanPage.getId(), globalBeanPage.getClassname());
		if (!globalBeanPage.getArgumentsList().isEmpty()) {
			for (AbstractCamelModelElement argument : globalBeanPage.getArgumentsList()) {
				Element children = (Element) newBeanNode.getChildNodes();
				children.appendChild(argument.getXmlNode());
			}
		}
		if (!globalBeanPage.getPropertyList().isEmpty()) {
			for (AbstractCamelModelElement property : globalBeanPage.getPropertyList()) {
				Element children = (Element) newBeanNode.getChildNodes();
				children.appendChild(property.getXmlNode());
			}
		}
		setGlobalConfigurationElementNode(newBeanNode);
		return true;
	}

	public void init() {
		setWindowTitle("Add Bean");
		setNeedsProgressMonitor(true);
		dbc = new DataBindingContext();
	}

}
