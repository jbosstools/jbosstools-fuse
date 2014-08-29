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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.navigator.dto.ProfileBean;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

/**
 * @author lhein
 */
public class ProfileNode extends RefreshableCollectionNode implements HasRefreshableUI, ImageProvider {
	
	private Map<String, ProfileNode> map = new HashMap<String, ProfileNode>();
	private Map<String, Object> data;
	private String id;
	private Fabric8Node fabric;
	private ProfileBean profile;
	
	public ProfileNode(VersionNode parent, String id, Map<String,Object> data, Fabric8Node fabric) {
		super(parent);
		this.id = id;
		this.data = data;
		this.fabric = fabric;
		this.profile = new ProfileBean(this, getVersion(), this.data);
		setPropertyBean(profile);
	}

	public ProfileNode(ProfileNode parent, String id, Map<String,Object> data, Fabric8Node fabric) {
		super(parent);
		this.id = id;
		this.data = data;
		this.fabric = fabric;
		this.profile = new ProfileBean(this, getVersion(), this.data);
		setPropertyBean(profile);
	}
	
	@Override
	public String toString() {
		return getId();
	}
	
	/**
	 * @return the fabric
	 */
	public Fabric8Node getFabric() {
		return this.fabric;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	@Override
	public RefreshableUI getRefreshableUI() {
		return super.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return Fabric8JMXPlugin.getDefault().getImage("profile.png");
	}

	@Override
	protected void loadChildren() {
		map.clear();
		ProfileNode[] profiles = getChildProfiles();
		if (profiles != null) {
			for (ProfileNode profile : profiles) {
				map.put(profile.getId(), profile);
				addChild(profile);
			}
		}
	}
	
	private ProfileNode[] getChildProfiles() {
		ArrayList<ProfileNode> nodes = new ArrayList<ProfileNode>();
		
		if (this.data != null && this.data.get("childIds") != null) {
			ArrayList profileIds = (ArrayList)this.data.get("childIds");
			for (Object id : profileIds) {
				String sId = (String)id;
				Map<String, Object> data = null;
				try {
					data = getFabric().getFacade().queryProfile(sId, getVersion());
				} catch (Exception ex) {
					Fabric8JMXPlugin.getLogger().error(ex);
					data = new HashMap<String, Object>();
				}
				nodes.add(new ProfileNode(this, sId, data, fabric));
			}
		}
		
		return nodes.toArray(new ProfileNode[nodes.size()]);
	}
	
	/**
	 * returns the version this profile belongs to
	 * 
	 * @return	the version or null if not found
	 */
	public String getVersion() {
		String version = null;
		Node versionNode = getParent();
		while (versionNode != null && version == null) {
			if (versionNode instanceof VersionNode) {
				version = ((VersionNode)versionNode).getId();
			} else {
				versionNode = versionNode.getParent();
			}
		}
		return version;
	}
}
