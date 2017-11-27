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
package org.fusesource.ide.camel.model.service.core.util;

import java.util.stream.Collectors;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

final class SearchLatestBomVersionAvailable implements ICallable<String> {
	private final MavenProject mavenProject;

	SearchLatestBomVersionAvailable(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}

	@Override
	public String call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
		VersionRangeRequest request = new VersionRangeRequest();
		request.setArtifact(new DefaultArtifact("org.jboss.fuse", "jboss-fuse-parent", "pom", "(0,]"));
		request.setRepositories(
				mavenProject
				.getRepositories()
				.stream()
				.map(repo -> new RemoteRepository.Builder(repo.getId(), repo.getLayout(), repo.getUrl())
						.build())
				.collect(Collectors.toList()));
		try {
			RepositorySystemSession repositorySession = context.getRepositorySession();
			VersionRangeResult result = newRepositorySystem().resolveVersionRange(repositorySession, request);
			Version highestVersion = result.getHighestVersion();
			if (highestVersion != null) {
				return highestVersion.toString();
			}
		} catch (VersionRangeResolutionException e) {
			CamelModelServiceCoreActivator.pluginLog().logError(e);
		}
		return null;
	}

	private RepositorySystem newRepositorySystem() {
		return ((MavenImpl)MavenPlugin.getMaven()).lookupComponent(RepositorySystem.class);
	}
}