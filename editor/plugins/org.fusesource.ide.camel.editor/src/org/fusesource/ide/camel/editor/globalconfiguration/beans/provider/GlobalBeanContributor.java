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

package org.fusesource.ide.camel.editor.globalconfiguration.beans.provider;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.AddGlobalBeanWizard;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.EditGlobalBeanWizard;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigElementType;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.CamelUtils;

/**
 * @author bfitzpat
 */
public class GlobalBeanContributor implements ICustomGlobalConfigElementContribution {

	private AddGlobalBeanWizard wizard = null;

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#createGlobalElement(org.w3c.dom.Document)
	 */
	@Override
	public GlobalConfigurationTypeWizard createGlobalElement(CamelFile camelFile) {
		wizard = createAddWizard(camelFile);
		return wizard;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#modifyGlobalElement(org.w3c.dom.Document)
	 */
	@Override
	public GlobalConfigurationTypeWizard modifyGlobalElement(CamelFile camelFile) {
		return createEditWizard(camelFile);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#getElementDependencies()
	 */
	@Override
	public List<Dependency> getElementDependencies() {
		if (wizard != null) {
			final Component component = wizard.getComponent();
			if (component != null) {
				return component.getDependencies();
			}
		}
		return Collections.emptyList();
	}

	@Override
	public void onGlobalElementDeleted(AbstractCamelModelElement camelModelElement) {
		// possible actions if one of my nodes got deleted from the context
	}

	@Override
	public boolean canHandle(AbstractCamelModelElement camelModelElementToHandle) {
		return CamelUtils.getTranslatedNodeName(camelModelElementToHandle.getXmlNode()).equalsIgnoreCase(CamelFile.BEAN_NODE);
	}

	@Override
	public GlobalConfigElementType getGlobalConfigElementType() {
		return GlobalConfigElementType.GLOBAL_BEAN;
	}

	/**
	 * Creates edit wizard
	 * 
	 * @return Bean edit wizard
	 */
	private EditGlobalBeanWizard createEditWizard(CamelFile camelFile) {
		IProject project = camelFile.getResource().getProject();
		CamelModel camelModel = CamelModelFactory.getModelForProject(project);
		ComponentModel componentModel = camelModel.getComponentModel();
		EditGlobalBeanWizard editWizard = new EditGlobalBeanWizard(camelFile, componentModel);
		editWizard.init();
		return editWizard;
	}

	/**
	 * Creates edit wizard
	 * 
	 * @return Bean edit wizard
	 */
	private AddGlobalBeanWizard createAddWizard(CamelFile camelFile) {
		IProject project = camelFile.getResource().getProject();
		CamelModel camelModel = CamelModelFactory.getModelForProject(project);
		ComponentModel componentModel = camelModel.getComponentModel();
		AddGlobalBeanWizard addWizard = new AddGlobalBeanWizard(camelFile, componentModel);
		addWizard.init();
		return addWizard;
	}
}
