/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.preferences;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class PreferredLabelEditor extends FieldEditor {

	private Table table;

	/**
	 * The button box containing the Add, Edit and Remove buttons.
	 */
	private Composite buttonBox;
	private Button addButton;
	private Button editButton;
	private Button removeButton;

	private SelectionListener selectionListener;

	/**
	 * Creates a table field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public PreferredLabelEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) table.getLayoutData()).horizontalSpan = numColumns - 1;

	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		table = getTableControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		table.setLayoutData(gd);

		buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up, and Down button.
	 *
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(event -> {
				addButton = null;
				editButton = null;
				removeButton = null;
				buttonBox = null;
			});

		} else {
			checkParent(buttonBox, parent);
		}

		selectionChanged();
		return buttonBox;
	}

	/**
	 * Invoked when the selection in the list has changed.
	 *
	 * <p>
	 * The default implementation of this method utilizes the selection index and the size of the list to toggle the
	 * enablement of the up, down and remove buttons.
	 * </p>
	 *
	 * <p>
	 * Subclasses may override.
	 * </p>
	 *
	 * @since 3.5
	 */
	protected void selectionChanged() {
		int index = table.getSelectionIndex();

		editButton.setEnabled(index >= 0);
		removeButton.setEnabled(index >= 0);
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 *
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box) {
		addButton = createPushButton(box, UIMessages.preferredLabelsAddButtonText);
		editButton = createPushButton(box, UIMessages.preferredLabelsEditButtonText);
		removeButton = createPushButton(box, UIMessages.preferredLabelsRemoveButtonText);
	}

	/**
	 * Helper method to create a push button.
	 *
	 * @param parent
	 *            the parent control
	 * @param label
	 *            the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/**
	 * Returns this field editor's list control.
	 *
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public Table getTableControl(Composite parent) {
		if (table == null) {
			table = new Table(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			table.setFont(parent.getFont());
			table.addSelectionListener(getSelectionListener());
			table.addDisposeListener(event -> table = null);
			String[] columnHeaders = getColumnHeaders();
			if (columnHeaders.length > 0) {
				table.setHeaderVisible(true);
				for (String columnHeader : columnHeaders) {
					TableColumn column = new TableColumn(table, SWT.NULL);
					column.setText(columnHeader);
				}
			}
			packColumns();
		} else {
			checkParent(table, parent);
		}
		return table;
	}

	public void addRow(String... values) {
		TableItem item = new TableItem(table, SWT.NULL);
		for (int i = 0; i < table.getColumns().length && i < values.length; i++) {
			item.setText(i, values[i]);
		}
		packColumns();
	}

	public void updateRow(int index, String... values) {
		TableItem item = table.getItem(index);
		for (int i = 0; i < table.getColumns().length; i++) {
			item.setText(i, values[i]);
		}
		packColumns();
	}

	public void removeRows(int... indices) {
		table.remove(indices);
		packColumns();
	}

	protected void packColumns() {
		for (TableColumn col : table.getColumns()) {
			col.pack();
		}
	}

	protected String[] getColumnHeaders() {
		return new String[] { UIMessages.preferredLabelsComponentHeader, UIMessages.preferredLabelsParameterHeader };
	}

	/**
	 * Returns this field editor's selection listener. The listener is created if necessary.
	 *
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener() {
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call this method.
	 * </p>
	 *
	 * @return the shell
	 */
	protected Shell getShell() {
		if (table == null) {
			return null;
		}
		return table.getShell();
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == editButton) {
					editPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == table) {
					selectionChanged();
				}
			}
		};
	}

	protected void addPressed() {
		PreferredLabelDialog dialog = new PreferredLabelDialog(getShell());
		dialog.setComponentValidator(new ComponentValidator(getComponents()));
		dialog.setParameterValidator(new ParameterValidator());
		if (dialog.open() == Window.OK) {
			addRow(dialog.getComponent(), dialog.getParameter());
		}
	}

	protected void editPressed() {
		int index = table.getSelectionIndex();
		if (index >= 0) {
			TableItem selectedItem = table.getItem(index);
			PreferredLabelDialog dialog = new PreferredLabelDialog(getShell());
			dialog.setComponentValidator(new ComponentValidator());
			dialog.setParameterValidator(new ParameterValidator());
			dialog.setComponent(selectedItem.getText(0));
			dialog.setParameter(selectedItem.getText(1));
			if (dialog.open() == Window.OK) {
				updateRow(index, dialog.getComponent(), dialog.getParameter());
			}
		}
	}

	protected void removePressed() {
		removeRows(table.getSelectionIndices());
	}

	@Override
	protected void doLoad() {
		if (table != null) {
			String s = getPreferenceStore().getString(getPreferenceName());
			parseString(s);
		}
	}

	@Override
	protected void doLoadDefault() {
		if (table != null) {
			table.removeAll();
			String s = getPreferenceStore().getDefaultString(getPreferenceName());
			parseString(s);
		}
	}

	@Override
	protected void doStore() {
		String stringOfPreferredLabels = createList(getPreferredLabels());
		if (stringOfPreferredLabels != null) {
			getPreferenceStore().setValue(getPreferenceName(), stringOfPreferredLabels);
		}
	}

	/**
	 * Splits the given string into a list of strings. This method is the converse of <code>createList</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 *
	 * @param stringList
	 *            the string
	 * @return an array of <code>String</code>
	 * @see #createList
	 */
	private void parseString(String stringList) {
		if (stringList.isEmpty()) {
			return;
		}
		String[] preferredLabels = stringList.split(AbstractCamelModelElement.USER_LABEL_DELIMETER);
		Stream.of(preferredLabels).forEach(label -> addRow(label.split("\\.")));
	}

	/**
	 * Combines the given list of items into a single string. This method is the converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 *
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	private String createList(List<String> items) {
		return String.join(AbstractCamelModelElement.USER_LABEL_DELIMETER, items);
	}

	public List<String> getPreferredLabels() {
		return Stream.of(table.getItems()).map(row -> row.getText(0) + "." + row.getText(1))
				.collect(Collectors.toList());
	}

	public String[] getComponents() {
		return Stream.of(table.getItems()).map(row -> row.getText(0)).toArray(String[]::new);
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	protected Table getTable() {
		return table;
	}

	protected Button getAddButton() {
		return addButton;
	}

	protected Button getEditButton() {
		return editButton;
	}

	protected Button getRemoveButton() {
		return removeButton;
	}

}
