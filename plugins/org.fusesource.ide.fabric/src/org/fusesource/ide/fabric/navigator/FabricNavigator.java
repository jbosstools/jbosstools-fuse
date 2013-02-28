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


import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ResourceTransfer;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.UIConstants;
import org.fusesource.ide.commons.ui.drop.DelegateDropListener;
import org.fusesource.ide.commons.ui.drop.DropHandler;
import org.fusesource.ide.deployment.maven.ProjectDropHandler;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.cloud.CloudsNode;
import org.fusesource.ide.jmx.ui.internal.actions.RefreshAction;
import org.fusesource.ide.jmx.ui.internal.views.navigator.Navigator;


public class FabricNavigator extends Navigator {

	public static String ID = UIConstants.FABRIC_EXPLORER_VIEW_ID;

	private Fabrics fabrics = new Fabrics(refreshableUI);
	private CloudsNode clouds = new CloudsNode(refreshableUI);

	@Override
	protected IAdaptable getInitialInput() {
		return this;
	}


	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		CommonViewer viewer = getCommonViewer();
		viewer.setExpandedElements(new Object[] {clouds, fabrics});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonNavigator#createCommonViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		return new CommonViewer(getViewSite().getId(), aParent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL) {

			@Override
			public void addDragSupport(int operations,
					Transfer[] transferTypes,
					DragSourceListener listener) {
				super.addDragSupport(operations, transferTypes, listener);
			}

			@Override
			public void addDropSupport(int operations,
					Transfer[] transferTypes,
					DropTargetListener listener) {

				Transfer[] newTransferTypes = new Transfer[] {
						LocalSelectionTransfer.getTransfer(),
						FileTransfer.getInstance(),
						ResourceTransfer.getInstance(),
						TextTransfer.getInstance() };

				super.addDropSupport(operations, newTransferTypes, new DelegateDropListener(this, listener));
			}
		};
	}

	public CloudsNode getCloudsNode() {
		return clouds;
	}

	public Fabrics getFabrics() {
		ensureDeployViewRegistered();
		return fabrics;
	}

	@Override
	public void fillActionBars() {
		// queryContribution = new QueryContribution(this);
		// getViewSite().getActionBars().getToolBarManager().add(queryContribution);
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		//toolBarManager.add(new NewConnectionAction(this));
		toolBarManager.add(fabrics.getAddAction());
		toolBarManager.add(new RefreshAction(getViewSite().getId()));
		toolBarManager.add(new Separator());
		getViewSite().getActionBars().updateActionBars();
	}


	@Override
	public void appendDeployActions(Menu menu, IResource resource) {
		if (resource instanceof IProject) {
			final IProject project = (IProject) resource;
			boolean added = false;
			// lets find all the connected fabrics and create a menu for each version/profile
			List<Fabric> list = fabrics.getFabrics();
			for (final Fabric fabric : list) {
				if (fabric.isConnected()) {
					VersionsNode versionsNode = fabric.getVersionsNode();
					if (versionsNode != null) {
						Menu fabricMenu = null;
						List<Node> versions = versionsNode.getChildrenList();
						for (Node node : versions) {
							if (node instanceof VersionNode) {
								Menu versionMenu = null;
								VersionNode versionNode = (VersionNode) node;
								List<ProfileNode> profiles = versionNode.getAllProfileNodes();
								for (ProfileNode profileNode : profiles) {
									DropHandler handler = profileNode.createDropHandler(null);
									if (handler instanceof ProjectDropHandler) {
										final ProjectDropHandler projectHandler = (ProjectDropHandler) handler;
										if (fabricMenu == null) {
											fabricMenu = new Menu(menu);
											MenuItem fabricMenuItem = new MenuItem(menu, SWT.CASCADE);
											fabricMenuItem.setMenu(fabricMenu);
											fabricMenuItem.setText(fabric.getName());
											fabricMenuItem.setImage(FabricPlugin.getDefault().getImage("fabric.png"));
										}
										if (versionMenu == null) {
											versionMenu = new Menu(fabricMenu);
											MenuItem versionMenuItem = new MenuItem(fabricMenu, SWT.CASCADE);
											versionMenuItem.setMenu(versionMenu);
											versionMenuItem.setText(versionNode.getVersionId());
											versionMenuItem.setImage(FabricPlugin.getDefault().getImage("version_folder.png"));
										}
										final MenuItem profileItem = new MenuItem(versionMenu, SWT.PUSH);
										profileItem.setText(profileNode.getId());
										profileItem.setImage(FabricPlugin.getDefault().getImage("profile.png"));
										profileItem.setData(profileNode);
										profileItem.addSelectionListener(new SelectionAdapter() {
											@Override
											public void widgetSelected(SelectionEvent e) {
												System.out.println("================= profile: widgetSelected " + profileItem);
												projectHandler.dropProject(project);
											}

											@Override
											public void widgetDefaultSelected(SelectionEvent e) {
												System.out.println("================= profile: widgetDefaultSelected " + profileItem);
											}
										});
										added = true;
									}

								}
							}
						}
					}
				}
			}
			if (added) {
				new MenuItem(menu, SWT.SEPARATOR);
			}
		}
	}


	@Override
	protected String getViewId() {
		return ID;
	}

}
