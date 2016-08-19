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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lhein
 */
public class TemplateModel {
	
	private static final String PROJECT_TEMPLATE_EXT_POINT_ID = "org.fusesource.ide.projecttemplates.templates";
	
	private static final String PROJECT_TEMPLATE_CATEGORY_ELEMENT = "projectTemplateCategory";
	private static final String PROJECT_TEMPLATE_CATEGORY_ATTR_ID = "id";
	private static final String PROJECT_TEMPLATE_CATEGORY_ATTR_NAME = "name";
	private static final String PROJECT_TEMPLATE_CATEGORY_ATTR_PARENT = "parent";
	private static final String PROJECT_TEMPLATE_CATEGORY_ATTR_WEIGHT = "weight";
	private static final String DEFAULT_CAT_ID = "fuse.projecttemplates.DEFAULT_CATEGORY";
	
	private static final String PROJECT_TEMPLATE_PROVIDER_ELEMENT = "projectTemplate";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_ID = "id";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_NAME = "name";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_KEYWORDS = "keywords";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_DESCRIPTION = "description";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_CATEGORY = "category";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_CONFIGURATOR = "class";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_WEIGHT = "weight";

	private List<CategoryItem> templateCategories = new ArrayList<CategoryItem>();

	private NameAndWeightComparator comparator = new NameAndWeightComparator();
	
	/**
	 * 
	 */
	public TemplateModel() {
		initialize();
	}
	
	private void initialize() {
		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(PROJECT_TEMPLATE_EXT_POINT_ID);
		// first read all categories
		for (IConfigurationElement e : extensions) {
			if (e.getName().equals(PROJECT_TEMPLATE_CATEGORY_ELEMENT)) {
				determineCategoryExtension(e);
			} 
		}
		// then read and assign all template providers
		for (IConfigurationElement e : extensions) {
			if (e.getName().equals(PROJECT_TEMPLATE_PROVIDER_ELEMENT)) {
				determineProviderExtension(e);
			} 
		} 
	}
	
	/**
	 * @param e
	 */
	private void determineProviderExtension(IConfigurationElement e) {
		try {
			final Object o = e.createExecutableExtension(PROJECT_TEMPLATE_PROVIDER_ATTR_CONFIGURATOR);
			if (o instanceof AbstractProjectTemplate) {
				AbstractProjectTemplate template = (AbstractProjectTemplate)o;
				String id = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_ID);
				String name = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_NAME);
				String description = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_DESCRIPTION);
				String weight = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_WEIGHT);
				String keywords = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_KEYWORDS);
				int iWeight = 10;
				try {
					iWeight = Integer.parseInt(weight);
				} catch (NumberFormatException ex) {
					ProjectTemplatesActivator.pluginLog().logError("Error in Template Provider definition for ID: " + id, ex);
				}
				String catId = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_CATEGORY);
				CategoryItem category = getCategory(catId);
				if (category == null) {
					category = getCategory(DEFAULT_CAT_ID);
				}
				TemplateItem item = new TemplateItem(id, name, description, iWeight, category, template, keywords);
				category.addTemplate(item);
			}
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}
	}

	/**
	 * @param e
	 */
	private void determineCategoryExtension(IConfigurationElement e) {
		try {
			String id = e.getAttribute(PROJECT_TEMPLATE_CATEGORY_ATTR_ID);
			String name = e.getAttribute(PROJECT_TEMPLATE_CATEGORY_ATTR_NAME);
			String parent = e.getAttribute(PROJECT_TEMPLATE_CATEGORY_ATTR_PARENT);
			String weight = e.getAttribute(PROJECT_TEMPLATE_CATEGORY_ATTR_WEIGHT);
			int iWeight = 10;
			try {
				iWeight = Integer.parseInt(weight);
			} catch (NumberFormatException ex) {
				ProjectTemplatesActivator.pluginLog().logError("Error in Template Category definition for ID: " + id, ex);
			}
			CategoryItem item = new CategoryItem(id, name, iWeight, parent);
			templateCategories.add(item);
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}
		
		// now assign the sub categories
		Iterator<CategoryItem> catIt = templateCategories.iterator();
		while (catIt.hasNext()) {
			CategoryItem cat = catIt.next();
			String parentCategory = cat.getParent();
			if (!Strings.isBlank(parentCategory)){
				CategoryItem parentCat = getCategory(parentCategory);
				parentCat.addSubCategory(cat);
				catIt.remove();
			}
		}
		
		// now sort the root list of categories
		Collections.sort(templateCategories, comparator);
	}

	private CategoryItem getCategory(String id) {
		return findCategory(templateCategories, id);
	}
	
	private CategoryItem findCategory(List<CategoryItem> categories, String catId) {
		for (CategoryItem cat : categories) {
			if (cat.getId().equals(catId)){
				return cat;
			}
			if (!cat.getSubCategories().isEmpty()) {
				CategoryItem catItemFoundInSubCategory = findCategory(cat.getSubCategories(), catId);
				if(catItemFoundInSubCategory != null){
					return catItemFoundInSubCategory;
				}
			}
		}
		return null;
	}
	
	/**
	 * returns all categories
	 * 
	 * @return the templateCategories
	 */
	public List<CategoryItem> getTemplateCategories() {
		return this.templateCategories;
	}
}
