/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

public class CamelMavenUtils {

	/**
	 * returns the dependencies for the supplied Maven model in the supplied project
	 * 
	 * @param project
	 * @param model
	 * @return
	 */
	public List<Dependency> getDependencies(IProject project, final Model model) {
		IMavenProjectFacade projectFacade = getMavenProjectFacade(project);
		List<Dependency> deps = new ArrayList<>();
		if (projectFacade != null) {
			try {
				MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());
				deps.addAll(mavenProject.getDependencies());
				deps.addAll(mavenProject.getCompileDependencies());
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logError(
						"Maven project has not been found (not imported?). Managed Dependencies won't be resolved.", e);
				deps.addAll(model.getDependencies());
			}
		} else {
			// In case the project was not imported in the workspace
			deps.addAll(model.getDependencies());
		}
		return deps;
	}

	/**
	 * /!\ public for test purpose
	 * 
	 * @param project
	 * @return the Maven project facade corresponding to the supplied project
	 */
	public IMavenProjectFacade getMavenProjectFacade(IProject project) {
		final IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		final IFile pomIFile = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		return projectRegistry.create(pomIFile, false, new NullProgressMonitor());
	}

	/**
	 * resolves the given artifact (assuming a jar)
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return	the artifact or null if not resolvable 
	 */
	public Artifact resolveArtifact(String groupId, String artifactId, String version) {
		try {
			return MavenPlugin.getMaven().resolve(groupId, artifactId, version, "jar", //$NON-NLS-1$
					null, null, new NullProgressMonitor());
		} catch (CoreException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}

	/**
	 * checks for the camel version in the dependencies of the pom.xml
	 * 
	 * @param project
	 * @return
	 */
	public String getCamelVersionFromMaven(IProject project) {
		if (project == null) return null;
		IPath pomPathValue = project.getProject().getRawLocation() != null
				? project.getProject().getRawLocation().append("pom.xml")
				: ResourcesPlugin.getWorkspace().getRoot().getLocation()
						.append(project.getFullPath().append("pom.xml"));
		String pomPath = pomPathValue.toOSString();
		final File pomFile = new File(pomPath);
		if (pomFile.exists() == false || pomFile.isDirectory()) return null;
		try {
			final Model model = MavenPlugin.getMaven().readModel(pomFile);

			// get camel-core or another camel dep
			List<Dependency> deps = new CamelMavenUtils().getDependencies(project, model);
			for (Dependency pomDep : deps) {
				if (pomDep.getGroupId().equalsIgnoreCase("org.apache.camel")
						&& pomDep.getArtifactId().startsWith("camel-")) {
					return pomDep.getVersion();
				}
			}
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
}
