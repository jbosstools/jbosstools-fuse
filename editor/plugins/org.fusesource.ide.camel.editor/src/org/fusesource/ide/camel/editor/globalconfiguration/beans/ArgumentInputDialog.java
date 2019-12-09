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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;

/**
 * @author brianf
 *
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ArgumentInputDialog extends AbstractBeanInputDialog {

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
		setTitle(UIMessages.argumentInputDialogDialogTitle);
		setMessage(UIMessages.argumentInputDialogDialogMessage);
		getShell().setText(UIMessages.argumentInputDialogDialogWindowTitle);

		Composite area = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(2, false);
		area.setLayout(gridLayout);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		Text argumentTypeText = createLabelAndText(area, UIMessages.argumentInputDialogTypeFieldLabel);
		if (argumentType != null && !argumentType.trim().isEmpty()) {
			argumentTypeText.setText(argumentType);
		}
		argumentTypeText.addModifyListener( input -> {
			argumentType = argumentTypeText.getText().trim();
			getButton(IDialogConstants.OK_ID).setEnabled(validate());
		});

		argumentValueText = createLabelAndText(area, UIMessages.argumentInputDialogValueFieldLabel);
		if (argumentValue != null && !argumentValue.trim().isEmpty()) {
			argumentValueText.setText(argumentValue);
		}
		argumentValueText.addModifyListener(input -> {
			argumentValue = argumentValueText.getText().trim();
			getButton(IDialogConstants.OK_ID).setEnabled(validate());
		});

		return area;
	}

	@Override
	protected boolean validate() {
		setErrorMessage(null);
		if (argumentValueText.getText().trim().isEmpty()) {
			setErrorMessage(UIMessages.argumentInputDialogErrorMessage);
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
