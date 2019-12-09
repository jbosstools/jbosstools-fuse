/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.wizards.pages.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;

/**
 * @author lhein
 *
 */
public class TemplateLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof CategoryItem) {
			return ((CategoryItem)element).getName();
		} else if (element instanceof TemplateItem) {
			TemplateItem templateItem = (TemplateItem)element;
			return templateItem.getName() + computeDslSuffix(templateItem);
		}
		return element.toString();
	}
	
	private String computeDslSuffix(TemplateItem templateItem) {
		String dslName = templateItem.getDslType().name();
		String dslNameFormatted = dslName.substring(0, 1).toUpperCase() + dslName.substring(1, dslName.length()).toLowerCase();
		return " - "+dslNameFormatted+" DSL";
	}
}
