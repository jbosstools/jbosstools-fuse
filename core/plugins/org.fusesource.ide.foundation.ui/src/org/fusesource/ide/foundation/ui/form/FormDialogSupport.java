/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.form;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.fusesource.ide.foundation.ui.util.ICanValidate;
import org.fusesource.ide.foundation.ui.util.Shells;


public abstract class FormDialogSupport extends Dialog implements ICanValidate {
	private FormSupport form;

	public FormDialogSupport() {
		super(Shells.getShell());
	}

	public FormDialogSupport(String title) {
		this();
		Shells.getShell().setText(title);
	}

	public FormSupport getForm() {
		return form;
	}

	public void setForm(FormSupport form) {
		this.form = form;
	}

	@Override
	public boolean close() {
		if (form != null) {
			form.dispose();
			form = null;
		}
		return super.close();
	}

	@Override
	public void create() {
		super.create();
		form.setFocus();
	}

	public void setFocus() {
		form.setFocus();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		applyDialogFont(parent);
		return form.createDialogArea(parent);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control answer = super.createButtonBar(parent);
		validate();
		return answer;
	}

	@Override
	public void validate() {
		boolean valid = form.isValid();
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok != null) {
			ok.setEnabled(valid);
		}
	}


	@Override
	protected void okPressed() {
		getForm().okPressed();
		super.okPressed();
	}

}