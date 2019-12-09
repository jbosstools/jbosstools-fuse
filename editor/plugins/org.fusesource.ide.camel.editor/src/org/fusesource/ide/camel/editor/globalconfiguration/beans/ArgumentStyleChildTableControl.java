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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.w3c.dom.Element;

/**
 * @author brianf
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ArgumentStyleChildTableControl extends ArgumentStyleBaseTableControl {

	private AbstractCamelModelElement inputElement;
	private List<AbstractCamelModelElement> argumentList = new ArrayList<>();

	public ArgumentStyleChildTableControl(Composite parent, int style) {
		this(parent, style, false);
	}

	public ArgumentStyleChildTableControl(Composite parent, int style, boolean isReadOnly) {
		super(parent, style, isReadOnly);

		propertyTreeTable.setInput(argumentList);
		updateArgumentTypeButtons();
	}

	protected void removeArgumentFromList() {
		if (!getStructuredSelection().isEmpty()) {
			AbstractCamelModelElement selectedProperty = (AbstractCamelModelElement) getStructuredSelection()
					.getFirstElement();
			argumentList.remove(selectedProperty);
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
			AbstractCamelModelElement selectedProperty = (AbstractCamelModelElement) getStructuredSelection()
					.getFirstElement();

			final ArgumentInputDialog dialog = new ArgumentInputDialog(Display.getCurrent().getActiveShell());
			final Element xmlElement = (Element) selectedProperty.getXmlNode();
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

	public void setInput(AbstractCamelModelElement input) {
		inputElement = input;
	}

	protected void addBeanArgument(String type, String value) {
		final CamelFile camelFile = inputElement.getCamelFile();
		Element propertyNode = beanConfigUtil.createBeanArgument(camelFile, type, value);
		CamelBasicModelElement newProperty = new CamelBasicModelElement(null, propertyNode);
		argumentList.add(newProperty);
	}

	public List<AbstractCamelModelElement> getArgumentList() {
		return argumentList;
	}

	public void setArgumentList(List<AbstractCamelModelElement> list) {
		this.argumentList = list;
	}

	@Override
	protected ITableLabelProvider getTableLabelProvider() {
		return new ArgumentTypeTreeLabelProvider();
	}

	@Override
	protected ITreeContentProvider getTableContentProvider() {
		return new ArgumentTypeTreeContentProvider();
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
			return element instanceof AbstractCamelModelElement
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
			if (element instanceof AbstractCamelModelElement && columnIndex == 0) {
				Element xmlElement = (Element) ((AbstractCamelModelElement) element).getXmlNode();
				return xmlElement.getAttribute(GlobalBeanEIP.ARG_TYPE);
			} else if (element instanceof AbstractCamelModelElement && columnIndex == 1) {
				Element xmlElement = (Element) ((AbstractCamelModelElement) element).getXmlNode();
				return xmlElement.getAttribute(GlobalBeanEIP.ARG_VALUE);
			}
			return null;
		}
	}
	
	private class ArgumentTypeTreeContentProvider implements ITreeContentProvider {

		private List<AbstractCamelModelElement> properties;

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof List<?>) {
				properties = (List<AbstractCamelModelElement>) newInput;
			}
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List<?>) {
				return properties.toArray();
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement parent = (AbstractCamelModelElement) parentElement;
				return new Object[] { parent.getChildElements() };
			}
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof AbstractCamelModelElement) {
				return ((AbstractCamelModelElement) element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof List<?>) {
				return !((List<?>) element).isEmpty();
			}
			return false;
		}
	}
}
