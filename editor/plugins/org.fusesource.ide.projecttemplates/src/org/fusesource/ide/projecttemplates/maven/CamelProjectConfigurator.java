/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.maven;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
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
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetDataModelProvider;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetVersionChangeDelegate;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;

public class CamelProjectConfigurator extends AbstractProjectConfigurator {

	public static final String ARTFIFACT_ID_CAMEL_PREFIX = "camel-"; //$NON-NLS-1$
	public static final String GROUP_ID_ORG_APACHE_CAMEL = "org.apache.camel"; //$NON-NLS-1$
	public static final String WAR_PACKAGE = "WAR"; //$NON-NLS-1$
	public static final String BUNDLE_PACKAGE = "BUNDLE"; //$NON-NLS-1$
	public static final String JAR_PACKAGE = "JAR"; //$NON-NLS-1$
	public static IProjectFacet camelFacet = ProjectFacetsManager.getProjectFacet("jst.camel"); //$NON-NLS-1$
	public static IProjectFacet javaFacet 	= ProjectFacetsManager.getProjectFacet("java"); //$NON-NLS-1$
	public static IProjectFacet m2eFacet 	= ProjectFacetsManager.getProjectFacet("jboss.m2");  //$NON-NLS-1$
	public static IProjectFacet utilFacet 	= ProjectFacetsManager.getProjectFacet("jst.utility"); //$NON-NLS-1$
	public static IProjectFacet webFacet    = ProjectFacetsManager.getProjectFacet("jst.web"); //$NON-NLS-1$
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#mavenProjectChanged(org.eclipse.m2e.core.project.MavenProjectChangedEvent, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent event, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = event.getMavenProject();
		if (event.getFlags() == MavenProjectChangedEvent.FLAG_DEPENDENCIES && facade != null) {
			IProject project = facade.getProject();
			MavenProject mavenProject = facade.getMavenProject(monitor);
			IFacetedProject fproj = ProjectFacetsManager.create(project);
			if (fproj != null && checkCamelContextsExist(project)) {
				installDefaultFacets(project, mavenProject, fproj, monitor);	
			}
		}
		super.mavenProjectChanged(event, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#configure(org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (checkCamelContextsExist(request.getProject())) {
			if (!isCamelFacetEnabled(request)) {
				// if we have a camel context but no facade set we do set it
				configureFacet(request.getMavenProject(), request.getProject(), monitor);
			}
			if (!isCamelNatureEnabled(request.getProject())) {
				// enable the camel nature
				configureNature(request.getProject(), request.getMavenProject(), monitor);
			}
			// handle linked resources for WAR deployments
			if (isWARProject(request.getProject())) {
				configureWARStructureMapping(request.getProject());	
			}		
		}
	}
	
	private void configureWARStructureMapping(IProject project) throws CoreException {
		final IVirtualComponent c = ComponentCore.createComponent(project, false);
		c.create(0, null);
		final IVirtualFolder webroot = c.getRootFolder();
		final IVirtualFolder classesFolder = webroot.getFolder("/WEB-INF/classes"); //$NON-NLS-1$
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, new NullProgressMonitor());
		updateMappings(m2prj.getCompileSourceLocations(), project, classesFolder);
		updateMappings(m2prj.getTestCompileSourceLocations(), project, classesFolder);
	}
	
	/**
	 * this methods maps a given set of local paths to a path on runtime / in the WAR
	 * 
	 * @param paths
	 * @param project
	 * @param vFolder
	 * @throws CoreException
	 */
	private void updateMappings(IPath[] paths, IProject project, IVirtualFolder vFolder) throws CoreException {
		for (IPath sourceLoc : paths) {
			IFolder srcFolder = project.getFolder(sourceLoc);
			IVirtualResource[] mappings = ComponentCore.createResources(srcFolder);
			boolean found = false;
			for (IVirtualResource mapping : mappings) {
				if (mapping.getProjectRelativePath().toOSString().equals("/" + srcFolder.getProjectRelativePath().toOSString())) {
					mapping.createLink(new Path("/").append(sourceLoc), 0, null);
					found = true;
					break;
				}
			}
			if (!found) {
				//create link for source folder only when it is not mapped
				vFolder.createLink(new Path("/").append(sourceLoc), 0, null);
			}
		}
	}
	
	private boolean isWARProject(IProject project) {
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, new NullProgressMonitor());
		return m2prj.getPackaging().equalsIgnoreCase(CamelProjectConfigurator.WAR_PACKAGE);
	}

	
	private boolean isCamelFacetEnabled(ProjectConfigurationRequest request) throws CoreException {
		IProject project = request.getProject();
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
	
	private void configureNature(IProject project, MavenProject m2Project, IProgressMonitor monitor) throws CoreException {
		boolean hasCamelDeps = checkCamelDependencies(m2Project);
		boolean hasCamelContextXML = false;
						
		// now determine if we have camel context files in the project 
		// (only if we don't have any camel deps already)		
		if (!hasCamelDeps) {
			hasCamelContextXML = checkCamelContextsExist(project);
		}
		
		// if we got camel deps and/or camel context files we add the fuse 
		// camel nature to this project
		if (hasCamelDeps || hasCamelContextXML) {
			addNature(project, RiderProjectNature.NATURE_ID, monitor);
		}
	}
	
	private void configureFacet(MavenProject mavenProject, IProject project, IProgressMonitor monitor)
			throws CoreException {

		if (!isCamelConfigurable(project, monitor)) {
			return;
		}
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		
		if (fproj == null) {
			// Add the modulecore nature
			WtpUtils.addNatures(project);
			addNature(project, FacetedProjectNature.NATURE_ID, monitor);
			fproj = ProjectFacetsManager.create(project);
		}
		
		if (fproj != null) {
			installDefaultFacets(project, mavenProject, fproj, monitor);
		}
	}

	private void installDefaultFacets(IProject project, MavenProject mavenProject, IFacetedProject fproj, IProgressMonitor monitor)
	        throws CoreException {
	    String camelVersion = getCamelVersion(mavenProject);
	    if (camelVersion != null) {
	        IFacetedProjectWorkingCopy fpwc = fproj.createWorkingCopy();

	        // adjust facets we install based on the packaging type we find
	        installFacet(fproj, fpwc, javaFacet, javaFacet.getLatestVersion(), monitor);
	        installFacet(fproj, fpwc, m2eFacet, m2eFacet.getLatestVersion(), monitor);
            if (mavenProject.getPackaging() != null) {
                String packaging = mavenProject.getPackaging();
	            if (WAR_PACKAGE.equalsIgnoreCase(packaging)) {
	                installFacet(fproj, fpwc, webFacet, javaFacet.getLatestVersion(), monitor);
	            } else if (BUNDLE_PACKAGE.equalsIgnoreCase(packaging) || JAR_PACKAGE.equalsIgnoreCase(packaging)) {
	                installFacet(fproj, fpwc, utilFacet, utilFacet.getLatestVersion(), monitor);
	            }
	        }
	        installCamelFacet(fproj, fpwc, camelVersion, monitor);
	        fpwc.commitChanges(monitor);
	        configureNature(project, mavenProject, monitor); 
	    }
	}
	
	private boolean isCamelConfigurable(IProject project, IProgressMonitor monitor) {
		// Look for a file that has parent "blueprint" and grandparent "OSGI-INF" or
		// spring in folder META-INF
		final Boolean[] found = new Boolean[1];
		found[0] = false;

		try {
			project.accept(new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (isMatchingPath(resource, "blueprint", "OSGI-INF") //$NON-NLS-1$ //$NON-NLS-2$
							|| isMatchingPath(resource, "spring", "META-INF")) { //$NON-NLS-1$ //$NON-NLS-2$
						found[0] = true;
					}				
					return !found[0];
				}

				private boolean isMatchingPath(IResource resource, String parentName, String grandParentName) {
					return resource.getName().endsWith(".xml") //$NON-NLS-1$
							&& resource.getParent().getName().equals(parentName)
							&& resource.getParent().getParent().getName().equals(grandParentName);
				}
			});
		} catch (CoreException ce) {
			ProjectTemplatesActivator.pluginLog().logError(ce);
		}

		if (!found[0]) {
			// check for java dsl
			IFile f = FuseIntegrationProjectCreatorRunnable.findJavaDSLRouteBuilderClass(project, monitor);
			if (f != null) {
				// java dsl found
				found[0] = true;
			}
		}

		return found[0];
	}

	private String getCamelVersion(MavenProject mavenProject) throws CoreException {
		for (Dependency dep : mavenProject.getDependencies()) {
			if (isCamelDependency(dep)) {
				return dep.getVersion();
			}
		}
		return CamelModelFactory.getLatestCamelVersion();
	}

	private void installCamelFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc, String camelVersionString, IProgressMonitor monitor)
			throws CoreException {
		IDataModel config = (IDataModel) new CamelFacetDataModelProvider().create();
		config.setBooleanProperty(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE, false);
		IProjectFacetVersion camelFacetVersion = getCamelFacetVersion(camelVersionString);
		installFacet(fproj, fpwc, camelFacet, camelFacetVersion == null ? camelFacet.getLatestVersion() : camelFacetVersion, monitor);
		if (camelFacetVersion == null) {
			// we need to switch dependency versions
			CamelFacetVersionChangeDelegate del = new CamelFacetVersionChangeDelegate();
			del.execute(fproj.getProject(), camelFacet.getLatestVersion(), config, monitor);
		}
	}

	private IProjectFacetVersion getCamelFacetVersion(String camelVersionString) throws CoreException {
		IProjectFacetVersion facetVersion = camelFacet.getVersion(camelVersionString);
		if (facetVersion == null) {
			facetVersion = camelFacet.getLatestVersion();
		}
		return facetVersion;
	}
	
	/**
	 * checks whether the project contains any camel context xml file
	 * 
	 * @param project
	 * @return
	 */
	private boolean checkCamelContextsExist(IProject project) throws CoreException {
		return findFiles(project) || FuseIntegrationProjectCreatorRunnable.findJavaDSLRouteBuilderClass(project, new NullProgressMonitor()) != null;
	}
	
	private boolean findFiles(IResource resource) throws CoreException {
		if (resource instanceof IContainer) {
			IResource[] children = ((IContainer) resource).members();
			for (IResource f : children) {
				if (f instanceof IContainer) {
					boolean found = findFiles(f);
					if (found)
						return true;
				} else {
					IFile ifile = (IFile) f;
					if (ifile != null) {
						IContentDescription contentDescription = ifile.getContentDescription();
						if (contentDescription != null
								&& CamelUtils.FUSE_CAMEL_CONTENT_TYPE.equals(contentDescription.getContentType().getId())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private void installFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc, IProjectFacet facet, IProjectFacetVersion facetVersion, IProgressMonitor mon) throws CoreException {
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
		return m2Project.getDependencies().stream()
				.anyMatch(this::isCamelDependency);
	}
	
	private boolean isCamelDependency(Dependency dep) {
		return GROUP_ID_ORG_APACHE_CAMEL.equals(dep.getGroupId()) && 
			dep.getArtifactId().startsWith(ARTFIFACT_ID_CAMEL_PREFIX);
	}
}
