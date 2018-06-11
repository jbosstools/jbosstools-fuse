/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.fusesource.ide.foundation.ui.Messages;

/**
 * @author lheinema
 */
public class ProjectCreationInfoPage extends WizardPage {

	public ProjectCreationInfoPage(ImageDescriptor wizBan) {
		super(Messages.newProjectWizardInfoPageName);
		setTitle(Messages.newProjectWizardInfoPageTitle);
		setDescription(Messages.newProjectWizardInfoPageDescription);
		setImageDescriptor(wizBan);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 10);
		Label infoText = new Label(container, SWT.FLAT | SWT.READ_ONLY);
		infoText.setLayoutData(gridData);
		infoText.setText(Messages.newProjectWizardInfoPageText);
		infoText.setToolTipText(Messages.newProjectWizardInfoPageDescription);
		setControl(container);
		infoText.setFocus();
	}
	
	private static void processUIEvents() {
		int count = 0;
		while (Display.getDefault().readAndDispatch() && count++ < 100) {
			// process UI events
		}
	}
	
	public static void showProjectCreationInformationPage(Wizard wizard, ProjectCreationInfoPage infoPageInstance) {
		wizard.getContainer().showPage(infoPageInstance);
		infoPageInstance.setPageComplete(true);
		// this call is needed for Linux to make the info page appear right after click on Finish
		processUIEvents();
	}
}
