/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.launcher.CamelContextLaunchConfigConstants;

/**
 * @author lhein
 *
 */
public class ExecutePomActionNoTests extends ExecutePomActionSupport {

	public ExecutePomActionNoTests() {
		super(
				"org.fusesource.ide.launcher.ui.launchConfigurationTabGroup.camelContext",
				CamelContextLaunchConfigConstants.CAMEL_CONTEXT_NO_TESTS_LAUNCH_CONFIG_TYPE_ID,
				CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS + " -Dmaven.test.skip=true");
	}
	
	protected void appendAttributes(IContainer basedir,
			ILaunchConfigurationWorkingCopy workingCopy, String goal) {

		String path = getSelectedFilePath();
		workingCopy.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE,
				path == null ? "" : path); // basedir.getLocation().toOSString()
	}

	protected String getSelectedFilePath() {
		ISelectionService selService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection isel = selService.getSelection();
		if (isel != null && isel instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection)isel;
			Object elem = ssel.getFirstElement();
			if (elem != null && elem instanceof IFile) {
				IFile f = (IFile)elem;
				return f.getLocationURI().toString();
			}
		}
		return null;
	}
}
