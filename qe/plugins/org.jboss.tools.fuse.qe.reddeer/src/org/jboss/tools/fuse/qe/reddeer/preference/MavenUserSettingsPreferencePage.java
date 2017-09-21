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
package org.jboss.tools.fuse.qe.reddeer.preference;

import org.jboss.reddeer.jface.preference.PreferencePage;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.text.DefaultText;

/**
 * Represents "Maven --> User Settings" preference page
 * 
 * @author tsedmik
 */
public class MavenUserSettingsPreferencePage extends PreferencePage {

	public MavenUserSettingsPreferencePage() {
		super("Maven", "User Settings");
	}

	public String getGlobalSettings() {
		return new DefaultText(1).getText();
	}

	public String getUserSettings() {
		return new DefaultText(2).getText();
	}

	public void setGlobalSettings(String path) {
		new DefaultText(1).setText(path);
	}

	public void setUserSettings(String path) {
		new DefaultText(2).setText(path);
	}

	public void updateSettings() {
		new PushButton("Update Settings").click();
	}

	public void reindex() {
		new PushButton("Reindex").click();
	}

	public String getRepositoryLocation() {
		return new DefaultText(3).getText();
	}
}
