package org.fusesource.ide.fabric.actions.jclouds;

import org.fusesource.ide.commons.ui.form.FormDialogSupport;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudDetailsDialog extends FormDialogSupport {

	public CloudDetailsDialog() {
		super(Messages.jclouds_cloudDetails);
		setForm(new CloudDetailsForm(this));
	}

	@Override
	public CloudDetailsForm getForm() {
		return (CloudDetailsForm) super.getForm();
	}

	public CloudDetails getCloudDetails() {
		return getForm().getDetails();
	}


}
