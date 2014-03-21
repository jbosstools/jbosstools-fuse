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

import io.fabric8.api.Version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateVersionAction;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;

public class VersionsNode extends FabricNodeSupport implements ImageProvider,
		ContextMenuProvider {
	private Map<String, VersionNode> map = new HashMap<String, VersionNode>();

	public VersionsNode(Fabric fabric) {
		super(fabric, fabric);
	}

	@Override
	public String toString() {
		return "Versions";
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("version_folder.png");
	}

	@Override
	protected void loadChildren() {
		map.clear();
		Version[] versions = getFabric().getFabricService().getVersions();
		if (versions != null) {
			for (Version version : versions) {
				VersionNode node = new VersionNode(this, version);
				map.put(node.getVersionId(), node);
				addChild(node);
			}
		}
	}

	public VersionNode getVersionNode(String version) {
		return map.get(version);
	}

	public VersionNode getDefaultVersionNode() {
		checkLoaded();
		List<Node> childrenList = getChildrenList();
		for (Node node : childrenList) {
			if (node instanceof VersionNode) {
				VersionNode answer = (VersionNode) node;
				// force load
				answer.getChildren();
				return answer;
			}
		}
		return null;
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(new CreateVersionAction(this));
	}

}
