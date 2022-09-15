/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.maven;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetUtils;
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
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.camel.model.service.core.util.JavaCamelFilesFinder;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetDataModelProvider;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetVersionChangeDelegate;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;

public class CamelProjectConfigurator extends AbstractProjectConfigurator {

	private static final String ARTFIFACT_ID_CAMEL_PREFIX = "camel-"; //$NON-NLS-1$
	private static final String GROUP_ID_ORG_APACHE_CAMEL = "org.apache.camel"; //$NON-NLS-1$
	public static final String WAR_PACKAGE = "WAR"; //$NON-NLS-1$
	public static final String BUNDLE_PACKAGE = "BUNDLE"; //$NON-NLS-1$
	public static final String JAR_PACKAGE = "JAR"; //$NON-NLS-1$
	
	public static final IProjectFacet camelFacet = ProjectFacetsManager.getProjectFacet("jst.camel"); //$NON-NLS-1$
	public static final IProjectFacet javaFacet = ProjectFacetsManager.getProjectFacet("java"); //$NON-NLS-1$
	public static final IProjectFacet m2eFacet = ProjectFacetsManager.getProjectFacet("jboss.m2"); //$NON-NLS-1$
	public static final IProjectFacet utilFacet = ProjectFacetsManager.getProjectFacet("jst.utility"); //$NON-NLS-1$
	public static final IProjectFacet webFacet = WebFacetUtils.WEB_FACET;

	public static final String DEFAULT_CAMEL_FACET_VERSION = "3.0";
	
	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent event, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = event.getMavenProject();
		if ((event.getFlags() == MavenProjectChangedEvent.FLAG_DEPENDENCIES || event.getKind() == MavenProjectChangedEvent.KIND_ADDED) && facade != null) {
			IProject project = facade.getProject();
			MavenProject mavenProject = facade.getMavenProject(monitor);
			IFacetedProject fproj = ProjectFacetsManager.create(project);
			if (fproj != null && checkCamelContextsExist(project, monitor) && isValidCamelFacetCandidate(project)) {
				installDefaultFacets(project, mavenProject, fproj, monitor);
			}
		}
		super.mavenProjectChanged(event, monitor);
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (checkCamelContextsExist(request.mavenProjectFacade().getProject(), monitor) && isValidCamelFacetCandidate(request.mavenProjectFacade().getProject())) {
			if (!isCamelFacetEnabled(request)) {
				// if we have a camel context but no facade set we do set it
				configureFacet(request.mavenProject(), request.mavenProjectFacade().getProject(), monitor);
			}
			// handling for projects migrated from tooling version 9.x and earlier with deprecated fact version usage 
			if (isInvalidCamelFacetVersion(request)) {
				repairCamelFacet(request, monitor);
			}
			if (!isCamelNatureEnabled(request.mavenProjectFacade().getProject())) {
				// enable the camel nature
				configureNature(request.mavenProjectFacade().getProject(), request.mavenProject(), monitor);
			}
			// handle linked resources for WAR deployments
			if (isWARProject(request.mavenProjectFacade().getProject(), monitor)) {
				configureWARStructureMapping(request.mavenProjectFacade().getProject(), monitor);
			}
			
		}
	}

	private void configureWARStructureMapping(IProject project, IProgressMonitor monitor) throws CoreException {
		final IVirtualComponent c = ComponentCore.createComponent(project, false);
		c.create(IVirtualResource.NONE, monitor);
		final IVirtualFolder webroot = c.getRootFolder();
		final IVirtualFolder classesFolder = webroot.getFolder("/WEB-INF/classes"); //$NON-NLS-1$
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
		updateMappings(m2prj.getCompileSourceLocations(), project, classesFolder, monitor);
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
	private void updateMappings(List<IPath> paths, IProject project, IVirtualFolder vFolder, IProgressMonitor monitor)
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
						ProjectTemplatesActivator.pluginLog().logError(e);
					}
				});
	}

	private boolean isWARProject(IProject project, IProgressMonitor monitor) {
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
		return CamelProjectConfigurator.WAR_PACKAGE.equalsIgnoreCase(m2prj.getPackaging());
	}

	private boolean isInvalidCamelFacetVersion(ProjectConfigurationRequest request) throws CoreException {
		IProject project = request.mavenProjectFacade().getProject();
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		if (fproj != null && fproj.getInstalledVersion(camelFacet) != null) {
			return fproj.getInstalledVersion(camelFacet).getVersionString().compareTo(DEFAULT_CAMEL_FACET_VERSION) < 0;
		}
		return true;
	}
	
	private void repairCamelFacet(ProjectConfigurationRequest request, IProgressMonitor mon) throws CoreException {
		SubMonitor subMon = SubMonitor.convert(mon, 3);
		IProject project = request.mavenProjectFacade().getProject();
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		if (fproj != null) {
			IProjectFacetVersion oldfv = fproj.getInstalledVersion(camelFacet);
			if (oldfv != null) {
				fproj.uninstallProjectFacet(oldfv, null, subMon.split(1));
			}
			subMon.setWorkRemaining(2);
			IFacetedProjectWorkingCopy fpwc = fproj.createWorkingCopy();
			installCamelFacet(fproj, fpwc, subMon.split(1));
			fpwc.commitChanges(subMon.split(1));
		}
	}
	
	private boolean isCamelFacetEnabled(ProjectConfigurationRequest request) throws CoreException {
		IProject project = request.mavenProjectFacade().getProject();
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		if (fproj != null) {
			Set<IProjectFacetVersion> facets = fproj.getProjectFacets();
			Iterator<IProjectFacetVersion> itFacet = facets.iterator();
			while (itFacet.hasNext()) {
				IProjectFacetVersion f = itFacet.next();
				if (ICamelFacetDataModelProperties.CAMEL_PROJECT_FACET.equals(f.getProjectFacet().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCamelNatureEnabled(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] ids = projectDescription.getNatureIds();
		return Arrays.stream(ids).anyMatch(RiderProjectNature.NATURE_ID::equals);
	}

	private void configureNature(IProject project, MavenProject m2Project, IProgressMonitor monitor)
			throws CoreException {
		boolean hasCamelDeps = checkCamelDependencies(m2Project);
		boolean hasCamelContextXML = false;

		// now determine if we have camel context files in the project
		// (only if we don't have any camel deps already)
		if (!hasCamelDeps) {
			hasCamelContextXML = checkCamelContextsExist(project, monitor);
		}

		// if we got camel deps and/or camel context files we add the fuse
		// camel nature to this project
		if (hasCamelDeps || hasCamelContextXML) {
			addNature(project, RiderProjectNature.NATURE_ID, monitor);
		}
	}

	private void configureFacet(MavenProject mavenProject, IProject project, IProgressMonitor monitor)
			throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.configuringFacets, 4);
		if (!checkCamelContextsExist(project, subMonitor.split(1))) {
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

	private void installDefaultFacets(IProject project, MavenProject mavenProject, IFacetedProject fproj, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.installingRequiredFacetsForCamelProject, 4);
		String camelVersion = new CamelMavenUtils().getCamelVersionFromMaven(project, false);
		subMonitor.setWorkRemaining(3);
		if (camelVersion != null) {
			IFacetedProjectWorkingCopy fpwc = fproj.createWorkingCopy();

			// adjust facets we install based on the packaging type we find
			installFacet(fproj, fpwc, javaFacet, javaFacet.getLatestVersion());
			installFacet(fproj, fpwc, m2eFacet, m2eFacet.getLatestVersion());
			if (mavenProject.getPackaging() != null) {
				String packaging = mavenProject.getPackaging();
				if (WAR_PACKAGE.equalsIgnoreCase(packaging)) {
					installFacet(fproj, fpwc, webFacet, javaFacet.getLatestVersion());
				} else if (BUNDLE_PACKAGE.equalsIgnoreCase(packaging) || JAR_PACKAGE.equalsIgnoreCase(packaging)) {
					installFacet(fproj, fpwc, utilFacet, utilFacet.getLatestVersion());
				}
			}
			installCamelFacet(fproj, fpwc, subMonitor.split(1));
			fpwc.commitChanges(subMonitor.split(1));
			configureNature(project, mavenProject, subMonitor.split(1));
			updateMavenProject(project);
		}
	}

	private void updateMavenProject(final IProject project) {
		// MANIFEST.MF is probably not built yet
		if (project != null) {
			// update the maven project so we start in a deployable state
			// with a valid MANIFEST.MF built as part of the build process.
			Job updateJob = new UpdateMavenProjectJob(Collections.singletonList(project));
			updateJob.schedule();
		}
	}

	private void installCamelFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc,
			IProgressMonitor monitor) throws CoreException {
		IDataModel config = (IDataModel) new CamelFacetDataModelProvider().create();
		config.setBooleanProperty(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE, false);
		installFacet(fproj, fpwc, camelFacet, getCamelFacetVersion());
		// we need to switch dependency versions
		CamelFacetVersionChangeDelegate del = new CamelFacetVersionChangeDelegate();
		del.execute(fproj.getProject(), getCamelFacetVersion(), config, monitor);
	}

	private IProjectFacetVersion getCamelFacetVersion() throws CoreException {
		try {
			IProjectFacetVersion facetVersion = camelFacet.getVersion(DEFAULT_CAMEL_FACET_VERSION);
			if (facetVersion != null) {
				return facetVersion;
			}
		} catch (IllegalArgumentException iae) {
			return camelFacet.getLatestVersion();
		}
		return camelFacet.getLatestVersion();
	}
	
	/**
	 * common feature of all our templates is the use of the camel-maven-plugin. if it is not defined in the
	 * pom.xml then we don't apply facet and nature on those projects.
	 * 
	 * @param project
	 * @return
	 */
	private boolean isValidCamelFacetCandidate(IProject project) {
		return !new MavenUtils().isSyndesisExtensionProject(project);
	}
	
	/**
	 * checks whether the project contains any camel context xml file
	 * 
	 * @param project
	 * @return
	 */
	private boolean checkCamelContextsExist(IProject project, IProgressMonitor monitor) {
		return !new CamelFilesFinder().findFiles(project).isEmpty()
				|| new JavaCamelFilesFinder().findJavaDSLRouteBuilderClass(project, monitor) != null;
	}

	private void installFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc, IProjectFacet facet,
			IProjectFacetVersion facetVersion) {
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

	/**
	 * checks whether the pom contains any apache camel dependency
	 * 
	 * @param m2Project
	 * @return
	 */
	private boolean checkCamelDependencies(MavenProject m2Project) {
		return m2Project.getDependencies().stream().anyMatch(this::isCamelDependency);
	}

	private boolean isCamelDependency(Dependency dep) {
		return GROUP_ID_ORG_APACHE_CAMEL.equals(dep.getGroupId())
				&& dep.getArtifactId().startsWith(ARTFIFACT_ID_CAMEL_PREFIX);
	}
}
