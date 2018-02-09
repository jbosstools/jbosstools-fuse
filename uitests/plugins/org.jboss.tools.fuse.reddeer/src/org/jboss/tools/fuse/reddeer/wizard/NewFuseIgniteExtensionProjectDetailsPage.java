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

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents the last page of "New Fuse Ignite Extension Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIgniteExtensionProjectDetailsPage extends WizardPage {

	public NewFuseIgniteExtensionProjectDetailsPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public LabeledText getExtensionIdTXT() {
		return new LabeledText(this, "Extension Id");
	}

	public String getTextExtensionId() {
		return new LabeledText(this, "Extension Id").getText();
	}

	public void setTextExtensionId(String str) {
		new LabeledText(this, "Extension Id").setText(str);
	}

	public LabeledText getVersionTXT() {
		return new LabeledText(this, "Version");
	}

	public String getTextVersion() {
		return new LabeledText(this, "Version").getText();
	}

	public void setTextVersion(String str) {
		new LabeledText(this, "Version").setText(str);
	}

	public LabeledText getNameTXT() {
		return new LabeledText(this, "Name");
	}

	public String getTextName() {
		return new LabeledText(this, "Name").getText();
	}

	public void setTextName(String str) {
		new LabeledText(this, "Name").setText(str);
	}

	public LabeledText getDescriptionTXT() {
		return new LabeledText(this, "Description");
	}

	public String getTextDescription() {
		return new LabeledText(this, "Description").getText();
	}

	public void setTextDescription(String str) {
		new LabeledText(this, "Description").setText(str);
	}

	public LabeledText getTagsTXT() {
		return new LabeledText(this, "Tags");
	}

	public String getTextTags() {
		return new LabeledText(this, "Tags").getText();
	}

	public void setTextTags(String str) {
		new LabeledText(this, "Tags").setText(str);
	}
}
