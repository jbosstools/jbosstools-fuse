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

package org.fusesource.ide.deployment.maven;

import java.io.File;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.fusesource.ide.commons.ui.drop.DropHandlerSupport;
import org.fusesource.ide.deployment.DeployPlugin;


public class ProjectDropHandler extends DropHandlerSupport {
	private final ProjectDropTarget target;

	public ProjectDropHandler(ProjectDropTarget target) {
		this.target = target;
	}

	@Override
	protected void dropFile(Object object) {
		if (object instanceof IProject) {
			dropProject((IProject) object);
		}
		super.dropFile(object);
	}

	public void dropProject(IProject project) {
		Model model = null;
		IFile file = project.getFile("pom.xml");
		if (file != null) {
			String pomPath = file.getRawLocation().toOSString();
			File pomFile = new File(pomPath);
			if (pomFile.exists()) {
				try {
					model = MavenPlugin.getMaven().readModel(pomFile);
				} catch (CoreException e) {
					DeployPlugin.getLogger().error("Failed to parse " + pomPath + ". Reason: " + e, e);
				}
			}
		}
		target.dropProject(project, model);
	}


	@Override
	public void dropIFile(IFile ifile) {
		// TODO
	}

	@Override
	public void dropFile(File resource) {
		// TODO
	}
}
