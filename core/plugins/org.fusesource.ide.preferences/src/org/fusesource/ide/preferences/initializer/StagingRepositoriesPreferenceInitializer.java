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
package org.fusesource.ide.preferences.initializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.fusesource.ide.preferences.Activator;
import org.fusesource.ide.preferences.StagingRepositoriesConstants;

public class StagingRepositoriesPreferenceInitializer extends AbstractPreferenceInitializer {
	
	private static final String PRODUCT_STAGING_REPO_URI =
			"fuse-internal"+ 
			StagingRepositoriesConstants.NAME_URL_SEPARATOR+
			"http://download.eng.brq.redhat.com/brewroot/repos/jb-fuse-6.2-build/latest/maven/";
	private static final String PRODUCT_FIS_STAGING_REPO_URI =
			"fis-internal"+
			StagingRepositoriesConstants.NAME_URL_SEPARATOR+
			"http://download-node-02.eng.bos.redhat.com/brewroot/repos/jb-fis-2.0-maven-build/latest/maven/";
	private static final String PRODUCT_FUSE_NON_PRODUCTIZED_STAGING_REPO_URI =
			"fuse-early-access"+
			StagingRepositoriesConstants.NAME_URL_SEPARATOR+
			"https://origin-repository.jboss.org/nexus/content/groups/ea/";
	private static final String THIRD_PARTY_STAGING_REPO_URI =
			"redhat-ea"+
			StagingRepositoriesConstants.NAME_URL_SEPARATOR+
			"https://maven.repository.redhat.com/earlyaccess/all";
	private static final String ASF_SNAPSHOT_REPO_URI = 
			"asf-snapshots" +
			StagingRepositoriesConstants.NAME_URL_SEPARATOR +
			"https://repository.apache.org/content/groups/snapshots/";

	public StagingRepositoriesPreferenceInitializer() {
		// Keep for reflection initialization
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setDefault(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES, false);
		preferenceStore.setDefault(StagingRepositoriesConstants.STAGING_REPOSITORIES,
				PRODUCT_STAGING_REPO_URI + StagingRepositoriesConstants.REPO_SEPARATOR +
				THIRD_PARTY_STAGING_REPO_URI + StagingRepositoriesConstants.REPO_SEPARATOR +
				PRODUCT_FIS_STAGING_REPO_URI + StagingRepositoriesConstants.REPO_SEPARATOR +
				ASF_SNAPSHOT_REPO_URI + StagingRepositoriesConstants.REPO_SEPARATOR +
				PRODUCT_FUSE_NON_PRODUCTIZED_STAGING_REPO_URI);
	}

	IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	public boolean isStagingRepositoriesEnabled(){
		return getPreferenceStore().getBoolean(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES);
	}
	
	public void setStagingRepositoriesEnablement(boolean enable){
		getPreferenceStore().setValue(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES, enable);
	}
	
	/**
	 * @return A list of pair in List for Name/URl of repositories
	 */
	public List<List<String>> getStagingRepositories(){
		String storedValue = getStagingRepositoriesString();
		return getStagingRepositoriesAsList(storedValue);
	}

	/**
	 * @return the stored string list of repositories
	 */
	public String getStagingRepositoriesString(){
		return getPreferenceStore().getString(StagingRepositoriesConstants.STAGING_REPOSITORIES);
	}
	
	/**
	 * Utility method to parse into a more digestible list
	 * @param list
	 * @return A list of pair in List for Name/URl of repositories
	 */
	public List<List<String>> getStagingRepositoriesAsList(String list){
		return Arrays.asList(list.split(StagingRepositoriesConstants.REPO_SEPARATOR))
				.stream()
				.map(repoName -> Arrays.asList(repoName.split(StagingRepositoriesConstants.NAME_URL_SEPARATOR)))
				.collect(Collectors.toList());
	}

	/**
	 * allows to add a staging repo 
	 * 
	 * @param repoName	the name of the repo
	 * @param repoUrl	the url of the repo
	 */
	public void addStagingRepository(String repoName, String repoUrl) {
		// do not duplicate URIs
		if (getStagingRepositoriesString().indexOf(repoUrl) != -1) {
			String newRepoString = String.format("%s%s%s%s%s", getStagingRepositoriesString(), StagingRepositoriesConstants.REPO_SEPARATOR, repoName, StagingRepositoriesConstants.NAME_URL_SEPARATOR, repoUrl);
			getPreferenceStore().setValue(StagingRepositoriesConstants.STAGING_REPOSITORIES, newRepoString);
		}
	}
}
