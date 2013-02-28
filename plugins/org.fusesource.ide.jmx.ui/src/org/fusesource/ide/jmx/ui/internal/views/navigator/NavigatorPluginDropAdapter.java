/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.views.navigator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.PluginDropAdapter;

public class NavigatorPluginDropAdapter extends PluginDropAdapter {

	public NavigatorPluginDropAdapter(StructuredViewer viewer) {
		super(viewer);
	}

	@Override
	public void drop(DropTargetEvent event) {
		super.drop(event);
	}

	@Override
	public boolean performDrop(Object data) {
		return super.performDrop(data);
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;
		//return super.validateDrop(target, operation, transferType);
	}

	

}
