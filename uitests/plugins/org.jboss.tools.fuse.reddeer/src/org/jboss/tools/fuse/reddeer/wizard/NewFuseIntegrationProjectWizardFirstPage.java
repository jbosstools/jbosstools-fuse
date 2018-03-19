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
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * Represents the first page of "New Fuse Integration Project" wizard
 * 
 * @author tsedmik
 */
public class NewFuseIntegrationProjectWizardFirstPage extends WizardPage {

	private static final String PROJECT_NAME_LABEL = "Project Name";
	private static final String LOCATION_GROUP_LABEL = "Location";
	private static final String LOCATION_PATH_LABEL = "Path";
	private static final String DEFAULT_WORKSPACE_LABEL = "Use default workspace location";

	public NewFuseIntegrationProjectWizardFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public void setProjectName(String name) {
		new LabeledText(this, PROJECT_NAME_LABEL).setText(name);
	}

	public void setLocation(String path) {
		new LabeledText(new DefaultGroup(LOCATION_GROUP_LABEL), LOCATION_PATH_LABEL).setText(path);
	}

	public void useDefaultLocation(boolean choice) {
		new CheckBox(new DefaultGroup(LOCATION_GROUP_LABEL), DEFAULT_WORKSPACE_LABEL).toggle(choice);
	}

	public boolean isPathEditable() {
		return new LabeledText(new DefaultGroup(LOCATION_GROUP_LABEL), LOCATION_PATH_LABEL).isEnabled();
	}
}
