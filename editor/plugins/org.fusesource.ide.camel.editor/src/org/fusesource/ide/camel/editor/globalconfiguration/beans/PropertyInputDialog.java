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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author bfitzpat
 * 
 */
public class PropertyInputDialog extends TitleAreaDialog {

	private Text propertyNameText;
	private Text propertyValueText;

	private String propertyName = null;
	private String initialPropertyName = null;
	private String propertyValue = null;

	private List<AbstractCamelModelElement> propertyList = new ArrayList<>();
	private Element inputElement;
	private boolean isEditDialog = false;

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
		setTitle("Property Details");
		getShell().setText("Bean Property");
		if (isEditDialog) {
			setMessage("Edit name and value details for the property.");
		} else {
			setMessage("Specify name and value details for the new property.");
		}
		Composite area = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2, false);
		area.setLayout(gridLayout);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		propertyNameText = createLabelAndText(area, "Name*");
		if (propertyName != null && !propertyName.trim().isEmpty()) {
			propertyNameText.setText(propertyName);
		}
		propertyNameText.addModifyListener(input -> propertyName = propertyNameText.getText().trim());

		propertyValueText = createLabelAndText(area, "Value*");
		if (propertyValue != null && !propertyValue.trim().isEmpty()) {
			propertyValueText.setText(propertyValue);
		}
		propertyValueText.addModifyListener(input -> propertyValue = propertyValueText.getText().trim());

		return area;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control rtnControl = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(validate());
		setErrorMessage(null);
		return rtnControl;
	}

	/**
	 * @param parent parent composite
	 * @param label string to put in label
	 * @return reference to created Text control
	 */
	protected Text createLabelAndText(Composite parent, String label) {
		new Label(parent, SWT.NONE).setText(label);
		Text newText = new Text(parent, SWT.BORDER);
		newText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		newText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// ignore
			}

			@Override
			public void focusLost(FocusEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(validate());
			}
		});
		newText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// ignore
			}

			@Override
			public void keyReleased(KeyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(validate());
			}
		});
		return newText;
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
	
	protected boolean validate() {
		setErrorMessage(null);
		String newPropName = propertyNameText.getText();
		if (propertyNameText.getText().trim().isEmpty()) {
			setErrorMessage("No property name specified. Please specify a property name.");
		} else if (propertyList != null && !propertyList.isEmpty()) {
			for (Iterator<AbstractCamelModelElement> iter = propertyList.iterator(); iter.hasNext();) {
				AbstractCamelModelElement camelElement = iter.next();
				Element xmlElement = (Element) camelElement.getXmlNode();
				String propName = xmlElement.getAttribute(CamelBean.PROP_NAME);
				boolean nameIsUnique = nameIsUnique(propName, newPropName);
				if (!nameIsUnique) {
					setErrorMessage("Property names must be unique. One already exists with that name.");
					break;
				}
			}
		} else if (inputElement != null) {
			NodeList childList = inputElement.getElementsByTagName(CamelBean.TAG_PROPERTY);
			for (int i = 0; i < childList.getLength(); i++) {
				Element arrayElement = (Element) childList.item(i);
				String propName = arrayElement.getAttribute(CamelBean.PROP_NAME);
				boolean nameIsUnique = nameIsUnique(propName, newPropName);
				if (!nameIsUnique) {
					setErrorMessage("Property names must be unique. One already exists with that name.");
					break;
				}
			}
		} else if (propertyValueText.getText().trim().isEmpty()) {
			setErrorMessage("No property value specified. Please specify a property value.");
		}
		return getErrorMessage() == null;
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

	public void setPropertyList(List<AbstractCamelModelElement> list) {
		this.propertyList = list;
	}

	public void setInput(Element input) {
		this.inputElement = input;
	}
	
	public void setIsEditDialog(boolean flag) {
		this.isEditDialog = flag;
	}
}

