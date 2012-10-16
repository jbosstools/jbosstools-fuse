package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.actions.Messages;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class CloudDetailsEditAction extends Action {

	public CloudDetailsEditAction() {
		super(Messages.jcloud_editCloudButton);
		setToolTipText(Messages.jcloud_editCloudButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("prop_ps.gif"));
	}

	protected abstract CloudDetails getSelectedCloudDetails();

	protected abstract void onCloudDetailsEdited(Object found);


	@Override
	public void run() {
		CloudDetailsDialog dialog = new CloudDetailsDialog() {

			@Override
			protected void okPressed() {
				ConfigurationDetails details = getCloudDetails();
				super.okPressed();
				editCloud(details);
			}

		};
		CloudDetails selectedCloud = getSelectedCloudDetails();
		if (selectedCloud != null) {
			// lets create a new copy
			CloudDetails copy = CloudDetails.copy(selectedCloud);
			dialog.getForm().setDetails(copy);
		}
		dialog.open();
	}

	protected void editCloud(final ConfigurationDetails cloudDetails) {
		cloudDetails.flush();
		try {
			CloudDetails.reloadCloudDetailList();
		} catch (Exception e) {
			FabricPlugin.getLogger().error(e);
		}

		// now lets select the one with this id
		Object found = Iterables.find(CloudDetails.getCloudDetailList(), new Predicate<CloudDetails>(){

			@Override
			public boolean apply(CloudDetails details) {
				return Objects.equal(cloudDetails.getId(), details.getId());
			}});

		if (found != null){
			onCloudDetailsEdited(found);
		}
	}

}
