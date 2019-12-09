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
 * @author brianf
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ArgumentXMLStyleChildTableControl extends ArgumentStyleBaseTableControl {

	private Element inputElement;

	public ArgumentXMLStyleChildTableControl(Composite parent, int style) {
		this(parent, style, false);
	}

	public ArgumentXMLStyleChildTableControl(Composite parent, int style, boolean isReadOnly) {
		super(parent, style, isReadOnly);

		propertyTreeTable.setInput(inputElement);
		updateArgumentTypeButtons();
	}

	protected void removeArgumentFromList() {
		if (!getStructuredSelection().isEmpty()) {
			Element selectedProperty = (Element) getStructuredSelection()
					.getFirstElement();
			this.inputElement.removeChild(selectedProperty);
		}
	}

	protected void addArgumentTypeToList() {
		final ArgumentInputDialog dialog = new ArgumentInputDialog(Display.getCurrent().getActiveShell());
		int rtnValue = dialog.open();
		if (rtnValue == PropertyInputDialog.OK) {
			final String type = dialog.getArgumentType();
			final String value = dialog.getArgumentValue();
			addBeanArgument(type, value);
		}
	}

	protected void editArgumentType() {

		if (!getStructuredSelection().isEmpty()) {
			Element selectedProperty = (Element) getStructuredSelection()
					.getFirstElement();

			final ArgumentInputDialog dialog = new ArgumentInputDialog(Display.getCurrent().getActiveShell());
			final Element xmlElement = selectedProperty;
			if (xmlElement.getAttribute(GlobalBeanEIP.ARG_TYPE) != null) {
				dialog.setArgumentType(xmlElement.getAttribute(GlobalBeanEIP.ARG_TYPE));
			}
			if (xmlElement.getAttribute(GlobalBeanEIP.ARG_VALUE) != null) {
				dialog.setArgumentValue(xmlElement.getAttribute(GlobalBeanEIP.ARG_VALUE));
			}
			int rtnValue = dialog.open();
			if (rtnValue == PropertyInputDialog.OK) {
				final String type = dialog.getArgumentType();
				final String value = dialog.getArgumentValue();
				beanConfigUtil.editBeanArgument(xmlElement, type, value);
			}
		}
	}

	public void setInput(Element input) {
		this.inputElement = input;
		if (propertyTreeTable != null && !propertyTreeTable.getControl().isDisposed()) {
			propertyTreeTable.setInput(this.inputElement);
		}
	}

	protected void addBeanArgument(String type, String value) {
		Element propertyNode = beanConfigUtil.createBeanArgument(inputElement, type, value);
		this.inputElement.appendChild(propertyNode);
	}

	private class ArgumentTypeTreeContentProvider implements ITreeContentProvider {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Element) {
				Element parent = (Element) inputElement;
				String tagName = beanConfigUtil.getArgumentTag(parent);
				return convertToArray(parent.getElementsByTagName(tagName));
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

	private class ArgumentTypeTreeLabelProvider implements ITableLabelProvider {
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
					&& (property.equalsIgnoreCase(GlobalBeanEIP.ARG_TYPE) || property.equalsIgnoreCase(GlobalBeanEIP.ARG_VALUE));
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
				return xmlElement.getAttribute(GlobalBeanEIP.ARG_TYPE);
			} else if (element instanceof Element && columnIndex == 1) {
				Element xmlElement = (Element) element;
				return xmlElement.getAttribute(GlobalBeanEIP.ARG_VALUE);
			}
			return null;
		}
	}

	@Override
	protected ITableLabelProvider getTableLabelProvider() {
		return new ArgumentTypeTreeLabelProvider();
	}

	@Override
	protected ITreeContentProvider getTableContentProvider() {
		return new ArgumentTypeTreeContentProvider();
	}
}
