/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.ide.fabric.navigator.VersionNode;

/**
 * @author lhein
 */
public class DeleteVersionAction extends Action {
	private final Fabric fabric;
	private VersionNode versionNode;
	
	public DeleteVersionAction(final VersionNode versionNode) {
		init();
		this.versionNode = versionNode;
		this.fabric = versionNode.getFabric();
	}

	private void init() {
		setText(Messages.deleteVersionMenuLabel);
		setToolTipText(Messages.deleteVersionDescription);
		setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("delete.gif"));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isDeletePossible();
	}
	
	private boolean isDeletePossible() {
		return false; // currently impossible as the core of fabric doesn't allow it (UnsupportedOperationException)
	}
	
	@Override
	public void run() {
		if (MessageDialog.openQuestion(Shells.getShell(), Messages.deleteVersionDialogTitle, Messages.deleteVersionDialogMessage)) {
			deleteVersion();
		}
	}

	protected void deleteVersion() {
		FabricPlugin.getLogger().debug("Deleting version " + this.versionNode.getVersionId() + "...");
		// temporarily commented out - pleacu fabric.getFabricService().deleteVersion(this.versionNode.getVersionId());
		fabric.getVersionsNode().refresh();
	}
}
