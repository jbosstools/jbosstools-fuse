package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric.actions.Messages;


public class CloudContainerDetailsWizardPage extends WizardPage implements ICanValidate {
	private CloudContainerDetailsForm form;
	private final CreateJCloudsContainerWizard wizard;

	public CloudContainerDetailsWizardPage(CreateJCloudsContainerWizard wizard) {
		super(Messages.jclouds_chooseAgentDetailsTitle);
		this.wizard = wizard;
		setDescription(Messages.jclouds_chooseAgentDetailsDescription);
	}

	public CloudContainerDetailsForm getForm() {
		return form;
	}

	@Override
	public void createControl(Composite parent) {
		form = new CloudContainerDetailsForm(this, wizard.getVersionNode(), wizard.getSelectedAgent(), wizard.getDefaultAgentName(), wizard.getSelectedProfile());
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
