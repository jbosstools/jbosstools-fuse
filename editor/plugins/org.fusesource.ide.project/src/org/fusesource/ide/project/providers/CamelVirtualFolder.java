/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.providers;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.project.Activator;

public class CamelVirtualFolder implements ContextMenuProvider {
	
	private static final String NEW_CAMEL_XML_FILE_WIZARD_ID = "org.fusesource.ide.camel.editor.wizards.NewCamelXmlWizard";
	
	private IProject project;
	private Set<IResource> camelFiles = new HashSet<>();

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

	/**
	 * @return the camelFiles
	 */
	public Set<IResource> getCamelFiles() {
		return this.camelFiles;
	}

	public void populateChildren() {
		if (project != null) {
			camelFiles.addAll(new CamelFilesFinder().findFiles(project));
		}
	}

	class CamelVirtualFolderListener implements IResourceChangeListener {

		private IProject project;

		public CamelVirtualFolderListener(IProject project) {
			this.project = project;
		}

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				try {
					event.getDelta().accept(new DeltaPrinter(project));
				} catch (CoreException ex) {
					Activator.getLogger().error(ex);
				}
			}
		}
	}
	
	class DeltaPrinter implements IResourceDeltaVisitor {

		private IProject project;
		private CamelFilesFinder camelFilesFinder= new CamelFilesFinder();

		public DeltaPrinter(IProject project) {
			this.project = project;
		}

		@Override
		public boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();

			if (resource.getProject() != null
					&& !resource.getProject().equals(project)) {
				// we are not interested in changes of other projects
				return false;
			}

			int deltaKind = delta.getKind();
			if(deltaKind == IResourceDelta.ADDED) {
				visitAddedFile(resource);
			} else if(deltaKind == IResourceDelta.REMOVED) {
				// a resource has been removed, check if we need to remove
				// it from the virtual camel folder
				camelFiles.remove(resource);
			}
			return true; // visit the children
		}

		private void visitAddedFile(IResource resource) {
			if (!camelFilesFinder.isWorkProjectFolder(project, getAncestorDirectChildOfProject(project, resource))) {
				// a resource was added, check if we need to add it the the
				// camel virtual folder too
				try {
					if (resource instanceof IFile && new CamelFilesFinder().isFuseCamelContentType((IFile) resource)) {
						camelFiles.add(resource);
					}
				} catch (CoreException ex) {
					// ignore file
				}
			}
		}
	}
	
	private IResource getAncestorDirectChildOfProject(IProject project, IResource resource){
		IResource parent = resource.getParent();
		if(project.equals(parent) || parent == null){
			return resource;
		} else {
			return getAncestorDirectChildOfProject(project, parent);
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
							   IStructuredSelection sel;
							   if (!getCamelFiles().isEmpty()) {
								   sel = new StructuredSelection(getCamelFiles().iterator().next().getParent());
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
