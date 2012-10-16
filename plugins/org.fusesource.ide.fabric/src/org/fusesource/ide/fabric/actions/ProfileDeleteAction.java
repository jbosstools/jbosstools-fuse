package org.fusesource.ide.fabric.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.fusesource.fabric.api.Profile;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ProfileNode;


public class ProfileDeleteAction extends Action {
	private final ProfileNode node;

	public ProfileDeleteAction(ProfileNode node) {
		super(Messages.profileDeleteLabel);
		this.node = node;
		setToolTipText(Messages.profileDeleteTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("delete.gif"));
	}

	@Override
	public void run() {
		Profile profile = node.getProfile();
		String message = Messages.bind(Messages.deleteProfileMessage, profile.getId());
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), Messages.deleteProfileDialogTitle,
				message);
		if (confirm) {
			node.getFabric().getFabricService().deleteProfile(profile);
			Node parent = node.getParent();
			if (parent != null){
				parent.removeChild(node);
			}
		}
	}

}
