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

package org.fusesource.ide.fabric8.ui.actions;

import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.fabric8.ui.FabricConnector;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;


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
		return this.fabric != null && this.fabric.isConnected() == false;
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
				try {
					fabric.getConnector().connect();
				} catch (IOException ex) {
					// already thrown
				} finally {
					fabric.refresh();
				}
			}
		});
	}
}
