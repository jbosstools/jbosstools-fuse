package org.fusesource.ide.launcher.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.launcher.CamelContextLaunchConfigConstants;


/**
 * Launches the camel run goal
 */
public class ExecutePomAction extends ExecutePomActionSupport {

	public ExecutePomAction() {
		super(
				"org.fusesource.ide.launcher.ui.launchConfigurationTabGroup.camelContext",
				CamelContextLaunchConfigConstants.CAMEL_CONTEXT_LAUNCH_CONFIG_TYPE_ID,
				CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS);
	}

	protected void appendAttributes(IContainer basedir,
			ILaunchConfigurationWorkingCopy workingCopy, String goal) {

		String path = getSelectedFilePath();
		workingCopy.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE,
				path == null ? "" : path); // basedir.getLocation().toOSString()
	}

	protected String getSelectedFilePath() {
		ISelectionService selService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection isel = selService.getSelection();
		if (isel != null && isel instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection)isel;
			Object elem = ssel.getFirstElement();
			if (elem != null && elem instanceof IFile) {
				IFile f = (IFile)elem;
				return f.getLocationURI().toString();
			}
		}
		return null;
	}
}