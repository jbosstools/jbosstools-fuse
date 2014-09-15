/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric8.ui.actions.jclouds;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.fabric8.ui.actions.Messages;


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
