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
package org.fusesource.ide.projecttemplates.tests.integration.wizards.pages.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateModel;
import org.junit.Test;

public class TemplateModelIT {
	
	@Test
	public void testInitializationWorks() throws Exception {
		new TemplateModel();
	}
	
	@Test
	public void testCorrectNumberOfCategories() throws Exception {
		//currently only 4: Jboss Fuse, Fuse on EAP, Fuse on OpenShift and Default category
		assertThat(new TemplateModel().getTemplateCategories()).hasSize(4);
	}
	
	@Test
	public void testCorrectMediumFuseContent() throws Exception {
		List<CategoryItem> templateCategories = new TemplateModel().getTemplateCategories();
		CategoryItem fuseOnEapCategoryItem = templateCategories.stream().filter(templateCategory -> "fuse.projecttemplates.eap".equals(templateCategory.getId())).findFirst().get();
		CategoryItem fuseOnEapMediumCategory = fuseOnEapCategoryItem.getSubCategories().stream().filter(templateCategory -> "fuse.projecttemplates.eap.medium".equals(templateCategory.getId())).findFirst().get();
		assertThat(fuseOnEapMediumCategory.getTemplates()).hasSize(1);
	}


}
