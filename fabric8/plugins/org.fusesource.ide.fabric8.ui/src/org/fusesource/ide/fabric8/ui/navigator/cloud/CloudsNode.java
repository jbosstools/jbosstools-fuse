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

package org.fusesource.ide.fabric8.ui.navigator.cloud;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.actions.jclouds.CloudDetails;
import org.fusesource.ide.fabric8.ui.actions.jclouds.CloudDetailsAddAction;
import org.jboss.tools.jmx.ui.ImageProvider;

public class CloudsNode extends RefreshableCollectionNode implements ImageProvider, HasRefreshableUI, ContextMenuProvider {
	private final RefreshableUI refreshableUI;
	private List<CloudNode> cloudNodes = new CopyOnWriteArrayList<CloudNode>();
	private CloudDetailsAddAction addAction;

	public CloudsNode(RefreshableUI refreshableUI) {
		super(null);
		this.refreshableUI = refreshableUI;

		addAction = new CloudDetailsAddAction() {
			@Override
			protected void onCloudDetailsAdded(ConfigurationDetails details) {
				RefreshableUI refreshableUI = getRefreshableUI();
				if (refreshableUI instanceof HasViewer) {
					HasViewer v = (HasViewer) refreshableUI;
					v.getViewer().setSelection(new StructuredSelection(details));
				}
			}
		};
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return refreshableUI;
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("cloud_folder.png");
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		menu.add(addAction);
	}

	public void addChangeListener(IChangeListener listener) {
		getCloudDetailList().addChangeListener(listener);
	}

	public void removeChangeListener(IChangeListener listener) {
		getCloudDetailList().removeChangeListener(listener);
	}

	@Override
	protected void loadChildren() {
		WritableList cloudDetailList = getCloudDetailList();
		List<CloudDetails> list = cloudDetailList;
		for (CloudDetails details : list) {
			addCloudNode(new CloudNode(this, details));
		}

		cloudDetailList.addChangeListener(new IChangeListener() {

			@Override
			public void handleChange(ChangeEvent event) {
				refresh();
			}
		});
	}

	public WritableList getCloudDetailList() {
		return CloudDetails.getCloudDetailList();
	}

	@Override
	public String toString() {
		return "Clouds";
	}

	public List<CloudNode> getCloudNodes() {
		return cloudNodes;
	}

	public CloudNode getCloudNode(String name) {
		for (CloudNode cloudNode : cloudNodes) {
			if (cloudNode.getName().equals(name)) {
				return cloudNode;
			}
		}
		return null;
	}


	/**
	 * Adds a new fabric
	 */
	public void addCloudNode(CloudNode fabric) {
		cloudNodes.add(fabric);
		super.addChild(fabric);
	}

}
