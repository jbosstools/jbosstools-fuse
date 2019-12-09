/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.preferences.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.fusesource.ide.preferences.Activator;
import org.fusesource.ide.preferences.StagingRepositoriesConstants;
import org.fusesource.ide.preferences.StagingRepositoriesPreferencePage;
import org.fusesource.ide.preferences.StagingRepositoryDialog;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.junit.After;
import org.junit.Test;

/**
 * @author brianf
 *
 */
public class StagingRepositoriesPreferencePageIT {
	
	private String testList = "name1,http://my.url1;name2,http://my.url2;";

	@Test
	public void testStagingRepositoryPreferencePage() throws Exception {
		// grab the preference store
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		assertThat(preferenceStore).isNotNull();
		
		// set new test values
		preferenceStore.setValue(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES, true);
		preferenceStore.setValue(StagingRepositoriesConstants.STAGING_REPOSITORIES, testList);

		// make sure they set properly
		assertThat(preferenceStore.getBoolean(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES)).isTrue();
		assertThat(preferenceStore.getString(StagingRepositoriesConstants.STAGING_REPOSITORIES)).isEqualTo(testList);
		
		// open the preference dialog with the staging repositories page and get a handle to that page
		PreferenceDialog prefsDialog =
			PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
			StagingRepositoriesConstants.STAGING_REPOSITORIES_PREFERENCE_PAGE_ID, 
			new String[] {StagingRepositoriesConstants.STAGING_REPOSITORIES_PREFERENCE_PAGE_ID}, null);
		prefsDialog.setPreferenceStore(preferenceStore);
		assertThat(prefsDialog).isNotNull();
		Object page = prefsDialog.getSelectedPage();
		assertThat(page).isNotNull();
		assertThat(page).isInstanceOf(StagingRepositoriesPreferencePage.class);
		StagingRepositoriesPreferencePage stagingPage = (StagingRepositoriesPreferencePage) page;
		
		// make sure that the checkbox is checked and the list is what we expect it to be
		assertThat(stagingPage.getEnableStagingRepositoriesEditor().getBooleanValue()).isEqualTo(preferenceStore.getBoolean(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES));
		assertThat(stagingPage.getStagingRepositoryListEditor().getItemList()).isEqualTo(testList);
		assertThat(stagingPage.getStagingRepositoryListEditor().isListEnabled()).isTrue();
		
		// close it
		assertThat(prefsDialog.close()).isTrue();
		
		// update the enable setting to be false to test enablement of the list control
		preferenceStore.setValue(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES, false);
		
		// reopen the dialog and grab the page
		prefsDialog =
				PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				StagingRepositoriesConstants.STAGING_REPOSITORIES_PREFERENCE_PAGE_ID, 
				new String[] {StagingRepositoriesConstants.STAGING_REPOSITORIES_PREFERENCE_PAGE_ID}, null);
		prefsDialog.setPreferenceStore(preferenceStore);
		page = prefsDialog.getSelectedPage();
		stagingPage = (StagingRepositoriesPreferencePage) page;
		
		// make sure that the list control is disabled
		assertThat(stagingPage.getEnableStagingRepositoriesEditor().getBooleanValue()).isEqualTo(preferenceStore.getBoolean(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES));
		assertThat(stagingPage.getStagingRepositoryListEditor().isListEnabled()).isFalse();
	}
	
	@After
	public void resetPreferences() {
		// now set things back in case there are further tests
		StagingRepositoriesPreferenceInitializer initializer = new StagingRepositoriesPreferenceInitializer();
		initializer.initializeDefaultPreferences();
	}
	
	@Test
	public void testNewRepositoryDialog() throws Exception {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				StagingRepositoryDialog dialog = new StagingRepositoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				assertThat(dialog).isNotNull();
				dialog.create();
				
				assertThat(dialog.getErrorMessage()).isNull();
				
				dialog.setName(" ");
				assertThat(dialog.getErrorMessage()).isNotNull();
				
				dialog.setName("with spaces");
				assertThat(dialog.getErrorMessage()).isNotNull();
				
				dialog.setName("valid");
				// even if name is valid, URL still is not
				assertThat(dialog.getErrorMessage()).isNotNull();
				
				dialog.setURL(" ");
				assertThat(dialog.getErrorMessage()).isNotNull();
				
				dialog.setURL("invalidurl");
				assertThat(dialog.getErrorMessage()).isNotNull();

				dialog.setURL("http://myvalidurl");
				assertThat(dialog.getErrorMessage()).isNull();
				
				dialog.pressOK();
				assertThat(dialog.getName()).isEqualTo("valid");
				assertThat(dialog.getURL()).isEqualTo("http://myvalidurl");
			}}
		);
	}

}
