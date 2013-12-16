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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import io.fabric8.api.FabricService;
import io.fabric8.api.Profile;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.fabric.navigator.VersionNode;


public class ProfileAddAction extends Action {
	private final VersionNode versionNode;
	private ProfileNode profileNode;

	public ProfileAddAction(VersionNode versionNode) {
		super(Messages.profileAddLabel);
		this.versionNode = versionNode;
		setToolTipText(Messages.profileAddTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("new_profile.png"));
	}

	public ProfileAddAction(ProfileNode profileNode) {
		this(profileNode.getVersionNode());
		this.profileNode = profileNode;
	}

	@Override
	public void run() {
		InputDialog dialog = new InputDialog(Shells.getShell(), Messages.profileAddDialogTitle,
				Messages.profileAddDialogMessage, "", null);
		int result = dialog.open();
		if (result == Window.OK) {
			String name = dialog.getValue();
			createProfile(name);
		}
	}

	protected void createProfile(String name) {
		String versionId = versionNode.getVersionId();
		FabricService service = versionNode.getFabric().getFabricService();
		//Profile profile = service.createProfile(versionId, name);
		Profile profile = service.getVersion(versionId).createProfile(name);
		if (profileNode != null) {
			Profile parentProfile = profileNode.getProfile();
			profile.setParents(new Profile[]{ parentProfile });
		}
		versionNode.refresh();
	}

}
