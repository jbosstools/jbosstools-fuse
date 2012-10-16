package org.fusesource.ide.commons.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class Tables {

	public static void disposeColumns(TableViewer viewer) {
		if (viewer != null) {
			Table table = viewer.getTable();
			disposeColumns(table);
		}
	}

	public static void disposeColumns(Table table) {
		if (table != null && !table.isDisposed()) {
			TableColumn[] columns = table.getColumns();
			if (columns != null) {
				for (TableColumn column : columns) {
					Widgets.dispose(column);
				}
			}
		}
	}

	public static TableColumn[] getColumns(TableViewer viewer) {
		if (viewer != null) {
			Table table = viewer.getTable();
			if (table != null && !table.isDisposed()) {
				TableColumn[] columnArray = table.getColumns();
				if (columnArray != null) {
					return columnArray;
				}
			}
		}
		return new TableColumn[0];
	}

	public static TableViewerColumn getTableViewerColumn(TableColumn column) {
		return (TableViewerColumn) column.getData("org.eclipse.jface.columnViewer");
	}

}
