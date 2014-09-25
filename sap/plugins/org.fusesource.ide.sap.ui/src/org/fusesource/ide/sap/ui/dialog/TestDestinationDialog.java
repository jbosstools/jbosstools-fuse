/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

public class TestDestinationDialog extends TitleAreaDialog {
	
	private static final int TEST_ID = IDialogConstants.CLIENT_ID + 1;
	
	private static final int CLEAR_ID = IDialogConstants.CLIENT_ID + 2;

	private String destinationName;

	private Text text;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TestDestinationDialog(Shell parentShell, String destinationName) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM);
		this.destinationName = destinationName;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(Activator.getDefault().getImageRegistry().get(Activator.FUSE_ICON_16C_IMAGE));
		super.configureShell(newShell);
		newShell.setText(Messages.TestDestinationDialog_shellTitle);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle(NLS.bind(Messages.TestDestinationDialog_dialogTitle, destinationName));
		setMessage(""); //$NON-NLS-1$
		setTitleImage(Activator.getDefault().getImageRegistry().get(Activator.FUSE_RS_IMAGE));

		return contents;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		text = new Text(container, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, TEST_ID, Messages.TestDestinationDialog_Test, false);
		createButton(parent, CLEAR_ID, Messages.TestDestinationDialog_Clear, false);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == TEST_ID) {
			testPressed();
		} else if (buttonId == CLEAR_ID) {
			clearConsole();
		} else if (buttonId == IDialogConstants.CLOSE_ID) {
			closePressed();
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(550, 335);
	}
	
	private void closePressed() {
		setReturnCode(OK);
		close();
	}
	
	private void testPressed() {
		testDestination();
	}
	
	private void testDestination() {
		try {
			JCoDestination jcoDestination = JCoDestinationManager.getDestination(destinationName);
			jcoDestination.ping();
			append2Console(NLS.bind(Messages.TestDestinationDialog_7, destinationName));
		} catch (JCoException e) {
			append2Console("\n" + e.getMessage()); //$NON-NLS-1$
		}
	}
	
	private void clearConsole() {
		text.setText(""); //$NON-NLS-1$
	}
	
	private void append2Console(String str) {
		String log = text.getText();
		log = log + str;
		text.setText(log);
	}

}
