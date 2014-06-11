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

import io.fabric8.api.Profile;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.jboss.tools.jmx.core.tree.Node;


public class ProfileDeleteAction extends Action {
	private final ProfileNode node;

	public ProfileDeleteAction(ProfileNode node) {
		super(Messages.profileDeleteLabel);
		this.node = node;
		setToolTipText(Messages.profileDeleteTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("delete.gif"));
	}

	@Override
	public void run() {
		Profile profile = node.getProfile();
		String message = Messages.bind(Messages.deleteProfileMessage, profile.getId());
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), Messages.deleteProfileDialogTitle,
				message);
		if (confirm) {
			node.getFabric().getFabricService().deleteProfile(profile);
			Node parent = node.getParent();
			if (parent != null){
				parent.removeChild(node);
			}
		}
	}

}
