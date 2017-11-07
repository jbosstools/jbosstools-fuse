/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.syndesis.extensions.ui.maven;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.project.facet.J2EEModuleFacetInstallDataModelProvider;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;

/**
 * @author lheinema
 *
 */
public class SyndesisFacetDataModelProvider extends J2EEModuleFacetInstallDataModelProvider 
	implements ICamelFacetDataModelProperties {
	@Override
	public Set<Object> getPropertyNames() {
		Set<Object> names = super.getPropertyNames();
		names.add(ISyndesisExtensionFacetDataModelProperties.SYNDESIS_EXTENSION_CONTENT_FOLDER);
		names.add(ISyndesisExtensionFacetDataModelProperties.SYNDESIS_EXTENSION_PROJECT_VERSION);
		names.add(ISyndesisExtensionFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE);
		names.add(ISyndesisExtensionFacetDataModelProperties.SYNDESIS_EXTENSION_PROJECT_METADATA);
		return names;
	}
	
	@Override
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return ISyndesisExtensionFacetDataModelProperties.FACET_JST_SYNDESIS_EXTENSION;
		}
		else if(ISyndesisExtensionFacetDataModelProperties.SYNDESIS_EXTENSION_CONTENT_FOLDER.equals(propertyName)){
			return ISyndesisExtensionFacetDataModelProperties.DEFAULT_CAMEL_CONFIG_RESOURCE_FOLDER;
		} else if( ISyndesisExtensionFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE.equals(propertyName)) {
			return false;
		}
		return super.getDefaultProperty(propertyName);
	}
	
	@Override
	public IStatus validate(String propertyName) {
		return OK_STATUS;
	}
}
