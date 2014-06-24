/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;

/**
 * Computes the default source lookup path for a Camel launch configuration.
 * The default source lookup path is the folder or project containing 
 * the Camel context being launched. If the camel context is not specified, the workspace
 * is searched by default.
 * 
 * @author lhein
 */
public class CamelSourcePathComputerDelegate implements
		ISourcePathComputerDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		
		// we store the context file to run in the ATTR_FILE
		IFile file = CamelDebugRegistry.getInstance().getEntry(configuration).getEditorInput().getFile();
				
		ISourceContainer sourceContainer = null;
		if (file != null) {
			sourceContainer = new DirectorySourceContainer(file.getLocation().toFile().getParentFile(), true);
		}
		
		if (sourceContainer == null) {
			sourceContainer = new WorkspaceSourceContainer();
		}
		return new ISourceContainer[]{sourceContainer};
	}

}
