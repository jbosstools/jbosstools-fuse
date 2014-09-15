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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric8.ui.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric8.ui.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.fabric8.ui.navigator.properties.ContainerTableSheetPage;
import org.fusesource.ide.fabric8.ui.view.logs.FabricLogBrowser;
import org.fusesource.ide.fabric8.ui.view.logs.HasLogBrowser;
import org.fusesource.ide.fabric8.ui.view.logs.ILogBrowser;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

public class ContainersNode extends FabricNodeSupport implements ImageProvider, HasLogBrowser {
	
	private ILogBrowser logBrowser;
	
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
		List<ContainerDTO> agents = getFabric().getFabricService().getContainers();
		if (agents != null) {
			IdBasedFabricNode[] answer = new IdBasedFabricNode[agents.size()];
			int idx = 0;
			for (ContainerDTO agent : agents) {
				IdBasedFabricNode agentNode = new ContainerNode(ContainersNode.this, agent);
				addChild(agentNode);
			}
		}
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
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			checkLoaded();
			if (getFabric() != null) {
				return new ContainerTableSheetPage(getFabric());
			}
		}
		return super.getAdapter(adapter);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fusesource.ide.fabric8.core.ui.navigator.FabricNodeSupport#provideContextMenu
	 * (org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void provideContextMenu(IMenuManager menu) {
		CreateChildContainerAction.addIfSingleRootContainer(menu, getFabric());
		menu.add(new CreateJCloudsContainerAction(getFabric()));
		menu.add(new CreateSshContainerAction(getFabric()));
	}
	
	@Override
	public ILogBrowser getLogBrowser() {
		if (logBrowser == null) {
			logBrowser = new FabricLogBrowser(this);
		}
		return logBrowser;
	}
}
