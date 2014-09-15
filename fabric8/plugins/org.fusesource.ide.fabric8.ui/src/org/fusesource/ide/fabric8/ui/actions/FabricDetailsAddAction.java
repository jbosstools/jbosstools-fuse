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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.Fabrics;


public class FabricDetailsAddAction extends Action {

	private final Fabrics fabrics;

	public FabricDetailsAddAction(Fabrics fabrics) {
		super(Messages.fabricAddButton);
		this.fabrics = fabrics;
		setToolTipText(Messages.fabricAddButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("new_fabric.png"));
	}

	@Override
	public void run() {
		FabricDetailsDialog dialog = new FabricDetailsDialog() {

			@Override
			protected void okPressed() {
				FabricDetails details = getFabricDetails();
				addCloud(details);
				super.okPressed();
			}

		};
		dialog.open();
	}

	public void addCloud(FabricDetails details) {
		if (!FabricDetails.getDetailList().contains(details)) {
			Fabric node = fabrics.addFabric(details);
			Viewer viewer = fabrics.getViewer();
			Viewers.expand(viewer, node, 2);
			FabricDetails.getDetailList().add(details);
			onFabricDetailsAdded(details);
		}
	}

	protected void onFabricDetailsAdded(ConfigurationDetails details) {
		RefreshableUI refreshableUI = fabrics.getRefreshableUI();
		// TODO refresh???
		if (refreshableUI instanceof HasViewer) {
			HasViewer v = (HasViewer) refreshableUI;
			v.getViewer().setSelection(new StructuredSelection(details));
		}
	}

}
