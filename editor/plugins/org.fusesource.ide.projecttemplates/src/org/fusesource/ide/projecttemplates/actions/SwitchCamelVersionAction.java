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

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.projecttemplates.actions.ui.SwitchCamelVersionWizard;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lheinema
 *
 */
public class SwitchCamelVersionAction implements IObjectActionDelegate {

	/** The current selection (a project). */
	private ISelection selection = null;
	
	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IProject project = (IProject) Selections.getFirstSelection(selection);
			String currentVersion = new CamelMavenUtils().getCamelVersionFromMaven(project, false);
			SwitchCamelVersionWizard switchCamelVersionWizard = new SwitchCamelVersionWizard(currentVersion);
			if (Window.OK == new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), switchCamelVersionWizard).open() ) {
				String newVersion = switchCamelVersionWizard.getSelectedCamelVersion();
				if (!newVersion.equalsIgnoreCase(currentVersion)) {
					if (shouldWarnAboutDozerAPIBreak(currentVersion, newVersion)) {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.dozerInformationApiBreakTitle, Messages.dozerInformationApiBreakMessage);
					}
					new ChangeCamelVersionJob(project, newVersion).schedule();
				}
			}
		} else {
			ProjectTemplatesActivator.pluginLog().logError("Cannot determine target project with selection of type " + selection.getClass().getName());
		}
	}

	protected boolean shouldWarnAboutDozerAPIBreak(String currentVersion, String newVersion) {
		ComparableVersion maxVersion = new ComparableVersion("2.20.0");
		return maxVersion.compareTo(new ComparableVersion(newVersion)) <= 0 && maxVersion.compareTo(new ComparableVersion(currentVersion)) > 0
				|| maxVersion.compareTo(new ComparableVersion(newVersion)) > 0 && maxVersion.compareTo(new ComparableVersion(currentVersion)) <= 0;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// not used
	}
}
