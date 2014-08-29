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

import io.fabric8.api.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.jboss.tools.jmx.ui.ImageProvider;

/**
 * @author lhein
 */
public class VersionsNode extends RefreshableCollectionNode implements
		ImageProvider {

	private Map<String, VersionNode> map = new HashMap<String, VersionNode>();
	private Fabric8Node parent;
	
	public VersionsNode(Fabric8Node parent) {
		super(parent);
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "Versions";
	}
	
	public Fabric8Node getFabric() {
		return this.parent;
	}

	@Override
	public Image getImage() {
		return Fabric8JMXPlugin.getDefault().getImage("version_folder.png");
	}

	@Override
	protected void loadChildren() {
		map.clear();
		VersionNode[] versions = getVersions();
		if (versions != null) {
			for (VersionNode version : versions) {
				map.put(version.getId(), version);
				addChild(version);
			}
		}
	}
	
	private VersionNode[] getVersions() {
		ArrayList<VersionNode> nodes = new ArrayList<VersionNode>();
		try {
			List<Map<String, Object>> resultSet = this.parent.getFacade().listVersions();
			for (Map<String, Object> result : resultSet) {
				if (result.get("id") != null) {
					String id = (String)result.get("id");
					nodes.add(new VersionNode(this, id, result, getFabric()));
				}
			}
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
		}
		return nodes.toArray(new VersionNode[nodes.size()]);
	}
}
