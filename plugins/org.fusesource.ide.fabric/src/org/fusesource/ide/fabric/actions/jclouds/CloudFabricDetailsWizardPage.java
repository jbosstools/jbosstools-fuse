package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudFabricDetailsWizardPage extends WizardPage implements ICanValidate {
	private CloudFabricDetailsForm form;
	private final CreateJCloudsFabricWizard wizard;

	public CloudFabricDetailsWizardPage(CreateJCloudsFabricWizard wizard) {
		super(Messages.jclouds_fabricDetailsTitle);
		this.wizard = wizard;
		setDescription(Messages.jclouds_fabricDetailsDescription);
	}


	public CloudFabricDetailsForm getForm() {
		return form;
	}

	@Override
	public void createControl(Composite parent) {
		form = new CloudFabricDetailsForm(this, wizard.getDefaultAgentName());
		form.createWizardArea(parent);
		setControl(form.getForm().getContent());
		updateSelectedCloud();
	}

	@Override
	public boolean isPageComplete() {
		return form.isValid();
	}

	@Override
	public void validate() {
		setPageComplete(isPageComplete());
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			updateSelectedCloud();
		}
	}

	protected void updateSelectedCloud() {
		form.setSelectedCloud(wizard.getSelectedCloud());
	}


}
