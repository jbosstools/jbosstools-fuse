/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.widget;

import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.core.handler.WidgetHandler;
import org.jboss.reddeer.core.lookup.WidgetLookup;
import org.jboss.reddeer.core.util.Display;

public class WithLabelMatcherExt extends BaseMatcher<String> {

	public static final String ASTERISK_REGEX = "\\p{Blank}*\\*?\\p{Blank}*";

	private java.util.List<Control> allWidgets;

	protected Matcher<String> matcher;

	public WithLabelMatcherExt(String label) {
		this(label, true);
	}

	public WithLabelMatcherExt(String label, boolean ignoreAsterisk) {
		if (ignoreAsterisk) {
			matcher = new RegexMatcher(label + ASTERISK_REGEX);
		} else {
			matcher = Is.<String> is(label);
		}
	}

	@Override
	public boolean matches(Object obj) {
		if (!(obj instanceof Text || obj instanceof Combo || obj instanceof CCombo || obj instanceof List)) {
			return false;
		}

		findAllWidgets();

		int widgetIndex = allWidgets.indexOf(obj);
		ListIterator<? extends Widget> listIterator = allWidgets.listIterator(widgetIndex);
		while (listIterator.hasPrevious()) {
			Widget previousWidget = listIterator.previous();
			if (isLabel(previousWidget)) {
				String label = WidgetHandler.getInstance().getText(previousWidget);
				if (matcher.matches(label)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void describeTo(Description desc) {
		desc.appendText("with label " + matcher);
	}

	private boolean isLabel(Widget widget) {
		return widget instanceof Label || widget instanceof CLabel;
	}

	private java.util.List<Control> findAllWidgets() {
		final Control activeControl = WidgetLookup.getInstance().getActiveWidgetParentControl();
		allWidgets = new ArrayList<Control>();
		Display.syncExec(new Runnable() {

			@Override
			public void run() {
				findWidgets(activeControl);
			}
		});
		return allWidgets;
	}

	private void findWidgets(Control control) {
		allWidgets.add(control);
		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			for (Control child : composite.getChildren()) {
				findWidgets(child);
			}
		}
	}
}
