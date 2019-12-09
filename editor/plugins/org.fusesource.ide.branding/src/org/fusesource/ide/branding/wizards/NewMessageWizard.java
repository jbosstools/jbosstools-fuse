/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.branding.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;


/**
 * Wizard for creating new message files
 */
public class NewMessageWizard extends BasicNewFileResourceWizard {

	public NewMessageWizard() {
		super();
		setWindowTitle(WizardMessages.NewMessageWizard_Title);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/new_message_wizard.png"));
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		((WizardNewFileCreationPage)getStartingPage()).setFileExtension("xml");
		((WizardNewFileCreationPage)getStartingPage()).setFileName("message.xml");
		((WizardNewFileCreationPage)getStartingPage()).canFlipToNextPage();
	}
}