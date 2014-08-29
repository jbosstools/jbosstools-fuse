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
package org.fusesource.ide.jmx.fabric8.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.navigator.properties.ContainerTableSheetPage;
import org.jboss.tools.jmx.ui.ImageProvider;

/**
 * @author lhein
 */
public class ContainersNode extends RefreshableCollectionNode implements
		ImageProvider {
	
	private Fabric8Node parent;
	
	public ContainersNode(Fabric8Node parent) {
		super(parent);
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "Containers";
	}

	@Override
	public Image getImage() {
		return Fabric8JMXPlugin.getDefault().getImage("container_folder.png");
	}

	@Override
	protected void loadChildren() {
		ContainerNode[] agents = getContainers();
		if (agents != null) {
			for (ContainerNode agent : agents) {
				addChild(agent);
			}
		}
	}
	
	public ContainerNode getContainerNode(String id) {
		for (ContainerNode node : getContainers()) {
			if (node.getId().equals(id)) {
				return node;
			}
		}
		return null;
	}
	
	private ContainerNode[] getContainers() {
		List<ContainerNode> nodes = new ArrayList<ContainerNode>();
		try {
			List<Map<String, Object>> resultSet = this.parent.getFacade().listContainers();
			for (Map<String, Object> result : resultSet) {
				if (result.get("id") != null) {
					String id = (String)result.get("id");	
					nodes.add(new ContainerNode(this, id, result, parent));
				}
			}
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
		}
		return nodes.toArray(new ContainerNode[nodes.size()]);
	}
	
	public Fabric8Node getFabric() {
		return parent;
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

}
