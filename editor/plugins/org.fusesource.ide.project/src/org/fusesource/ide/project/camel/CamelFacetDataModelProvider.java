/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.camel;

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.project.facet.J2EEModuleFacetInstallDataModelProvider;

public class CamelFacetDataModelProvider extends J2EEModuleFacetInstallDataModelProvider 
	implements ICamelFacetDataModelProperties {

	private static final String JBOSS_CAMEL_PROJECT_FACET = ICamelFacetDataModelProperties.CAMEL_PROJECT_FACET;

	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(ICamelFacetDataModelProperties.CAMEL_SOURCE_FOLDER);
		names.add(ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER);
		names.add(ICamelFacetDataModelProperties.CREATE_BLUEPRINT_DESCRIPTOR);
		names.add(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return JBOSS_CAMEL_PROJECT_FACET;
		}
		else if(ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER.equals(propertyName)){
			return ICamelFacetDataModelProperties.DEFAULT_CAMEL_CONFIG_RESOURCE_FOLDER;
		}
		else if(ICamelFacetDataModelProperties.CAMEL_SOURCE_FOLDER.equals(propertyName)){
			return ICamelFacetDataModelProperties.DEFAULT_CAMEL_SOURCE_FOLDER;
		} else if( ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE.equals(propertyName)) {
			return false;
		}
		return super.getDefaultProperty(propertyName);
	}

	// Superclass will take over this method
//	protected int convertFacetVersionToJ2EEVersion(IProjectFacetVersion version) {
//		return J2EEVersionConstants.J2EE_1_4_ID;
//	}
	public IStatus validate(String propertyName) {
		return OK_STATUS;
	}
}
