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

import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.ui.form.FormDialogSupport;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.ContainerNode;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionNode;


public class CreateSshContainerAction extends ActionSupport {
	public static final boolean createLocalAgents = true;

	private VersionNode versionNode;
	private final ContainerNode agentNode;
	private ProfileNode profileNode;

	public CreateSshContainerAction(VersionNode versionNode, ContainerNode agentNode, ProfileNode selectedProfile) {
		super(Messages.createSshAgentMenuLabel, Messages.createSshAgentToolTip, FabricPlugin.getDefault().getImageDescriptor("new_ssh_container.png"));
		this.versionNode = versionNode;
		this.agentNode = agentNode;
		this.profileNode = selectedProfile;
	}

	public CreateSshContainerAction(Fabric fabric) {
		super(Messages.createSshAgentMenuLabel, Messages.createSshAgentToolTip, FabricPlugin.getDefault().getImageDescriptor("new_ssh_container.png"));
		this.versionNode = fabric != null ? fabric.getDefaultVersionNode() : null;
		this.agentNode = null;
		this.profileNode = null;
	}

	@Override
	public void run() {
		showCreateAgentDialog();
	}

	public ProfileNode getProfileNode() {
		return profileNode;
	}

	public void setProfileNode(ProfileNode profileNode) {
		this.profileNode = profileNode;
	}

    public void setFabric(Fabric fabric) {
    	if (fabric != null) {
    		this.versionNode = fabric.getDefaultVersionNode();
    	}
    }

	protected void showCreateAgentDialog() {
		FormDialogSupport dialog = new CreateSshContainerDialog(versionNode, agentNode, versionNode.getFabric().getNewAgentName(), profileNode);
		dialog.open();
	}
}
