/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.wizard;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents the first page of "New Fuse Ignite Extension Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIgniteExtensionProjectFirstPage extends WizardPage {

	public NewFuseIgniteExtensionProjectFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public PushButton getBrowseBTN() {
		return new PushButton(new DefaultGroup(this, "Location"),"Browse");
	}

	public String getTextPath() {
		return new LabeledText(new DefaultGroup(this, "Location"),"Path").getText();
	}

	public LabeledText getPathTXT() {
		return new LabeledText(new DefaultGroup(this, "Location"),"Path");
	}

	public String getTextProjectName() {
		return new LabeledText(this, "Project Name").getText();
	}

	public void setTextPath(String str) {
		new LabeledText(new DefaultGroup(this, "Location"),"Path").setText(str);
	}

	public void setTextProjectName(String str) {
		new LabeledText(this, "Project Name").setText(str);
	}

	public CheckBox getUseDefaultWorkspaceLocationCHBgroup() {
		return new CheckBox(new DefaultGroup(this, "Location"),"Use default workspace location");
	}

	public void toggleUseDefaultWorkspaceLocationGroup(boolean choice) {
		new CheckBox(new DefaultGroup(this, "Location"),"Use default workspace location").toggle(choice);
	}

	public void clickBrowse() {
		new PushButton(new DefaultGroup(this, "Location"),"Browse").click();
	}
}
