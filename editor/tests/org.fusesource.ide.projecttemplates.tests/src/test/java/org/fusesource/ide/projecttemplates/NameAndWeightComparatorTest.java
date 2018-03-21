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
package org.fusesource.ide.projecttemplates;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.NameAndWeightComparator;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateOrCategoryItem;
import org.junit.Test;

/**
 * @author lhein
 */
public class NameAndWeightComparatorTest {

	private NameAndWeightComparator comp = new NameAndWeightComparator();
	
	/**
	 * tests the case where the name and the weight are equal
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualNameAndWeight() throws Exception {
		TemplateOrCategoryItem o1 = new CategoryItem("o1", "test", 10, null);
		TemplateOrCategoryItem o2 = new CategoryItem("o2", "test", 10, null);
		int res = comp.compare(o1, o2);
		assertThat(res).isEqualTo(0);
	}
	
	/**
	 * tests the case where the weights are equal but names differ
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualWeightDifferentName() throws Exception {
		TemplateOrCategoryItem o1 = new CategoryItem("o1", "test1", 10, null);
		TemplateOrCategoryItem o2 = new CategoryItem("o2", "test2", 10, null);
		int res = comp.compare(o1, o2);
		assertThat(res).isEqualTo(-1);
	}
	
	/**
	 * tests the case where the name and the weight are not equal
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNameAndWeightComparison() throws Exception {
		TemplateOrCategoryItem o1 = new CategoryItem("o1", "item 1", 10, null);
		TemplateOrCategoryItem o2 = new CategoryItem("o1", "item x", 0, null);
		int res = comp.compare(o1, o2);
		assertThat(res).isEqualTo(1);
	}
}
