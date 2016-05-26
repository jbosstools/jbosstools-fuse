/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.adopters.configurators;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetDataModelProvider;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;

/**
 * this configurator does nothing. it just provides additional helper 
 * methods to retrieve a facet data model and to install a facet on the project
 * 
 * @author lhein
 */
public class DefaultTemplateConfigurator implements TemplateConfiguratorSupport {

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport#configure(org.eclipse.core.resources.IProject, org.fusesource.ide.projecttemplates.util.NewProjectMetaData, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean configure(IProject project, NewProjectMetaData metadata, IProgressMonitor monitor) {
		IProjectFacetVersion javaFacet = ProjectFacetsManager.getProjectFacet("jst.java").getDefaultVersion();
		try {
			// add java facet
			installFacet(project, "jst.java", javaFacet.getVersionString(), null, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			// add m2 facet
			installFacet(project, "jboss.m2", null, null, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			// now add jst utility
			installFacet(project, "jst.utility", null, null, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			project.getFile(".classpath").delete(true, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}

	/**
	 * creates the datamodel used for the facet installation delegate
	 * 
	 * @param projectMetaData	the projects metadata
	 * @return	a facet configuration
	 */
	protected IDataModel getCamelFacetDataModel(NewProjectMetaData projectMetaData) {
		CamelFacetDataModelProvider dmProv = new CamelFacetDataModelProvider();
		dmProv.create();
		IDataModel dm = dmProv.getDataModel();
		dm.setStringProperty(ICamelFacetDataModelProperties.CAMEL_CONTENT_FOLDER, ICamelFacetDataModelProperties.DEFAULT_CAMEL_CONFIG_RESOURCE_FOLDER);
		dm.setStringProperty(ICamelFacetDataModelProperties.CAMEL_DSL, projectMetaData.getDslType().toString());
		dm.setStringProperty(ICamelFacetDataModelProperties.CAMEL_PROJECT_VERSION, projectMetaData.getCamelVersion());
		dm.setBooleanProperty(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE, true);
		dm.setProperty(ICamelFacetDataModelProperties.CAMEL_PROJECT_METADATA, projectMetaData);
		return dm;
	}
	
	/**
	 * installs the given facet with the given version and config in the project
	 * 
	 * @param project		the project to install the facet into
	 * @param facetName		the name / id of the facet
	 * @param facetVersion	the facet version to use or null to use default
	 * @param config		the datamodel or null if not needed
	 * @param monitor		the progress monitor to use
	 * @throws CoreException on errors
	 */
	protected void installFacet(IProject project, String facetName, String facetVersion, IDataModel config, IProgressMonitor monitor) throws CoreException {
		IFacetedProject fp = ProjectFacetsManager.create(project, true, monitor);
		if (facetVersion != null) {
			fp.installProjectFacet(ProjectFacetsManager.getProjectFacet(facetName).getVersion(facetVersion), config, monitor);
		} else {
			fp.installProjectFacet(ProjectFacetsManager.getProjectFacet(facetName).getDefaultVersion(), config, monitor);
		}
	}
}
