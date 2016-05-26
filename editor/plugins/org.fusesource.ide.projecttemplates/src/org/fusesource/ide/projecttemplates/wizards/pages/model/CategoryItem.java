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
package org.fusesource.ide.projecttemplates.wizards.pages.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author lhein
 */
public class CategoryItem {
	private String id;
	private String name;
	private String parent;
	private int weight;
	private CategoryItem parentCategory;
	private List<CategoryItem> subCategories = new ArrayList<>();
	private List<TemplateItem> templates = new ArrayList<>();
	
	/**
	 * creates a template category
	 * 
	 * @param id		the category id
	 * @param name		the category name
	 * @param weight	the weight for sorting
	 * @param parent	the parent category id
	 */
	public CategoryItem(String id, String name, int weight, String parent) {
		this.id = id;
		this.name = name;
		this.weight = weight;
		this.parent = parent;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the weight
	 */
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * @return the parent
	 */
	public String getParent() {
		return this.parent;
	}
	
	/**
	 * @return the templates
	 */
	public List<TemplateItem> getTemplates() {
		return this.templates;
	}
	
	/**
	 * returns a list of all sub categories
	 * 
	 * @return
	 */
	public List<CategoryItem> getSubCategories() {
		return this.subCategories;
	}
	
	/**
	 * adds a template 
	 * 
	 * @param template
	 */
	public void addTemplate(TemplateItem template) {
		if (templates.contains(template) == false) {
			templates.add(template);
			Collections.sort(this.templates, new Comparator<TemplateItem>() {
				/* (non-Javadoc)
				 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
				 */
				@Override
				public int compare(TemplateItem o1, TemplateItem o2) {
					int res = Integer.compare(o1.getWeight(), o2.getWeight());
					if (res == 0) res = o1.getName().compareTo(o2.getName());
					return res;
				}
			});
		}
	}
	
	/**
	 * adds a sub category
	 * 
	 * @param subCategory
	 */
	public void addSubCategory(CategoryItem subCategory) {
		if (subCategories.contains(subCategory) == false) {
			subCategories.add(subCategory);
			subCategory.setParentCategory(this);
			Collections.sort(this.subCategories, new Comparator<CategoryItem>() {
				/* (non-Javadoc)
				 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
				 */
				@Override
				public int compare(CategoryItem o1, CategoryItem o2) {
					int res = Integer.compare(o1.getWeight(), o2.getWeight());
					if (res == 0) res = o1.getName().compareTo(o2.getName());
					return res;
				}
			});
		}
	}
	
	/**
	 * @return the parentCategory
	 */
	public CategoryItem getParentCategory() {
		return this.parentCategory;
	}
	
	/**
	 * @param parentCategory the parentCategory to set
	 */
	public void setParentCategory(CategoryItem parentCategory) {
		this.parentCategory = parentCategory;
	}
}
