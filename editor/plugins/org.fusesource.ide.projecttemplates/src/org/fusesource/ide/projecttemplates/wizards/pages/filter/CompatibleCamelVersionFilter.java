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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;

public class CompatibleCamelVersionFilter extends ViewerFilter {
	
	private String camelVersion;

	public CompatibleCamelVersionFilter(String camelVersion) {
		this.setCamelVersion(camelVersion);
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TemplateItem) {
			return ((TemplateItem) element).isCompatible(camelVersion);
		} else if (element instanceof CategoryItem) {
			return hasCompatibleChildren((CategoryItem)element);
		}
		return true;
	}

	private boolean hasCompatibleChildren(CategoryItem category) {
		boolean hasCompatibleTemplate = category.getTemplates().stream()
				.filter(template -> template.isCompatible(camelVersion))
				.findAny()
				.isPresent();
		if (!hasCompatibleTemplate) {
			for (CategoryItem subCat : category.getSubCategories()) {
				if (hasCompatibleChildren(subCat)) {
					return true;
				}
			}
		}
		return hasCompatibleTemplate;
	}

	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}

}
