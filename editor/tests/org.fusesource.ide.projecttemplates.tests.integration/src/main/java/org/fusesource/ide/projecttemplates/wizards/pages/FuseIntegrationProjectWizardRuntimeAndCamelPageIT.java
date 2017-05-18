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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wst.server.core.IRuntime;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FuseIntegrationProjectWizardRuntimeAndCamelPageIT {
	
	@Mock
	FuseIntegrationProjectWizardRuntimeAndCamelPage page;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime runtime;
	@Mock
	private StyledText camelInfoText;
	@Mock
	private Combo camelversionCombo;
	@Rule
	public  TemporaryFolder tmpFolder = new TemporaryFolder();
	private File fakeEAPCamelFolder;
	
	@Before
	public void setup() throws IOException{
		fakeEAPCamelFolder = tmpFolder.newFolder("fakeEAPCamelFolder");
		new File(fakeEAPCamelFolder, "camel-core-2.17.3.jar").createNewFile();
		doCallRealMethod().when(page).validate();
		doCallRealMethod().when(page).determineRuntimeCamelVersion(runtime);
		doCallRealMethod().when(page).setCamelInfoText(Mockito.any());
		doCallRealMethod().when(page).setCamelVersionCombo(Mockito.any());
		doReturn(false).when(camelversionCombo).isDisposed();
		page.setCamelVersionCombo(camelversionCombo);
		page.setCamelInfoText(camelInfoText);
		doReturn("2.17.3").when(page).getSelectedCamelVersion();
	}
	
	@Test
	public void testValidationMessageForRuntimeWithoutCamel() throws Exception {
		doReturn(runtime).when(page).getSelectedRuntime();
		when(runtime.getRuntimeType().getId()).thenReturn(RuntimeCamelVersionFinder.FUSE_RUNTIME_PREFIX);
		
		page.validate();
		
		verify(camelInfoText).setText(Messages.FuseIntegrationProjectWizardRuntimeAndCamelPage_WarningMessageWhenCamelVersionCannotBeDeterminedInRuntime);
	}
	
	@Test
	public void testValidationMessageClearedWhenNoRuntimeCalledAndValidCamelVersionSyntax() throws Exception {
		doReturn(null).when(page).getSelectedRuntime();

		doReturn("2.0.0").when(camelversionCombo).getText();
		
		page.validate();
		
		verify(camelInfoText).setText("");
	}
	
	@Test
	public void testValidationMessageAppearedForInvalidCamelVersionSyntax() throws Exception {
		doReturn(null).when(page).getSelectedRuntime();
		doReturn("invalidVersion").when(camelversionCombo).getText();
		
		page.validate();
		
		ArgumentCaptor<String> validationMessageCaptor = ArgumentCaptor.forClass(String.class);
		verify(camelInfoText).setText(validationMessageCaptor.capture());
		assertThat(validationMessageCaptor.getValue()).isNotEmpty();
	}
	
	@Test
	public void testValidationMessageClearedWhenRuntimeWithCorrectCamel() throws Exception {
		doReturn(runtime).when(page).getSelectedRuntime();
		when(runtime.getRuntimeType().getId()).thenReturn(RuntimeCamelVersionFinder.EAP_RUNTIME_PREFIX);
		when(runtime.getLocation().append("modules").append("system").append("layers").append("fuse").append("org").append("apache").append("camel").append("core").append("main").toFile()).thenReturn(fakeEAPCamelFolder);
		
		page.validate();
		
		verify(camelInfoText).setText("");
	}

}
