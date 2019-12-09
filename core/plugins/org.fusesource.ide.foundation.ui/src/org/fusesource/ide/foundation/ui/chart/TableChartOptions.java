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

import java.util.ArrayList;
import java.util.List;

/**
 * The options from the table which can be used to show a chart.
 */
public class TableChartOptions {
	private List<TableChartColumnInfo> numericColumns = new ArrayList<>();

	public void addNumericColumn(TableChartColumnInfo labelProvider) {
		numericColumns.add(labelProvider);
	}


	public List<TableChartColumnInfo> getNumericColumns() {
		return numericColumns;
	}

	public boolean isValid() {
		return !numericColumns.isEmpty();
	}
}
