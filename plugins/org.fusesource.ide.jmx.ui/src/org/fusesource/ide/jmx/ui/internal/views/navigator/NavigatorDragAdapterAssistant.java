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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.navigator.CommonDragAdapterAssistant;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.ResourceTransfer;

public class NavigatorDragAdapterAssistant extends CommonDragAdapterAssistant {

	public NavigatorDragAdapterAssistant() {
	}

	@Override
	public Transfer[] getSupportedTransferTypes() {
		return new Transfer[] { TextTransfer.getInstance(),
				FileTransfer.getInstance(),
				ResourceTransfer.getInstance(),
				EditorInputTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer()};
	}

	
	@Override
	public void dragStart(DragSourceEvent event,
			IStructuredSelection selection) {
		super.dragStart(event, selection);
	}

	@Override
	public void dragFinished(DragSourceEvent event,
			IStructuredSelection selection) {
		super.dragFinished(event, selection);
	}

	@Override
	public boolean setDragData(DragSourceEvent event,
			IStructuredSelection selection) {
		return false;
	}

}
