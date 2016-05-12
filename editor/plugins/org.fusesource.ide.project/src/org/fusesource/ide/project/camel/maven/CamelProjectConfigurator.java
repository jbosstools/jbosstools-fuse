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
package org.fusesource.ide.project.camel.maven;

import java.util.Arrays;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.common.project.facet.JavaFacetInstallDataModelProvider;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.jst.j2ee.internal.project.facet.UtilityFacetInstallDataModelProvider;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.project.camel.CamelFacetDataModelProvider;
import org.fusesource.ide.project.camel.ICamelFacetDataModelProperties;
import org.fusesource.ide.project.providers.CamelVirtualFolder;
import org.jboss.tools.maven.core.IJBossMavenConstants;
import org.jboss.tools.maven.core.internal.project.facet.MavenFacetInstallDataModelProvider;

public class CamelProjectConfigurator extends AbstractProjectConfigurator {

	protected static final IProjectFacet camelFacet, utilityFacet, webFacet, staticWebFacet, ejbFacet, earFacet;
	protected static final IProjectFacetVersion camelFacetVersion, utilityFacetVersion;

	protected static final IProjectFacet m2Facet;
	protected static final IProjectFacetVersion m2Version;
	private static final String DEFAULT_CAMEL_VERSION;

	static {
		camelFacet = ProjectFacetsManager.getProjectFacet("jst.camel"); //$NON-NLS-1$
		camelFacetVersion = camelFacet.getVersion("2.15"); //$NON-NLS-1$

		// Facets that conflict
		webFacet = ProjectFacetsManager.getProjectFacet("jst.web"); //$NON-NLS-1$
		staticWebFacet = ProjectFacetsManager.getProjectFacet("wst.web"); //$NON-NLS-1$
		ejbFacet = ProjectFacetsManager.getProjectFacet("jst.ejb"); //$NON-NLS-1$
		earFacet = ProjectFacetsManager.getProjectFacet("jst.ear"); //$NON-NLS-1$

		utilityFacet = ProjectFacetsManager.getProjectFacet("jst.utility"); //$NON-NLS-1$
		utilityFacetVersion = utilityFacet.getVersion("1.0"); //$NON-NLS-1$
		DEFAULT_CAMEL_VERSION = "2.15";
		m2Facet = ProjectFacetsManager.getProjectFacet("jboss.m2"); //$NON-NLS-1$
		m2Version = m2Facet.getVersion("1.0"); //$NON-NLS-1$
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		MavenProject mavenProject = request.getMavenProject();
		IProject project = request.getProject();
		configureNature(project, mavenProject, monitor);
		configureInternal(mavenProject, project, monitor);
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
			addFuseNature(project, monitor);
		}
	}
	
	private void configureInternal(MavenProject mavenProject, IProject project, IProgressMonitor monitor)
			throws CoreException {

		if (!isCamelConfigurable(mavenProject, project)) {
			return;
		}
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		String packaging = mavenProject.getPackaging();
		boolean isOsgiBundle = "bundle".equals(packaging);

		if (!isOsgiBundle || (fproj != null && (fproj.hasProjectFacet(camelFacet) && fproj.hasProjectFacet(m2Facet)))) {
			// Camel support already installed. Since there's no support for
			// version update -yet- we stop here
			return;
		}

		if (fproj == null) {
			// Add the modulecore nature
			WtpUtils.addNatures(project);
			ProjectUtilities.addNatureToProject(project, FacetedProjectNature.NATURE_ID);
			fproj = ProjectFacetsManager.create(project);

		}
		if (fproj != null && isOsgiBundle) {
			String camelVersion = getCamelVersion(project, mavenProject);
			if (camelVersion != null) {
				installDefaultFacets(fproj, camelVersion, monitor);
			}
		}
	}

	private boolean isCamelConfigurable(MavenProject mavenProject, IProject project) {
		// Look for a file that has parent "blueprint" and grandparent
		// "OSGI-INF"

		final Boolean[] found = new Boolean[1];
		found[0] = false;
		try {
			project.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getName().endsWith(".xml") && resource.getParent().getName().equals("blueprint")
							&& resource.getParent().getParent().getName().equals("OSGI-INF"))
						found[0] = true;
					return !found[0];
				}
			});
		} catch (CoreException ce) {

		}
		return found[0];
	}

	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent event, IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = event.getMavenProject();
		if (event.getFlags() == MavenProjectChangedEvent.FLAG_DEPENDENCIES && facade != null) {
			IProject project = facade.getProject();
			if (isWTPProject(project)) {
				MavenProject mavenProject = facade.getMavenProject(monitor);
				configureInternal(mavenProject, project, monitor);
			}
		}
		super.mavenProjectChanged(event, monitor);
	}

	private boolean isWTPProject(IProject project) {
		return ModuleCoreNature.getModuleCoreNature(project) != null;
	}

	private void installM2Facet(IFacetedProject fproj, IProgressMonitor monitor) throws CoreException {
		IDataModel config = (IDataModel) new MavenFacetInstallDataModelProvider().create();
		config.setBooleanProperty(IJBossMavenConstants.MAVEN_PROJECT_EXISTS, true);
		installFacet(fproj, m2Facet, m2Version, config, monitor);
	}

	@SuppressWarnings("unchecked")
	private void installDefaultFacets(IFacetedProject fproj, String camelVersion, IProgressMonitor monitor)
			throws CoreException {
		IProjectFacet java = ProjectFacetsManager.getProjectFacet("java");
		IDataModel javaModel = DataModelFactory.createDataModel(new JavaFacetInstallDataModelProvider());
		installFacet(fproj, java, java.getVersion("1.8"), javaModel, monitor);
		installUtilityFacet(fproj, monitor);
		installM2Facet(fproj, monitor);
		installCamelFacet(fproj, camelVersion, monitor);
	}

	private void installCamelFacet(IFacetedProject fproj, String camelVersionString, IProgressMonitor monitor)
			throws CoreException {
		IDataModel config = (IDataModel) new CamelFacetDataModelProvider().create();
		config.setBooleanProperty(ICamelFacetDataModelProperties.UPDATE_PROJECT_STRUCTURE, false);
		installFacet(fproj, camelFacet, getCamelFacetVersion(camelVersionString), config, monitor);
	}

	private void installUtilityFacet(IFacetedProject fproj, IProgressMonitor mon) throws CoreException {
		installFacet(fproj, utilityFacet, utilityFacetVersion,
				(IDataModel) new UtilityFacetInstallDataModelProvider().create(), mon);
	}

	private void installFacet(IFacetedProject fproj, IProjectFacet facet, IProjectFacetVersion facetVersion,
			IDataModel config, IProgressMonitor mon) throws CoreException {
		try {
			if (!fproj.hasProjectFacet(facet)) {
				if (facetVersion != null) { // $NON-NLS-1$
					fproj.installProjectFacet(facetVersion, config, mon);
				}
			}
		} catch (CoreException ce) {
			ce.printStackTrace();
			throw ce;
		}
	}

	private IProjectFacetVersion getCamelFacetVersion(String camelVersionString) {
		// Only one version declared for now
		return camelFacetVersion;
	}

	private String getCamelVersion(IProject project, MavenProject mavenProject) throws CoreException {
		// TODO how can we discover a camel version from this?
		// For now, just return 2.15 since its' the only one we support
		return DEFAULT_CAMEL_VERSION;
	}

	private String inferCamelVersionFromDependencies(MavenProject mavenProject) {
		// Read from some descriptor?
		// Fallback to default CDI version
		// if (hasCandidates && cdiVersion == null) {
		return DEFAULT_CAMEL_VERSION;
		// }
	}

	private boolean isKnownFuseGroup(Artifact artifact) {
		return (artifact.getGroupId().startsWith("org.jboss.quickstarts.fuse."));
	}
	
	/**
	 * checks whether the pom contains any apache camel dependency
	 * 
	 * @param m2Project
	 * @return
	 */
	private boolean checkCamelDependencies(MavenProject m2Project) {
		for (Dependency dep : m2Project.getDependencies()) {
			if (dep.getGroupId().equalsIgnoreCase("org.apache.camel") && 
				dep.getArtifactId().toLowerCase().startsWith("camel-")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks whether the project contains any camel context xml file
	 * 
	 * @param project
	 * @return
	 */
	private boolean checkCamelContextsExist(IProject project) throws CoreException {
		return findFiles(project);
	}
	
	/**
	 * adds the fuse camel and java nature to the project if needed
	 * 
	 * @param project
	 */
	private void addFuseNature(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] ids = projectDescription.getNatureIds();

		boolean javaNatureFound = Arrays.stream(ids).anyMatch(s -> JavaCore.NATURE_ID.equals(s));
		boolean camelNatureFound = Arrays.stream(ids).anyMatch(s -> RiderProjectNature.NATURE_ID.equals(s));

		int toAdd = 0;
		if (!camelNatureFound) {
			toAdd++;
		}
		if (!javaNatureFound) {
			toAdd++;
		}
		String[] newIds = new String[ids.length + toAdd];
		System.arraycopy(ids, 0, newIds, 0, ids.length);
		if (!camelNatureFound && !javaNatureFound) {
			newIds[ids.length] = RiderProjectNature.NATURE_ID;
			newIds[newIds.length - 1] = JavaCore.NATURE_ID;
		} else if (!camelNatureFound) {
			newIds[ids.length] = RiderProjectNature.NATURE_ID;
		} else if (!javaNatureFound) {
			newIds[ids.length] = JavaCore.NATURE_ID;
		}
		projectDescription.setNatureIds(newIds);
		project.setDescription(projectDescription, monitor);

		project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
	}
	
	private boolean findFiles(IResource resource) throws CoreException {
		if (resource instanceof IContainer ) {
			IResource[] children = ((IContainer)resource).members();
			for (IResource f : children) {
				if (f instanceof IContainer) {
					boolean found = findFiles(f);
					if (found) return true;
				} else {
					IFile ifile = (IFile)f;
					if (ifile != null) {
						if (ifile.getContentDescription() != null && 
							ifile.getContentDescription()
							  	 .getContentType()
								 .getId()
								 .equals(CamelVirtualFolder.FUSE_CAMEL_CONTENT_TYPE)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
