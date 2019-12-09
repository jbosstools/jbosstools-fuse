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

package org.fusesource.ide.foundation.ui.views;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.Selections;


public class DynamicPropertySheetTracker {
	public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";
	private static final boolean compareInstances = true;

	private Class<?> oldPageType = PropertySheetPage.class;
	private IPropertySheetPage oldPage;


	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		PropertySheet propertySheet = getPropertySheet();
		if (propertySheet == null) {
			return;
		}
		IPropertySheetPage answer = null;
		Object first = Selections.getFirstSelection(selection);
		if (first instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) first;
			answer = adaptable.getAdapter(IPropertySheetPage.class);
		}
		if (answer == null) {
			answer = new PropertySheetPage();
		}
		Class<?> newPageType = answer.getClass();
		boolean isNew;
		if (compareInstances) {
			isNew = !Objects.equal(answer, oldPage);
		} else {
			isNew = newPageType != oldPageType;
		}
		if (isNew) {
			changePropertySheet(answer, propertySheet, part, selection);
			if (compareInstances) {
				oldPage = answer;
			} else {
				oldPageType = newPageType;
			}
			return;
		}
	}

	private void changePropertySheet(IPropertySheetPage page, PropertySheet propertySheet, IWorkbenchPart part,
			ISelection selection) {
		if (part != null) {
			propertySheet.partClosed(part);
			propertySheet.partActivated(part);

			// lets check if we have created and initialised a different page instance in the activation...
			IPage currentPage = propertySheet.getCurrentPage();
			IPropertySheetPage selectionPage = page;
			if (currentPage instanceof IPropertySheetPage) {
				selectionPage = (IPropertySheetPage) currentPage;

				// now lets dispose the old page we are not using
				page.dispose();
			}

			if (selectionPage instanceof TabFolderSupport2) {
				TabFolderSupport2 tfs = (TabFolderSupport2) selectionPage;
				tfs.init(propertySheet);
			}
			if (selection != null) {
				selectionPage.selectionChanged(part, selection);
			}

		}
	}

	protected PropertySheet getPropertySheet() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IWorkbenchPage page = wbw.getActivePage();
				if (page != null) {
					try {
						IViewPart view = page.findView(PROPERTIES_VIEW_ID);
						if (view instanceof PropertySheet) {
							return (PropertySheet) view;
						}
					} catch (Exception ex) {
						FoundationUIActivator.pluginLog().logError(ex);
					}
				}
			}
		}
		return null;
	}

}
