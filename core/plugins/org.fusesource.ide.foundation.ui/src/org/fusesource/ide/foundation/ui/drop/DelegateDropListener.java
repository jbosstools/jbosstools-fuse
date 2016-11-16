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

package org.fusesource.ide.foundation.ui.drop;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TransferData;

public class DelegateDropListener extends ViewerDropAdapter {

	private final DropTargetListener delegate;

	public DelegateDropListener(Viewer viewer, DropTargetListener delegate) {
		super(viewer);
		this.delegate = delegate;
	}

	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);
		Object target = determineTarget(event);
		String translatedLocation ="";
		switch (location){
		case 1 :
			translatedLocation = "Dropped before the target ";
			break;
		case 2 :
			translatedLocation = "Dropped after the target ";
			break;
		case 3 :
			translatedLocation = "Dropped on the target ";
			break;
		case 4 :
			translatedLocation = "Dropped into nothing ";
			break;
		}

		DropHandler handler = createDropHandler(target, event);
		if (handler != null) {
			handler.drop(event);
		} else {
			super.drop(event);
		}
	}

	@Override
	public boolean performDrop(Object data) {
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {

		DropHandler handler = createDropHandler(target, null);
		if (handler != null) {
			return true;
		}
		return false;

	}

	public static DropHandler createDropHandler(Object target, DropTargetEvent event) {
		if (target instanceof DropHandler) {
			return (DropHandler) target;
		} else if (target instanceof DropHandlerFactory) {
			DropHandlerFactory factory = (DropHandlerFactory) target;
			return factory.createDropHandler(event);
		}
		return null;
	}




}