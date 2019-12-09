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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manage a list of name/value pairs in Property elements
 * 
 * @author brianf
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class PropertyXMLStyleChildTableControl extends PropertyStyleBaseTableControl {

	private Element inputElement;

	/**
	 * @param parent
	 * @param style
	 */
	public PropertyXMLStyleChildTableControl(Composite parent, int style) {
		this(parent, style, false);
	}

	public PropertyXMLStyleChildTableControl(Composite parent, int style, boolean isReadOnly) {
		super(parent, style, isReadOnly);

		propertyTreeTable.setInput(inputElement);
		updatePropertyTypeButtons();
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
			if (xmlElement.getAttribute(GlobalBeanEIP.PROP_NAME) != null) {
				dialog.setPropertyName(xmlElement.getAttribute(GlobalBeanEIP.PROP_NAME));
			}
			if (xmlElement.getAttribute(GlobalBeanEIP.PROP_VALUE) != null) {
				dialog.setPropertyValue(xmlElement.getAttribute(GlobalBeanEIP.PROP_VALUE));
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

	protected void addBeanProperty(String name, String value) {
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
				return convertToArray(parent.getElementsByTagName(GlobalBeanEIP.TAG_PROPERTY));
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
					&& (property.equalsIgnoreCase(GlobalBeanEIP.PROP_NAME) || property.equalsIgnoreCase(GlobalBeanEIP.PROP_VALUE));
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
				return xmlElement.getAttribute(GlobalBeanEIP.PROP_NAME);
			} else if (element instanceof Element && columnIndex == 1) {
				Element xmlElement = (Element) element;
				return xmlElement.getAttribute(GlobalBeanEIP.PROP_VALUE);
			}
			return null;
		}
	}

	@Override
	protected ITableLabelProvider getTableLabelProvider() {
		return new PropertyTypeTreeLabelProvider();
	}

	@Override
	protected ITreeContentProvider getTableContentProvider() {
		return new PropertyTypeTreeContentProvider();
	}
}
