/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.debug.editors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author lhein
 *
 */
public class BooleanVariableEditor extends TrayDialog {
	
	/**
	 * The title of the dialog.
	 */
	private String title;
	
	/**
	 * The message to display, or <code>null</code> if none.
	 */
	private String message;
	
	/**
	 * The input value
	 */
	private boolean value= false;
	
	/**
	 * Ok button widget.
	 */
	private Button okButton;
	
	/**
	 * Input combo widget.
	 */
	private Combo combo;
	
	/**
	 * Creates an input dialog with OK and Cancel buttons.
	 * Note that the dialog will have no visual representation (no widgets)
	 * until it is told to open.
	 * <p>
	 * Note that the <code>open</code> method blocks for input dialogs.
	 * </p>
	 *
	 * @param parentShell the parent shell
	 * @param dialogTitle the dialog title, or <code>null</code> if none
	 * @param dialogMessage the dialog message, or <code>null</code> if none
	 * @param initialValue the initial input value
	 */
	public BooleanVariableEditor(Shell parentShell, String dialogTitle, String dialogMessage, boolean initialValue) {
		super(parentShell);
		this.title = dialogTitle;
		message = dialogMessage;
		value = initialValue;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			value= Boolean.parseBoolean(combo.getText());
		} 
		super.buttonPressed(buttonId);
	}

	/* (non-Javadoc)
	 * Method declared in Window.
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}

	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		//do this here because setting the text will set enablement on the ok button
		combo.setFocus();
		combo.select(combo.indexOf(Boolean.toString(this.value)));
	}
	
	/* (non-Javadoc)
	 * Method declared on Dialog.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Font font = parent.getFont();
		// create composite
		Composite composite = (Composite)super.createDialogArea(parent);
		
		// create message
		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			GridData data = new GridData(
					GridData.GRAB_HORIZONTAL |
					GridData.HORIZONTAL_ALIGN_FILL |
					GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(font);
		}
		
		combo = new Combo(composite, SWT.READ_ONLY | SWT.BORDER);
		combo.add(Boolean.TRUE.toString());
		combo.add(Boolean.FALSE.toString());
		
		GridData gridData= new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gridData.heightHint = 12;
		gridData.widthHint = 30;
		combo.setLayoutData(gridData);
		combo.setFont(font);
		combo.addSelectionListener(new SelectionAdapter() {
			
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (okButton.isEnabled()) {
					return;
				}
				okButton.setEnabled(true);
			}
		});
		
		return composite;
	}

	/**
	 * Returns the ok button.
	 *
	 * @return the ok button
	 */
	protected Button getOkButton() {
		return okButton;
	}

	/**
	 * Returns the combo.
	 *
	 * @return the combo
	 */
	protected Combo getCombo() {
		return combo;
	}

	/**
	 * Returns the selected value.
	 *
	 * @return the selected value
	 */
	public boolean getValue() {
		return value;
	}

	@Override
	protected void okPressed() {
		if (okButton.isEnabled()) {
			super.okPressed();
		}
	}
}
