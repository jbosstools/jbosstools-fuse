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

import org.eclipse.reddeer.common.condition.WaitCondition;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.jface.condition.WindowIsAvailable;
import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.api.Button;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.YesButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;

/**
 * Represents "New OpenShift Connection" Wizard
 * 
 * @author tsedmik
 */
public class NewOpenShiftConnectionWizard extends WizardDialog {

	private static final String SSL_CERT = "Untrusted SSL Certificate";
	private static final String TITLE = "New OpenShift Connection";

	public NewOpenShiftConnectionWizard() {
		super(TITLE);
	}

	@Override
	public void finish() {
		checkShell();
		Button button = new FinishButton();
		button.click();

		// Accept 'Untrusted SSL Certificate'
		WaitCondition condition = new ShellIsAvailable(SSL_CERT);
		new WaitUntil(condition, false);
		if (condition.test()) {
			new YesButton().click();
			new WaitWhile(condition);
		}
		
		new WaitWhile(new WindowIsAvailable(this), TimePeriod.LONG);
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}


	public LabeledCombo getServerCMB() {
		return new LabeledCombo(this, "Server:");
	}

	public String getTextServer() {
		return new LabeledCombo(this, "Server:").getText();
	}

	public String getSelectionServer() {
		return new LabeledCombo(this, "Server:").getSelection();
	}

	public List<String> getItemsServer() {
		return new LabeledCombo(this, "Server:").getItems();
	}

	public void setSelectionServer(String str) {
		new LabeledCombo(this, "Server:").setText(str);
	}

	public CheckBox getUseDefaultServerCHB() {
		return new CheckBox(this, "Use default server");
	}
	
	public void toggleUseDefaultServerCHB(boolean choice) {
		new CheckBox(this, "Use default server").toggle(choice);
	}
	
	public void clickBrowseBTN() {
		new PushButton(this, "Browse...").click();
	}

	public void clickDiscoverBTN() {
		new PushButton(this, "Discover...").click();
	}

	public void clickAdvancedBTN() {
		new PushButton(this, " Advanced >> ").click();
	}

	public PushButton getBrowseBTN() {
		return new PushButton(this, "Browse...");
	}

	public String getTextBrowse() {
		return new PushButton(this, "Browse...").getText();
	}

	public CheckBox getOverrideOcLocationCHB() {
		return new CheckBox(this, "Override 'oc' location: ");
	}

	public String getTextOverrideOcLocation() {
		return new CheckBox(this, "Override 'oc' location: ").getText();
	}

	public LabeledText getClusterNamespaceTXT() {
		return new LabeledText(this, "Cluster namespace:");
	}

	public String getTextClusterNamespace() {
		return new LabeledText(this, "Cluster namespace:").getText();
	}

	public PushButton getDiscoverBTN() {
		return new PushButton(this, "Discover...");
	}

	public String getTextDiscover() {
		return new PushButton(this, "Discover...").getText();
	}

	public LabeledText getImageRegistryURLTXT() {
		return new LabeledText(this, "Image Registry URL:");
	}

	public String getTextImageRegistryURL() {
		return new LabeledText(this, "Image Registry URL:").getText();
	}

	public PushButton getAdvancedBTN() {
		return new PushButton(this, " Advanced >> ");
	}

	public String getTextAdvanced() {
		return new PushButton(this, " Advanced >> ").getText();
	}

	public CheckBox getSaveTokenCouldTriggerSecureStorageLoginCHBgroup() {
		return new CheckBox(new DefaultGroup("Authentication"), "Save token (could trigger secure storage login)");
	}

	public String getTextSaveTokenCouldTriggerSecureStorageLogin() {
		return new CheckBox(new DefaultGroup("Authentication"), "Save token (could trigger secure storage login)")
				.getText();
	}

	public LabeledText getTokenTXT() {
		return new LabeledText(new DefaultGroup("Authentication"), "Token");
	}

	public String getTextToken() {
		return new LabeledText(new DefaultGroup("Authentication"), "Token").getText();
	}

	public CheckBox getSavePasswordCouldTriggerSecureStorageLoginCHBgroup() {
		return new CheckBox(new DefaultGroup("Authentication"), "Save password (could trigger secure storage login)");
	}

	public String getTextSavePasswordCouldTriggerSecureStorageLogin() {
		return new CheckBox(new DefaultGroup("Authentication"), "Save password (could trigger secure storage login)")
				.getText();
	}

	public LabeledText getPasswordTXT() {
		return new LabeledText(new DefaultGroup("Authentication"), "Password:");
	}

	public String getTextPassword() {
		return new LabeledText(new DefaultGroup("Authentication"), "Password:").getText();
	}

	public LabeledText getUsernameTXT() {
		return new LabeledText(new DefaultGroup("Authentication"), "Username:");
	}

	public String getTextUsername() {
		return new LabeledText(new DefaultGroup("Authentication"), "Username:").getText();
	}

	public LabeledCombo getProtocolCMB() {
		return new LabeledCombo(new DefaultGroup("Authentication"), "Protocol:");
	}

	public String getTextProtocol() {
		return new LabeledCombo(new DefaultGroup("Authentication"), "Protocol:").getText();
	}

	public String getSelectionProtocol() {
		return new LabeledCombo(new DefaultGroup("Authentication"), "Protocol:").getSelection();
	}

	public List<String> getItemsProtocol() {
		return new LabeledCombo(new DefaultGroup("Authentication"), "Protocol:").getItems();
	}

	public LabeledCombo getServerTypeCMB() {
		return new LabeledCombo(this, "Server type:");
	}

	public String getTextServerType() {
		return new LabeledCombo(this, "Server type:").getText();
	}

	public String getSelectionServerType() {
		return new LabeledCombo(this, "Server type:").getSelection();
	}

	public List<String> getItemsServerType() {
		return new LabeledCombo(this, "Server type:").getItems();
	}

	public LabeledCombo getConnectionCMB() {
		return new LabeledCombo(this, "Connection:");
	}

	public String getTextConnection() {
		return new LabeledCombo(this, "Connection:").getText();
	}

	public String getSelectionConnection() {
		return new LabeledCombo(this, "Connection:").getSelection();
	}

	public List<String> getItemsConnection() {
		return new LabeledCombo(this, "Connection:").getItems();
	}

	public DefaultShell getShellNewOpenShiftConnection() {
		return new DefaultShell(TITLE);
	}

	public String getTextNewOpenShiftConnection() {
		return new DefaultShell(TITLE).getText();
	}

	public boolean isCheckedOverrideOcLocationCHB() {
		return new CheckBox(this, "Override 'oc' location: ").isChecked();
	}

	public boolean isCheckedSaveTokenCouldTriggerSecureStorageLoginGroup() {
		return new CheckBox(new DefaultGroup("Authentication"), "Save token (could trigger secure storage login)")
				.isChecked();
	}

	public boolean isCheckedSavePasswordCouldTriggerSecureStorageLoginGroup() {
		return new CheckBox(new DefaultGroup("Authentication"), "Save password (could trigger secure storage login)")
				.isChecked();
	}

	public boolean isCheckedUseDefaultServerCHB() {
		return new CheckBox(this, "Use default server").isChecked();
	}

	public void setTextClusterNamespace(String str) {
		new LabeledText(this, "Cluster namespace:").setText(str);
	}

	public void setTextImageRegistryURL(String str) {
		new LabeledText(this, "Image Registry URL:").setText(str);
	}

	public void setTextToken(String str) {
		new LabeledText(new DefaultGroup("Authentication"), "Token").setText(str);
	}

	public void setTextPassword(String str) {
		new LabeledText(new DefaultGroup("Authentication"), "Password:").setText(str);
	}

	public void setTextUsername(String str) {
		new LabeledText(new DefaultGroup("Authentication"), "Username:").setText(str);
	}

	public void setSelectionProtocol(String str) {
		new LabeledCombo(new DefaultGroup("Authentication"), "Protocol:").setSelection(str);
	}

	public void setSelectionServerType(String str) {
		new LabeledCombo(this, "Server type:").setText(str);
	}

	public void setSelectionConnection(String str) {
		new LabeledCombo(this, "Connection:").setText(str);
	}

	public void toggleOverrideOcLocationCHB(boolean choice) {
		new CheckBox(this, "Override 'oc' location: ").toggle(choice);
	}

	public void setOCLocation(String path) {
		new DefaultText(this, 4).setText(path);
	}

	public void toggleSaveTokenCouldTriggerSecureStorageLoginGroup(boolean choice) {
		new CheckBox(new DefaultGroup("Authentication"), "Save token (could trigger secure storage login)")
				.toggle(choice);
	}

	public void toggleSavePasswordCouldTriggerSecureStorageLoginGroup(boolean choice) {
		new CheckBox(new DefaultGroup("Authentication"), "Save password (could trigger secure storage login)")
				.toggle(choice);
	}
}
