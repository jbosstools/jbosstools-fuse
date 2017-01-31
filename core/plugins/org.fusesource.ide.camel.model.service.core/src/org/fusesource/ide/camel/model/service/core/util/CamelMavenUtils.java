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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

public class CamelMavenUtils {

	/**
	 * @param project
	 * @param model
	 * @return The dependencies for the supplied Maven model in the supplied
	 *         project
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
	 * @param project
	 * @return the Maven project facade corresponding to the supplied project
	 */
	public IMavenProjectFacade getMavenProjectFacade(IProject project) {
		final IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		final IFile pomIFile = project.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		return projectRegistry.create(pomIFile, false, new NullProgressMonitor());
	}
	
	
}
