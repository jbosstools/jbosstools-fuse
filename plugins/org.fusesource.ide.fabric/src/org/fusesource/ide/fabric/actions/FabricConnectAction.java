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
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.fabric.FabricConnector;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabric;


/**
 * @author lhein
 */
public class FabricConnectAction extends Action {

	private Fabric fabric;

	public FabricConnectAction(Fabric fabric) {
		super(Messages.fabricConnectButton);
		setToolTipText(Messages.fabricConnectButtonTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("connect.gif"));
		this.fabric = fabric;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		if (this.fabric != null && this.fabric.isConnected() == false) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (this.fabric == null) return;

		Jobs.schedule("Connecting to Fabric at " + fabric.getDetails().getUrls() + " ...", new Runnable() {
			
			@Override
			public void run() {
				// establishing the connection by recreating the connector
				fabric.setConnector(new FabricConnector(fabric));
				fabric.onConnect();
				fabric.refresh();
			}
		});
	}
}
