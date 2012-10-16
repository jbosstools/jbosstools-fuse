package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.Messages;


public abstract class CloudDetailsDeleteAction extends Action {

	public CloudDetailsDeleteAction() {
		super(Messages.jcloud_deleteCloudButton);
		setToolTipText(Messages.jcloud_deleteCloudButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("delete.gif"));
	}

	protected abstract CloudDetails getSelectedCloudDetails();

	@Override
	public void run() {
		CloudDetails details = getSelectedCloudDetails();
		if (details != null) {
			details.delete();
			// If we're still trying to load details, stop.
			CloudDetailsCachedData cachedData = CloudDetailsCachedData.getInstance(details);
			if (cachedData != null) {
				cachedData.cancelLoadingJobs();
			}
			CloudDetails.getCloudDetailList().remove(details);
		}
	}

}
