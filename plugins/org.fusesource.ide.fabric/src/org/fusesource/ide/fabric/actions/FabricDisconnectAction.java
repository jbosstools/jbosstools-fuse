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

package org.fusesource.ide.fabric.actions;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabric;


/**
 * @author lhein
 */
public class FabricDisconnectAction extends Action {
	
	private Fabric fabric;
	
	public FabricDisconnectAction(Fabric fabric) {
		super(Messages.fabricDisconnectButton);
		setToolTipText(Messages.fabricDisconnectButtonTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("disconnect.gif"));
		this.fabric = fabric;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		if (this.fabric != null && this.fabric.isConnected()) {		
			return true;		
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (this.fabric == null || this.fabric.getConnector() == null) return;
		
		// disposing the connector
		this.fabric.getConnector().dispose();
		this.fabric.setConnector(null);
		this.fabric.onDisconnect();
		this.fabric.refresh();
	}
}
