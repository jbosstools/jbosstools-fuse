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
package org.fusesource.ide.projecttemplates.preferences.initializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

public class StagingRepositoriesPreferenceInitializer extends AbstractPreferenceInitializer {
	
	public static final String NAME_URL_SEPARATOR = ",";
	public static final String REPO_SEPARATOR = ";";
	public static final String ENABLE_STAGING_REPOSITORIES = "enableStagingRepositories";
	public static final String STAGING_REPOSITORIES = "stagingRepositories";
	private static final String PRODUCT_STAGING_REPO_URI = "fuse-internal"+NAME_URL_SEPARATOR+"http://download.eng.brq.redhat.com/brewroot/repos/jb-fuse-6.2-build/latest/maven";
	private static final String THIRD_PARTY_STAGING_REPO_URI = "redhat-ea"+NAME_URL_SEPARATOR+"https://maven.repository.redhat.com/earlyaccess/all";
	public static final String DEFAULT_STAGING_REPOSITORIES = PRODUCT_STAGING_REPO_URI + REPO_SEPARATOR + THIRD_PARTY_STAGING_REPO_URI;

	public StagingRepositoriesPreferenceInitializer() {
		// Keep for reflection initialization
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.setDefault(ENABLE_STAGING_REPOSITORIES, false);
		preferenceStore.setDefault(STAGING_REPOSITORIES, DEFAULT_STAGING_REPOSITORIES);
	}

	IPreferenceStore getPreferenceStore() {
		return ProjectTemplatesActivator.getDefault().getPreferenceStore();
	}
	
	public boolean isStagingRepositoriesEnabled(){
		return getPreferenceStore().getBoolean(ENABLE_STAGING_REPOSITORIES);
	}
	
	public void setStagingRepositoriesEnablement(boolean enable){
		getPreferenceStore().setValue(ENABLE_STAGING_REPOSITORIES, enable);
	}
	
	/**
	 * @return A list of pair in List for Name/URl of repositories
	 */
	public List<List<String>> getStagingRepositories(){
		String storedValue = getPreferenceStore().getString(STAGING_REPOSITORIES);
		return Arrays.asList(storedValue.split(REPO_SEPARATOR))
				.stream()
				.map(repoName -> Arrays.asList(repoName.split(NAME_URL_SEPARATOR)))
				.collect(Collectors.toList());
	}

}
