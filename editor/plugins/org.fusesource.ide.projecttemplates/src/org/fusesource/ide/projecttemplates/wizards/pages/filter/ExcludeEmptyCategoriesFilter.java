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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;

/**
 * @author lhein
 */
public class ExcludeEmptyCategoriesFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof CategoryItem) {
			return !isEmptyCategory((CategoryItem)element);
		}
		return true;
	}
	
	public boolean isEmptyCategory(CategoryItem cat) {
		boolean empty = cat.getTemplates().isEmpty();
		if (empty) {
			for (CategoryItem subCat : cat.getSubCategories()) {
				if (!isEmptyCategory(subCat)) {
					return false;
				}
			}
		}
		return empty;
	}
}
