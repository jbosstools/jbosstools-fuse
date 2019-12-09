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

package org.fusesource.ide.server.karaf.ui.runtime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.karaf.ui.Messages;


public class KarafServerPorpertiesComposite extends Composite implements
		Listener {

	private final IWizardHandle wizardHandle;
	private final KarafWizardDataModel model;
	private Text txtUserName;
	private Text txtPortNumber;
	private Text txtPassword;
	private boolean valid = true;

	public KarafServerPorpertiesComposite(Composite parent,
			IWizardHandle wizardHandle, KarafWizardDataModel model) {
		super(parent, SWT.NONE);
		this.wizardHandle = wizardHandle;
		this.model = model;
		wizardHandle.setTitle(Messages.KarafServerPorpertiesComposite_wizard_title);
		wizardHandle
				.setDescription(Messages.KarafServerPorpertiesComposite_wizard_desc);
		wizardHandle.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_RUNTIME));
	}

	@Override
	public void handleEvent(Event event) {
		boolean val = false;
		if (event.type == SWT.FocusIn) {
			handleFocusEvent(event);
		} else {
			if (event.widget == txtPortNumber) {
				val = validate();
				if (val) {
					try {
						model.setPortNumber(Integer.parseInt(txtPortNumber
								.getText()));
					} catch (NumberFormatException ne) {
						// shdn't happen
					}
				}
			} else if (event.widget == txtUserName) {
				model.setUserName(txtUserName.getText());
			} else if (event.widget == txtPassword) {
				model.setPassword(txtPassword.getText());
			}
		}

		wizardHandle.update();
	}

	private void handleFocusEvent(Event event) {

	}

	protected boolean validate() {
		try {
			Integer.parseInt(txtPortNumber.getText().trim());
			valid = true;
		} catch (NumberFormatException ne) {
			valid = false;
			// ignore
		}
		return valid;
	}

	void createContents() {
		setLayout(new GridLayout(2, false));
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;

		Label lblPortNumber = new Label(this, SWT.NONE);
		lblPortNumber.setText(Messages.KarafServerPorpertiesComposite_port_number_label);
		txtPortNumber = new Text(this, SWT.BORDER);
		txtPortNumber.setText(Integer.toString(model.getPortNumber()));
		txtPortNumber.setLayoutData(gd);
		txtPortNumber.setTextLimit(5);
		txtPortNumber.addListener(SWT.Modify, this);

		Label lblUserName = new Label(this, SWT.NONE);
		lblUserName.setText(Messages.KarafServerPorpertiesComposite_user_name_label);
		txtUserName = new Text(this, SWT.BORDER);
		txtUserName.setText(model.getUserName() != null ? model.getUserName() : "");
		txtUserName.setLayoutData(gd);
		txtUserName.addListener(SWT.Modify, this);

		Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText(Messages.KarafServerPorpertiesComposite_password_label);
		txtPassword = new Text(this, SWT.PASSWORD | SWT.BORDER);
		txtPassword.setText(model.getPassword() != null ? model.getPassword() : "");
		txtPassword.setLayoutData(gd);
		txtPassword.addListener(SWT.Modify, this);
		
		wizardHandle.update();
	}

	void performFinish() {
	}

	protected boolean isValid(){
		return valid;
	}
}
