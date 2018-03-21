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

import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.OPENSHIFT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.EAP;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.SPRINGBOOT;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;

/**
 * Represents the second page of "New Fuse Integration Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIntegrationProjectWizardRuntimePage extends WizardPage {

	private static final String CAMEL_VERSION_GROUP_LABEL = "Select the Camel version";
	private static final String DEPLOYMENT_GROUP_LABEL = "Choose the deployment platform";
	private static final String RUNTIME_GROUP_LABEL = "Choose the runtime environment";
	private static final String CAMEL_VERSION_GROUP_VERIFY_BTN_LABEL = "Verify";

	public NewFuseIntegrationProjectWizardRuntimePage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public void setDeploymentType(NewFuseIntegrationProjectWizardDeploymentType deployment) {
		new RadioButton(new DefaultGroup(DEPLOYMENT_GROUP_LABEL), deployment.getLabel()).toggle(true);
	}

	public NewFuseIntegrationProjectWizardDeploymentType getDeploymentType() {
		if (new RadioButton(new DefaultGroup(DEPLOYMENT_GROUP_LABEL), STANDALONE.getLabel()).isSelected()) {
			return STANDALONE;
		} else {
			return OPENSHIFT;
		}
	}

	public void setRuntimeType(NewFuseIntegrationProjectWizardRuntimeType runtime) {
		new RadioButton(new DefaultGroup(RUNTIME_GROUP_LABEL), runtime.getLabel()).toggle(true);
	}

	public NewFuseIntegrationProjectWizardRuntimeType getRuntimeType() {
		if (new RadioButton(new DefaultGroup(RUNTIME_GROUP_LABEL), EAP.getLabel()).isSelected()) {
			return EAP;
		}
		if (new RadioButton(new DefaultGroup(RUNTIME_GROUP_LABEL), KARAF.getLabel()).isSelected()) {
			return KARAF;
		}
		return SPRINGBOOT;
	}

	public boolean isEnabledEAPRuntime() {
		return new RadioButton(new DefaultGroup(RUNTIME_GROUP_LABEL), EAP.getLabel()).isEnabled();
	}

	public boolean isEnabledKarafRuntime() {
		return new RadioButton(new DefaultGroup(RUNTIME_GROUP_LABEL), KARAF.getLabel()).isEnabled();
	}

	public boolean isEnabledSpringBootRuntime() {
		return new RadioButton(new DefaultGroup(RUNTIME_GROUP_LABEL), SPRINGBOOT.getLabel()).isEnabled();
	}

	public List<String> getKarafRuntimes() {
		return new DefaultCombo(new DefaultGroup(RUNTIME_GROUP_LABEL), 0).getItems();
	}

	public void selectKarafRuntime(String name) {
		new DefaultCombo(new DefaultGroup(RUNTIME_GROUP_LABEL), 0).setSelection(name);
	}

	public List<String> getEAPRuntimes() {
		return new DefaultCombo(new DefaultGroup(RUNTIME_GROUP_LABEL), 1).getItems();
	}

	public void selectEAPRuntime(String name) {
		new DefaultCombo(new DefaultGroup(RUNTIME_GROUP_LABEL), 1).setSelection(name);
	}

	public void clickNewRuntimeButton() {
		PushButton karafBTN = new PushButton(new DefaultGroup(RUNTIME_GROUP_LABEL), 0);
		PushButton eapBTN = new PushButton(new DefaultGroup(RUNTIME_GROUP_LABEL), 1);
		if (karafBTN.isEnabled()) {
			karafBTN.click();
		}
		if (eapBTN.isEnabled()) {
			eapBTN.click();
		}
	}

	public List<String> getAllAvailableCamelVersions() {
		List<String> items = new ArrayList<>();
		for (String s : new DefaultCombo(new DefaultGroup(CAMEL_VERSION_GROUP_LABEL), 0).getItems()) {
			items.add(s.split("\\s")[0].trim());
		}
		return items;
	}

	public String getSelectedCamelVersion() {
		return new DefaultCombo(new DefaultGroup(CAMEL_VERSION_GROUP_LABEL), 0).getText().split("\\s")[0].trim();
	}

	public boolean isCamelVersionComboEditable() {
		return new DefaultCombo(new DefaultGroup(CAMEL_VERSION_GROUP_LABEL), 0).isEnabled();
	}

	public void selectCamelVersion(String version) {
		new DefaultCombo(new DefaultGroup(CAMEL_VERSION_GROUP_LABEL), 0).setSelection(version);
	}

	public void typeCamelVersion(String version) {
		new DefaultCombo(new DefaultGroup(CAMEL_VERSION_GROUP_LABEL), 0).setText(version);
	}

	public void clickVerifyCamelVersionButton() {
		new PushButton(new DefaultGroup(CAMEL_VERSION_GROUP_LABEL), CAMEL_VERSION_GROUP_VERIFY_BTN_LABEL).click();
	}
}
