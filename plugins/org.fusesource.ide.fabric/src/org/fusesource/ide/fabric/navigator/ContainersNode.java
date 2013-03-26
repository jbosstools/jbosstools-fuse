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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.fusesource.fabric.api.Container;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricConnector;
import org.fusesource.ide.fabric.FabricPlugin;


public class ContainersNode extends FabricNodeSupport implements ImageProvider {
	public ContainersNode(Fabric fabric) {
		super(fabric, fabric);
	}

	@Override
	public String toString() {
		return "Containers";
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("container_folder.png");
	}

	@Override
	protected void loadChildren() {
		FabricConnector connector = getFabric().getConnector();
		if (connector == null) return;
		Container[] agents = connector.getAgents();
		if (agents != null) {
			IdBasedFabricNode[] answer = new IdBasedFabricNode[agents.length];
			int idx = 0;
			for (Container agent : agents) {
				IdBasedFabricNode agentNode = new ContainerNode(this, agent);
				addChild(agentNode);
			}}
	}

	public ContainerNode getContainerNode(String agentName) {
		List<Node> childrenList = getChildrenList();
		for (Node node : childrenList) {
			if (node instanceof ContainerNode) {
				ContainerNode an = (ContainerNode) node;
				if (Objects.equal(agentName, an.getId())) {
					return an;
				}
			}
		}
		return null;
	}

	@Override
    public boolean requiresContentsPropertyPage() {
        return false;
    }

    @Override
	protected PropertySourceTableSheetPage createPropertySourceTableSheetPage() {
		return new ContainerTableSheetPage(getFabric());
	}

	public List<ContainerNode> getContainerNodes() {
		List<ContainerNode> answer = new ArrayList<ContainerNode>();
		Node[] children = getChildren();
		if (children != null) {
			for (Node node : children) {
				if (node instanceof ContainerNode) {
					answer.add((ContainerNode) node);
				}
			}
		}
		return answer;
	}

}
