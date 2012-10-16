package org.fusesource.ide.commons.ui.chart;

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
