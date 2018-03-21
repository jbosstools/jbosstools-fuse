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

import java.util.List;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;

/**
 * @author lhein
 */
public class TemplateItem implements TemplateOrCategoryItem {
	
	private TemplateItemIdentity templateItemIdentity;
	private int weight;
	private CategoryItem category;
	private AbstractProjectTemplate template;
	private CamelDSLType dslType;
	
	public TemplateItem(TemplateItemIdentity templateItemIdentity, int weight, CategoryItem category, AbstractProjectTemplate template, CamelDSLType dslType) {
		this.templateItemIdentity = templateItemIdentity;
		this.weight = weight;
		this.category = category;
		this.template = template;
		this.dslType = dslType;
	}
	
	/**
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return templateItemIdentity.getKeywords();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return templateItemIdentity.getId();
	}
	
	@Override
	public String getName() {
		return templateItemIdentity.getName();
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return templateItemIdentity.getDescription();
	}

	@Override
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * @return the category
	 */
	public CategoryItem getCategory() {
		return this.category;
	}
	
	/**
	 * @return the template
	 */
	public AbstractProjectTemplate getTemplate() {
		return this.template;
	}

	public boolean isCompatible(EnvironmentData environment) {
		return template.isCompatible(environment);
	}

	public CamelDSLType getDslType() {
		return dslType;
	}
}
