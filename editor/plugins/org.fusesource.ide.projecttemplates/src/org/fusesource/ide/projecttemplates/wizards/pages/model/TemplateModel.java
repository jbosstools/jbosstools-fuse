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
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
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
	
	private static final String PROJECT_TEMPLATE_PROVIDER_ELEMENT = "projectTemplate";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_ID = "id";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_NAME = "name";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_KEYWORDS = "keywords";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_DESCRIPTION = "description";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_CATEGORY = "category";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_CONFIGURATOR = "class";
	private static final String PROJECT_TEMPLATE_PROVIDER_ATTR_WEIGHT = "weight";

	private List<TemplateOrCategoryItem> templateRoots = new ArrayList<>();

	private NameAndWeightComparator comparator = new NameAndWeightComparator();
	
	public TemplateModel() {
		initialize();
	}
	
	private void initialize() {
		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(PROJECT_TEMPLATE_EXT_POINT_ID);
		// first read all categories
		for (IConfigurationElement e : extensions) {
			if (PROJECT_TEMPLATE_CATEGORY_ELEMENT.equals(e.getName())) {
				determineCategoryExtension(e);
			} 
		}
		// then read and assign all template providers
		for (IConfigurationElement e : extensions) {
			if (PROJECT_TEMPLATE_PROVIDER_ELEMENT.equals(e.getName())) {
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
				String keywords = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_KEYWORDS);
				String weight = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_WEIGHT);
				int iWeight = 10;
				try {
					iWeight = Integer.parseInt(weight);
				} catch (NumberFormatException ex) {
					ProjectTemplatesActivator.pluginLog().logError("Error in Template Provider definition for ID: " + id, ex);
				}
				String catId = e.getAttribute(PROJECT_TEMPLATE_PROVIDER_ATTR_CATEGORY);
				CategoryItem category = getCategory(catId);
				createTemplateForEachDSL(template, id, name, description, keywords, iWeight, category);
			}
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}
	}

	private void createTemplateForEachDSL(AbstractProjectTemplate template, String id, String name, String description, String keywords, int iWeight, CategoryItem category) {
		for (CamelDSLType dslType : CamelDSLType.values()) {
			if (template.supportsDSL(dslType)) {
				TemplateItem item = new TemplateItem(new TemplateItemIdentity(id, name, description, keywords), iWeight, category, template, dslType);
				if (category !=null) {
					category.addTemplate(item);
				} else {
					templateRoots.add(item);
				}
			}
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
			int iWeight = getWeight(e, id);
			CategoryItem item = new CategoryItem(id, name, iWeight, parent);
			templateRoots.add(item);
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}
		
		// now assign the sub categories
		Iterator<TemplateOrCategoryItem> templateOrCategoryItems = templateRoots.iterator();
		while (templateOrCategoryItems.hasNext()) {
			TemplateOrCategoryItem templateOrCategoryItem = templateOrCategoryItems.next();
			if (templateOrCategoryItem instanceof CategoryItem) {
				CategoryItem categoryItem = (CategoryItem) templateOrCategoryItem;
				String parentCategory = categoryItem.getParent();
				if (!Strings.isBlank(parentCategory)){
					CategoryItem parentCat = getCategory(parentCategory);
					if (parentCat != null) {
						parentCat.addSubCategory(categoryItem);
					}
					templateOrCategoryItems.remove();
				}
			} else {
				templateOrCategoryItems.remove();
			}
		}
		
		// now sort the root list of categories
		Collections.sort(templateRoots, comparator);
	}

	private int getWeight(IConfigurationElement e, String id) {
		String weight = e.getAttribute(PROJECT_TEMPLATE_CATEGORY_ATTR_WEIGHT);
		int iWeight = 10;
		try {
			iWeight = Integer.parseInt(weight);
		} catch (NumberFormatException ex) {
			ProjectTemplatesActivator.pluginLog().logError("Error in Template Category definition for ID: " + id, ex);
		}
		return iWeight;
	}

	private CategoryItem getCategory(String id) {
		return findCategory(templateRoots, id);
	}
	
	private CategoryItem findCategory(List<? extends TemplateOrCategoryItem> templateOrCategoryItems, String catId) {
		for (TemplateOrCategoryItem templateOrCategoryItem : templateOrCategoryItems) {
			if(templateOrCategoryItem instanceof CategoryItem)  {
				CategoryItem categoryItem = (CategoryItem)templateOrCategoryItem;
				if (categoryItem.getId().equals(catId)) {
					return (CategoryItem)templateOrCategoryItem;
				}
				List<CategoryItem> subCategories = categoryItem.getSubCategories();
				if (!subCategories.isEmpty()) {
					CategoryItem catItemFoundInSubCategory = findCategory(subCategories, catId);
					if (catItemFoundInSubCategory != null) {
						return catItemFoundInSubCategory;
					}
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
	public List<TemplateOrCategoryItem> getRootTemplates() {
		return this.templateRoots;
	}
}
