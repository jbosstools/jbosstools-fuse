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
import org.junit.Test;

/**
 * @author lhein
 *
 */
public class FuseIntegrationWizardRuntimeAndCamelPageIT {
	
	@Test
	public void testCompatibleCamelVersionSelectedForUnsupportedBrandedVersion() throws Exception {
		FuseIntegrationProjectWizard wiz = new FuseIntegrationProjectWizard();
		WizardDialog dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		FuseIntegrationProjectWizardRuntimeAndCamelPage page = getWizardRuntimePage(wiz);
		
		page.preselectCamelVersionForRuntime("2.17.0.redhat-630175");
		String selectedCamelVersion = page.getSelectedCamelVersion();
		assertThat(selectedCamelVersion.startsWith("2.17.0.redhat-630"));
	}
	
	@Test
	public void testCompatibleCamelVersionSelectedForUnsupportedCommunityVersion() throws Exception {
		FuseIntegrationProjectWizard wiz = new FuseIntegrationProjectWizard();
		WizardDialog dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		FuseIntegrationProjectWizardRuntimeAndCamelPage page = getWizardRuntimePage(wiz);
		
		page.preselectCamelVersionForRuntime("2.17.0");
		String selectedCamelVersion = page.getSelectedCamelVersion();
		assertThat(selectedCamelVersion.startsWith("2.17.3"));
	}

	@Test
	public void testExactCamelVersionSelectedForBrandedVersion() throws Exception {
		FuseIntegrationProjectWizard wiz = new FuseIntegrationProjectWizard();
		WizardDialog dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		FuseIntegrationProjectWizardRuntimeAndCamelPage page = getWizardRuntimePage(wiz);
		
		page.preselectCamelVersionForRuntime("2.17.0.redhat-630187");
		String selectedCamelVersion = page.getSelectedCamelVersion();
		assertThat(selectedCamelVersion.equalsIgnoreCase("2.17.0.redhat-630187"));
	}
	
	private FuseIntegrationProjectWizardRuntimeAndCamelPage getWizardRuntimePage(FuseIntegrationProjectWizard wiz) {
		FuseIntegrationProjectWizardRuntimeAndCamelPage page = null;
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
