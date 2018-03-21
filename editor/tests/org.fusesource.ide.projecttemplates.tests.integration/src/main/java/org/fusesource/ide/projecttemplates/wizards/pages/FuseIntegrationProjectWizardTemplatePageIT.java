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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectWizard;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateModel;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateOrCategoryItem;
import org.junit.Test;

/**
 * @author lhein
 *
 */
public class FuseIntegrationProjectWizardTemplatePageIT {

	@Test
	public void testTemplatesAvailable() throws Exception {
		FuseIntegrationProjectWizard wiz = new FuseIntegrationProjectWizard();
		WizardDialog dlg = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dlg.create();
		FuseIntegrationProjectWizardTemplatePage page = getWizardTemplatePage(wiz);
		
		TemplateModel tm = page.getTemplates();
		List<TemplateOrCategoryItem> cats = tm.getRootTemplates();
		
		TemplateItem cbrTemplate = findCBRTemplate(cats);
		assertThat(cbrTemplate).isNotNull();
		
		TemplateItem eapTemplate = findEAPTemplate(cats);
		assertThat(eapTemplate).isNotNull();
	}

	private TemplateItem findEAPTemplate(List<TemplateOrCategoryItem> templateOrCategoryItems) {
		for (TemplateOrCategoryItem templateOrCategoryItem : templateOrCategoryItems) {
				if ((templateOrCategoryItem instanceof TemplateItem)
						&& "org.fusesource.ide.projecttemplates.eapSpringTemplateMediumv7".equals(((TemplateItem)templateOrCategoryItem).getId())) {
					return (TemplateItem)templateOrCategoryItem;
				}
		}
		return null;
	}

	private TemplateItem findCBRTemplate(List<TemplateOrCategoryItem> templateOrCategoryItems) {
		for (TemplateOrCategoryItem templateOrCategoryItem : templateOrCategoryItems) {
			if (templateOrCategoryItem instanceof CategoryItem) {
				CategoryItem cat = (CategoryItem) templateOrCategoryItem;
				if ("fuse.projecttemplates.jbossfuse.simple".equals(cat .getId())) {
					List<TemplateItem> templates = cat.getTemplates();
					for (TemplateItem ti : templates) {
						if ("org.fusesource.ide.projecttemplates.cbrTemplateSimplev7".equals(ti.getId())) {
							return ti;
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
