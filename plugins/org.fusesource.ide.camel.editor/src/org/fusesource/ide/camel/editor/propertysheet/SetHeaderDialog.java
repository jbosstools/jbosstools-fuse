package org.fusesource.ide.camel.editor.propertysheet;

import org.apache.camel.model.SetHeaderDefinition;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.ui.form.FormDialogSupport;


public class SetHeaderDialog extends FormDialogSupport {

	public SetHeaderDialog(SetHeaderDefinition definition, Refreshable refreshable) {
		super("Set Header Details");
		setForm(new SetHeaderForm(definition, refreshable));
	}

	@Override
	public SetHeaderForm getForm() {
		return (SetHeaderForm) super.getForm();
	}

	public SetHeaderDefinition getSetHeader() {
		return getForm().getDefinition();
	}

	public static void showDialog(SetHeaderDefinition sh, Refreshable refreshable) {
		SetHeaderDialog dialog = new SetHeaderDialog(sh, refreshable);
		dialog.open();
	}


}
