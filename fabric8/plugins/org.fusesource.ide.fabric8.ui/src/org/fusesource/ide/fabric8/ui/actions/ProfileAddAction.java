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

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionNode;


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
		Fabric8Facade service = versionNode.getFabric().getFabricService();
		//Profile profile = service.createProfile(versionId, name);
		ProfileDTO profile = service.createProfile(versionId, name);
		if (profileNode != null) {
			ProfileDTO parentProfile = profileNode.getProfile();
			profile.setParentIds(Arrays.asList(parentProfile.getId()));
		}
		versionNode.refresh();
	}

}
