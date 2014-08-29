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

import io.fabric8.api.ProfileStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.navigator.actions.OpenWebConsoleAction;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.core.tree.Root;
import org.jboss.tools.jmx.ui.ImageProvider;

/**
 * @author lhein
 */
public class Fabric8Node extends RefreshableCollectionNode implements
		ImageProvider, ContextMenuProvider, GraphableNode {

	private Fabric8JMXFacade facade;
	private ContainersNode containersNode;
	private VersionsNode versionsNode;
	private Set<Runnable> fabricUpdateTasks = new HashSet<Runnable>();

	public Fabric8Node(Root root, Fabric8JMXFacade facade) {
		super(root);
		this.facade = facade;
	}

	@Override
	public String toString() {
		return "Fabric8";
	}

	/**
	 * @return the facade
	 */
	public Fabric8JMXFacade getFacade() {
		return this.facade;
	}

	@Override
	protected void loadChildren() {
		containersNode = new ContainersNode(this);
		versionsNode = new VersionsNode(this);
		addChild(containersNode);
		addChild(versionsNode);
	}

	@Override
	public Image getImage() {
		return Fabric8JMXPlugin.getDefault().getImage("fabric8.png");
	}
	
	/**
	 * @return the containersNode
	 */
	public ContainersNode getContainersNode() {
		return this.containersNode;
	}
	
	/**
	 * @return the versionsNode
	 */
	public VersionsNode getVersionsNode() {
		return this.versionsNode;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.ContextMenuProvider#provideContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(new OpenWebConsoleAction(this));	
	}
	
	@Override
	public List<Node> getChildrenGraph() {
		Set<Node> answer = new HashSet<Node>();
		answer.add(this);
		answer.addAll(getChildrenList());
		return new ArrayList<Node>(answer);
	}
	
	public Collection<ProfileStatus> getProfileStatuses() {
		try {
			return getFacade().queryProfileStatusMap();
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
		}
		return new ArrayList<ProfileStatus>();
	}
	

	public void addFabricUpdateRunnable(Runnable runnable) {
		fabricUpdateTasks.add(runnable);
		Fabric8JMXPlugin.getLogger().debug("=============== Now have " + fabricUpdateTasks.size() + " runnables");
	}

	public void removeFabricUpdateRunnable(Runnable runnable) {
		fabricUpdateTasks.remove(runnable);
	}
}
