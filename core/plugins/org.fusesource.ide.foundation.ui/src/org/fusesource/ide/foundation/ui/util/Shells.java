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

package org.fusesource.ide.foundation.ui.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Shells {

	/**
	 * Returns the current {@link Shell} or null if none could be found
	 */
	public static Shell getShell() {
		Shell answer = null;
		Display current = Display.getCurrent();
		if (current != null) {
			answer = current.getActiveShell();
		}
		if (answer == null) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow activeWorkbenchWindow = workbench
						.getActiveWorkbenchWindow();
				if (activeWorkbenchWindow != null) {
					answer = activeWorkbenchWindow.getShell();
				}
			}
		}
		return answer;
	}

	public static Display getDisplay() {
		Shell shell = getShell();
		final Display display;
		if (shell == null || shell.isDisposed()) {
			display = Display.getCurrent();
			// The dialog should be always instantiated in UI thread.
			// However it was possible to instantiate it in other threads
			// (the code worked in most cases) so the assertion covers
			// only the failing scenario. See bug 107082 for details.
			Assert.isNotNull(display,
					"The dialog should be created in UI thread"); //$NON-NLS-1$
		} else {
			display = shell.getDisplay();
		}
		return display;
	}
}
