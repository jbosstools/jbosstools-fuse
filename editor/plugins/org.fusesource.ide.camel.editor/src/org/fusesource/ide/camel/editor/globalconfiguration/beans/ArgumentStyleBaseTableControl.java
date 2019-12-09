/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.globalconfiguration.beans;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;

/**
 * @author brianf
 *
 */
public abstract class ArgumentStyleBaseTableControl extends Composite {

	protected static final String[] TREE_COLUMNS = new String[] { GlobalBeanEIP.ARG_TYPE, GlobalBeanEIP.ARG_VALUE };

	protected Button addButton;
	protected Button removeButton;
	protected Button editButton;
	protected boolean isReadOnly = false;
	protected String warningMsg = null;
	protected ListenerList<ChangeListener> changeListeners;
	protected TreeViewer propertyTreeTable;
	protected BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	
	public ArgumentStyleBaseTableControl(Composite parent, int style) {
		this(parent, style, false);
	}

	public ArgumentStyleBaseTableControl(Composite parent, int style, boolean isReadOnly) {
		super(parent, style);

		this.isReadOnly = isReadOnly;
		this.changeListeners = new ListenerList<>();

		int additionalStyles;
		if (isReadOnly) {
			additionalStyles = SWT.READ_ONLY;
		} else {
			additionalStyles = SWT.NONE;
		}
		setLayout(GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).create());

		propertyTreeTable = new TreeViewer(this,
				SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.FULL_SELECTION | style | additionalStyles);
		this.propertyTreeTable.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		propertyTreeTable.getTree().setLayoutData(
				GridDataFactory.fillDefaults().span(1, 5).grab(true, true).hint(SWT.DEFAULT, 100).create());
		propertyTreeTable.getTree().setHeaderVisible(true);
		propertyTreeTable.getTree().setLinesVisible(true);
		TreeColumn typeColumn = new TreeColumn(propertyTreeTable.getTree(), SWT.LEFT);
		typeColumn.setText(UIMessages.argumentStyleChildTableControlTypeColumnLabel);
		typeColumn.setWidth(200);
		TreeColumn valueColumn = new TreeColumn(propertyTreeTable.getTree(), SWT.LEFT);
		valueColumn.setText(UIMessages.argumentStyleChildTableControlValueColumnLabel);
		valueColumn.setWidth(200);

		propertyTreeTable.setColumnProperties(TREE_COLUMNS);

		propertyTreeTable.setLabelProvider(getTableLabelProvider());

		propertyTreeTable.setContentProvider(getTableContentProvider());

		propertyTreeTable.setCellEditors(new CellEditor[] { new TextCellEditor(propertyTreeTable.getTree()),
				new TextCellEditor(propertyTreeTable.getTree()), null });

		this.addButton = new Button(this, SWT.NONE);
		this.addButton.setLayoutData(GridDataFactory.fillDefaults().create());
		this.addButton.setText(UIMessages.argumentStyleChildTableControlAddButtonLabel);
		this.addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addArgumentTypeToList();
				propertyTreeTable.refresh();
				updateArgumentTypeButtons();
				fireChangedEvent(e.getSource());
			}
		});

		this.addButton.setEnabled(false);

		this.editButton = new Button(this, SWT.NONE);
		this.editButton.setLayoutData(GridDataFactory.fillDefaults().create());
		this.editButton.setText(UIMessages.argumentStyleChildTableControlEditButtonLabel);
		this.editButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editArgumentType();
				propertyTreeTable.refresh();
				updateArgumentTypeButtons();
				fireChangedEvent(e.getSource());
			}
		});

		this.editButton.setEnabled(false);

		propertyTreeTable.addDoubleClickListener(e -> {
			editArgumentType();
			propertyTreeTable.refresh();
			updateArgumentTypeButtons();
			fireChangedEvent(e.getSource());
		});

		propertyTreeTable.getTree().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateArgumentTypeButtons();
			}
		});

		this.removeButton = new Button(this, SWT.NONE);
		this.removeButton.setLayoutData(GridDataFactory.fillDefaults().create());
		this.removeButton.setText(UIMessages.argumentStyleChildTableControlRemoveButtonLabel);
		this.removeButton.setEnabled(false);
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeArgumentFromList();
				propertyTreeTable.refresh();
				updateArgumentTypeButtons();
				fireChangedEvent(e.getSource());
			}
		});

		// implementers should set input here and update buttons
	}

	/**
	 * If we changed, fire a changed event.
	 * 
	 * @param source
	 */
	protected void fireChangedEvent(Object source) {
		ChangeEvent e = new ChangeEvent(source);
		// inform any listeners of the resize event
		Object[] listeners = this.changeListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ChangeListener) listeners[i]).stateChanged(e);
		}
	}

	/**
	 * Add a change listener.
	 * 
	 * @param listener
	 *            new listener
	 */
	public void addChangeListener(ChangeListener listener) {
		this.changeListeners.add(listener);
	}

	/**
	 * Remove a change listener.
	 * 
	 * @param listener
	 *            old listener
	 */
	public void removeChangeListener(ChangeListener listener) {
		this.changeListeners.remove(listener);
	}

	/**
	 * Update button state based on what's selected.
	 */
	public void updateArgumentTypeButtons() {
		if (isReadOnly) {
			this.addButton.setEnabled(false);
			this.editButton.setEnabled(false);
			this.removeButton.setEnabled(false);

		} else {
			this.addButton.setEnabled(true);

			// enable if a selection is made
			boolean enable = getStructuredSelection() != null && !getStructuredSelection().isEmpty();
			this.editButton.setEnabled(enable);
			this.removeButton.setEnabled(enable);
		}
	}

	/**
	 * @return the current selection from the table
	 */
	public IStructuredSelection getStructuredSelection() {
		if (propertyTreeTable != null && !propertyTreeTable.getSelection().isEmpty()) {
			return (IStructuredSelection) propertyTreeTable.getSelection();
		}
		return null;
	}

	/**
	 * @return warning string
	 */
	public String getWarning() {
		return this.warningMsg;
	}

	protected abstract void removeArgumentFromList();

	protected abstract void addArgumentTypeToList();

	protected abstract void editArgumentType();
	
	protected abstract void addBeanArgument(String type, String value);
	
	protected abstract ITableLabelProvider getTableLabelProvider();

	protected abstract ITreeContentProvider getTableContentProvider();
}
