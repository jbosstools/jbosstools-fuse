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

package org.fusesource.ide.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.TextFilters;


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
