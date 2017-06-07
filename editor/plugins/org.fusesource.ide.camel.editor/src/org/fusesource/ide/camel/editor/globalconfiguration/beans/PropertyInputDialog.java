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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author bfitzpat
 * 
 */
public class PropertyInputDialog extends AbstractBeanInputDialog {

	private String propertyName = null;
	private String initialPropertyName = null;
	private String propertyValue = null;

	private List<AbstractCamelModelElement> propertyList = new ArrayList<>();
	private Element inputElement;

	/**
	 * Dialog constructor.
	 * 
	 * @param parent the parent
	 */
	public PropertyInputDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(UIMessages.PropertyInputDialog_PropertyDialogTitle);
		getShell().setText(UIMessages.PropertyInputDialog_PropertyDialogWindowTitle);
		if (isEditDialog()) {
			setMessage(UIMessages.PropertyInputDialog_PropertyDialogEditMessage);
		} else {
			setMessage(UIMessages.PropertyInputDialog_PropertyDialogNewPropertyMessage);
		}
		Composite area = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2, false);
		area.setLayout(gridLayout);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		Text propertyNameText = createLabelAndText(area, UIMessages.PropertyInputDialog_NameFieldLabel);
		if (propertyName != null && !propertyName.trim().isEmpty()) {
			propertyNameText.setText(propertyName);
		}
		propertyNameText.addModifyListener(input -> propertyName = propertyNameText.getText().trim());

		Text propertyValueText = createLabelAndText(area, UIMessages.PropertyInputDialog_ValueFieldLabel);
		if (propertyValue != null && !propertyValue.trim().isEmpty()) {
			propertyValueText.setText(propertyValue);
		}
		propertyValueText.addModifyListener(input -> propertyValue = propertyValueText.getText().trim());

		return area;
	}

	private boolean nameIsUnique(String propName, String newPropName) {
		boolean namesMatch = propName.contentEquals(newPropName);
		if (namesMatch) {
			if (initialPropertyName != null) {
				boolean isOldName = initialPropertyName.contentEquals(newPropName);
				if (!isOldName) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * For testing purposes
	 * @param newPropName
	 * @param newPropValue
	 * @return
	 */
	public String validate(String newPropName, String newPropValue) {
		if (Strings.isEmpty(newPropName)) {
			return UIMessages.PropertyInputDialog_ErrorNoNameSpecified;
		}
		if (!propertyList.isEmpty()) {
			for (AbstractCamelModelElement camelElement : propertyList) {
				Element xmlElement = (Element) camelElement.getXmlNode();
				String propName = xmlElement.getAttribute(CamelBean.PROP_NAME);
				boolean nameIsUnique = nameIsUnique(propName, newPropName);
				if (!nameIsUnique) {
					return UIMessages.PropertyInputDialog_NameNotUnique;
				}
			}
		} else if (inputElement != null) {
			NodeList childList = inputElement.getElementsByTagName(CamelBean.TAG_PROPERTY);
			for (int i = 0; i < childList.getLength(); i++) {
				Element arrayElement = (Element) childList.item(i);
				String propName = arrayElement.getAttribute(CamelBean.PROP_NAME);
				if (!nameIsUnique(propName, newPropName)) {
					return UIMessages.PropertyInputDialog_NameNotUnique;
				}
			}
		}
		if (Strings.isEmpty(newPropValue)) {
			return UIMessages.PropertyInputDialog_ValueNotSpecified;
		}
		return null;
	}
	
	@Override
	protected boolean validate() {
		setErrorMessage(null);
		String newPropName = getPropertyName();
		String newPropValue = getPropertyValue();
		String msg = validate(newPropName, newPropValue);
		if (msg != null) {
			setErrorMessage(msg);
		}
		return true;
	}

	/**
	 * @return input type
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return output type
	 */
	public String getPropertyValue() {
		return propertyValue;
	}

	/**
	 * @param name prop name
	 */
	public void setPropertyName(String name) {
		propertyName = name;
		initialPropertyName = name;
	}

	/**
	 * @param value prop value
	 */
	public void setPropertyValue(String value) {
		propertyValue = value;
	}

	/**
	 * @param list
	 */
	public void setPropertyList(List<AbstractCamelModelElement> list) {
		this.propertyList = list;
	}

	/**
	 * @param input
	 */
	public void setInput(Element input) {
		this.inputElement = input;
	}
}

