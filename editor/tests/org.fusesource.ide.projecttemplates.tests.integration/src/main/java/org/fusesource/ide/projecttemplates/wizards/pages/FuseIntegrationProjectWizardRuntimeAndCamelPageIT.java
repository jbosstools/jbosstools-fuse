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
package org.fusesource.ide.projecttemplates.wizards.pages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.eclipse.wst.server.core.IRuntime;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FuseIntegrationProjectWizardRuntimeAndCamelPageIT {
	
	@Mock
	FuseIntegrationProjectWizardRuntimeAndCamelPage page;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime runtime;
	@Rule
	public  TemporaryFolder tmpFolder = new TemporaryFolder();
	private File fakeEAPCamelFolder;
	
	@Before
	public void setup() throws IOException{
		fakeEAPCamelFolder = tmpFolder.newFolder("fakeEAPCamelFolder");
		new File(fakeEAPCamelFolder, "camel-core-2.17.3.jar").createNewFile();
		doCallRealMethod().when(page).validate();
		doCallRealMethod().when(page).getErrorMessage();
		doCallRealMethod().when(page).determineRuntimeCamelVersion(runtime);
		doCallRealMethod().when(page).setErrorMessage(Mockito.any());
		doReturn("2.17.3").when(page).getSelectedCamelVersion();
	}
	
	@Test
	public void testValidationMessageForRuntimeWithoutCamel() throws Exception {
		doReturn(runtime).when(page).getSelectedRuntime();
		when(runtime.getRuntimeType().getId()).thenReturn(RuntimeCamelVersionFinder.FUSE_RUNTIME_PREFIX);
		page.validate();
		verify(page).setErrorMessage(Messages.fuseIntegrationProjectWizardRuntimeAndCamelPageWarningMessageWhenCamelVersionCannotBeDeterminedInRuntime);
	}
	
	@Test
	public void testValidationMessageClearedWhenNoRuntimeCalledAndValidCamelVersionSyntax() throws Exception {
		doReturn(null).when(page).getSelectedRuntime();

		doReturn("2.0.0").when(page).getSelectedCamelVersion();
		
		page.validate();
		
		verify(page).setErrorMessage(null);
	}
	
	@Test
	public void testValidationMessageAppearedForInvalidCamelVersionSyntax() throws Exception {
		doReturn(null).when(page).getSelectedRuntime();
		doReturn("invalidVersion").when(page).getSelectedCamelVersion();
		
		page.validate();
		
		assertThat(page.getErrorMessage()).isNotNull();
	}
	
	@Test
	public void testValidationMessageClearedWhenRuntimeWithCorrectCamel() throws Exception {
		doReturn(runtime).when(page).getSelectedRuntime();
		when(runtime.getRuntimeType().getId()).thenReturn(RuntimeCamelVersionFinder.EAP_RUNTIME_PREFIX);
		when(runtime.getLocation().append("modules").append("system").append("layers").append("fuse").append("org").append("apache").append("camel").append("core").append("main").toFile()).thenReturn(fakeEAPCamelFolder);
		
		page.validate();
		
		verify(page).setErrorMessage(null);
	}

}
