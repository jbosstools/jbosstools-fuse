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
import org.fusesource.fabric.api.Version;
import org.fusesource.fabric.api.VersionSequence;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.ide.fabric.navigator.Fabrics;
import org.fusesource.ide.fabric.navigator.VersionNode;
import org.fusesource.ide.fabric.navigator.VersionsNode;

import com.google.common.base.Supplier;

public class CreateVersionAction extends Action {
	private final Supplier<Version> oldVersionSupplier;
	private final Fabric fabric;

	public CreateVersionAction(final VersionsNode versionsNode) {
		init();
		this.fabric = versionsNode.getFabric();
		oldVersionSupplier = new Supplier<Version>() {

			@Override
			public Version get() {
				return getLatestVersion();
			}
		};
	}

	public CreateVersionAction(final VersionNode versionNode) {
		init();
		this.fabric = versionNode.getFabric();
		oldVersionSupplier = new Supplier<Version>() {

			@Override
			public Version get() {
				return versionNode.getVersion();
			}
		};
	}

	private void init() {
		setText(Messages.createVersionMenuLabel);
		setToolTipText(Messages.createVersionDescription);
		setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("new_version.png"));
	}

	@Override
	public void run() {
		String defaultValue = "";
		Version latestVersion = getLatestVersion();
		if (latestVersion != null) {
			VersionSequence next = latestVersion.getSequence().next();
			if (next != null) {
				defaultValue = next.getName();
			}
		}
		InputDialog dialog = new InputDialog(Shells.getShell(), Messages.createVersionDialogTitle,
				Messages.createVersionDialogMessage, defaultValue, null);
		int result = dialog.open();
		if (result == Window.OK) {
			String newVersion = dialog.getValue();
			createVersion(newVersion);
		}
	}

	protected void createVersion(String newVersion) {
		Version oldVersion = oldVersionSupplier.get();
		FabricPlugin.getLogger().debug("Creating new version " + newVersion + " from old " + Fabrics.getVersionName(oldVersion));

		if (oldVersion != null) {
			fabric.getFabricService().createVersion(oldVersion, newVersion);
		} else {
			fabric.getFabricService().createVersion(newVersion);
		}
		fabric.getVersionsNode().refresh();
	}

	public Version getLatestVersion() {
		return Fabrics.getLatestVersion(fabric.getFabricService());
	}

}
