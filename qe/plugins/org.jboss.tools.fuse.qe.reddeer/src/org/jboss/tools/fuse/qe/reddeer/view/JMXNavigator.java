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
package org.jboss.tools.fuse.qe.reddeer.view;

import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;

/**
 * Performs operations with JMX Navigator View
 * 
 * @author tsedmik
 */
public class JMXNavigator extends WorkbenchView {

	public static final String TITLE = "JMX Navigator";
	public static final String CONNECT_CONTEXT_MENU = "Connect...";

	private static final Logger log = Logger.getLogger(JMXNavigator.class);

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
	 * Tries to connect to a specified node under <i>Local Processes</i> in
	 * <i>JMX Navigator</i> View.
	 * 
	 * @param name
	 *            prefix name of the node
	 * @return the desired node or null if the searched node is not present
	 */
	public TreeItem connectTo(String name) {

		log.info("Connecting to '" + name + "'.");
		open();
		List<TreeItem> items = new DefaultTree().getItems();

		for (TreeItem item : items) {
			if (item.getText().equals("Local Processes")) {
				item.expand();
				items = item.getItems();
				break;
			}
		}

		for (TreeItem item : items) {
			if (item.getText().contains(name)) {
				item.select();
				AbstractWait.sleep(TimePeriod.getCustom(2));
				try {
					new ContextMenu(CONNECT_CONTEXT_MENU).select();
				} catch (CoreLayerException ex) {
					log.info("Already connected to '" + name + "'.");
				}
				AbstractWait.sleep(TimePeriod.getCustom(2));
				item.expand();
				return item;
			}
		}

		return null;
	}

	/**
	 * Tries to decide if a particular node described with the path is in the
	 * tree in <i>JMX Navigator</i> View (below the node <i>Local Processes</i>
	 * ).
	 * 
	 * @param path
	 *            Path to the desired node (names of nodes in the tree in <i>JMX
	 *            Navigator</i> View without the <i>Local Processes</i> node.
	 * @return The node if exists, <b>null</b> - otherwise
	 */
	public TreeItem getNode(String... path) {

		activate();
		if (path == null)
			return null;
		log.info("Accessing child items of 'Local Processes'");
		List<TreeItem> items = new DefaultTreeItem("Local Processes").getItems();
		log.info("Child items of 'Local Processes' are: " + logTreeItems(items));
		log.info("Trying to identify the right process");
		TreeItem rightItem = null;
		log.info("Looking for '" + path[0] + "' item");
		rightItem = getTreeItem(items, path[0]);
		if (rightItem == null) {
			for (TreeItem item : items) {
				if (path[0].equals("Local Camel Context") && item.getText().startsWith("maven [")
						|| path[0].equals("karaf")
								&& (item.getText().contains("karaf") || item.getText().startsWith("JBoss Fuse"))) {
					item.select();
					item.doubleClick();
					expand(item);
					if (getTreeItem(item.getItems(), "Camel") != null) {
						rightItem = item;
						log.info("The item with Camel Context was found: " + item.getText());
						break;
					}
				}
			}
		} else {
			log.info("'" + path[0] + "' item was found");
			rightItem.select();
			rightItem.doubleClick();
			expand(rightItem);
		}
		if (rightItem == null) {
			log.warn("'" + path[0] + "' item was NOT found");
			return rightItem;
		}
		for (int i = 1; i < path.length; i++) {
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
		new ContextMenu("Refresh").select();
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
			if (item.getText().startsWith(name)) {
				return item;
			}
		}
		return null;
	}
}
