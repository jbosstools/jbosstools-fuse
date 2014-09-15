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

import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.core.dto.VersionSequenceDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.Fabrics;
import org.fusesource.ide.fabric8.ui.navigator.VersionNode;
import org.fusesource.ide.fabric8.ui.navigator.VersionsNode;
import org.jboss.tools.jmx.core.tree.Node;

import com.google.common.base.Supplier;

public class CreateVersionAction extends ActionSupport {
	private Supplier<VersionDTO> oldVersionSupplier;
	private Fabric fabric;

	public CreateVersionAction(final VersionsNode versionsNode) {
		super(Messages.createVersionMenuLabel, Messages.createVersionDescription, FabricPlugin.getDefault().getImageDescriptor("new_version.png"));
		if (versionsNode != null) this.fabric = versionsNode.getFabric();
		setVersioNode(null);
	}

	public CreateVersionAction(final VersionNode versionNode) {
		super(Messages.createVersionMenuLabel, Messages.createVersionDescription, FabricPlugin.getDefault().getImageDescriptor("new_version.png"));
		if (versionNode != null) this.fabric = versionNode.getFabric();
		setVersioNode(versionNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return FabricPlugin.getDefault().getImageDescriptor("version.png");
	}

	@Override
	public void run() {
		String defaultValue = "";
		VersionDTO latestVersion = oldVersionSupplier.get();
		if (latestVersion != null) {
			VersionSequenceDTO next = latestVersion.getVersionSequence().next();
			while (next != null && getVersionNode(next.getName()) != null) {
				next = next.next();
			}
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
	
	protected VersionNode getVersionNode(String version) {
		List<Node> existingVersions = fabric.getVersionsNode().getChildrenList();
		for (Node n : existingVersions) {
			if (n != null && n instanceof VersionNode) {
				if (((VersionNode)n).getVersionId().equals(version)) {
					return (VersionNode)n;
				}
			}
		}
		return null;
	}

	protected void createVersion(String newVersion) {
		VersionDTO oldVersion = oldVersionSupplier.get();
		FabricPlugin.getLogger().debug("Creating new version " + newVersion + " from old " + Fabrics.getVersionName(oldVersion));

		if (oldVersion != null) {
			fabric.getFabricService().createVersion(oldVersion.getId(), newVersion);
		} else {
			fabric.getFabricService().createVersion(newVersion);
		}
		fabric.getVersionsNode().refresh();
	}

	public VersionDTO getLatestVersion() {
		return Fabrics.getLatestVersion(fabric.getFabricService());
	}

	public void setVersioNode(final VersionNode versionNode) {
		oldVersionSupplier = new Supplier<VersionDTO>() {

			@Override
			public VersionDTO get() {
				if (versionNode != null) {
					return versionNode.getVersion();
				}
				return getLatestVersion();
			}
		};
	}
	
	public void setFabric(Fabric fabric) {
		this.fabric = fabric;
	}
}
