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
package org.fusesource.ide.camel.editor.globalconfiguration.beans;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manage a list of name/value pairs in Property elements
 * 
 * @author brianf
 *
 */
public class PropertyXMLStyleChildTableControl extends Composite {

	private static final String[] TREE_COLUMNS = new String[] { CamelBean.PROP_NAME, CamelBean.PROP_VALUE };

	private Button addButton;
	private Button removeButton;
	private Button editButton;
	private boolean isReadOnly = false;
	private String warningMsg = null;
	private ListenerList<ChangeListener> changeListeners;
	private TreeViewer propertyTreeTable;
	private Element inputElement;
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();

	/**
	 * @param parent
	 * @param style
	 */
	public PropertyXMLStyleChildTableControl(Composite parent, int style) {
		this(parent, style, false);
	}

	public PropertyXMLStyleChildTableControl(Composite parent, int style, boolean isReadOnly) {
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
		TreeColumn nameColumn = new TreeColumn(propertyTreeTable.getTree(), SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setWidth(200);
		TreeColumn valueColumn = new TreeColumn(propertyTreeTable.getTree(), SWT.LEFT);
		valueColumn.setText("Value");
		valueColumn.setWidth(200);

		propertyTreeTable.setColumnProperties(TREE_COLUMNS);

		propertyTreeTable.setLabelProvider(new PropertyTypeTreeLabelProvider());
		propertyTreeTable.setContentProvider(new PropertyTypeTreeContentProvider());

		propertyTreeTable.setCellEditors(new CellEditor[] { new TextCellEditor(propertyTreeTable.getTree()),
				new TextCellEditor(propertyTreeTable.getTree()), null });

		this.addButton = new Button(this, SWT.NONE);
		this.addButton.setLayoutData(GridDataFactory.fillDefaults().create());
		this.addButton.setText("Add");
		this.addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addPropertyTypeToList();
				propertyTreeTable.refresh();
				updatePropertyTypeButtons();
				fireChangedEvent(e.getSource());
			}
		});

		this.addButton.setEnabled(false);

		this.editButton = new Button(this, SWT.NONE);
		this.editButton.setLayoutData(GridDataFactory.fillDefaults().create());
		this.editButton.setText("Edit");
		this.editButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editPropertyType();
				propertyTreeTable.refresh();
				updatePropertyTypeButtons();
				fireChangedEvent(e.getSource());
			}
		});

		this.editButton.setEnabled(false);

		propertyTreeTable.addDoubleClickListener(e -> {
			editPropertyType();
			propertyTreeTable.refresh();
			updatePropertyTypeButtons();
			fireChangedEvent(e.getSource());
		});

		propertyTreeTable.getTree().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePropertyTypeButtons();
			}
		});

		this.removeButton = new Button(this, SWT.NONE);
		this.removeButton.setLayoutData(GridDataFactory.fillDefaults().create());
		this.removeButton.setText("Remove");
		this.removeButton.setEnabled(false);
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removePropertyFromList();
				propertyTreeTable.refresh();
				updatePropertyTypeButtons();
				fireChangedEvent(e.getSource());
			}
		});

		propertyTreeTable.setInput(inputElement);
		updatePropertyTypeButtons();
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
	public void updatePropertyTypeButtons() {
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

	protected void removePropertyFromList() {
		if (!getStructuredSelection().isEmpty()) {
			Element selectedProperty = (Element) getStructuredSelection()
					.getFirstElement();
			this.inputElement.removeChild(selectedProperty);
		}
	}

	protected void addPropertyTypeToList() {
		final PropertyInputDialog dialog = new PropertyInputDialog(Display.getCurrent().getActiveShell());
		dialog.setInput(inputElement);
		int rtnValue = dialog.open();
		if (rtnValue == PropertyInputDialog.OK) {
			final String name = dialog.getPropertyName();
			final String value = dialog.getPropertyValue();
			addBeanProperty(name, value);
		}
	}

	protected void editPropertyType() {

		if (!getStructuredSelection().isEmpty()) {
			Element selectedProperty = (Element) getStructuredSelection()
					.getFirstElement();

			final PropertyInputDialog dialog = new PropertyInputDialog(Display.getCurrent().getActiveShell());
			dialog.setIsEditDialog(true);
			Element xmlElement = selectedProperty;
			if (xmlElement.getAttribute(CamelBean.PROP_NAME) != null) {
				dialog.setPropertyName(xmlElement.getAttribute(CamelBean.PROP_NAME));
			}
			if (xmlElement.getAttribute(CamelBean.PROP_VALUE) != null) {
				dialog.setPropertyValue(xmlElement.getAttribute(CamelBean.PROP_VALUE));
			}
			dialog.setInput(inputElement);
			int rtnValue = dialog.open();
			if (rtnValue == PropertyInputDialog.OK) {
				final String name = dialog.getPropertyName();
				final String value = dialog.getPropertyValue();
				beanConfigUtil.editBeanProperty(xmlElement, name, value);
			}
		}
	}

	public void setInput(Element input) {
		this.inputElement = input;
		if (propertyTreeTable != null && !propertyTreeTable.getControl().isDisposed()) {
			propertyTreeTable.setInput(this.inputElement);
		}
	}

	private void addBeanProperty(String name, String value) {
		Element propertyNode = beanConfigUtil.createBeanProperty(inputElement, name, value);
		this.inputElement.appendChild(propertyNode);
	}

	private class PropertyTypeTreeContentProvider implements ITreeContentProvider {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Element) {
				Element parent = (Element) inputElement;
				return convertToArray(parent.getElementsByTagName(CamelBean.TAG_PROPERTY));
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return getElements(parentElement);
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof Element) {
				return ((Element) element).getParentNode();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Element) {
				return ((Element)element).hasChildNodes();
			}
			return false;
		}

		private Object[] convertToArray(NodeList list)
		{
			int length = list.getLength();
			Node[] copy = new Node[length];
			for (int n = 0; n < length; ++n) {
				copy[n] = list.item(n);
			}
			return copy;
		}
	}

	private class PropertyTypeTreeLabelProvider implements ITableLabelProvider {
		@Override
		public void addListener(ILabelProviderListener listener) {
			// empty
		}

		@Override
		public void dispose() {
			// empty
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return element instanceof Element
					&& (property.equalsIgnoreCase(CamelBean.PROP_NAME) || property.equalsIgnoreCase(CamelBean.PROP_VALUE));
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// empty
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Element && columnIndex == 0) {
				Element xmlElement = (Element) element;
				return xmlElement.getAttribute(CamelBean.PROP_NAME);
			} else if (element instanceof Element && columnIndex == 1) {
				Element xmlElement = (Element) element;
				return xmlElement.getAttribute(CamelBean.PROP_VALUE);
			}
			return null;
		}
	}
}
