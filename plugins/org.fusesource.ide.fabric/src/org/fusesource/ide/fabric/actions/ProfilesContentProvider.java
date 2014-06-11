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

package org.fusesource.ide.fabric.actions;

import io.fabric8.api.Container;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.fabric.navigator.Profiles;
import org.fusesource.ide.fabric.navigator.VersionNode;
import org.jboss.tools.jmx.core.tree.Node;


public class ProfilesContentProvider implements ITreeContentProvider {
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	@Override
	public Object getParent(Object element) {
		ProfileNode node = Profiles.toProfileNode(element);
		if (node != null) {
			return Profiles.toProfileNode(node.getParent());
		}
		return null;
	}

	@Override
	public Object[] getElements(Object element) {
		if (element instanceof ContainerNode) {
			ContainerNode node = (ContainerNode) element;
			Container agent = node.getContainer();
			return agent.getProfiles();
		}
		if (element instanceof VersionNode) {
			VersionNode version = (VersionNode) element;
			return getProfileChildren(version);
		}
		ProfileNode node = Profiles.toProfileNode(element);
		if (node != null) {
			return getProfileChildren(node);
		}
		return null;
	}

	protected Object[] getProfileChildren(ProfileNode node) {
		return Objects.getArrayOf(getChildrenList(node), ProfileNode.class);
	}

	protected Object[] getProfileChildren(VersionNode version) {
		return Objects.getArrayOf(getChildrenList(version), ProfileNode.class);
	}

	protected List<Node> getChildrenList(Node node) {
		return node.getChildrenList();	
	}

	@Override
	public Object[] getChildren(Object element) {
		ProfileNode node = Profiles.toProfileNode(element);
		if (node != null) {
			return getProfileChildren(node);
		}
		return null;
	}
}