/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.fusesource.ide.foundation.ui.util.TextFilter;
import org.fusesource.ide.foundation.ui.util.TextFilters;


/**
 * A {@link ViewerFilter} for elements implementing {@link TextFilter}
 */
public class TextTreeViewFilter extends ViewerFilter {

	private String searchText;
	private ITreeContentProvider contentProvider;

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public ITreeContentProvider getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(ITreeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean matches = TextFilters.matches(searchText, element);
		if (!matches && contentProvider != null && searchText != null && searchText.length() > 0) {
			Object[] children = contentProvider.getChildren(element);
			for (Object child : children) {
				if (select(viewer, element, child)) {
					return true;
				}
			}
		}
		return matches;
	}

}
