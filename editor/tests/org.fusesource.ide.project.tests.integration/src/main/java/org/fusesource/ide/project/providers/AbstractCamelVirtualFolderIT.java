/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.project.providers;

import org.eclipse.core.resources.IProject;

public class AbstractCamelVirtualFolderIT {

	protected CamelVirtualFolder initializeCamelVirtualFolder(IProject project) {
		CamelVirtualFolder camelVirtualFolder = new CamelVirtualFolder(project);
		camelVirtualFolder.populateChildren();
		return camelVirtualFolder;
	}
}
