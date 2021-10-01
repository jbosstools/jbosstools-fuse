/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.condition.TreeItemHasMinChildren;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.menu.ContextMenu;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.fuse.reddeer.condition.ContextMenuHasItem;
import org.jboss.tools.fuse.reddeer.condition.OpenShiftExplorerLoadingIsCompleted;
import org.jboss.tools.fuse.reddeer.condition.OpenShiftPodIsRunning;

/**
 * Represents "OpenShift Explorer" View
 * 
 * @author tsedmik
 */
public class OpenShiftExplorer extends WorkbenchView {

	public static final String TITLE = "OpenShift Explorer";
	public static final String NEW_CONNECTION_TOOLTIP = "Connection...";
	public static final String NEW_CONNECTION_WIZARD = "New OpenShift Connection";

	private DefaultTreeItem selectedConnection;

	public OpenShiftExplorer() {
		super(TITLE);
	}

	/**
	 * Clicks on "New Connection" option in the tool bar
	 */
	public void clickNewConnection() {
		new DefaultToolItem(NEW_CONNECTION_TOOLTIP).click();
		new WaitUntil(new ShellIsAvailable(NEW_CONNECTION_WIZARD));
	}

	/**
	 * Selects given connection
	 * 
	 * @param name
	 *            the whole name of the connection displayed in the OpenShift Explorer View
	 */
	public void selectConnection(String name) {
		selectedConnection = new DefaultTreeItem(new DefaultTree(this), name);
		selectedConnection.select();
	}

	/**
	 * Checks whether OpenShift Explorer contains given connection
	 * 
	 * @param name
	 *            the whole name of the connection in OpenShift Explorer view
	 * @return true - the connection is present, false - otherwise
	 */
	public boolean isConnectionPresent(String name) {

		// If there is no connection available, the link to create a new one is displayed
		try {
			new DefaultLink(this);
			return false;
		} catch (CoreLayerException e) {
			// There are some connections defined. We need to check them.
		}

		for (TreeItem item : new DefaultTree(this).getItems()) {
			if (item.getText().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clicks on the "New --> Project" context menu of selected connection. <br/>
	 * <b>Some connection must be selected!</b>
	 */
	public void clickNewProject() {
		if (selectedConnection != null) {
			new ContextMenuItem(selectedConnection, "New", "Project...").select();
		} else {
			throw new OpenShiftExplorerException("No Connection was selected!");
		}
	}

	/**
	 * Opens Pod Log of given pod defined by the whole path in the OpenShift Explorer.<br/>
	 * Note: It opens the log of the first pod under given deployment.<br/>
	 * Note: The connection will lose its selection
	 * 
	 * @param path
	 *            the whole path in the OpenShift Explorer: $CONNECTION --> $PROJECT --> $DEPLOYMENT
	 */
	public void openPodLog(String... path) {

		TreeItem treeItem = new DefaultTreeItem(new DefaultTree(this), path[0]);
		for (int i = 1; i <= path.length; i++) {
			treeItem.expand();
			new WaitUntil(new TreeItemHasMinChildren(treeItem, 1), TimePeriod.getCustom(20));
			new WaitUntil(new OpenShiftExplorerLoadingIsCompleted(treeItem), TimePeriod.getCustom(20));
			if (i != path.length) {
				String name = path[i];
				treeItem = treeItem.getItems().stream().filter(t -> t.getText().startsWith(name)).findFirst().orElse(null);
			} else {
				treeItem = treeItem.getItems().get(0);
			}
		}
		new WaitUntil(new OpenShiftPodIsRunning(treeItem), TimePeriod.getCustom(600));
		treeItem.select();
		selectedConnection = null;
		new WaitUntil(new ContextMenuHasItem(new ContextMenu(treeItem), "Pod Log..."), TimePeriod.getCustom(20));
		new ContextMenuItem(treeItem, "Pod Log...").select();

		// handle "No pod selected" shell
		new WaitUntil(new ShellIsAvailable("No pod selected"), TimePeriod.DEFAULT, false);
		if (new ShellIsAvailable("No pod selected").test()) {
			new OkButton(new DefaultShell("No pod selected")).click();
			new ContextMenuItem(treeItem, "Pod Log...").select();
		}
	}

	/**
	 * Returns a list of all available projects under the given connection<br/>
	 * <b>Some connection must be selected!</b>
	 * 
	 * @return list of projects
	 */
	public List<String> getAllAvailableProjects() {
		if (selectedConnection != null) {
			selectedConnection.expand();
			new WaitUntil(new TreeItemHasMinChildren(selectedConnection, 1), TimePeriod.getCustom(20));
			new WaitUntil(new OpenShiftExplorerLoadingIsCompleted(selectedConnection), TimePeriod.getCustom(20));
			List<String> tmp = new ArrayList<>();
			for (TreeItem item : selectedConnection.getItems()) {
				tmp.add(item.getText());
			}
			return tmp;
		} else {
			throw new OpenShiftExplorerException("No Connection was selected!");
		}
	}

	/**
	 * Deletes the given project from the selected connection<br/>
	 * <b>Some connection must be selected!</b><br/>
	 * Note: The connection will lose its selection
	 * 
	 * @param openshiftProjectName
	 *            name of the project (complete name which is shown in OpenShift Explorer)
	 */
	public void deleteProject(String openshiftProjectName) {
		for (String project : getAllAvailableProjects()) {
			if (project.equals(openshiftProjectName)) {
				selectedConnection.getItem(project).select();
				selectedConnection = null;
				new ContextMenuItem("Delete").select();
				new WaitUntil(new ShellIsAvailable("Delete OpenShift Project"));
				new OkButton(new DefaultShell("Delete OpenShift Project")).click();
				new WaitWhile(new JobIsRunning(), false);
				return;
			}
		}
		throw new OpenShiftExplorerException(
				"No '" + openshiftProjectName + "' project is available in '" + selectedConnection.getText() + "'");
	}
}
