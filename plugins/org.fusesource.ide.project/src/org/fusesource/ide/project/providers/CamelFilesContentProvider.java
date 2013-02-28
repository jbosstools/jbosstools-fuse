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

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.branding.Activator;

/**
 * @author lhein
 * 
 */
public class CamelFilesContentProvider implements ITreeContentProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;

			if (!project.isAccessible()) {
			    return new Object[0];
			}

			CamelVirtualFolder cvf = new CamelVirtualFolder(project);
			cvf.populateChildren();

			boolean validNature = false;
			try {
				IProjectDescription description = project.getDescription();
				String[] natures = description.getNatureIds();
				for (int i = 0; i < natures.length; ++i) {
					if (Activator.CAMEL_NATURE_ID.equals(natures[i])) {
						validNature = true;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (!validNature && cvf.getCamelFiles().size()>0) {
				// add camel nature
				try {
					addCamelNature(project, null);
				} catch (CoreException ex) {
					System.err.println("Unable to set Camel Nature on the project...");
				} finally {
					validNature = true;
				}
			}

			if (validNature) {
				CamelVirtualFolder[] resVal = new CamelVirtualFolder[1];
				resVal[0] = cvf;
				return resVal;
			}
			return new Object[0];
		} else if (parentElement instanceof CamelVirtualFolder) {
			CamelVirtualFolder cvf = (CamelVirtualFolder)parentElement;
			if (cvf.getCamelFiles().size()<1) {
				cvf.populateChildren();
			}
			return cvf.getCamelFiles().toArray(new IFile[cvf.getCamelFiles().size()]);
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	/**
	 * adds the Camel nature to the project
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	private void addCamelNature(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] ids = projectDescription.getNatureIds();
		String[] newIds = new String[ids.length + 1];
		System.arraycopy(ids,0,newIds,0,ids.length);
		newIds[ids.length] = Activator.CAMEL_NATURE_ID;
		projectDescription.setNatureIds(newIds);
		project.setDescription(projectDescription, monitor);
	}

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
			IPath p = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().append(project.getFullPath());
			try {
				findFiles(p.toFile());
			} catch (CoreException ex) {
				// ignore
			}
		}

		private void findFiles(File folder) throws CoreException {
			// ignore the target folder
			if (folder.getName().equalsIgnoreCase("target") && folder.getParentFile().getName().equalsIgnoreCase(project.getName())) return;
			File[] files = folder.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
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

	public class CamelVirtualFile extends org.eclipse.core.internal.resources.File {

		/**
		 * 
		 */
		public CamelVirtualFile(org.eclipse.core.internal.resources.File file) {
			super(file.getFullPath(), (Workspace)file.getWorkspace());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.internal.resources.Resource#getName()
		 */
		@Override
		public String getName() {
			return getFullPath().toString();
		}
	}
}
