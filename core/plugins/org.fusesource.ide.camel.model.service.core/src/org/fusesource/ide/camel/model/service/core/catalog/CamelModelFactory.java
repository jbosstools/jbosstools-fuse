/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 */
public class CamelModelFactory {
	
	private static final String CAMEL_SPRING_BOOT_STARTER = "camel-spring-boot-starter";
	private static final String CAMEL_WILDFLY_CDI = "camel-cdi";
	private static final String CAMEL_WILDFLY_SPRING = "camel-core";
	
	public static final String DEFAULT_CAMEL_VERSION = "2.18.1.redhat-000012";
	
	public static final String RUNTIME_PROVIDER_KARAF = "karaf";
	public static final String RUNTIME_PROVIDER_SPRINGBOOT = "springboot";
	public static final String RUNTIME_PROVIDER_WILDFLY = "wildfly";
	
	/**
	 * returns the latest and greatest supported Camel version we have a catalog 
	 * for. If there are 2 catalogs with the same version (for instance 2.15.1 and 
	 * 2.15.1.redhat-114) then we will always prefer the Red Hat variant.
	 * 
	 * @return
	 */
	public static String getLatestCamelVersion() {
		RepositorySystem system = CamelMavenUtils.newRepositorySystem();
        RepositorySystemSession session = CamelMavenUtils.newRepositorySystemSession( system );
        Artifact artifact = new DefaultArtifact( "org.apache.camel:camel-core:[2,)" );
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.setRepositories( CamelMavenUtils.newRepositories( system, session ) );
        try {
	        VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );
	        org.eclipse.aether.version.Version newestVersion = rangeResult.getHighestVersion();
			if (!Strings.isBlank(newestVersion.toString())) {
				return newestVersion.toString();
			}		
        } catch (Exception ex) {
        	CamelModelServiceCoreActivator.pluginLog().logError(ex);
        }
		return DEFAULT_CAMEL_VERSION;
	}
	
	/**
	 * TODO   This method should be used as much as possible to make sure
	 * the editor is pulling the proper model for the given project. 
	 * 
	 * @return
	 */
	public static String getCamelVersion(IProject p) {
		String version = getCamelVersionFromMaven(p);
		if (version != null) {
			return version;
		}
		return getLatestCamelVersion();
	}
	
	/**
	 * checks for the camel version in the dependencies of the pom.xml
	 * 
	 * @param project
	 * @return
	 */
	public static String getCamelVersionFromMaven(IProject project) {
		if (project == null) return null;
		IPath pomPathValue = project.getProject().getRawLocation() != null ? project.getProject().getRawLocation().append("pom.xml") : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath().append("pom.xml"));
        String pomPath = pomPathValue.toOSString();
        final File pomFile = new File(pomPath);
        if (pomFile.exists() == false || pomFile.isDirectory()) return null;
        try {
        	final Model model = MavenPlugin.getMaven().readModel(pomFile);
        	// get camel-core or another camel dep
	        List<Dependency> deps = new CamelMavenUtils().getDependencies(project, model);
	        for (Dependency pomDep : deps) {
	            if (pomDep.getGroupId().equalsIgnoreCase("org.apache.camel") &&
	                pomDep.getArtifactId().startsWith("camel-")) {
	                if (!Strings.isBlank(pomDep.getVersion())) {
	                	return pomDep.getVersion();
	                }
	            }
	        }
        } catch (Exception ex) {
        	CamelModelServiceCoreActivator.pluginLog().logError("Unable to load camel version from " + pomPath, ex);
        }
        return null;
	}

	public static String getRuntimeprovider(IProject camelProject, IProgressMonitor monitor) {
		if(camelProject != null){
			IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(camelProject, monitor);
			try {
				if(m2prj != null){
					MavenProject mavenProject = m2prj.getMavenProject(monitor);
					if(mavenProject != null){
						List<Dependency> dependencies = mavenProject.getDependencies();
						if(hasSpringBootDependency(dependencies)){
							return RUNTIME_PROVIDER_SPRINGBOOT;
						} else if (hasWildflyDependency(dependencies)) {
							return RUNTIME_PROVIDER_WILDFLY;
						} else {
							return RUNTIME_PROVIDER_KARAF;
						}
					}
				}
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logWarning(e);
			}
		}
		return RUNTIME_PROVIDER_KARAF;
	}
	
	public static boolean hasSpringBootDependency(List<Dependency> dependencies){
		return dependencies != null
				&& dependencies.stream()
					.filter(dependency -> CAMEL_SPRING_BOOT_STARTER.equals(dependency.getArtifactId()))
					.findFirst().isPresent();
	}
	
	// TODO: put in the correct maven coords for a wildfly swarm project
	public static boolean hasWildflyDependency(List<Dependency> dependencies){
		return dependencies != null
				&& dependencies.stream()
					.filter(dependency -> CAMEL_WILDFLY_CDI.equals(dependency.getArtifactId()))
					.findFirst().isPresent();
	}
}
