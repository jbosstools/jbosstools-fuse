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
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;

/**
 * Represents the second page of "New Fuse Ignite Extension Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIgniteExtensionProjectDependenciesPage extends WizardPage {

	public NewFuseIgniteExtensionProjectDependenciesPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public LabeledCombo getSpringBootVersionCMB() {
		return new LabeledCombo(this, "Spring Boot Version");
	}

	public String getTextSpringBootVersion() {
		return new LabeledCombo(this, "Spring Boot Version").getText();
	}

	public String getSelectionSpringBootVersion() {
		return new LabeledCombo(this, "Spring Boot Version").getSelection();
	}

	public List<String> getItemsSpringBootVersion() {
		return new LabeledCombo(this, "Spring Boot Version").getItems();
	}

	public void setTextSpringBootVersion(String str) {
		new LabeledCombo(this, "Spring Boot Version").setText(str);
	}

	public LabeledCombo getCamelVersionCMB() {
		return new LabeledCombo(this, "Camel Version");
	}

	public String getTextCamelVersion() {
		return new LabeledCombo(this, "Camel Version").getText();
	}

	public String getSelectionCamelVersion() {
		return new LabeledCombo(this, "Camel Version").getSelection();
	}

	public List<String> getItemsCamelVersion() {
		return new LabeledCombo(this, "Camel Version").getItems();
	}

	public void setTextCamelVersion(String str) {
		new LabeledCombo(this, "Camel Version").setText(str);
	}

	public LabeledCombo getFuseIgniteVersionCMB() {
		return new LabeledCombo(this, "Fuse Ignite Version");
	}

	public String getTextFuseIgniteVersion() {
		return new LabeledCombo(this, "Fuse Ignite Version").getText();
	}

	public String getSelectionFuseIgniteVersion() {
		return new LabeledCombo(this, "Fuse Ignite Version").getSelection();
	}

	public List<String> getItemsFuseIgniteVersion() {
		return new LabeledCombo(this, "Fuse Ignite Version").getItems();
	}

	public void setTextFuseIgniteVersion(String str) {
		new LabeledCombo(this, "Fuse Ignite Version").setText(str);
	}

	public PushButton getVerifyBTN() {
		return new PushButton(this, "Verify");
	}

	public void clickVerifyBTN() {
		new PushButton(this, "Verify").click();
	}
}
