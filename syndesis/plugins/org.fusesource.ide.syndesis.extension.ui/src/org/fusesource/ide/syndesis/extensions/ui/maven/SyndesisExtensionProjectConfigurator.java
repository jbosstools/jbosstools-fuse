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

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.fusesource.ide.projecttemplates.maven.CamelProjectConfigurator;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;

/**
 * @author lheinema
 */
public class SyndesisExtensionProjectConfigurator extends AbstractProjectConfigurator {
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#mavenProjectChanged(org.eclipse.m2e.core.project.MavenProjectChangedEvent, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent event, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = event.getMavenProject();
		if (event.getFlags() == MavenProjectChangedEvent.FLAG_DEPENDENCIES && facade != null) {
			IProject project = facade.getProject();
			MavenProject mavenProject = facade.getMavenProject(monitor);
			IFacetedProject fproj = ProjectFacetsManager.create(project);
			if (fproj != null && checkSyndesisExtensionsMetaDataExist(project)) {
				installDefaultFacets(project, mavenProject, fproj, monitor);
			}
		}
		super.mavenProjectChanged(event, monitor);
	}
	
	private boolean checkSyndesisExtensionsMetaDataExist(IProject project) {
		// check for file: 
		return project != null && project.findMember(new Path("src").append("main").append("resources").append("META-INF").append("syndesis").append("extension-definition.json")) != null;
	}

	private void installDefaultFacets(IProject project, MavenProject mavenProject, IFacetedProject fproj, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.installingRequiredFacetsForSyndesisExtensionProject, 4);
		subMonitor.setWorkRemaining(3);

		IFacetedProjectWorkingCopy fpwc = fproj.createWorkingCopy();

		// adjust facets we install based on the packaging type we find
		installFacet(fproj, fpwc, CamelProjectConfigurator.javaFacet, CamelProjectConfigurator.javaFacet.getLatestVersion());
		installFacet(fproj, fpwc, CamelProjectConfigurator.m2eFacet, CamelProjectConfigurator.m2eFacet.getLatestVersion());
		if (mavenProject.getPackaging() != null) {
			String packaging = mavenProject.getPackaging();
			if (CamelProjectConfigurator.WAR_PACKAGE.equalsIgnoreCase(packaging)) {
				installFacet(fproj, fpwc, CamelProjectConfigurator.webFacet, CamelProjectConfigurator.javaFacet.getLatestVersion());
			} else if (CamelProjectConfigurator.BUNDLE_PACKAGE.equalsIgnoreCase(packaging) || CamelProjectConfigurator.JAR_PACKAGE.equalsIgnoreCase(packaging)) {
				installFacet(fproj, fpwc, CamelProjectConfigurator.utilFacet, CamelProjectConfigurator.utilFacet.getLatestVersion());
			}
		}
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
		// nothing to do		
	}
}
