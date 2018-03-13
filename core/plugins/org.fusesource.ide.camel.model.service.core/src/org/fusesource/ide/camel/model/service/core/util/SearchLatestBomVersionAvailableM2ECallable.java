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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
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

final class SearchLatestBomVersionAvailableM2ECallable implements ICallable<String> {
	
	private Dependency bomUsed;
	private List<Repository> repositories;
	private String versionExpression;

	SearchLatestBomVersionAvailableM2ECallable(List<org.apache.maven.model.Repository> repositories, Dependency bomUsed) {
		this(repositories, bomUsed, null);
	}
	
	SearchLatestBomVersionAvailableM2ECallable(List<org.apache.maven.model.Repository> repositories, Dependency bomUsed, String searchExpression) {
		this.repositories = repositories;
		this.bomUsed = bomUsed;
		this.versionExpression = searchExpression;
	}

	@Override
	public String call(IMavenExecutionContext context, IProgressMonitor monitor) throws CoreException {
		VersionRangeRequest request = new VersionRangeRequest();
		request.setArtifact(new DefaultArtifact(bomUsed.getGroupId(), bomUsed.getArtifactId(), bomUsed.getType(), "(0,]"));
		request.setRepositories(
				repositories.stream()
				.map(repo -> new RemoteRepository.Builder(repo.getId(), repo.getLayout(), repo.getUrl()).build())
				.collect(Collectors.toList()));
		try {
			RepositorySystemSession repositorySession = context.getRepositorySession();
			VersionRangeResult result = retrieveRepositorySystem().resolveVersionRange(repositorySession, request);
			List<Version> productizedversion = result.getVersions().stream().filter(version -> {
				String versionAsString = version.toString();
				if (versionExpression != null) {
					return (versionAsString.contains("fuse") || versionAsString.contains("redhat")) && versionAsString.startsWith(versionExpression);
				}
				return versionAsString.contains("fuse") || versionAsString.contains("redhat");
			}).collect(Collectors.toList());
			VersionRangeResult versionRangeResultForProductizedVersion = new VersionRangeResult(request);
			versionRangeResultForProductizedVersion.setVersions(productizedversion);
			Version productizedHighestVersion = versionRangeResultForProductizedVersion.getHighestVersion();
			if(productizedHighestVersion != null) {
				return productizedHighestVersion.toString();
			}
			Version highestVersion = result.getHighestVersion();
			if (highestVersion != null && versionExpression == null || 
				highestVersion != null && highestVersion.toString().startsWith(versionExpression)) {
				return highestVersion.toString();
			}
		} catch (VersionRangeResolutionException e) {
			CamelModelServiceCoreActivator.pluginLog().logError(e);
		}
		return null;
	}

	private RepositorySystem retrieveRepositorySystem() {
		return ((MavenImpl)MavenPlugin.getMaven()).lookupComponent(RepositorySystem.class);
	}
}