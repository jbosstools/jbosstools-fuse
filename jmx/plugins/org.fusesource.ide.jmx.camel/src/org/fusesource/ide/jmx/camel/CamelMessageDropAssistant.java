/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.camel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.fusesource.ide.foundation.ui.drop.DropHandler;
import org.fusesource.ide.jmx.camel.navigator.EndpointNode;

/**
 * @author lhein
 */
public class CamelMessageDropAssistant extends CommonDropAdapterAssistant {

	/**
	 * 
	 */
	public CamelMessageDropAssistant() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public IStatus validateDrop(Object target, int operation,
			TransferData transferType) {
		if (target != null && target instanceof EndpointNode) {
			return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonDropAdapterAssistant#handleDrop(org.eclipse.ui.navigator.CommonDropAdapter, org.eclipse.swt.dnd.DropTargetEvent, java.lang.Object)
	 */
	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter,
			DropTargetEvent aDropTargetEvent, Object aTarget) {
		if (aTarget != null && aTarget instanceof EndpointNode) {
			EndpointNode node = (EndpointNode)aTarget;
			Object data = aDropTargetEvent.data;
			if (isSupported(data)) {
				DropHandler dh = node.createDropHandler(aDropTargetEvent);
				dh.drop(aDropTargetEvent);
				return Status.OK_STATUS;
			}
		}			
		return Status.CANCEL_STATUS;
	}
	
	protected boolean isSupported(Object data) {
		if (data != null) {
			if (data instanceof TreeSelection) {
				TreeSelection sel = (TreeSelection)data;
				Object selItem = sel.getFirstElement();
				return isSupported(selItem);
			} else if (data instanceof IFile) {
				return true;				
			} else {
				System.err.println("[CamelMessageDropAssistant] Unsupported transfer type: " + data.getClass().getName());
			}
		}
		return false;
	}
}
