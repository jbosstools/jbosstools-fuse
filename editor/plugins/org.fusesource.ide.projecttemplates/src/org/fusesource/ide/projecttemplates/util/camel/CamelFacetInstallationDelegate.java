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
package org.fusesource.ide.projecttemplates.util.camel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;

/**
 * The camel facet as currently implemented requires either a utility facet
 * or a web facet.  Natures and other such should be added automatically by them. 
 * 
 * In the event of a utility facet being present (instead of web), 
 * we still need to make a content mapping folder. 
 * 
 */
public class CamelFacetInstallationDelegate implements IDelegate {
	
	@Override
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel) config;
		if (!model.isPropertySet(ICamelFacetDataModelProperties.CAMEL_PROJECT_METADATA)) {
			return;
		}
		NewFuseIntegrationProjectMetaData metadata = (NewFuseIntegrationProjectMetaData)model.getProperty(ICamelFacetDataModelProperties.CAMEL_PROJECT_METADATA);
		
		// store the camel version as project property
		project.setPersistentProperty(ICamelFacetDataModelProperties.QNAME_CAMEL_VERSION, metadata.getCamelVersion());
	}
}
