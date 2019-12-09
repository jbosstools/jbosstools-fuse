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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.part.IPageSite;
import org.fusesource.ide.foundation.ui.tree.HasViewer;
import org.fusesource.ide.foundation.ui.tree.RefreshableUI;

public class Selections {

	private Selections() {
	}

	public static Object getFirstSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			return structuredSelection.getFirstElement();
		}
		return null;
	}

	public static List<Object> getSelectionList(Viewer viewer) {
		List<Object> answer = new ArrayList<>();
		if (viewer != null) {
			ISelection selection = viewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				Iterator<?> iter = structuredSelection.iterator();
				while (iter.hasNext()) {
					Object next = iter.next();
					answer.add(next);
				}
			}
		}
		return answer;
	}

	public static Object getFirstSelection(Viewer viewer) {
		if (viewer != null) {
			return getFirstSelection(viewer.getSelection());
		}
		return null;
	}

	public static Object getFirstSelection(IWorkbenchSite viewSite) {
		if (viewSite != null) {
			ISelectionProvider selectionProvider = viewSite.getSelectionProvider();
			if (selectionProvider != null) {
				return getFirstSelection(selectionProvider.getSelection());
			}
		}
		return null;
	}

	public static ISelection getSelection(IWorkbenchSite site) {
		if (site != null) {
			return getSelection(site.getSelectionProvider());
		}
		return null;
	}


	protected static ISelection getSelection(ISelectionProvider selectionProvider) {
		if (selectionProvider != null) {
			return selectionProvider.getSelection();
		}
		return null;
	}

	public static IStructuredSelection getStructuredSelection(Viewer site) {
		ISelection selection = getSelection(site);
		if (selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return null;
	}


	public static IStructuredSelection getStructuredSelection(IPageSite site) {
		ISelection selection = getSelection(site);
		if (selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return null;
	}

	public static IStructuredSelection getStructuredSelection(IWorkbenchPartSite site) {
		ISelection selection = getSelection(site);
		if (selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return null;
	}

	public static ISelectionProvider getSelectionProvider(IViewSite viewSite) {
		if (viewSite != null) {
			return viewSite.getSelectionProvider();
		}
		return null;
	}

	public static ISelectionProvider getSelectionProvider(IViewPart view) {
		if (view != null) {
			return getSelectionProvider(view.getViewSite());
		}
		return null;
	}

	public static ISelectionProvider getSelectionProvider(IWorkbenchPart workbenchPart) {
		if (workbenchPart != null) {
			return getSelectionProvider(workbenchPart.getSite());
		}
		return null;
	}

	public static ISelectionProvider getSelectionProvider(IWorkbenchPartSite site) {
		if (site != null) {
			return site.getSelectionProvider();
		}
		return null;
	}

	public static void setSingleSelection(RefreshableUI refreshableUI, Object singleValue) {
		if (refreshableUI instanceof HasViewer) {
			HasViewer v = (HasViewer) refreshableUI;
			Viewer viewer = v.getViewer();
			if (viewer != null) {
				viewer.setSelection(new StructuredSelection(singleValue));
				Viewers.reveal(viewer, singleValue);

			}
		}
	}


	public static void setSelection(RefreshableUI refreshableUI, ISelection selection) {
		if (refreshableUI instanceof HasViewer) {
			HasViewer v = (HasViewer) refreshableUI;
			Viewer viewer = v.getViewer();
			if (viewer != null) {
				viewer.setSelection(selection);
			}
		}
	}

	public static Object getFirstWorkbenchSelection() {
		Object firstSelection = null;
		IWorkbenchPage page = Workbenches.getActiveWorkbenchPage();
		if (page != null) {
			firstSelection = getFirstSelection(page.getSelection());
		}
		return firstSelection;
	}

	public static ISelection getWorkbenchSelection() {
		IWorkbenchPage page = Workbenches.getActiveWorkbenchPage();
		if (page != null) {
			return page.getSelection();
		}
		return null;
	}

	public static boolean selectionIs(Viewer viewer, Object singleSelection) {
		List<Object> list = Selections.getSelectionList(viewer);
		if (list.size() == 1) {
			Object current = list.get(0);
			return current == singleSelection;
		}
		return false;
	}
}
