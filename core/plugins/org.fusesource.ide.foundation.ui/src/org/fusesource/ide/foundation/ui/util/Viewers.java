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
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;


public class Viewers {

	private Viewers() {
	}

	public static Viewer getViewer(Object object) {
		if (object instanceof Viewer) {
			return (Viewer) object;
		}
		return null;
	}

	public static void expand(Viewer viewer, Object elementOrTreePath, int level) {
		if (viewer instanceof TreeViewer) {
			TreeViewer tv = (TreeViewer) viewer;
			tv.expandToLevel(elementOrTreePath, level);
		}

	}

	/**
	 * Refreshes the viewer and by default makes sure the selection is viewable too
	 * @param viewer
	 */
	public static void refresh(final Viewer viewer) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (isValid(viewer)) {
					viewer.refresh();
					Control control = viewer.getControl();
					if (control instanceof Table) {
						((Table) control).showSelection();
					} else if (control instanceof Tree) {
						((Tree) control).showSelection();
					}
				}
			}
		});
	}

	public static void async(Runnable runnable) {
		Display display = getDisplay();
		if (display != null) {
			display.asyncExec(runnable);
		}
	}

	public static Display getDisplay() {
		Display display = Display.getDefault();
		if (display == null) {
			display = Display.getCurrent();
		}
		return display;
	}


	public static void refreshAsync(final Viewer viewer) {
		async(() -> refresh(viewer));
	}

	/**
	 * Returns true if the viewer is not disposed and is visible
	 */
	public static boolean isValid(Viewer viewer) {
		boolean valid = false;
		if (viewer != null) {
			Control widget = viewer.getControl();
			valid = Widgets.isValid(widget);// && widget.isVisible();
		}
		return valid;
	}

	/**
	 * Reveals the given element in the view t
	 */
	public static void reveal(Viewer viewer, Object element) {
		if (viewer instanceof StructuredViewer) {
			StructuredViewer sv = (StructuredViewer) viewer;
			sv.reveal(element);
		}
	}

	public static void setInput(Viewer viewer, Object input) {
		if (viewer != null && isValid(viewer)) {
			viewer.setInput(input);
		}
	}

	public static boolean isVisible(Viewer viewer) {
		if (isValid(viewer)) {
			Control control = viewer.getControl();
			return control != null && control.isVisible();
		}
		return false;
	}

	public static void setSelected(Viewer viewer, ISelection selection) {
		if (viewer != null) {
			viewer.setSelection(selection);
		}
	}

	public static void addExpanded(Viewer viewer, final Object... expanded) {
		if (viewer instanceof AbstractTreeViewer) {
			final AbstractTreeViewer cv = (AbstractTreeViewer) viewer;
			Object[] expandedElements = cv.getExpandedElements();
			List<Object> list = new ArrayList<>();
			if (expandedElements != null) {
				list.addAll(Arrays.asList(expandedElements));
			}
			list.addAll(Arrays.asList(expanded));
			cv.setExpandedElements(expanded);
		}
	}

}
