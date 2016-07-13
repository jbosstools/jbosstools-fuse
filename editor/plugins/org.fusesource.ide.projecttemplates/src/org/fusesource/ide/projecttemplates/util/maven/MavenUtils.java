/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.projecttemplates.util.maven;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;

/**
 * @author lhein
 *
 */
public class MavenUtils {
	/**
	 * @param plugins
	 * @param camelVersion
	 */
	public static void updateCamelVersionPlugins(List<Plugin> plugins, String camelVersion) {
		for (Plugin p : plugins) {
			if ("org.apache.camel".equalsIgnoreCase(p.getGroupId()) && p.getArtifactId().startsWith("camel-")) {
				p.setVersion(camelVersion);
			}
		}
	}

	/**
	 * @param dependencies
	 * @param camelVersion
	 */
	public static void updateCamelVersionDependencies(List<Dependency> dependencies, String camelVersion) {
		for (Dependency dep : dependencies) {
			if ("org.apache.camel".equalsIgnoreCase(dep.getGroupId()) && dep.getArtifactId().startsWith("camel-")) {
				dep.setVersion(camelVersion);
			}
		}
	}
	
	/**
	 * 
	 * @param repositories
	 * @param repoURI
	 * @param newId
	 */
	public static void ensureRepositoryExists(List<Repository> repositories, String repoURI, String newId) {
		boolean exists = false;
		for (Repository rep : repositories) {
			if (rep.getUrl().equalsIgnoreCase(repoURI)) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			Repository repo = new Repository();
			repo.setId(newId);
			repo.setUrl(repoURI);
			repositories.add(repo);
		}
	}
}
