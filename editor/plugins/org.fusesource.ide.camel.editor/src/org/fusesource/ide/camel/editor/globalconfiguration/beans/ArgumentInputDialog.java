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

/**
 * @author bfitzpat
 * 
 */
public class ArgumentInputDialog extends TitleAreaDialog {

	private Text argumentValueText;

	private String argumentType = null;
	private String argumentValue = null;

	/**
	 * constructor.
	 * 
	 * @param parent the parent
	 */
	public ArgumentInputDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Argument Details");
		setMessage("Specify type and value details for the new argument.");
		getShell().setText("Bean Argument");

		Composite area = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2, false);
		area.setLayout(gridLayout);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		Text argumentTypeText = createLabelAndText(area, "Type");
		if (argumentType != null && !argumentType.trim().isEmpty()) {
			argumentTypeText.setText(argumentType);
		}
		argumentTypeText.addModifyListener( input -> argumentType = argumentTypeText.getText().trim() );

		argumentValueText = createLabelAndText(area, "Value*");
		if (argumentValue != null && !argumentValue.trim().isEmpty()) {
			argumentValueText.setText(argumentValue);
		}
		argumentValueText.addModifyListener(input -> argumentValue = argumentValueText.getText().trim());

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

	protected boolean validate() {
		setErrorMessage(null);
		if (argumentValueText.getText().trim().isEmpty()) {
			setErrorMessage("No argument value specified. Please specify an argument value.");
		}
		return getErrorMessage() == null;
	}

	/**
	 * @return input type
	 */
	public String getArgumentType() {
		return argumentType;
	}

	/**
	 * @return output type
	 */
	public String getArgumentValue() {
		return argumentValue;
	}

	/**
	 * @param type arg type
	 */
	public void setArgumentType(String type) {
		argumentType = type;
	}

	/**
	 * @param value prop value
	 */
	public void setArgumentValue(String value) {
		argumentValue = value;
	}
}
