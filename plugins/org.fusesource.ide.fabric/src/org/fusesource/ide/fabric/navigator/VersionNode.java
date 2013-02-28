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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.graphics.Image;
import org.fusesource.fabric.api.Profile;
import org.fusesource.fabric.api.Version;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.CreateVersionAction;
import org.fusesource.ide.fabric.actions.ProfileAddAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


public class VersionNode extends RefreshableCollectionNode implements HasRefreshableUI, ImageProvider, ContextMenuProvider {
	private Map<String,ProfileNode> map = new HashMap<String, ProfileNode>();
	private Map<String,Set<Profile>> childrenMap = new HashMap<String,Set<Profile>>();

	private final Fabric fabric;
	private final Version version;

	public VersionNode(VersionsNode parent, Version version) {
		super(parent);
		this.fabric = parent.getFabric();
		this.version = version;
		setPropertyBean(version);
	}

	@Override
	public String toString() {
		return version.getName();
	}

	public Version getVersion() {
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
		Profile[] profiles = getVersionProfiles();
		Map<String,Profile> profileMap = new HashMap<String,Profile>();
		for (Profile profile : profiles) {
			String id = profile.getId();
			profileMap.put(id, profile);
			Profile[] parents = profile.getParents();
			for (Profile parent : parents) {
				String pid = parent.getId();
				Set<Profile> childSet = childrenMap.get(pid);
				if (childSet == null) {
					childSet = new HashSet<Profile>();
					childrenMap.put(pid, childSet);
				}
				childSet.add(profile);
			}
		}

		// lets find the nodes with no parents
		for (Profile profile : profiles) {
			Profile[] parents = profile.getParents();
			if (parents == null | parents.length == 0) {
				ProfileNode node = createProfile(this, profile);
				addChild(node);
				/*
				appendChildren(node);
				 */
			}
		}

	}

	public Profile[] getVersionProfiles() {
		return version.getProfiles();
	}

	public List<ProfileNode> getAllProfileNodes() {
		List<ProfileNode> answer = new ArrayList<ProfileNode>();
		Profile[] profiles = getVersionProfiles();
		if (profiles != null) {
			for (Profile profile : profiles) {
				ProfileNode node = getProfileNode(profile);
				if (node != null) {
					answer.add(node);
				}
			}
		}
		Collections.sort(answer);
		return answer;
	}
	public ProfileNode createProfile(Node parent, Profile profile) {
		ProfileNode node = new ProfileNode(this, parent, profile);
		map.put(node.getProfileId(), node);
		return node;
	}


	public Fabric getFabric() {
		return fabric;
	}

	public String getVersionId() {
		return version.getName();
	}

	public ProfileNode getProfileNode(String profileId) {
		checkLoaded();
		ProfileNode answer = map.get(profileId);
		if (answer == null) {
			answer = ProfileNode.getProfileNode(profileId, getProfileChildren());
		}
		return answer;
	}

	public ProfileNode getProfileNode(Profile profile) {
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

	public Set<Profile> getChildProfiles(String profileId) {
		return childrenMap.get(profileId);
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		if (CreateJCloudsContainerAction.createLocalAgents) {
			menu.add(new CreateJCloudsContainerAction(this, null, null));
		}
		if (CreateSshContainerAction.createLocalAgents) {
			menu.add(new CreateSshContainerAction(this, null, null));
		}
		menu.add(new ProfileAddAction(this));
		menu.add(new Separator());
		menu.add(new CreateVersionAction(this));
	}

}
