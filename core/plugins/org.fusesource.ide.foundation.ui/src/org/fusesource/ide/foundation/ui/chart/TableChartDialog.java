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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ViewSettingsDialog;
import org.fusesource.ide.foundation.ui.actions.Messages;
import org.fusesource.ide.foundation.ui.views.ColumnViewSupport;


/**
 * The dialog to choose what kind of chart to create
 */
public class TableChartDialog extends ViewSettingsDialog {

	CheckboxTableViewer columnsViewer;

	private final ColumnViewSupport tableView;

	/** The configurable columns. */

	/**
	 * The constructor.
	 * 
	 * @param columns
	 *            The configurable columns
	 */
	public TableChartDialog(ColumnViewSupport tableView) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.tableView = tableView;
	}


	@Override
	protected boolean isResizable() {
		return true;
	}


	/*
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.createChartTitle);
	}

	/*
	 * @see Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite inner = new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		createColumnsViewer(inner);
		createButtons(inner);

		loadPreference();
		applyDialogFont(composite);

		return composite;
	}

	/*
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		// storePreferences();
		super.okPressed();
	}

	/*
	 * @see ViewSettingsDialog#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		//loadPreference();
		columnsViewer.refresh();
	}

	/**
	 * Creates the columns viewer.
	 * 
	 * @param parent
	 *            The parent composite
	 */
	private void createColumnsViewer(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.configureColumnsMessage);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);

		columnsViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER
				| SWT.MULTI | SWT.FULL_SELECTION);
		columnsViewer.getTable()
		.setLayoutData(new GridData(GridData.FILL_BOTH));
		columnsViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof TableChartColumnInfo) {
					TableChartColumnInfo col = (TableChartColumnInfo) element;
					return col.getName();
				}
				return super.getText(element);
			}});
		columnsViewer.setContentProvider(new ArrayContentProvider());
	}

	/**
	 * Creates the buttons.
	 * 
	 * @param parent
	 *            The parent composite
	 */
	private void createButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		Button selectAllButton = new Button(composite, SWT.PUSH);
		selectAllButton.setText(Messages.selectAllLabel);
		setButtonLayoutData(selectAllButton);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				columnsViewer.setAllChecked(true);
			}
		});

		Button deselectAllButton = new Button(composite, SWT.PUSH);
		deselectAllButton.setText(Messages.deselectAllLabel);
		setButtonLayoutData(deselectAllButton);
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				columnsViewer.setAllChecked(false);
			}
		});
	}


	/**
	 * Loads the preference.
	 */
	private void loadPreference() {
		List<TableChartColumnInfo> columnList = tableView.getChartOptions().getNumericColumns();

		List<TableChartColumnInfo> checkedList = new ArrayList<>(columnList.size());
		for (TableChartColumnInfo column : columnList) {
			/*
			if (column.isVisible()) {
				checkedList.add(column);
			}
			 */
		}
		columnsViewer.setInput(columnList);
		columnsViewer.setCheckedElements(checkedList.toArray());
	}

	protected List selectedList() {
		return ((IStructuredSelection) columnsViewer.getSelection())
				.toList();
	}
}