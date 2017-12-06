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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.core.ui.internal.UpdateMavenProjectJob;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.fusesource.ide.projecttemplates.maven.CamelProjectConfigurator;
import org.fusesource.ide.syndesis.extensions.ui.SyndesisExtensionProjectNature;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;

/**
 * @author lheinema
 */
public class SyndesisExtensionProjectConfigurator extends AbstractProjectConfigurator {

	public static final IProjectFacet syndesisExtensionFacet = ProjectFacetsManager.getProjectFacet("jst.syndesis.extension"); //$NON-NLS-1$
	
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

	/* (non-Javadoc)
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#configure(org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (checkSyndesisExtensionsMetaDataExist(request.getProject())) {
			if (!isSyndesisExtensionsFacetEnabled(request)) {
				// if we have a camel context but no facade set we do set it
				configureFacet(request.getMavenProject(), request.getProject(), monitor);
			}
			if (!isSyndesisExtensionsNatureEnabled(request.getProject())) {
				// enable the camel nature
				configureNature(request.getProject(), monitor);
			}
			// handle linked resources for WAR deployments
			if (isWARProject(request.getProject(), monitor)) {
				configureWARStructureMapping(request.getProject(), monitor);
			}
			
		}
	}

	private boolean isSyndesisExtensionsFacetEnabled(ProjectConfigurationRequest request) throws CoreException {
		IProject project = request.getProject();
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		if (fproj != null) {
			Set<IProjectFacetVersion> facets = fproj.getProjectFacets();
			Iterator<IProjectFacetVersion> itFacet = facets.iterator();
			while (itFacet.hasNext()) {
				IProjectFacetVersion f = itFacet.next();
				if (syndesisExtensionFacet.getId().equals(f.getProjectFacet().getId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isSyndesisExtensionsNatureEnabled(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] ids = projectDescription.getNatureIds();
		return Arrays.stream(ids).anyMatch(SyndesisExtensionProjectNature.NATURE_ID::equals);
	}
	
	private void configureFacet(MavenProject mavenProject, IProject project, IProgressMonitor monitor)
			throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.configuringFacets, 4);
		if (!checkSyndesisExtensionsMetaDataExist(project)) {
			subMonitor.setWorkRemaining(0);
			return;
		}
		IFacetedProject fproj = ProjectFacetsManager.create(project, true, subMonitor.split(1));

		if (fproj == null) {
			// Add the modulecore nature
			WtpUtils.addNatures(project);
			addNature(project, FacetedProjectNature.NATURE_ID, subMonitor.split(1));
			fproj = ProjectFacetsManager.create(project);
		}
		subMonitor.setWorkRemaining(1);

		if (fproj != null) {
			installDefaultFacets(project, mavenProject, fproj, subMonitor.split(1));
		}
		subMonitor.setWorkRemaining(0);
	}
	
	private boolean isWARProject(IProject project, IProgressMonitor monitor) {
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
		return CamelProjectConfigurator.WAR_PACKAGE.equalsIgnoreCase(m2prj.getPackaging());
	}
	
	private boolean checkSyndesisExtensionsMetaDataExist(IProject project) {
		// check for file: 
		return project != null && project.findMember(new Path("src").append("main").append("resources").append("META-INF").append("syndesis").append("extension-definition.json")) != null;
	}

	private void configureWARStructureMapping(IProject project, IProgressMonitor monitor) throws CoreException {
		final IVirtualComponent c = ComponentCore.createComponent(project, false);
		c.create(IVirtualResource.NONE, monitor);
		final IVirtualFolder webroot = c.getRootFolder();
		final IVirtualFolder classesFolder = webroot.getFolder("/WEB-INF/classes"); //$NON-NLS-1$
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
		updateMappings(m2prj.getCompileSourceLocations(), project, classesFolder, monitor);
		updateMappings(m2prj.getTestCompileSourceLocations(), project, classesFolder, monitor);
	}
	
	/**
	 * this methods maps a given set of local paths to a path on runtime / in
	 * the WAR
	 * 
	 * @param paths
	 * @param project
	 * @param vFolder
	 * @param monitor
	 * @throws CoreException
	 */
	private void updateMappings(IPath[] paths, IProject project, IVirtualFolder vFolder, IProgressMonitor monitor)
			throws CoreException {
		for (IPath sourceLoc : paths) {
			IFolder srcFolder = project.getFolder(sourceLoc);
			IPath absSourcePath = srcFolder.getProjectRelativePath().makeAbsolute();
			IVirtualResource[] mappings = ComponentCore.createResources(srcFolder);
			boolean found = false;
			for (IVirtualResource mapping : mappings) {
				if (mapping.getProjectRelativePath().equals(absSourcePath)) {
					mapping.createLink(absSourcePath, IVirtualResource.NONE, monitor);
					found = true;
					break;
				}
			}
			if (!found) {
				// create link for source folder only when it is not mapped
				vFolder.createLink(absSourcePath, IVirtualResource.NONE, monitor);
			} else {
				removeRedundantMappingToRootRuntimePath(monitor, absSourcePath, mappings);
			}
		}
	}

	private void removeRedundantMappingToRootRuntimePath(IProgressMonitor monitor, IPath absSourcePath,
			IVirtualResource[] mappings) {
		Arrays.stream(mappings).filter(mapping -> mapping.getProjectRelativePath().equals(absSourcePath))
				.filter(mapping -> "/".equals(mapping.getRuntimePath().toPortableString()))
				.forEach(mapping -> {
					try {
						mapping.delete(IVirtualResource.IGNORE_UNDERLYING_RESOURCE, monitor);
					} catch (CoreException e) {
						SyndesisExtensionsUIActivator.pluginLog().logError(e);
					}
				});
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
		installSyndesisExtensionFacet(fproj, fpwc, subMonitor.split(1));
		fpwc.commitChanges(subMonitor.split(1));
		configureNature(project, subMonitor.split(1));
		updateMavenProject(project);
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
	
	private void configureNature(IProject project, IProgressMonitor monitor)
			throws CoreException {
		addNature(project, SyndesisExtensionProjectNature.NATURE_ID, monitor);
	}
	
	private void installSyndesisExtensionFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc, IProgressMonitor monitor) throws CoreException {
		installFacet(fproj, fpwc, syndesisExtensionFacet, syndesisExtensionFacet.getLatestVersion());
		// we need to switch dependency versions
		SyndesisFacetVersionChangeDelegate del = new SyndesisFacetVersionChangeDelegate();
		del.execute(fproj.getProject(), syndesisExtensionFacet.getLatestVersion(), null, monitor);
	}

}
