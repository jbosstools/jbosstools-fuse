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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;


public class Widgets {

	public static void dispose(Widget widget) {
		if (widget != null && !widget.isDisposed()) {
			Widgets.removeListeners(widget, SWT.Move);
			widget.dispose();
		}
	}

	/**
	 * Removes the listeners on the given widget for the event typeype
	 */
	public static void removeListeners(Widget widget, int eventType) {
		Listener[] listeners = widget.getListeners(eventType);
		for (Listener listener : listeners) {
			widget.removeListener(eventType, listener);
		}
	}

	public static Button createActionButton(Composite parent, Action action) {
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.fill(parent);
		Button button = (Button) aci.getWidget();
		if (button != null) {
			// lets try make sure we get the text and the image
			button.setText(action.getText());
		}
		return button;
	}

	public static void refresh(Viewer v) {
		Viewers.refresh(v);
	}

	/**
	 * Returns if the widget is valid and called from the correct thread
	 */
	public static boolean isValid(Widget widget) {
		if (widget == null || widget.isDisposed()) {
			return false;
		}
		Display display = widget.getDisplay();
		if (display == null)
			return false;
		if (display.getThread() != Thread.currentThread ())
			return false;
		return !widget.isDisposed();
	}

	/**
	 * Returns true if the widget is valid but the current caller thread may be different
	 */
	public static boolean isValidFromOtherThread(Widget widget) {
		if (widget == null || widget.isDisposed()) {
			return false;
		}
		Display display = widget.getDisplay();
		if (display == null)
			return false;
		return !widget.isDisposed();
	}

	public static void setDoubleClickAction(StructuredViewer viewer, final Action doubleClickAction) {
		if (doubleClickAction != null) {
			viewer.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					if (doubleClickAction.isEnabled()) {
						doubleClickAction.run();
					}
				}
			});
		}
	}

	/**
	 * Returns true if the control has been created and been disposed or false if its not been created yet or not been disposed
	 */
	public static boolean isDisposed(Control control) {
		return control == null || control.isDisposed();
	}

	/**
	 * Returns true if the Control Viewer has been created and been disposed or
	 * false if its not been created yet or not been disposed
	 */
	public static boolean isDisposed(Viewer viewer) {
		return viewer == null || isDisposed(viewer.getControl());
	}
}
