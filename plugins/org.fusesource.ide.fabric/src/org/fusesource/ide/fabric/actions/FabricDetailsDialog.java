package org.fusesource.ide.fabric.actions;

import org.fusesource.ide.commons.ui.form.FormDialogSupport;


public class FabricDetailsDialog extends FormDialogSupport {

	public FabricDetailsDialog() {
		super(Messages.fabricDetailsDialog);
		setForm(new FabricDetailsForm(this));
	}

	@Override
	public FabricDetailsForm getForm() {
		return (FabricDetailsForm) super.getForm();
	}

	public FabricDetails getFabricDetails() {
		return getForm().getDetails();
	}


}
