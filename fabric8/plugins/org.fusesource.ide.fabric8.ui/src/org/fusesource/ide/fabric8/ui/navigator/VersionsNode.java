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

package org.fusesource.ide.fabric8.ui.navigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.CreateVersionAction;
import org.fusesource.ide.fabric8.ui.navigator.properties.VersionsTabViewPage;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

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
		clearChildren();
		map.clear();
		List<VersionDTO> versions = getFabric().getFabricService().getVersions();
		if (versions != null) {
			for (VersionDTO version : versions) {
				VersionNode node = new VersionNode(this, version);
				map.put(node.getVersionId(), node);
				addChild(node);
			}
		}
	}

	public VersionNode getVersionNode(String version) {
		if (map.isEmpty()) loadChildren();
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

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			checkLoaded();
			if (getFabric() != null) {
				return new VersionsTabViewPage(getFabric());
			}
		}
		return super.getAdapter(adapter);
	}
}
