package org.fusesource.ide.commons.ui.chart;

import java.util.ArrayList;
import java.util.List;

/**
 * The options from the table which can be used to show a chart.
 */
public class TableChartOptions {
	private List<TableChartColumnInfo> numericColumns = new ArrayList<TableChartColumnInfo>();

	public void addNumericColumn(TableChartColumnInfo labelProvider) {
		numericColumns.add(labelProvider);
	}


	public List<TableChartColumnInfo> getNumericColumns() {
		return numericColumns;
	}

	public boolean isValid() {
		return numericColumns.size() > 0;
	}
}
