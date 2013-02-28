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
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudDetailsAddAction extends Action {

	public CloudDetailsAddAction() {
		super(Messages.jcloud_addCloudButton);
		setToolTipText(Messages.jcloud_addCloudButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("add_obj.gif"));
	}

	@Override
	public void run() {
		CloudDetailsDialog dialog = new CloudDetailsDialog() {

			@Override
			protected void okPressed() {
				ConfigurationDetails details = getCloudDetails();
				addCloud(details);
				super.okPressed();
			}

		};
		dialog.open();
	}

	public void addCloud(ConfigurationDetails details) {
		if (!CloudDetails.getCloudDetailList().contains(details)) {
			details.flush();
			CloudDetails.getCloudDetailList().add(details);
			onCloudDetailsAdded(details);
		}
	}

	protected void onCloudDetailsAdded(ConfigurationDetails details) {
	}

}
