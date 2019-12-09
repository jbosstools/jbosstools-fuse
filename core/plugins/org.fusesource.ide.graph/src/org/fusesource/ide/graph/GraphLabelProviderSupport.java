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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.zest.core.viewers.GraphViewer;

public abstract class GraphLabelProviderSupport implements ILabelProvider {

	private final GraphViewer viewer;
	private boolean wrapLabel = true;
	private boolean showIcon = true;

	public GraphLabelProviderSupport(GraphViewer viewer) {
		this.viewer = viewer;
	}

	public GraphViewer getViewer() {
		return viewer;
	}


	public boolean isWrapLabel() {
		return wrapLabel;
	}


	public boolean isShowIcon() {
		return showIcon;
	}


	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
		viewer.refresh(true);
		viewer.getGraphControl().applyLayout();
	}

	public void setWrapLabel(boolean wrapLabel) {
		this.wrapLabel = wrapLabel;
		viewer.refresh(true);
		viewer.getGraphControl().applyLayout();
	}

	@Override
	public void dispose() {
	}

}