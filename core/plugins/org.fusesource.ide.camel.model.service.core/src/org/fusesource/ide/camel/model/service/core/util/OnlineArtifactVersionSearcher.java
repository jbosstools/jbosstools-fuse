/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Repository;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.preferences.StagingRepositoriesUtils;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;

public class OnlineArtifactVersionSearcher {
	
	public String findLatestBomVersionOnAvailableRepo(IProject project, IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		String bomVersion = null;
		try {
			if (project != null) {
				IMavenProjectFacade mavenProjectFacade = new CamelMavenUtils().getMavenProjectFacade(project);
				MavenProject mavenProject = mavenProjectFacade.getMavenProject(subMon.split(1));

				Dependency bomUsed = retrieveAnyFuseBomUsed(mavenProject);

				if (bomUsed != null) {
					bomVersion = findLatestBomVersionOnAvailableRepo(project, subMon.split(1), mavenProject, bomUsed);
				}
			}
		} catch (CoreException e) {
			CamelModelServiceCoreActivator.pluginLog().logError(e);
		}
		subMon.setWorkRemaining(0);
		return bomVersion;
	}

	protected String findLatestBomVersionOnAvailableRepo(IProject project, IProgressMonitor monitor, MavenProject mavenProject, Dependency bomToSearch) throws CoreException {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		return MavenPlugin.getMaven().createExecutionContext().execute(mavenProject, new SearchLatestBomVersionAvailableM2ECallable(mavenProject.getRepositories(), bomToSearch), subMon.split(1));
	}
	
	public String findLatestVersion(IProgressMonitor monitor, Dependency artifactToSearch) {
		return findLatestVersion(monitor, artifactToSearch, null);
	}
	
	public String findLatestVersion(IProgressMonitor monitor, Dependency artifactToSearch, String searchExpression) {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		
		// search with Aether APi
		List<List<String>> additionalRepos = StagingRepositoriesUtils.getAdditionalRepos();
		List<Repository> additionalMavenRepos = new ArrayList<>();
		for (List<String> repo : additionalRepos) {
			Repository mavenRepo = new Repository();
			mavenRepo.setId(repo.get(0));
			mavenRepo.setUrl(repo.get(1));
			additionalMavenRepos.add(mavenRepo);
		}
		StagingRepositoriesPreferenceInitializer initializer = new StagingRepositoriesPreferenceInitializer();
		if (!initializer.isStagingRepositoriesEnabled()) {
			Repository mavenRepo = new Repository();
			mavenRepo.setId("fuse-early-access");
			mavenRepo.setUrl("https://repository.jboss.org/nexus/content/groups/ea/");
			additionalMavenRepos.add(mavenRepo);
		}
		try {
			return MavenPlugin.getMaven().createExecutionContext()
					.execute(new SearchLatestBomVersionAvailableM2ECallable(additionalMavenRepos, artifactToSearch,
							searchExpression), subMon.split(1));
		} catch (OperationCanceledException | CoreException e) {
			CamelModelServiceCoreActivator.pluginLog().logError(e);
			return null;
		}
	}
	

	private Dependency retrieveAnyFuseBomUsed(MavenProject mavenProject) {
		DependencyManagement dependencyManagement = mavenProject.getDependencyManagement();
		return retrieveAnyFuseBomUsed(dependencyManagement);
	}

	public Dependency retrieveAnyFuseBomUsed(DependencyManagement dependencyManagement) {
		if(dependencyManagement != null) {
			List<Dependency> managedDependencies = dependencyManagement.getDependencies();
			if(managedDependencies != null) {
				Optional<Dependency> bomDependency = managedDependencies.stream()
				.filter(new FuseBomFilter())
				.findAny();
				if(bomDependency.isPresent()) {
					return bomDependency.get();
				}
			}
		}
		return null;
	}
	
}
