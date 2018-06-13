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

import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents the last page of "New Fuse Ignite Extension Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIgniteExtensionProjectSecondPage extends WizardPage {

	public NewFuseIgniteExtensionProjectSecondPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public LabeledText getExtensionIdTXT() {
		return new LabeledText(this, "ID");
	}

	public String getTextExtensionId() {
		return new LabeledText(this, "ID").getText();
	}

	public void setTextExtensionId(String str) {
		new LabeledText(this, "ID").setText(str);
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

	public DefaultCombo getFuseIgniteVersionCMB() {
		return new DefaultCombo(this);
	}

	public String getTextFuseIgniteVersion() {
		return new DefaultCombo(this).getText();
	}

	public String getSelectionFuseIgniteVersion() {
		return new DefaultCombo(this).getSelection();
	}

	public List<String> getItemsFuseIgniteVersion() {
		return new DefaultCombo(this).getItems();
	}

	public void setTextFuseIgniteVersion(String str) {
		new DefaultCombo(this).setText(str);
	}

	public PushButton getVerifyBTN() {
		return new PushButton(this, "Verify");
	}

	public void clickVerifyBTN() {
		new PushButton(this, "Verify").click();
	}

	public boolean isSelectedJavaBeanRDB() {
		return new RadioButton("Java bean").isSelected();
	}

	public boolean isSelectedCamelRouteRDB() {
		return new RadioButton("Camel route").isSelected();
	}

	public boolean isSelectedCustomConnectorRDB() {
		return new RadioButton("Custom Connector").isSelected();
	}

	public boolean isSelectedCustomStepRDB() {
		return new RadioButton("Custom Step").isSelected();
	}

	public void toggleJavaBeanRDB(boolean choice) {
		new RadioButton("Java bean").toggle(choice);
	}

	public void toggleCamelRouteRDB(boolean choice) {
		new RadioButton("Camel route").toggle(choice);
	}

	public void toggleCustomConnectorRDB(boolean choice) {
		new RadioButton("Custom Connector").toggle(choice);
	}

	public void toggleCustomStepRDB(boolean choice) {
		new RadioButton("Custom Step").toggle(choice);
	}
}
