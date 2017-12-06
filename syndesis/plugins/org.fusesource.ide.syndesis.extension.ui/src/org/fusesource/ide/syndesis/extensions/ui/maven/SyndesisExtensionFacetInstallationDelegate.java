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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;

/**
 * @author lheinema
 *
 */
public class SyndesisExtensionFacetInstallationDelegate implements IDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
			throws CoreException {
		IDataModel model = (IDataModel) config;
		if (!model.isPropertySet(ISyndesisExtensionFacetDataModelProperties.SYNDESIS_EXTENSION_PROJECT_METADATA)) {
			return;
		}
		SyndesisExtension metadata = (SyndesisExtension)model.getProperty(ISyndesisExtensionFacetDataModelProperties.SYNDESIS_EXTENSION_PROJECT_METADATA);
		
		// store the camel version as project property
		project.setPersistentProperty(ISyndesisExtensionFacetDataModelProperties.QNAME_CAMEL_VERSION, metadata.getCamelVersion());
		project.setPersistentProperty(ISyndesisExtensionFacetDataModelProperties.QNAME_SYNDESIS_EXTENSION_VERSION, metadata.getSyndesisVersion());
		project.setPersistentProperty(ISyndesisExtensionFacetDataModelProperties.QNAME_SPRING_BOOT_VERSION, metadata.getSpringBootVersion());
	}
}
