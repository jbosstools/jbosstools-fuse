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
package org.fusesource.ide.projecttemplates.preferences.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.fusesource.ide.projecttemplates.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StagingRepositoriesFieldEditorTest {
	
	@Mock
	private StagingRepositoriesFieldEditor srfe;
	
	@Before
	public void setup(){
		doCallRealMethod().when(srfe).doCheckState();
	}

	@Test
	public void testDoCheckStateForEmptyString() throws Exception {
		doReturn("").when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isTrue();
	}
	
	@Test
	public void testDoCheckStateForNullValue() throws Exception {
		doReturn(null).when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isTrue();
	}
	
	@Test
	public void testDoCheckStateForCorrectSingleValue() throws Exception {
		doReturn("repoName,http://my.url").when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isTrue();
	}
	
	@Test
	public void testDoCheckStateForCorrectDoubleValue() throws Exception {
		doReturn("repoName,http://my.url;repoName1,http://my.url1").when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isTrue();
	}
	
	@Test
	public void testDoCheckStateForInvalidValue() throws Exception {
		doReturn("repoName,http://my.url;repoName1http://my.url1").when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isFalse();
		verify(srfe).setErrorMessage(anyString());
	}
	
	@Test
	public void testDoCheckStateForDefaultValue() throws Exception {
		doReturn(StagingRepositoriesPreferenceInitializer.DEFAULT_STAGING_REPOSITORIES).when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isTrue();
	}
	
	@Test
	public void testDoCheckStateForDuplicateName() throws Exception {
		doReturn("sameRepoName,http://my.url;sameRepoName,http://my.url1").when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isFalse();
		verify(srfe).setErrorMessage(anyString());
	}
	
	@Test
	public void testDoCheckStateForURLWithCOmaInside() throws Exception {
		doReturn("repoName,http://my.url?withParamter=withacoma,isitworking").when(srfe).getStringValue();
		
		assertThat(srfe.doCheckState()).isTrue();
	}

}
