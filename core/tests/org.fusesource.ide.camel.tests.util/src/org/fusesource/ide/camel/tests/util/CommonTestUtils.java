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
package org.fusesource.ide.camel.tests.util;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * @author lhein
 *
 */
public class CommonTestUtils {
	
	private CommonTestUtils() {
		//static access only
	}
	
	/**
	 * closes all editors, perspectives, the welcome page and then switches to fuse integration perspective
	 * and creating a screenshot folder
	 * 
	 * @param screenshotFolder	the folder to store screenshots
	 * @throws CoreException 
	 */
	public static void prepareIntegrationTestLaunch(String screenshotFolder) throws CoreException {
		closeAllEditors();
		enablePerspectiveSwitchPreset();
		createScreenshotFolder(screenshotFolder);		
		closeWelcomePage();
		closeAllPerspectives();
		openFuseIntegrationPerspective();
		clearAllCamelBreakpoints();
	}

	private static void clearAllCamelBreakpoints() throws CoreException {
		for (IBreakpoint camelBreakpoint : DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL)) {
			Activator.pluginLog().logError("Breakpoint registered which shouldn't!" + camelBreakpoint);
			camelBreakpoint.delete();
		}
	}

	/**
	 * opens the fuse integration perspective
	 * @throws WorkbenchException 
	 * 
	 */
	public static void openFuseIntegrationPerspective() throws WorkbenchException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		}
	}
	
	/**
	 * closes all perspectives
	 */
	public static void closeAllPerspectives() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			page.closeAllPerspectives(false, false);
		}
	}
	
	/**
	 * closes the welcome page
	 */
	public static void closeWelcomePage() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IWorkbenchPart welcomePage = page.getActivePart();
			if(welcomePage != null){
				welcomePage.dispose();
			}
		}
	}
	
	/**
	 * creates the screenshot folder in the given location 
	 * 
	 * @param folder
	 */
	public static void createScreenshotFolder(String folder) {
		File f = new File(folder);
		f.mkdirs();
	}
	
	/**
	 * sets preference to always switch to the right perspective	
	 */
	public static void enablePerspectiveSwitchPreset() {
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
		DebugUIPlugin.getDefault().getPreferenceStore().setValue(IInternalDebugUIConstants.PREF_SWITCH_PERSPECTIVE_ON_SUSPEND, IDEInternalPreferences.PSPM_ALWAYS);
	}
	
	/**
	 * closes all editors
	 */
	public static void closeAllEditors() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
			if (page != null) {
				page.closeAllEditors(false);
			}
		}
	}
	
	/**
	 * @return
	 */
	public static IEditorPart getCurrentActiveEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}
	
	public static IEditorReference[] getCurrentOpenEditors() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			return page.getEditorReferences();
		}
		return new IEditorReference[0];
	}
	
	/**
	 * process ui events
	 * 
	 * @param currentNumberOfTry
	 */
	public static void readAndDispatch(int currentNumberOfTry) {
		try{
			while (Display.getDefault().readAndDispatch()) {
				// wait
			}
		} catch(SWTException swtException){
			//TODO: remove try catch when https://issues.jboss.org/browse/FUSETOOLS-1913 is done (CI with valid GUI)
			Activator.pluginLog().logWarning(swtException);
			if(currentNumberOfTry < 100){
				readAndDispatch(currentNumberOfTry + 1);
			} else {
				Activator.pluginLog().logError("Tried 100 times to wait for UI... Continue and see what happens.");
			}
		}
	}
}
