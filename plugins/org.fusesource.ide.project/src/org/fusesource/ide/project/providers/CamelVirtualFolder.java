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
package org.fusesource.ide.project.providers;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class CamelVirtualFolder {
	private IProject project;
	private ArrayList<IFile> camelFiles = new ArrayList<IFile>();

	/**
	 * 
	 */
	public CamelVirtualFolder(IProject prj) {
		this.project = prj;
	}

	/**
	 * @return the project
	 */
	public IProject getProject() {
		return this.project;
	}

	public String getName() {
		return "Camel Contexts";
	}

	public void addCamelFile(IFile file) {
		if (!this.camelFiles.contains(file)) {
			this.camelFiles.add(file);
		}
	}

	/**
	 * @return the camelFiles
	 */
	public ArrayList<IFile> getCamelFiles() {
		return this.camelFiles;
	}

	public void populateChildren() {
		IPath p = project.getRawLocation() != null ? project.getRawLocation() : ResourcesPlugin.getWorkspace().getRoot().getRawLocation().append(project.getFullPath());
		try {
			findFiles(p.toFile());
		} catch (CoreException ex) {
			// ignore
		}
	}

	private void findFiles(File folder) throws CoreException {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					// ignore the target folder
					if (f.getName().equalsIgnoreCase("target") && f.getParentFile().getName().equalsIgnoreCase(project.getName())) continue;
					findFiles(f);
				} else {
				    IFile[] mappedFiles = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(f.toURI());
					IFile ifile = mappedFiles.length > 0 ? mappedFiles[0] : null;
					if (ifile != null && ifile.getContentDescription() != null && ifile.getContentDescription().getContentType().getId().equals("org.fusesource.ide.camel.editor.camelContentType")) {
						addCamelFile(new CamelVirtualFile((org.eclipse.core.internal.resources.File)ifile));
					}
				}
			}

		}
	}
}