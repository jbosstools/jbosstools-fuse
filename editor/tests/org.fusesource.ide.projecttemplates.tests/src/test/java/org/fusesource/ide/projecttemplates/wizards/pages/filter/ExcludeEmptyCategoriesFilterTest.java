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
package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.wizards.pages.filter.ExcludeEmptyCategoriesFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.junit.Test;

/**
 * @author lhein
 */
public class ExcludeEmptyCategoriesFilterTest {

	private ExcludeEmptyCategoriesFilter filter = new ExcludeEmptyCategoriesFilter();
	
	@Test
	public void testEmptySingleCategory() throws Exception {
		CategoryItem cat1 = new CategoryItem("a.1", "Cat1", 0, null);
		assertThat(filter.isEmptyCategory(cat1)).isTrue();
	}
	
	@Test
	public void testEmptyNestedCategory() throws Exception {
		CategoryItem cat1 = new CategoryItem("a.1", "Cat1", 0, null);
		CategoryItem cat1_1 = new CategoryItem("a.1.1", "Cat1.1", 0, "a.1");
		cat1.addSubCategory(cat1_1);
		assertThat(filter.isEmptyCategory(cat1)).isTrue();
		assertThat(filter.isEmptyCategory(cat1_1)).isTrue();
	}
	
	@Test
	public void testFilledSingleCategory() throws Exception {
		CategoryItem cat1 = new CategoryItem("a.1", "Cat1", 0, null);
		cat1.addTemplate(new TemplateItem("t1", "test", "test", 0, cat1, null, "test", CamelDSLType.SPRING));
		assertThat(filter.isEmptyCategory(cat1)).isFalse();
	}
	
	@Test
	public void testFilledNestedCategory() throws Exception {
		CategoryItem cat1 = new CategoryItem("a.1", "Cat1", 0, null);
		CategoryItem cat1_1 = new CategoryItem("a.1.1", "Cat1.1", 0, "a.1");
		cat1.addSubCategory(cat1_1);
		cat1_1.addTemplate(new TemplateItem("t1", "test", "test", 0, cat1_1, null, "test", CamelDSLType.SPRING));
		assertThat(filter.isEmptyCategory(cat1)).isFalse();
		assertThat(filter.isEmptyCategory(cat1_1)).isFalse();
	}
	
	@Test
	public void testMixedCategoryAndTemplates() throws Exception {
		CategoryItem cat1 = new CategoryItem("a.1", "Cat1", 0, null);
		CategoryItem cat1_1 = new CategoryItem("a.1.1", "Cat1.1", 0, "a.1");
		cat1.addSubCategory(cat1_1);
		cat1_1.addTemplate(new TemplateItem("t1", "test", "test", 0, cat1_1, null, "test", CamelDSLType.SPRING));
		CategoryItem cat2 = new CategoryItem("a.2", "Cat2", 0, null);
		assertThat(filter.isEmptyCategory(cat1)).isFalse();
		assertThat(filter.isEmptyCategory(cat1_1)).isFalse();
		assertThat(filter.isEmptyCategory(cat2)).isTrue();
	}
}
