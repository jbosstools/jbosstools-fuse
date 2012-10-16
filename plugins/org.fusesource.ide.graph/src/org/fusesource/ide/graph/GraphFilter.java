package org.fusesource.ide.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.commons.util.TextFilters;


public class GraphFilter extends ViewerFilter {

	private String searchText;
	private final GraphViewSupport viewer;
	
	public GraphFilter(GraphViewSupport viewer) {
		this.viewer = viewer;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!Strings.isBlank(searchText) && element != null) {
			if (!(element instanceof GraphConnection) && !(element instanceof EntityConnectionData) && canFilterNode(element)) {
				return TextFilters.matches(searchText, element);
			}
		}
		return true;
	}
	
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	protected boolean canFilterNode(Object element) {
		return true;
	}
}
