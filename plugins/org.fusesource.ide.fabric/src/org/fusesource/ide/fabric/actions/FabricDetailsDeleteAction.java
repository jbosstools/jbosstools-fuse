package org.fusesource.ide.fabric.actions;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.fabric.FabricPlugin;


public abstract class FabricDetailsDeleteAction extends Action {

	public FabricDetailsDeleteAction() {
		super(Messages.fabricDeleteButton);
		setToolTipText(Messages.fabricDeleteButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("delete.gif"));
	}

	protected abstract FabricDetails getSelectedFabricDetails();

	@Override
	public void run() {
		FabricDetails details = getSelectedFabricDetails();
		if (details != null) {
			details.delete();
			FabricDetails.getDetailList().remove(details);
		}
	}

}
