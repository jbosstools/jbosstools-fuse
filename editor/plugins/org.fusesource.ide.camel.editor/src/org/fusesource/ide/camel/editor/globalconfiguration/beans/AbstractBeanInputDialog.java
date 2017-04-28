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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author bfitzpat
 * 
 */
public abstract class AbstractBeanInputDialog extends TitleAreaDialog {

	private boolean isEditDialog = false;

	public AbstractBeanInputDialog(Shell parentShell) {
		super(parentShell);
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

	protected abstract boolean validate();

	public void setIsEditDialog(boolean flag) {
		this.isEditDialog = flag;
	}
	
	protected boolean isEditDialog() {
		return this.isEditDialog;
	}
}

