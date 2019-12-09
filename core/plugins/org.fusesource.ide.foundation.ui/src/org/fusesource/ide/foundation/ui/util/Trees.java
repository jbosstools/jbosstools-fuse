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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;


public class Trees {

	public static void disposeColumns(TreeViewer viewer) {
		if (viewer != null) {
			Tree tree = viewer.getTree();
			disposeColumns(tree);
		}
	}

	public static void disposeColumns(Tree tree) {
		if (tree != null) {
			TreeColumn[] columns = tree.getColumns();
			if (columns != null) {
				for (TreeColumn column : columns) {
					Widgets.dispose(column);
				}
			}
		}
	}

	public static void expandAll(Tree tree) {
		TreeItem[] items = tree.getItems();
		for (TreeItem item : items) {
			expandAll(item);
		}
		tree.update();
	}

	public static void expandAll(TreeItem item) {
		item.setExpanded(true);
		TreeItem[] items = item.getItems();
		for (TreeItem child : items) {
			expandAll(child);
		}
	}

	public static TreeColumn[] getColumns(TreeViewer viewer) {
		if (viewer != null) {
			Tree tree = viewer.getTree();
			if (tree != null) {
				TreeColumn[] columnArray = tree.getColumns();
				if (columnArray != null) {
					return columnArray;
				}
			}
		}
		return new TreeColumn[0];
	}

	public static TreeViewerColumn getTreeViewerColumn(TreeColumn column) {
		return (TreeViewerColumn) column.getData("org.eclipse.jface.columnViewer");
	}




}
