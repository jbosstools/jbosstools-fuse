package org.fusesource.ide.commons.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.fusesource.ide.commons.util.TextFilter;
import org.fusesource.ide.commons.util.TextFilters;


/**
 * A {@link ViewerFilter} for elements implementing {@link TextFilter}
 */
public class TextViewFilter extends ViewerFilter {

	private String searchText;

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return TextFilters.matches(searchText, element);
	}

}
