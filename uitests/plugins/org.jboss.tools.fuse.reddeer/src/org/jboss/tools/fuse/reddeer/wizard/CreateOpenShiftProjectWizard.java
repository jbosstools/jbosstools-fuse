/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents 'Create OpenShift Project' dialog, which can be invoked on a connection in OpenShift Explorer view
 * 
 * @author tsedmik
 */
public class CreateOpenShiftProjectWizard extends WizardDialog {

	private static final String TITLE = "Create OpenShift Project";

	public CreateOpenShiftProjectWizard() {
		super(TITLE);
	}

	public LabeledText getDescriptionTXT() {
		return new LabeledText(this, "Description:");
	}

	public String getTextDescription() {
		return new LabeledText(this, "Description:").getText();
	}

	public LabeledText getDisplayNameTXT() {
		return new LabeledText(this, "Display Name:");
	}

	public String getTextDisplayName() {
		return new LabeledText(this, "Display Name:").getText();
	}

	public LabeledText getProjectNameTXT() {
		return new LabeledText(this, "Project Name:");
	}

	public String getTextProjectName() {
		return new LabeledText(this, "Project Name:").getText();
	}

	public void setTextDescription(String str) {
		new LabeledText(this, "Description:").setText(str);
	}

	public void setTextDisplayName(String str) {
		new LabeledText(this, "Display Name:").setText(str);
	}

	public void setTextProjectName(String str) {
		new LabeledText(this, "Project Name:").setText(str);
	}
}