package org.fusesource.ide.fabric.actions;

import org.eclipse.jface.action.Action;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.fabric.FabricPlugin;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class FabricDetailsEditAction extends Action {

	public FabricDetailsEditAction() {
		super(Messages.fabricEditButton);
		setToolTipText(Messages.fabricEditButtonTooktip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("prop_ps.gif"));
	}

	protected abstract FabricDetails getSelectedFabricDetails();

	protected abstract void onFabricDetailsEdited(FabricDetails found);


	@Override
	public void run() {
		FabricDetailsDialog dialog = new FabricDetailsDialog() {

			@Override
			protected void okPressed() {
				FabricDetails details = getFabricDetails();
				super.okPressed();
				editCloud(details);
			}

		};
		FabricDetails selectedCloud = getSelectedFabricDetails();
		if (selectedCloud != null) {
			// lets create a new copy
			FabricDetails copy = FabricDetails.copy(selectedCloud);
			dialog.getForm().setDetails(copy);
		}
		dialog.open();
	}

	protected void editCloud(final FabricDetails cloudDetails) {
		cloudDetails.flush();
		try {
			FabricDetails.reloadDetailList();
		} catch (Exception e) {
			FabricPlugin.getLogger().error(e);
		}

		// now lets select the one with this id
		Object found = Iterables.find(FabricDetails.getDetailList(), new Predicate<FabricDetails>(){

			@Override
			public boolean apply(FabricDetails details) {
				return Objects.equal(cloudDetails.getId(), details.getId());
			}});

		if (found instanceof FabricDetails){
			onFabricDetailsEdited((FabricDetails) found);
		}
	}

}
