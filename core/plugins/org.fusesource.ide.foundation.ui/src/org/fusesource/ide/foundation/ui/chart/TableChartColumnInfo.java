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

package org.fusesource.ide.foundation.ui.chart;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.swt.widgets.Item;

public class TableChartColumnInfo {
	private final Item column;
	private final CellLabelProvider labelProvider;

	public TableChartColumnInfo(Item column, CellLabelProvider labelProvider) {
		this.column = column;
		this.labelProvider = labelProvider;
	}

	public String getName() {
		return column.getText();
	}

	public Item getColumn() {
		return column;
	}

	public CellLabelProvider getLabelProvider() {
		return labelProvider;
	}



}
