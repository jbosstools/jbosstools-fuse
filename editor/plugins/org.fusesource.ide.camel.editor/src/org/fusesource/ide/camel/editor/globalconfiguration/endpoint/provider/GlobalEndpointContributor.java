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

package org.fusesource.ide.camel.editor.globalconfiguration.endpoint.provider;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.editor.globalconfiguration.endpoint.wizards.AddGlobalEndpointWizard;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigElementType;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.CamelUtils;

/**
 * @author lhein
 */
public class GlobalEndpointContributor implements ICustomGlobalConfigElementContribution {

	private static final String ENDPOINT = "endpoint";
	private AddGlobalEndpointWizard wizard = null;

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#createGlobalElement(org.w3c.dom.Document)
	 */
	@Override
	public GlobalConfigurationTypeWizard createGlobalElement(CamelFile camelFile) {
		IProject project = camelFile.getResource().getProject();
		final CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project);
		wizard = new AddGlobalEndpointWizard(camelFile, camelModel);
		return wizard;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#modifyGlobalElement(org.w3c.dom.Document)
	 */
	@Override
	public GlobalConfigurationTypeWizard modifyGlobalElement(CamelFile camelFile) {
		// It is redirected to Properties view
		return null;
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
		return CamelUtils.getTagNameWithoutPrefix(camelModelElementToHandle.getXmlNode()).equalsIgnoreCase(ENDPOINT);
	}

	@Override
	public GlobalConfigElementType getGlobalConfigElementType() {
		return GlobalConfigElementType.CONTEXT_ENDPOINT;
	}
}
