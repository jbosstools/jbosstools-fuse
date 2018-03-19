/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.preferences.StagingRepositoriesUtils;
import org.fusesource.ide.syndesis.extensions.core.internal.SyndesisExtensionsCoreActivator;

/**
 * @author lheinema
 */
public class SyndesisVersionUtil {
	
	public  static final String BOM_GROUPID = "io.syndesis.extension";
	public  static final String BOM_ARTIFACTID = "extension-bom";
	public  static final String BOM_TYPE = "pom";
	public  static final String PROP_SPRINGBOOT_VERSION = "spring-boot.version";
	public  static final String PROP_CAMEL_VERSION = "camel.version";
	public  static final String PROP_SYNDESIS_VERSION = "syndesis.version";
	
	private static final String FALLBACK_SPRINGBOOT_VERSION = "1.5.8.RELEASE";
	private static final String FALLBACK_CAMEL_VERSION = "2.20.1";
	
	private SyndesisVersionUtil() {
		// util class
	}
	
	public static boolean isSyndesisVersionExisting(String syndesisVersion, IProgressMonitor monitor) {
		return checkSyndesisVersionExisting(syndesisVersion, monitor).containsKey(PROP_SYNDESIS_VERSION);
	}
	
	public static Map<String, String> checkSyndesisVersionExisting(String syndesisVersion, IProgressMonitor monitor) {
		Map<String, String> versions = new HashMap<>();
		
		IMaven maven = MavenPlugin.getMaven();
		List<ArtifactRepository> repos;
		try {
			repos = getRepositoryList(maven);
		} catch (CoreException ex) {
			SyndesisExtensionsCoreActivator.pluginLog().logError(ex);
			repos = new ArrayList<>();
		}
		
		try {
			Artifact bom = maven.resolve(BOM_GROUPID, BOM_ARTIFACTID, syndesisVersion, BOM_TYPE, null, repos, monitor);
			if (bom != null) {
				versions.put(PROP_SYNDESIS_VERSION, syndesisVersion);
				
				Model bomModel = maven.readModel(bom.getFile());
				if (bomModel.getProperties().containsKey(PROP_SPRINGBOOT_VERSION)) {
					versions.put(PROP_SPRINGBOOT_VERSION, bomModel.getProperties().getProperty(PROP_SPRINGBOOT_VERSION, FALLBACK_SPRINGBOOT_VERSION));
				}
				if (bomModel.getProperties().containsKey(PROP_CAMEL_VERSION)) {
					versions.put(PROP_CAMEL_VERSION, bomModel.getProperties().getProperty(PROP_CAMEL_VERSION, FALLBACK_CAMEL_VERSION));
				}				
			}
		} catch (CoreException ex) {
			if (!Strings.isBlank(ex.getMessage()) && ex.getMessage().indexOf("Could not resolve artifact") != -1) {
				// in case we just don't find the artifact we don't want to log an exception as error
				// because in case of snapshots we are trying to resolve the issue by locating a matching existing version
				SyndesisExtensionsCoreActivator.pluginLog().logWarning(ex);
			} else {
				SyndesisExtensionsCoreActivator.pluginLog().logError(ex);
			}
		}
		return versions;
	}
	
	private static List<ArtifactRepository> getRepositoryList(IMaven maven) throws CoreException {
		List<ArtifactRepository> repoList = new ArrayList<>();
		List<List<String>> additionRepos = StagingRepositoriesUtils.getAdditionalRepos();
		for (List<String> repo : additionRepos) {
			String name = repo.get(0);
			String url = repo.get(1);
			repoList.add(maven.createArtifactRepository(name, url));
		}
		
		return repoList;
	}

}
