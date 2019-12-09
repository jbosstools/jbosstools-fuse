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

package org.fusesource.ide.foundation.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.Selections;


public class ConnectDisconnectAction extends Action {

	private final StructuredViewer viewer;

	public ConnectDisconnectAction(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public String getText() {
		if (shouldConnect()) {
			return Messages.connectLabel;
		} else {
			return Messages.disconnectLabel;
		}
	}

	@Override
	public void run() {
		IConnectable connectable = getConnectable();
		if (connectable != null) {
			String message = null;
			try {
				if (shouldConnect()) {
					message = "connecting";
					connectable.connect();
				} else {
					message = "disconnecting";
					connectable.disconnect();
				}
			} catch (Exception e) {
				onConnectionError(message, e);
			}
		}
	}


	protected void onConnectionError(String kind, Exception e) {
		FoundationUIActivator.pluginLog().logWarning("Failed to " + kind + ". " + e, e);
	}

	@Override
	public boolean isEnabled() {
		return getConnectable() != null;
	}

	public boolean shouldConnect() {
		IConnectable connectable = getConnectable();
		return connectable != null && connectable.shouldConnect();
	}

	public IConnectable getConnectable() {
		Object selection = Selections.getFirstSelection(viewer);
		if (selection instanceof IConnectable) {
			return (IConnectable) selection;
		}
		return null;
	}

	public void refresh() {
		setText(getText());
	}
}
