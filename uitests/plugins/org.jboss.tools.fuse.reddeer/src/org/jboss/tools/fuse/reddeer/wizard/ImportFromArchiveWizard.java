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

import org.eclipse.reddeer.eclipse.selectionwizard.ImportMenuWizard;

/**
 * Represents "Import from Folder or Archive" wizard
 * 
 * @author tsedmik
 */
public class ImportFromArchiveWizard extends ImportMenuWizard {

	public static final String SHELL_NAME = "Import Projects from File System or Archive";
	public static final String WIZARD_CATEGORY = "General";
	public static final String WIZARD_NAME = "Projects from Folder or Archive";

	public ImportFromArchiveWizard() {
		super(SHELL_NAME, WIZARD_CATEGORY, WIZARD_NAME);
	}
}
