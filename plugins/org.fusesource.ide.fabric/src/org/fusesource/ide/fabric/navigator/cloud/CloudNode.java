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

package org.fusesource.ide.fabric.navigator.cloud;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.jclouds.CloudDetails;
import org.fusesource.ide.fabric.actions.jclouds.CloudDetailsCachedData;
import org.fusesource.ide.fabric.actions.jclouds.CloudDetailsDeleteAction;
import org.fusesource.ide.fabric.actions.jclouds.CloudDetailsEditAction;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.jboss.tools.jmx.ui.ImageProvider;
import org.jclouds.compute.ComputeService;


public class CloudNode extends RefreshableCollectionNode implements ImageProvider, HasRefreshableUI, ContextMenuProvider {
	private final CloudsNode cloudsNode;
	private final CloudDetails details;
	private AtomicBoolean loaded = new AtomicBoolean(false);
	private boolean lazyLoad;
	private CloudDetailsEditAction editAction;
	private CloudDetailsDeleteAction deleteAction;
	private CloudDetailsCachedData cloudData;

	/*
	private AgentsNode agentsNode;
	private VersionsNode versionsNode;
	 */

	public CloudNode(CloudsNode cloudsNode, CloudDetails details) {
		super(cloudsNode);
		this.cloudsNode = cloudsNode;
		this.details = details;
		this.cloudData = CloudDetailsCachedData.getInstance(details);
		setPropertyBean(new CloudDetailsView(details));

		editAction = new CloudDetailsEditAction() {

			@Override
			protected CloudDetails getSelectedCloudDetails() {
				return getDetails();
			}

			@Override
			protected void onCloudDetailsEdited(Object found) {
			}
		};

		// TODO
		// setDoubleClickAction(editAction);

		deleteAction = new CloudDetailsDeleteAction() {
			@Override
			protected CloudDetails getSelectedCloudDetails() {
				return getDetails();
			}
		};

	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(editAction);
		menu.add(deleteAction);
	}


	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new CloudTabViewPage(this);
		}
		return super.getAdapter(adapter);
	}

	public String getName() {
		return details.getName();
	}


	public CloudsNode getCloudsNode() {
		return cloudsNode;
	}

	public ComputeService getComputeService() {
		return cloudData.getComputeClient();
	}

	public CloudDetails getDetails() {
		return details;
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return cloudsNode.getRefreshableUI();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("cloud.png");
	}

	@Override
	protected void loadChildren() {
		/*
		agentsNode = new AgentsNode(this);
		versionsNode = new VersionsNode(this);

		addChild(agentsNode);
		addChild(versionsNode);
		 */
	}

	public WritableList getNodes() {
		if (loaded.compareAndSet(false, true)) {
			reloadNodes();
		}
		return cloudData.getNodePropertyList();
	}

	public void reloadNodes() {
		cloudData.reloadNodes();
	}


}
