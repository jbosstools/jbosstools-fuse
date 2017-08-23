/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.projecttemplates.actions.ui.SwitchCamelVersionDialog;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lheinema
 *
 */
public class SwitchCamelVersionAction implements IObjectActionDelegate {

	/** The current selection (a project). */
	private ISelection selection = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IProject project = (IProject) Selections.getFirstSelection(selection);
			String currentVersion = new CamelMavenUtils().getCamelVersionFromMaven(project, false);
			SwitchCamelVersionDialog dialog = new SwitchCamelVersionDialog(Display.getDefault().getActiveShell());
			dialog.setSelectedCamelVersion(currentVersion);
			if (Window.OK == dialog.open() ) {
				String newVersion = dialog.getSelectedCamelVersion();
				if (!newVersion.equalsIgnoreCase(currentVersion)) {
					new ChangeCamelVersionJob(project, newVersion).schedule();
				}
			}
		} else {
			ProjectTemplatesActivator.pluginLog().logError("Cannot determine target project with selection of type " + selection.getClass().getName());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// not used
	}
}
