package org.fusesource.ide.commons.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;

public class Wizards {

	public static void openWizardDialog(IWizard wizard) {
		WizardDialog dialog = new WizardDialog(Shells.getShell(), wizard);
		dialog.create();
		dialog.open();
	}

}
