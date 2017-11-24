/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.impl.simple.AMQTemplate;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplate;
import org.fusesource.ide.projecttemplates.impl.simple.OSESpringBootXMLTemplate;
import org.fusesource.ide.projecttemplates.wizards.pages.FuseIntegrationProjectWizardRuntimeAndCamelPage;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CompatibleCamelVersionFilterTest {
	
	@Mock
	private FuseIntegrationProjectWizardRuntimeAndCamelPage page;
	
	@Test
	public void testAMQCompatibleWith63() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter(CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63);
		TemplateItem templateItem = createTemplateItem(new AMQTemplate());
		assertThat(filter.select(null, null, templateItem)).isTrue();
	}

	@Test
	public void testAMQNotCompatibleWithHigherThan2_20() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter("2.20.0");
		TemplateItem templateItem = createTemplateItem(new AMQTemplate());
		assertThat(filter.select(null, null, templateItem)).isFalse();
	}
	
	@Test
	public void testOpenShiftTemplateNotCompatibleWithLowerThan2_18() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter("2.17.9");
		TemplateItem templateItem = createTemplateItem(new OSESpringBootXMLTemplate());
		assertThat(filter.select(null, null, templateItem)).isFalse();
	}
	
	@Test
	public void testOpenShiftTemplateCompatibleWithHigherThan2_18() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter(CamelCatalogUtils.CAMEL_VERSION_LATEST_FIS_20);
		TemplateItem templateItem = createTemplateItem(new OSESpringBootXMLTemplate());
		assertThat(filter.select(null, null, templateItem)).isTrue();
	}

	@Test
	public void testCategoryItemNotFilteredOutIfContainsChild() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter("2.20.0");
		CategoryItem category = new CategoryItem("id", "name", 0, null);
		createTemplateItemInCategory(new EmptyProjectTemplate(), category);
		assertThat(filter.select(null, null, category)).isTrue();
	}
	
	@Test
	public void testCategoryWithDepthTwoItemNotFilteredOutIfContainsChild() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter("2.20.0");
		CategoryItem category1 = new CategoryItem("id1", "name", 0, null);
		CategoryItem category2 = new CategoryItem("id2", "name", 0, category1.getName());
		category2.setParentCategory(category1);
		category1.addSubCategory(category2);
		TemplateItem templateItem = createTemplateItemInCategory(new EmptyProjectTemplate(), category2);
		assertThat(filter.select(null, null, category1)).isTrue();
		assertThat(filter.select(null, null, category2)).isTrue();
		assertThat(filter.select(null, null, templateItem)).isTrue();
	}
	
	@Test
	public void testCategoryItemFilteredOutIfAllChildrenAlsoFiltered() throws Exception {
		CompatibleCamelVersionFilter filter = createFilter("2.17.0");
		CategoryItem category = new CategoryItem("id", "name", 0, null);
		createTemplateItemInCategory(new OSESpringBootXMLTemplate(), category);
		assertThat(filter.select(null, null, category)).isFalse();
	}

	protected TemplateItem createTemplateItem(AbstractProjectTemplate template) {
		return createTemplateItemInCategory(template, null);
	}
	
	protected TemplateItem createTemplateItemInCategory(AbstractProjectTemplate template, CategoryItem category) {
		TemplateItem templateItem = new TemplateItem("id", "name", "description", 1, category, template, "keywords");
		if (category != null) {
			category.addTemplate(templateItem);
		}
		return templateItem;
	}
	
	protected CompatibleCamelVersionFilter createFilter(String camelVersion) {
		doReturn(camelVersion).when(page).getSelectedCamelVersion();
		return new CompatibleCamelVersionFilter(camelVersion);
	}
}
