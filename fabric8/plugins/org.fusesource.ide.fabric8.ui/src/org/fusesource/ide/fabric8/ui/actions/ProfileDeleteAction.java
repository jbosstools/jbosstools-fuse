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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
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
		ProfileDTO profile = node.getProfile();
		String message = Messages.bind(Messages.deleteProfileMessage, profile.getId());
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), Messages.deleteProfileDialogTitle,
				message);
		if (confirm) {
			node.getFabric().getFabricService().deleteProfile(node.getVersionNode().getVersionId(), profile.getId());
			Node parent = node.getParent();
			if (parent != null){
				parent.removeChild(node);
			}
		}
	}

}
