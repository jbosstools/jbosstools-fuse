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
package org.fusesource.ide.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;

/**
 * @author lheinema
 */
public class StagingRepositoriesUtils {
	
	private StagingRepositoriesUtils() {
		// util class
	}
	
	public static List<List<String>> getAdditionalRepos() {
		List<List<String>> repoList = new ArrayList<>();

		StagingRepositoriesPreferenceInitializer initializer = new StagingRepositoriesPreferenceInitializer();
		
		// add staging repos if enabled
		if (initializer.isStagingRepositoriesEnabled()) {
			repoList.addAll(initializer.getStagingRepositories());
		}
				
		// public asf repo
		repoList.add(Arrays.asList("asf-public", "https://repo.maven.apache.org/maven2"));
		// old fuse repo
		repoList.add(Arrays.asList("old-fuse", "https://repository.jboss.org/nexus/content/repositories/fs-releases"));
		// red hat public GA repo
		repoList.add(Arrays.asList("redhat-ga", "https://maven.repository.redhat.com/ga/"));

		return repoList;
	}
}
