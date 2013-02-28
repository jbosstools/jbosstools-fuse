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

package org.fusesource.ide.fabric.navigator;

import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;

public class ProfileContainerTableSheetPage extends ContainerTableSheetPage {
	private final ProfileNode node;

	public ProfileContainerTableSheetPage(ProfileNode node) {
		super(node.getFabric());
		this.node = node;

		setCreateChildContainerAction(new CreateChildContainerAction(node));
		setCreateSshContainerAction(new CreateSshContainerAction(node.getVersionNode(), null, node));
		setCreateCloudContainerAction(new CreateJCloudsContainerAction(node.getVersionNode(), null, node));

		updateData();
	}

	@Override
	public void refresh() {
		updateData();
		super.refresh();
	}

	@Override
	protected void updateData() {
		setPropertySources(node.getContainerPropertySourceList());
	}
}
