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
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.project.Activator;

public class CamelVirtualFolder implements ContextMenuProvider {
	
	private static final String NEW_CAMEL_XML_FILE_WIZARD_ID = "org.fusesource.ide.camel.editor.wizards.NewCamelXmlWizard";
	
	private IProject project;
	private ArrayList<IResource> camelFiles = new ArrayList<IResource>();

	/**
	 * 
	 */
	public CamelVirtualFolder(IProject prj) {
		this.project = prj;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new CamelVirtualFolderListener(project), IResourceChangeEvent.POST_CHANGE);
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

	public void addCamelFile(IResource file) {
		if (!this.camelFiles.contains(file)) {
			this.camelFiles.add(file);
		}
	}

	/**
	 * @return the camelFiles
	 */
	public ArrayList<IResource> getCamelFiles() {
		return this.camelFiles;
	}

	public void populateChildren() {
		IPath p = project.getLocation();
		if (p != null) {
			try {
				findFiles(p.toFile());
			} catch (CoreException ex) {
				// ignore
			}
		}
	}

	private void findFiles(File folder) throws CoreException {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					// ignore the target folder
					if (f.getName().equalsIgnoreCase("target")
							&& f.getParentFile().getName()
									.equalsIgnoreCase(project.getName()))
						continue;
					findFiles(f);
				} else {
					final String FUSE_CAMEL_CONTENT_TYPE = "org.fusesource.ide.camel.editor.camelContentType";
					IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(f.getPath()));

// MASTER
					if (ifile != null) {
						if (ifile.getContentDescription() != null
								&& ifile.getContentDescription()
										.getContentType()
										.getId()
										.equals("org.fusesource.ide.camel.editor.camelContentType")) {
							addCamelFile(ifile);
						}
// END MASTER
					if (ifile != null && ifile.getContentDescription() != null ) {
						IContentType primary = ifile.getContentDescription().getContentType();
						boolean primaryMatches = primary.getId().equals(FUSE_CAMEL_CONTENT_TYPE);
						IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
						// try to find the obvious content type matching its name
						IContentType[] types = contentTypeManager.findContentTypesFor(ifile.getName());
						boolean hasCamelContentType = false;
						for( int i = 0; i < types.length && !hasCamelContentType; i++ ) {
							if( types[i].getId().equals(FUSE_CAMEL_CONTENT_TYPE)) {
								hasCamelContentType = true;
							}
						}
						if( hasCamelContentType )
							addCamelFile(ifile);
					}
				}
			}

		}
	}

	class CamelVirtualFolderListener implements IResourceChangeListener {

		private IProject _project;

		/**
		 * 
		 */
		public CamelVirtualFolderListener(IProject project) {
			this._project = project;
		}

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				try {
					event.getDelta().accept(new DeltaPrinter(_project));
				} catch (CoreException ex) {
					Activator.getLogger().error(ex);
				}
			}
		}
	}

	class DeltaPrinter implements IResourceDeltaVisitor {

		private IProject _project;

		/**
		 * 
		 */
		public DeltaPrinter(IProject project) {
			this._project = project;
		}

		@Override
		public boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();

			if (resource.getProject() != null
					&& !resource.getProject().equals(project)) {
				// we are not interested in changes of other projects
				return true;
			}

			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				if (resource
						.getFullPath()
						.toOSString()
						.contains(
								File.separator + "target" + File.separator
										+ "classes" + File.separator)) {
					// skip target folder
					break;
				}
				// a resource was added, check if we need to add it the the
				// camel virtual folder too
				try {
					if (resource != null
							&& resource instanceof IFile
							&& ((IFile) resource).getContentDescription() != null
							&& ((IFile) resource)
									.getContentDescription()
									.getContentType()
									.getId()
									.equals("org.fusesource.ide.camel.editor.camelContentType")) {
						addCamelFile(resource);
					}
				} catch (CoreException ex) {
					// ignore file
				}
				break;
			case IResourceDelta.REMOVED:
				// a resource has been removed, check if we need to remove
				// it from the virtual camel folder
				if (camelFiles.contains(resource)) {
					camelFiles.remove(resource);
				}
				break;
			}
			return true; // visit the children
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.ContextMenuProvider#provideContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void provideContextMenu(IMenuManager menu) {
		final IWizardDescriptor wiz = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(NEW_CAMEL_XML_FILE_WIZARD_ID);
				
		Action action = new Action() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#getText()
			 */
			@Override
			public String getText() {
				return "New " + wiz.getLabel();
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#getToolTipText()
			 */
			@Override
			public String getToolTipText() {
				return wiz.getDescription();
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				try  {
				   // Then if we have a wizard, open it.
				   if  (wiz != null) {
					   IWizard wizard = wiz.createWizard();
					   WizardDialog wd = new  WizardDialog(Display.getDefault().getActiveShell(), wizard);
					   wd.setTitle(wizard.getWindowTitle());

					   try {
						   Field selection = wizard.getClass().getDeclaredField("selection");
						   if (selection != null) {
							   selection.setAccessible(true);
							   
							   // if there are already other camel context files we use the path the first best is stored under,
							   // otherwise we use the project main folder
							   IStructuredSelection sel = null;
							   if (getCamelFiles().size()>0) {
								   sel = new StructuredSelection(getCamelFiles().get(0).getParent());
							   } else {
								   sel = new StructuredSelection(getProject());
							   }
							   
							   selection.set(wizard, sel);
							   
						   }
					   } catch (Exception ex) {
						   Activator.getLogger().error(ex);
					   }
					   
					   wd.open();
				   }
				} catch  (CoreException e) {
					Activator.getLogger().error(e);
				}
			}
		};
		
		menu.add(action);
	}
}
