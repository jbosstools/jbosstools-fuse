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

import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.core.ui.internal.UpdateMavenProjectJob;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.maven.CamelProjectConfigurator;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;

/**
 * @author lheinema
 */
public class SyndesisExtensionProjectConfigurator extends AbstractProjectConfigurator {
	
	private static final String SYNDESIS_PLUGIN_GROUPID = "io.syndesis.extension";
    private static final String SYNDESIS_PLUGIN_ARTIFACTID = "extension-maven-plugin";
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#mavenProjectChanged(org.eclipse.m2e.core.project.MavenProjectChangedEvent, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent event, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = event.getMavenProject();
		if ((event.getFlags() == MavenProjectChangedEvent.FLAG_DEPENDENCIES || event.getKind() == MavenProjectChangedEvent.KIND_ADDED) && facade != null) {
			IProject project = facade.getProject();
			IFacetedProject fproj = ProjectFacetsManager.create(project);
			if (fproj != null && isValidSyndesisProject(project)) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 20);
				installDefaultFacets(project, fproj, subMonitor.split(10));
				// we add the camel nature because this enables the Camel Contexts virtual folder in the project
				addNature(project, RiderProjectNature.NATURE_ID, subMonitor.split(10));
			}
		}
		super.mavenProjectChanged(event, monitor);
	}
	
	private boolean isValidSyndesisProject(IProject project) {
		Model model = new CamelMavenUtils().getMavenModel(project);
		if (model != null) {
			Build build = model.getBuild();
			if (build != null) {
				if (isSyndesisPluginDefined(build.getPlugins())) {
					return true;
				} else {
					PluginManagement pluginManagement = build.getPluginManagement();
					if (pluginManagement != null) {
						return isSyndesisPluginDefined(pluginManagement.getPlugins());
					}
				}
			}
		}
		return false;
	}

	private boolean isSyndesisPluginDefined(List<Plugin> plugins) {
		if (plugins != null) {
			for (Plugin p : plugins) {
				if (SYNDESIS_PLUGIN_GROUPID.equalsIgnoreCase(p.getGroupId()) && 
					SYNDESIS_PLUGIN_ARTIFACTID.equalsIgnoreCase(p.getArtifactId()) ) {
					return true;
				}
			}
		}
		return false;
	}

	private void installDefaultFacets(IProject project, IFacetedProject fproj, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.installingRequiredFacetsForSyndesisExtensionProject, 3);

		IFacetedProjectWorkingCopy fpwc = fproj.createWorkingCopy();

		// adjust facets we install based on the packaging type we find
		installFacet(fproj, fpwc, CamelProjectConfigurator.javaFacet, CamelProjectConfigurator.javaFacet.getLatestVersion());
		installFacet(fproj, fpwc, CamelProjectConfigurator.m2eFacet, CamelProjectConfigurator.m2eFacet.getLatestVersion());
		installFacet(fproj, fpwc, CamelProjectConfigurator.utilFacet, CamelProjectConfigurator.utilFacet.getLatestVersion());
		fpwc.commitChanges(subMonitor.split(1));
		updateMavenProject(project);
		subMonitor.setWorkRemaining(0);
	}

	private void updateMavenProject(final IProject project) {
		// MANIFEST.MF is probably not built yet
		if (project != null) {
			// update the maven project so we start in a deployable state
			// with a valid MANIFEST.MF built as part of the build process.
			Job updateJob = new UpdateMavenProjectJob(new IProject[] { project });
			updateJob.schedule();
		}
	}
	
	private void installFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc, IProjectFacet facet, IProjectFacetVersion facetVersion) {
		if (facet != null && !fproj.hasProjectFacet(facet)) {
			fpwc.addProjectFacet(facetVersion);
		} else {
			IProjectFacetVersion f = fproj.getProjectFacetVersion(facet);
			if (!f.getVersionString().equals(facetVersion.getVersionString())) {
				// version change
				fpwc.changeProjectFacetVersion(facetVersion);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#configure(org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		// we add the camel nature because this enables the Camel Contexts virtual folder in the project
		SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
		addNature(request.getProject(), RiderProjectNature.NATURE_ID, subMonitor.split(10));
	}
}
