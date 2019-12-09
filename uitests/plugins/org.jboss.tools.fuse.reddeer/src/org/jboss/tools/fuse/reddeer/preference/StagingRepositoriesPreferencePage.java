/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.preference;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.preference.PreferencePage;
import org.eclipse.reddeer.swt.impl.button.CheckBox;

/**
 * Represents the "Fuse Tooling --> Staging Repositories" preference page
 * 
 * @author tsedmik
 */
public class StagingRepositoriesPreferencePage extends PreferencePage {

	public StagingRepositoriesPreferencePage(ReferencedComposite ref) {
		super(ref, "Fuse Tooling", "Staging Repositories");
	}

	/**
	 * Switch "Enable Staging Repositories" according to the given value
	 * @param value true - turns on staging repositories, false - turns off staging repositories
	 */
	public void toggleStagingRepositories(boolean value) {
		new CheckBox("Enable Staging Repositories").toggle(value);
	}
}
