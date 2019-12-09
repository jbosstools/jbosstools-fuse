/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at https://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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

package org.fusesource.ide.foundation.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.fusesource.ide.foundation.ui.config.ColumnConfiguration;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.util.IConfigurableColumns;
import org.fusesource.ide.foundation.ui.util.Viewers;


/**
 * The dialog to configure columns.
 */
public class ConfigureColumnsDialog extends ViewSettingsDialog {

	/** The columns viewer. */
	CheckboxTableViewer columnsViewer;

	private Button upButton;
	private Button downButton;
	private Button topButton;
	private Button bottomButton;

	/** The select-all button. */
	private Button selectAllButton;

	/** The deselect-all button. */
	private Button deselectAllButton;

	/** The configurable columns. */
	private IConfigurableColumns columns;

	private TableConfiguration configuration;


	/**
	 * The constructor.
	 * 
	 * @param columns
	 *            The configurable columns
	 */
	protected ConfigureColumnsDialog(IConfigurableColumns columns) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.columns = columns;
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
		newShell.setText(Messages.configureColumnsTitle);
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
		storePreference();
		columns.updateColumnConfiguration(configuration);
		super.okPressed();
	}

	/*
	 * @see ViewSettingsDialog#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		// lets zap the persistent configuration
		configuration.clear();

		// lets clear the configuration forcing a lazy reload
		columns.updateColumnConfiguration(null);

		loadPreference();
		Viewers.refresh(columnsViewer);
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
				if (element instanceof ColumnConfiguration) {
					ColumnConfiguration config = (ColumnConfiguration) element;
					return config.getName();
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

		topButton = new Button(composite, SWT.PUSH);
		topButton.setText(Messages.topLabel);
		setButtonLayoutData(topButton);
		topButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveTop(selectedList());
			}
		});

		upButton = new Button(composite, SWT.PUSH);
		upButton.setText(Messages.upLabel);
		setButtonLayoutData(upButton);
		upButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveUp(selectedList());
			}
		});

		downButton = new Button(composite, SWT.PUSH);
		downButton.setText(Messages.downLabel);
		setButtonLayoutData(downButton);
		downButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveDown(selectedList());
			}
		});

		bottomButton = new Button(composite, SWT.PUSH);
		bottomButton.setText(Messages.bottomLabel);
		setButtonLayoutData(bottomButton);
		bottomButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveBottom(selectedList());
			}
		});

		// lets add a separator
		new Label(composite, SWT.NONE);

		selectAllButton = new Button(composite, SWT.PUSH);
		selectAllButton.setText(Messages.selectAllLabel);
		setButtonLayoutData(selectAllButton);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				columnsViewer.setAllChecked(true);
			}
		});

		deselectAllButton = new Button(composite, SWT.PUSH);
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
		configuration = columns.getConfiguration();
		List<ColumnConfiguration> columnList = configuration.getColumnConfigurations();

		List<ColumnConfiguration> checkedList = new ArrayList<ColumnConfiguration>(columnList.size());
		for (ColumnConfiguration column : columnList) {
			if (column.isVisible()) {
				checkedList.add(column);
			}
		}
		columnsViewer.setInput(columnList);
		columnsViewer.setCheckedElements(checkedList.toArray());
	}

	/**
	 * Stores the current preference.
	 */
	private void storePreference() {
		Set<Object> checkedColumns = new HashSet<Object>(Arrays.asList(columnsViewer.getCheckedElements()));

		List<ColumnConfiguration> list = getColumns();
		configuration.setColumnConfigurations(list);

		for (ColumnConfiguration column : list) {
			boolean checked = checkedColumns.contains(column);
			column.setVisible(checked);
		}
		configuration.flush();
	}

	/**
	 * Gets the columns.
	 * 
	 * @return The columns
	 */
	@SuppressWarnings("unchecked")
	private List<ColumnConfiguration> getColumns() {
		return (List<ColumnConfiguration>) columnsViewer.getInput();
	}

	/**
	 * Moves the items up.
	 * 
	 * @param items
	 *            The items to move up
	 */
	void moveUp(List<ColumnConfiguration> items) {
		if (!items.isEmpty() && !items.get(0).equals(getColumns().get(0))) {
			for (ColumnConfiguration item : items) {
				moveUp(item);
			}
		}
	}

	protected void moveTop(List<ColumnConfiguration> selectedList) {
		// use reverse order to preserve selection order
		List<ColumnConfiguration> reverseOrder = new ArrayList<>(selectedList);
		Collections.reverse(reverseOrder);
		for (ColumnConfiguration config : reverseOrder) {
			moveTop(config);
		}
	}

	protected void moveBottom(List<ColumnConfiguration> selectedList) {
		for (ColumnConfiguration config : selectedList) {
			moveBottom(config);
		}
	}

	private void moveTop(ColumnConfiguration item) {
		List<ColumnConfiguration> newColumns = new ArrayList<>();
		newColumns.add(item);
		for (ColumnConfiguration column : getColumns()) {
			if (!column.equals(item)) {
				newColumns.add(column);
			}
		}
		columnsViewer.setInput(newColumns);
	}

	private void moveBottom(ColumnConfiguration item) {
		List<ColumnConfiguration> newColumns = new ArrayList<>();
		for (ColumnConfiguration column : getColumns()) {
			if (!column.equals(item)) {
				newColumns.add(column);
			}
		}
		newColumns.add(item);
		columnsViewer.setInput(newColumns);
	}

	/**
	 * Moves the items down.
	 * 
	 * @param items
	 *            The items to move down
	 */
	void moveDown(List<ColumnConfiguration> items) {
		if (!items.isEmpty()
				&& !items.get(items.size() - 1).equals(
						getColumns().get(getColumns().size() - 1))) {
			for (int i = items.size() - 1; i >= 0; i--) {
				moveDown(items.get(i));
			}
		}
	}

	/**
	 * Moves the item up.
	 * 
	 * @param item
	 *            The item to move up
	 */
	private void moveUp(ColumnConfiguration item) {
		ColumnConfiguration movedColumn = null;
		List<ColumnConfiguration> newColumns = new ArrayList<ColumnConfiguration>();
		for (ColumnConfiguration column : getColumns()) {
			if (!column.equals(item) || movedColumn == null) {
				newColumns.add(column);
				movedColumn = column;
				continue;
			}
			newColumns.remove(movedColumn);
			newColumns.add(column);
			newColumns.add(movedColumn);
		}
		columnsViewer.setInput(newColumns);
	}

	/**
	 * Moves the item down.
	 * 
	 * @param item
	 *            The item to move down
	 */
	private void moveDown(ColumnConfiguration item) {
		ColumnConfiguration movedColumn = null;
		List<ColumnConfiguration> newColumns = new ArrayList<ColumnConfiguration>();
		for (ColumnConfiguration column : getColumns()) {

			if (column.equals(item)) {
				movedColumn = item;
				continue;
			}

			newColumns.add(column);

			if (movedColumn != null) {
				newColumns.add(movedColumn);
				movedColumn = null;
			}
		}
		columnsViewer.setInput(newColumns);
	}


	protected List selectedList() {
		return ((IStructuredSelection) columnsViewer.getSelection())
				.toList();
	}
}