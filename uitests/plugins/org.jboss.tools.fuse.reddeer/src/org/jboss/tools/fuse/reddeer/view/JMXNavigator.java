/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.view;

import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.menu.ContextMenu;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.impl.view.WorkbenchView;

/**
 * Performs operations with JMX Navigator View
 * 
 * @author tsedmik
 */
public class JMXNavigator extends WorkbenchView {

	public static final String TITLE = "JMX Navigator";
	public static final String CONNECT_CONTEXT_MENU = "Connect...";

	public static final Logger log = Logger.getLogger(JMXNavigator.class);

	private boolean shouldCollapseLocalProcesses = true;

	public JMXNavigator() {
		super(TITLE);
	}

	public boolean isShouldCollapseLocalProcesses() {
		return shouldCollapseLocalProcesses;
	}

	public void setShouldCollapseLocalProcesses(boolean shouldCollapseLocalProcesses) {
		this.shouldCollapseLocalProcesses = shouldCollapseLocalProcesses;
	}

	/**
	 * Tries to connect to a specified node in <i>JMX Navigator</i> View.
	 * 
	 * @param name
	 *            prefix name of the node
	 * @return the desired node or null if the searched node is not present
	 */
	public TreeItem connectTo(String... strings) {
		log.info("Connecting to '" + strings + "'.");
		open();
		List<TreeItem> items = new DefaultTree().getItems();
		for (TreeItem item : items) {
			if (item.getText().equals(strings[0])) {
				item.expand();
				items = item.getItems();
				break;
			}
		}
		for (TreeItem item : items) {
			if (item.getText().contains(strings[1])) {
				item.select();
				AbstractWait.sleep(TimePeriod.MEDIUM);
				try {
					new ContextMenuItem(CONNECT_CONTEXT_MENU).select();
				} catch (CoreLayerException ex) {
					log.info("Already connected to '" + strings[1] + "'.");
				}
				AbstractWait.sleep(TimePeriod.MEDIUM);
				item.expand();
				return item;
			}
		}
		return null;
	}

	/**
	 * Tries to decide if a particular node described with the path is in the tree in <i>JMX Navigator</i> View.
	 * 
	 * @param path
	 *            Path to the desired node (names of nodes in the tree in <i>JMX Navigator</i>.
	 * @return The node if exists, <b>null</b> - otherwise
	 */
	public TreeItem getNode(String... path) {
		activate();
		if (path == null || path.length == 0)
			return null;
		log.info("Accessing child items of '" + path[0] + "'");
		List<TreeItem> items = new DefaultTreeItem(path[0]).getItems();
		log.info("Child items of '" + path[0] + "' are: " + logTreeItems(items));
		log.info("Trying to identify the right process");
		TreeItem rightItem = null;
		log.info("Looking for '" + path[1] + "' item");
		rightItem = getTreeItem(items, new RegexMatcher(path[1] + ".*").toString());
		if (rightItem == null) {
			for (TreeItem item : items) {
				String itemName = item.getText();
				if ((path[1].equals("Local Camel Context") && itemName.startsWith("maven [")
						|| path[1].equals("karaf")
								&& (itemName.contains("karaf") || itemName.startsWith("JBoss Fuse") || itemName.startsWith("Red Hat Fuse") || itemName.startsWith("Apache Karaf") || itemName.startsWith("org.apache.karaf.main.Main")) || itemName.contains(path[1]))
						|| path[0].equals("User-Defined Connections")) {
					item.select();
					item.doubleClick();
					expand(item);
					if (getTreeItem(item.getItems(), "Camel") != null) {
						rightItem = item;
						log.info("The item with Camel Context was found: " + itemName);
						break;
					}
				}
			}
		} else {
			log.info("'" + path[1] + "' item was found");
			rightItem.select();
			rightItem.doubleClick();
			expand(rightItem);
		}
		if (rightItem == null) {
			log.warn("'" + path[1] + "' item was NOT found");
			return rightItem;
		}
		for (int i = 2; i < path.length; i++) {
			log.info("Looking for '" + path[i] + "'");
			rightItem = getTreeItem(rightItem.getItems(), path[i]);
			if (rightItem == null) {
				log.warn("'" + path[i] + "' item was NOT found");
				return rightItem;
			}
			rightItem.select();
			if (i < path.length - 1)
				expand(rightItem);
		}
		return rightItem;
	}

	public void refreshNode(String... path) {
		getNode(path);
		new ContextMenuItem("Refresh").select();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public void refreshLocalProcesses() {
		activate();
		TreeItem localProcess = new DefaultTreeItem("Local Processes");
		localProcess.select();
		new ContextMenu(localProcess).getItem("Refresh").select();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	private void expand(TreeItem item) {
		for (int i = 0; i < 10; i++) {
			log.info("Trying to expand '" + item.getText() + "' Tree Item");
			item.expand();
			AbstractWait.sleep(TimePeriod.SHORT);
			if (item.isExpanded()) {
				log.info("Tree Item '" + item.getText() + "' is expanded");
				AbstractWait.sleep(TimePeriod.SHORT);
				return;
			}
			log.error("Tree Item '" + item.getText() + "' is NOT expanded!");
		}
	}

	private String logTreeItems(List<TreeItem> items) {
		StringBuilder output = new StringBuilder();
		output.append("\n");
		for (TreeItem item : items) {
			output.append(item.getText());
			output.append("\n");
		}
		return output.toString();
	}

	private TreeItem getTreeItem(List<TreeItem> items, String name) {
		for (TreeItem item : items) {
			if (item.getText().startsWith(name) || item.getText().toLowerCase().matches(".*" + name.toLowerCase() + ".*")) {
				return item;
			}
		}
		return null;
	}

}
