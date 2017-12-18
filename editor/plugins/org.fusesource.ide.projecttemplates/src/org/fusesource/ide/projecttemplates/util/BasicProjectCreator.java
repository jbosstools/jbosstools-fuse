/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * a basic project creator which only creates the project folder
 * and some basic configuration
 *
 * @author lhein
 */
public class BasicProjectCreator {

	private IProject project;
	private CommonNewProjectMetaData metadata;

	public BasicProjectCreator(CommonNewProjectMetaData metadata) {
		this.metadata = metadata;
	}

	/**
	 * creates the project
	 *
	 * @param monitor
	 * @return	true on success
	 */
	public boolean create(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.basicProjectCreatorCreatingProjectMonitorMessage, 2);
		try {
			// first create the project
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject(metadata.getProjectName());
			final IPath specificLocationPath = metadata.getLocationPath();
			if (specificLocationPath != null) {
				IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(metadata.getProjectName());
				description.setLocation(specificLocationPath);
				project.create(description, subMonitor.split(1));
			} else {
				project.create(subMonitor.split(1));
			}
			project.open(subMonitor.split(1));
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}

	/**
	 * returns the created project
	 *
	 * @return
	 */
	public IProject getProject() {
		return project;
	}
}
