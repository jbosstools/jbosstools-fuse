/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.maven.MavenUtils;

/**
 * @author  lheinema
 */
public class ChangeCamelVersionJob extends WorkspaceJob {

	private IProject project;
	private String newVersion;
	
	/**
	 * Creates a new camel version change job
	 * 
	 * @param project		the project to use
	 * @param newVersion	the new camel version to set. This version must have been checked to be valid before providing it.
	 */
	public ChangeCamelVersionJob(IProject project, String newVersion) {
		super(Messages.switchCamelVersionDialogName);
		this.project = project;
		this.newVersion = newVersion;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		if (!MavenUtils.configureCamelVersionForProject(project, newVersion, monitor)) {
			return Status.CANCEL_STATUS;
		}
		project.setSessionProperty(CamelMavenUtils.CAMEL_VERSION_QNAME, newVersion);
		Display.getDefault().asyncExec( () -> {
			try {
				notifyOpenEditors();
			} catch (PartInitException ex) {
				ProjectTemplatesActivator.pluginLog().logError(ex);
			}
		});
		return Status.OK_STATUS;
	}
	
	private void notifyOpenEditors() throws PartInitException {
		List<IEditorReference> openCamelEditors = findOpenCamelEditors();
		
		for (IEditorReference eRef : openCamelEditors) {
			CamelXMLEditorInput input = getCamelEditorInput(eRef);
			if (input == null) {
				// unable to determine the input of the editor - ignore
				continue;
			}

			if (input.getCamelContextFile().getProject().equals(this.project)) {
				// that editor should be reopened
				IEditorPart editor = eRef.getEditor(true);
				if (!editor.isDirty()) {
					editor.getEditorSite().getPage().closeEditor(editor, false);
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), input.getCamelContextFile()); 
				} else {
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), input, CamelUtils.CAMEL_EDITOR_ID);
					MessageDialog.openWarning(editor.getEditorSite().getShell(), Messages.reOpenCamelEditorAfterVersionVersionChangeDialogTitle, Messages.reOpenCamelEditorAfterVersionVersionChangeDialogText);
				}
			}
		}
	}

	private CamelXMLEditorInput getCamelEditorInput(IEditorReference eRef) throws PartInitException {
		IEditorInput einput = eRef.getEditorInput();
		if (einput instanceof CamelXMLEditorInput) {
			return (CamelXMLEditorInput)einput;
		}
		return null;
	}
	
	private List<IEditorReference> findOpenCamelEditors() {
		List<IEditorReference> openCamelEditors = new ArrayList<>();
		
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null && PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
			for (IEditorReference eRef : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {
				if (CamelUtils.CAMEL_EDITOR_ID.equals(eRef.getId())) {
					// we found an open camel editor
					openCamelEditors.add(eRef);
				}
			}
		}
		
		return openCamelEditors;
	}
}
