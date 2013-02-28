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

package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.Messages;


public abstract class CloudDetailsDeleteAction extends Action {

	public CloudDetailsDeleteAction() {
		super(Messages.jcloud_deleteCloudButton);
		setToolTipText(Messages.jcloud_deleteCloudButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("delete.gif"));
	}

	protected abstract CloudDetails getSelectedCloudDetails();

	@Override
	public void run() {
		CloudDetails details = getSelectedCloudDetails();
		if (details != null) {
			details.delete();
			// If we're still trying to load details, stop.
			CloudDetailsCachedData cachedData = CloudDetailsCachedData.getInstance(details);
			if (cachedData != null) {
				cachedData.cancelLoadingJobs();
			}
			CloudDetails.getCloudDetailList().remove(details);
		}
	}

}
