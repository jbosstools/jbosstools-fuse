package org.fusesource.ide.fabric.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabrics;


public class CreateFabricAction extends Action {
	private final Fabrics fabrics;

	public CreateFabricAction(Fabrics fabrics) {
		super(Messages.fabricAddLabel);
		this.fabrics = fabrics;
		setToolTipText(Messages.fabricAddTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("new_fabric.png"));
	}


	@Override
	public void run() {
		InputDialog dialog = new InputDialog(Shells.getShell(), Messages.fabricAddDialogTitle,
				Messages.fabricAddDialogMessage, "", null);
		int result = dialog.open();
		if (result == Window.OK) {
			String name = dialog.getValue();
			String urls = name;
			createFabric(name, urls);
		}
	}

	protected void createFabric(String name, String urls) {
		fabrics.addFabric(name, urls);
		//fabrics.refresh();
	}
}
