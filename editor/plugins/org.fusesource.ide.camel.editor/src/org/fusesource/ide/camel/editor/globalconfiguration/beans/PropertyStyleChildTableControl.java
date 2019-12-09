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
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.w3c.dom.Element;

/**
 * Manage a list of name/value pairs in Property elements
 * 
 * @author brianf
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class PropertyStyleChildTableControl extends PropertyStyleBaseTableControl {

	private AbstractCamelModelElement inputElement;
	private List<AbstractCamelModelElement> propertyList = new ArrayList<>();

	/**
	 * @param parent
	 * @param style
	 */
	public PropertyStyleChildTableControl(Composite parent, int style) {
		this(parent, style, false);
	}

	public PropertyStyleChildTableControl(Composite parent, int style, boolean isReadOnly) {
		super(parent, style, isReadOnly);

		propertyTreeTable.setInput(propertyList);
		updatePropertyTypeButtons();
	}

	protected void removePropertyFromList() {
		if (!getStructuredSelection().isEmpty()) {
			AbstractCamelModelElement selectedProperty = (AbstractCamelModelElement) getStructuredSelection()
					.getFirstElement();
			propertyList.remove(selectedProperty);
		}
	}

	protected void addPropertyTypeToList() {
		final PropertyInputDialog dialog = new PropertyInputDialog(Display.getCurrent().getActiveShell());
		dialog.setPropertyList(propertyList);
		int rtnValue = dialog.open();
		if (rtnValue == PropertyInputDialog.OK) {
			final String name = dialog.getPropertyName();
			final String value = dialog.getPropertyValue();
			addBeanProperty(name, value);
		}
	}

	protected void editPropertyType() {
		if (!getStructuredSelection().isEmpty()) {
			AbstractCamelModelElement selectedProperty = (AbstractCamelModelElement) getStructuredSelection()
					.getFirstElement();
			final PropertyInputDialog dialog = new PropertyInputDialog(Display.getCurrent().getActiveShell());
			dialog.setIsEditDialog(true);
			Element xmlElement = (Element) selectedProperty.getXmlNode();
			if (xmlElement.getAttribute(GlobalBeanEIP.PROP_NAME) != null) {
				dialog.setPropertyName(xmlElement.getAttribute(GlobalBeanEIP.PROP_NAME));
			}
			if (xmlElement.getAttribute(GlobalBeanEIP.PROP_VALUE) != null) {
				dialog.setPropertyValue(xmlElement.getAttribute(GlobalBeanEIP.PROP_VALUE));
			}
			dialog.setPropertyList(propertyList);
			int rtnValue = dialog.open();
			if (rtnValue == PropertyInputDialog.OK) {
				final String name = dialog.getPropertyName();
				final String value = dialog.getPropertyValue();
				beanConfigUtil.editBeanProperty(xmlElement, name, value);
			}
		}
	}

	public void setInput(AbstractCamelModelElement input) {
		inputElement = input;
	}

	protected void addBeanProperty(String name, String value) {
		Element propertyNode = beanConfigUtil.createBeanProperty(inputElement.getCamelFile(), name, value);
		CamelBasicModelElement newProperty = new CamelBasicModelElement(null, propertyNode);
		propertyList.add(newProperty);
	}

	private class PropertyTypeTreeContentProvider implements ITreeContentProvider {
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
			return element instanceof AbstractCamelModelElement
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
			if (element instanceof AbstractCamelModelElement && columnIndex == 0) {
				Element xmlElement = (Element) ((AbstractCamelModelElement) element).getXmlNode();
				return xmlElement.getAttribute(GlobalBeanEIP.PROP_NAME);
			} else if (element instanceof AbstractCamelModelElement && columnIndex == 1) {
				Element xmlElement = (Element) ((AbstractCamelModelElement) element).getXmlNode();
				return xmlElement.getAttribute(GlobalBeanEIP.PROP_VALUE);
			}
			return null;
		}
	}

	public List<AbstractCamelModelElement> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<AbstractCamelModelElement> list) {
		this.propertyList = list;
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
