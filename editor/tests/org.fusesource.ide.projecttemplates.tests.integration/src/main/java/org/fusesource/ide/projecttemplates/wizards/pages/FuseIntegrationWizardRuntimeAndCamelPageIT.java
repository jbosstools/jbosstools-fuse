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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectWizard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 *
 */
public class FuseIntegrationWizardRuntimeAndCamelPageIT {
	
	private WizardDialog dlg;
	private FuseIntegrationProjectWizard wiz;
	private FuseIntegrationProjectWizardRuntimeAndCamelPage page;
	
	@Before
	public void setup() {
		wiz = new FuseIntegrationProjectWizard();
		dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		page = getWizardRuntimePage(wiz);
	}
	
	@After
	public void tearDown() {
		dlg.close();
		wiz.dispose();
	}
	
	@Test
	public void testCamelVersionSelectedForUnsupportedCommunityVersion() throws Exception {
		FuseIntegrationProjectWizardRuntimeAndCamelPage page = createWizardRuntimePage();
		
		page.preselectCamelVersionForRuntime("2.19.0");
		page.validateCamelVersion();
		String selectedCamelVersion = page.getSelectedCamelVersion();
		assertThat(selectedCamelVersion.startsWith("2.19.0")).isTrue();
		assertThat(page.isWarningShown()).isFalse();
	}
	
	@Test
	public void testCamelVersionSelectedForInvalidCommunityVersion() throws Exception {
		FuseIntegrationProjectWizardRuntimeAndCamelPage page = createWizardRuntimePage();
		
		page.preselectCamelVersionForRuntime("2.19.a");
		page.validateCamelVersion();
		String selectedCamelVersion = page.getSelectedCamelVersion();
		assertThat(selectedCamelVersion.startsWith("2.19.a")).isTrue();
		assertThat(page.isWarningShown()).isTrue();
	}
	
	private FuseIntegrationProjectWizardRuntimeAndCamelPage createWizardRuntimePage() {
		wiz = new FuseIntegrationProjectWizard();
		dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		return getWizardRuntimePage(wiz);
	}
	
	private FuseIntegrationProjectWizardRuntimeAndCamelPage getWizardRuntimePage(FuseIntegrationProjectWizard wiz) {
		for (IWizardPage p : wiz.getPages()) {
			if (p instanceof FuseIntegrationProjectWizardRuntimeAndCamelPage) {
				page = (FuseIntegrationProjectWizardRuntimeAndCamelPage)p;
				break;
			}
		}
		assertThat(page).isNotNull();
		return page;
	}
}
