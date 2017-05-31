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
import org.jboss.reddeer.swt.impl.button.CheckBox;

/**
 * Represents the "Fuse Tooling --> Editor" preference page
 * 
 * @author tsedmik
 */
public class FuseToolingEditorPreferencePage extends PreferencePage {

	public FuseToolingEditorPreferencePage() {
		super("Fuse Tooling", "Editor");
	}

	/**
	 * Sets checkbox "If enabled the ID values will be used for labels if existing"
	 * 
	 * @param value
	 *            true - check the checkbox, false - uncheck the checkbox
	 */
	public void setShowIDinEditor(boolean value) {

		new CheckBox("If enabled the ID values will be used for labels if existing").toggle(value);
	}
}
