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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasOwner;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.ProfileAddAction;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

public class VersionNode extends FabricNodeSupport implements HasRefreshableUI, ImageProvider, ContextMenuProvider {
	private Map<String, ProfileNode> map = new HashMap<String, ProfileNode>();
	private Map<String, Set<ProfileDTO>> childrenMap = new HashMap<String, Set<ProfileDTO>>();

	private final Fabric fabric;
	private final VersionDTO version;

	public VersionNode(VersionsNode parent, VersionDTO version) {
		super(parent, parent.getFabric());
		this.fabric = parent.getFabric();
		this.version = version;
		setPropertyBean(version);
	}

	@Override
	public String toString() {
		return version.getId();
	}

	public VersionDTO getVersion() {
		return version;
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return super.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("version.png");
	}

	@Override
	protected void loadChildren() {
		map.clear();
		childrenMap.clear();
		List<ProfileDTO> profiles = getVersionProfiles();
		Map<String, ProfileDTO> profileMap = new HashMap<String, ProfileDTO>();
		for (ProfileDTO profile : profiles) {
			String id = profile.getId();
			profileMap.put(id, profile);
			List<ProfileDTO> parents = profile.getParents();
			for (ProfileDTO parent : parents) {
				String pid = parent.getId();
				Set<ProfileDTO> childSet = childrenMap.get(pid);
				if (childSet == null) {
					childSet = new HashSet<ProfileDTO>();
					childrenMap.put(pid, childSet);
				}
				childSet.add(profile);
			}
		}

		// lets find the nodes with no parents
		for (ProfileDTO profile : profiles) {
			List<ProfileDTO> parents = profile.getParents();
			if (parents == null || parents.size() == 0) {
				ProfileNode node = createProfile(this, profile);
				addChild(node);
				/*
				 * appendChildren(node);
				 */
			}
		}

	}

	public List<ProfileDTO> getVersionProfiles() {
		return version.getProfiles();
	}

	public List<ProfileNode> getAllProfileNodes() {
		List<ProfileNode> answer = new ArrayList<ProfileNode>();
		List<ProfileDTO> profiles = getVersionProfiles();
		if (profiles != null) {
			for (ProfileDTO profile : profiles) {
				ProfileNode node = getProfileNode(profile);
				if (node != null) {
					answer.add(node);
				}
			}
		}
		Collections.sort(answer);
		return answer;
	}

	public ProfileNode createProfile(Node parent, ProfileDTO profile) {
		ProfileNode node = new ProfileNode(this, parent, profile);
		map.put(node.getProfileId(), node);
		return node;
	}

	public Fabric getFabric() {
		return fabric;
	}

	public String getVersionId() {
		return version.getId();
	}

	public ProfileNode getProfileNode(String profileId) {
		checkLoaded();
		ProfileNode answer = map.get(profileId);
		if (answer == null) {
			answer = ProfileNode
					.getProfileNode(profileId, getProfileChildren());
		}
		return answer;
	}

	public ProfileNode getProfileNode(ProfileDTO profile) {
		return getProfileNode(profile.getId());
	}

	public ProfileNode[] getProfileChildren() {
		return Objects.getArrayOf(getChildrenList(), ProfileNode.class);
	}

	public <T extends Node> T[] getChildrenOf(Class<T> aType) {
		List<Node> list = getChildrenList();
		List<T> answer = new ArrayList<T>(list.size());
		for (Node node : list) {
			if (aType.isInstance(node)) {
				answer.add(aType.cast(node));
			}
		}
		T[] array = (T[]) Array.newInstance(aType, list.size());
		return list.toArray(array);
	}

	public Set<ProfileDTO> getChildProfiles(String profileId) {
		return childrenMap.get(profileId);
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(new ProfileAddAction(this));
	}

	public static VersionNode toVersionNode(Object object) {
		if (object instanceof VersionNode) {
			return (VersionNode) object;
		}
		if (object instanceof HasOwner) {
			HasOwner ho = (HasOwner) object;
			return toVersionNode(ho.getOwner());
		}
		return null;
	}

	public static VersionDTO toVersion(Object object) {
		if (object instanceof VersionDTO) {
			return (VersionDTO) object;
		}
		VersionNode node = toVersionNode(object);
		if (node != null) {
			return node.getVersion();
		}
		return null;
	}
}
