/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.launchconfigurations;

import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.LaunchConfigurationTab;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.text.DefaultText;

public class DefaultLaunchConfigurationJRETab extends LaunchConfigurationTab {

	/**
	 * Represents 'JRE' tab in Local Camel Context launch configuration
	 * 
	 * @author djelinek
	 */
	public DefaultLaunchConfigurationJRETab() {
		super("JRE");
	}

	public DefaultText getVMArgumentsTXT() {
		return new DefaultText(new DefaultGroup("VM arguments:"));
	}

	public String getTextVMArgumentsTXT() {
		return new DefaultText(new DefaultGroup("VM arguments:"), 0).getText();
	}

	public void setTextVMArgumentsTXT(String str) {
		new DefaultText(new DefaultGroup("VM arguments:"), 0).setText(str);
	}

	public PushButton getVariablesBTN() {
		return new PushButton(new DefaultGroup("Variables..."));
	}

	public void clickVariablesBTN() {
		new PushButton(new DefaultGroup("Variables...")).click();
	}

	public void clickApplyBTN() {
		new PushButton("Apply").click();
	}

	public void clickRevertBTN() {
		new PushButton("Revert").click();
	}

}
