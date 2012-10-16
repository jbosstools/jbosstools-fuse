/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.commons.ui.Trees;
import org.fusesource.ide.commons.ui.config.ColumnConfiguration;


/**
 * The call tree filtered tree.
 */
public class CallTreeFilteredTree extends AbstractFilteredTree {

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param actionBars
	 *            The action bars
	 */
	public CallTreeFilteredTree(Composite parent, IActionBars actionBars) {
		super(parent, actionBars);
	}

	/*
	 * @see IConfigurableColumn#getColumns()
	 */
	@Override
	public List<String> getColumns() {
		ArrayList<String> columnLabels = new ArrayList<String>();
		for (CallTreeColumn column : CallTreeColumn.values()) {
			columnLabels.add(column.label);
		}
		return columnLabels;
	}

	public void updateColumnConfiguration() {
		configureTree();
	}

	/*
	 * @see IConfigurableColumn#getDefaultVisibility(String)
	 */
	@Override
	public boolean getDefaultVisibility(String column) {
		return true;
	}

	/*
	 * @see AbstractFilteredTree#getViewerType()
	 */
	@Override
	public ViewerType getViewerType() {
		return ViewerType.CallTree;
	}

	/*
	 * @see AbstractFilteredTree#configureTree()
	 */
	@Override
	protected void configureTree() {
		Trees.disposeColumns(getViewer());

		getViewer().getTree().setLinesVisible(true);
		getViewer().getTree().setHeaderVisible(true);

		List<ColumnConfiguration> columns = getConfiguration().getColumnConfigurations();
		for (ColumnConfiguration config : columns) {
			if (!config.isVisible()) {
				continue;
			}
			CallTreeColumn column = CallTreeColumn.getColumn(config.getName());

			TreeColumn treeColumn = new TreeColumn(getViewer().getTree(),
					SWT.NONE);
			treeColumn.setText(column.label);
			treeColumn.setWidth(column.defalutWidth);
			treeColumn.setAlignment(column.alignment);
			treeColumn.setToolTipText(column.toolTip);
		}
		getConfiguration().addColumnListeners(getViewer());
	}
}
