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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.views.DynamicPropertySheetTracker;


public class Workbenches {

	public static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null) {
				return activeWorkbenchWindow.getActivePage();
			}
		}
		return null;
	}

	public static IWorkbench getActiveWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public static IWorkbenchPartSite getActiveWorkbenchPartSite() {
		IWorkbenchPart activePart = getActiveWorkbenchPart();
		if (activePart != null) {
			return activePart.getSite();
		}
		return null;
	}

	public static IWorkbenchPart getActiveWorkbenchPart() {
		IWorkbenchPage page = getActiveWorkbenchPage();
		IWorkbenchPart activePart = null;
		if (page != null) {
			activePart = page.getActivePart();
		}
		return activePart;
	}

	public static IViewPart findView(String id) {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IViewPart view = findView(id, wbw);
				if (view != null){
					return view;
				}
			}
			IWorkbenchWindow[] workbenchWindows = wb.getWorkbenchWindows();
			if (workbenchWindows != null) {
				for (IWorkbenchWindow window : workbenchWindows) {
					IViewPart view = findView(id, window);
					if (view != null){
						return view;
					}
				}
			}
		}
		return null;
	}

	public static IViewPart findView(String id, IWorkbenchWindow wbw) {
		IWorkbenchPage page = wbw.getActivePage();
		if (page != null) {
			try {
				IViewPart view = page.findView(id);
				return view;
			} catch (Exception ex) {
				FoundationUIActivator.pluginLog().logError(ex);
			}
		}
		return null;
	}

	public static IPage getPropertySheetPage() {
		final IViewPart view = findView(DynamicPropertySheetTracker.PROPERTIES_VIEW_ID);
		if (view instanceof PropertySheet) {
			PropertySheet propertySheet = (PropertySheet) view;
			return propertySheet.getCurrentPage();
		}
		return null;
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchPage page = getActiveWorkbenchPage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				return editor;
			}
		}
		return null;
	}

}
