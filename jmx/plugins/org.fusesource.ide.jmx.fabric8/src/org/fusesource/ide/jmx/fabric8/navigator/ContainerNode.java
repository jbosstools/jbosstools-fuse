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

import io.fabric8.api.Container;
import io.fabric8.api.Profile;
import io.fabric8.api.Version;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasOwner;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.jboss.tools.jmx.ui.ImageProvider;

import com.google.common.base.Objects;

/**
 * @author lhein
 */
public class ContainerNode extends RefreshableCollectionNode implements
		HasRefreshableUI, ImageProvider {

	private Fabric8Node fabric;
	private String id;
	private Map<String, Object> values;

	public ContainerNode(RefreshableNode parent, String id,
			Map<String, Object> values, Fabric8Node fabric) {
		super(parent);
		this.id = id;
		this.values = values;
		this.fabric = fabric;

		// setPropertyBean(new ContainerViewBean(agent));
	}

	/**
	 * @return the fabric
	 */
	public Fabric8Node getFabric() {
		return this.fabric;
	}

	@Override
	public Image getImage() {
		return Fabric8JMXPlugin.getDefault().getImage("container.png");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.commons.tree.RefreshableNode#loadChildren()
	 */
	@Override
	protected void loadChildren() {
		// nothing to do
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	public Container getContainer() {
		return null;
	}

	public static ContainerNode toContainerNode(Object object) {
		if (object instanceof ContainerNode) {
			return (ContainerNode) object;
		}
		if (object instanceof HasOwner) {
			HasOwner ho = (HasOwner) object;
			return toContainerNode(ho.getOwner());
		}
		return null;
	}

	public static Container toContainer(Object object) {
		if (object instanceof Container) {
			return (Container) object;
		}
		ContainerNode node = toContainerNode(object);
		if (node != null) {
			return node.getContainer();
		}
		return null;
	}

	public boolean matches(ProfileNode profile) {
		Container ag = getContainer();
		Profile[] profiles = ag.getProfiles();
		for (Profile prof : profiles) {
			if (Objects.equal(prof.getId(), profile.getId())) {
				if (Objects.equal(ag.getVersionId(), profile.getVersion())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean matches(Version version) {
		return Objects.equal(getContainer().getVersion().getId(),
				version.getId());
	}
}
