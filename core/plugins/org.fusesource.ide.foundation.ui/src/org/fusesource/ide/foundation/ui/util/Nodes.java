/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.foundation.ui.tree.HasViewer;
import org.fusesource.ide.foundation.ui.tree.Refreshable;
import org.jboss.tools.jmx.core.tree.Node;


public class Nodes {

	public static void refreshParent(final Node node) {
		final Node parent = node.getParent();
		if (parent instanceof Refreshable) {
			final Viewer viewer = getViewer(node);
			final Object firstSelection = Selections.getFirstSelection(viewer);
			final String text = node.toString();
			Refreshable refreshable = (Refreshable) parent;
			refreshable.refresh();
			if (viewer != null) {
				Viewers.async(new Runnable() {
					@Override
					public void run() {
						if (firstSelection == node) {
							selectChild(viewer, parent, text);
						} else {
							addExpanded(viewer, parent, text);
						}
					}});
			}
		} else if (node instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) node;
			refreshable.refresh();
		}

	}

	public static void refreshParentUsingFullPath(Node node) {
		final Node parent = node.getParent();
		if (parent instanceof Refreshable) {
			final LinkedList<String> path = new LinkedList<String>();
			final Node root = getRootAndAppendPath(path, node);
			final Viewer viewer = getViewer(node);
			Refreshable refreshable = (Refreshable) parent;
			refreshable.refresh();
			if (viewer != null) {
				Viewers.async(new Runnable() {
					@Override
					public void run() {
						selectPath(viewer, root, path);
					}});
			}
		} else if (node instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) node;
			refreshable.refresh();
		}

	}



	public static List<String> getTreePath(Node node) {
		LinkedList<String> list = new LinkedList<String>();
		getRootAndAppendPath(list, node);
		return list;
	}


	public static Node getRootAndAppendPath(LinkedList<String> list, Node node) {
		if (node != null) {
			Node parent = node.getParent();
			if (parent != null) {
				list.addFirst(node.toString());
				return getRootAndAppendPath(list, parent);
			}
		}
		return node;
	}

	public static void selectPath(Viewer viewer, Node root, LinkedList<String> path) {
		List<Node> nodes = new ArrayList<Node>();
		Node node = root;
		nodes.add(node);
		for (String text : path) {
			node = findChild(node, text);
			if (node == null) {
				break;
			} else {
				nodes.add(node);
			}
		}
		if (node != null && viewer instanceof AbstractTreeViewer) {
			AbstractTreeViewer tv = (AbstractTreeViewer) viewer;
			Object[] expandElements = nodes.toArray();
			tv.setExpandedElements(expandElements);
			/*
			TreePath[] expandedTreePaths = tv.getExpandedTreePaths();
			boolean found = false;
			if (expandElements.length > 0) {
				Object expandRoot = expandElements[0];
				for (TreePath treePath : expandedTreePaths) {
					int count = treePath.getSegmentCount();
					for (int i = 0; i < count; i++) {
						Object first = treePath.getSegment(i);
						if (first == expandRoot) {
							// TODO do we need to add the previous nodes?
							found = true;
						}

					}
				}
			}
			if (!found) {
				Activator.getLogger().debug("Could not find root expanded path!!");
			}
			 */
			tv.setSelection(new StructuredSelection(node));
		}
	}

	public static void addExpanded(Viewer viewer, Node node, String text) {
		Node child = findChild(node, text);
		if (child != null) {
			Viewers.addExpanded(viewer, child);
		}
	}


	public static void selectChild(Viewer viewer, Node node, String text) {
		Node child = findChild(node, text);
		if (child != null) {
			setSelection(viewer, child);
		}
	}

	public static Node findChild(Node node, String text) {
		Node[] children = node.getChildren();
		if (children != null) {
			for (Node child : children) {
				String childText = child.toString();
				if (text.equals(childText)) {
					return child;
				}
			}
		}
		return null;
	}

	public static void setSelection(Node node, final Node... selectedNodes) {
		Viewer viewer = getViewer(node);
		setSelection(viewer, selectedNodes);
	}



	public static void setSelection(Viewer viewer, final Node... selectedNodes) {
		Viewers.addExpanded(viewer, selectedNodes);
		Viewers.setSelected(viewer, new StructuredSelection(selectedNodes));
	}


	public static Viewer getViewer(Node node) {
		HasViewer hv = null;
//		if (node instanceof HasRefreshableUI) {
//			HasRefreshableUI hr = node;
//			RefreshableUI refreshableUI = hr.getRefreshableUI();
//			if (refreshableUI instanceof HasViewer) {
//				hv = (HasViewer) refreshableUI;
//			}
//		}
		if (hv == null && node instanceof HasViewer) {
			hv = (HasViewer) node;
		}
		Viewer viewer = null;
		if (hv != null) {
			viewer = hv.getViewer();
		}
		return viewer;
	}

	public static void refreshSelection(final Node node) {
		final Viewer viewer = getViewer(node);
		if (viewer != null) {
			if (Selections.selectionIs(viewer, node)) {
				viewer.setSelection(null);
				Viewers.async(new Runnable() {

					@Override
					public void run() {
						viewer.setSelection(new StructuredSelection(node));
					}});
			}
		}
	}

}
