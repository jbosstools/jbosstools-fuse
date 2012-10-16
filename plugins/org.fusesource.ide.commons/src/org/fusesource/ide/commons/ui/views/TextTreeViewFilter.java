package org.fusesource.ide.commons.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.fusesource.ide.commons.util.TextFilter;
import org.fusesource.ide.commons.util.TextFilters;


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
