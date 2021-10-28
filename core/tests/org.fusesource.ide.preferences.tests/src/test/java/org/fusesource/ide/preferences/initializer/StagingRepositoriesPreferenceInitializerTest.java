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
package org.fusesource.ide.preferences.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.fusesource.ide.preferences.StagingRepositoriesConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StagingRepositoriesPreferenceInitializerTest {
	
	@Mock
	IPreferenceStore preferenceStore;
	@Spy
	StagingRepositoriesPreferenceInitializer initializer;
	
	@Before
	public void setup() {
		doReturn(preferenceStore).when(initializer).getPreferenceStore();
	}
	
	@Test
	public void testGetStagingRepositories() throws Exception {
		doReturn("name1,http://my.url1;name2,http://my.url2").when(preferenceStore).getString(StagingRepositoriesConstants.STAGING_REPOSITORIES);
		List<List<String>> stagingRepositories = initializer.getStagingRepositories();
		
		assertThat(stagingRepositories.get(0)).containsExactly("name1", "http://my.url1");
		assertThat(stagingRepositories.get(1)).containsExactly("name2", "http://my.url2");
	}
	
}
