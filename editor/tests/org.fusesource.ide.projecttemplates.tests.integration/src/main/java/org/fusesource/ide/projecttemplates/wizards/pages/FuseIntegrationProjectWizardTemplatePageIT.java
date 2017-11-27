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

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectWizard;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateModel;
import org.junit.Test;

/**
 * @author lhein
 *
 */
public class FuseIntegrationProjectWizardTemplatePageIT {

	@Test
	public void testDSLButtonsIntellisense() throws Exception {
		FuseIntegrationProjectWizard wiz = new FuseIntegrationProjectWizard();
		WizardDialog dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		FuseIntegrationProjectWizardTemplatePage page = getWizardTemplatePage(wiz);
		
		TemplateModel tm = page.getTemplates();
		List<CategoryItem> cats = tm.getTemplateCategories();
		
		TemplateItem cbrTemplate = findCBRTemplate(cats);
		assertThat(cbrTemplate).isNotNull();
		
		TemplateItem eapTemplate = findEAPTemplate(cats);
		assertThat(eapTemplate).isNotNull();
		
		page.getBtnTemplateProject().setSelection(true);
		assertThat(page.getBtnTemplateProject().getSelection()).isTrue();
		
		page.getListTemplates().getViewer().setSelection(new StructuredSelection(cbrTemplate));
		assertThat(page.getBtnJavaDSL().isEnabled()).isTrue();
		
		page.getBtnJavaDSL().setSelection(true);
		assertThat(page.getBtnJavaDSL().getSelection()).isTrue();
		
		page.getListTemplates().getViewer().setSelection(new StructuredSelection(eapTemplate));
		assertThat(page.getBtnBlueprintDSL().getSelection()).isTrue();
	}

	private TemplateItem findEAPTemplate(List<CategoryItem> cats) {
		for (CategoryItem cat : cats) {
			if (cat.getId().equalsIgnoreCase("fuse.projecttemplates.eap")) {
				for (CategoryItem subCat : cat.getSubCategories()) {
					if (subCat.getId().equals("fuse.projecttemplates.eap.medium")) {
						List<TemplateItem> templates = subCat.getTemplates();
						for (TemplateItem ti : templates) {
							if (ti.getId().equals("org.fusesource.ide.projecttemplates.eapSpringTemplateMediumv7")) {
								return ti;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private TemplateItem findCBRTemplate(List<CategoryItem> cats) {
		for (CategoryItem cat : cats) {
			if (cat.getId().equalsIgnoreCase("fuse.projecttemplates.jbossfuse")) {
				for (CategoryItem subCat : cat.getSubCategories()) {
					if (subCat.getId().equals("fuse.projecttemplates.jbossfuse.simple")) {
						List<TemplateItem> templates = subCat.getTemplates();
						for (TemplateItem ti : templates) {
							if (ti.getId().equals("org.fusesource.ide.projecttemplates.cbrTemplateSimplev7")) {
								return ti;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private FuseIntegrationProjectWizardTemplatePage getWizardTemplatePage(FuseIntegrationProjectWizard wiz) {
		FuseIntegrationProjectWizardTemplatePage page = null;
		for (IWizardPage p : wiz.getPages()) {
			if (p instanceof FuseIntegrationProjectWizardTemplatePage) {
				page = (FuseIntegrationProjectWizardTemplatePage)p;
				break;
			}
		}
		assertThat(page).isNotNull();
		return page;
	}
}
