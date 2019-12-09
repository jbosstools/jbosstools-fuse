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

package org.fusesource.ide.foundation.ui.label;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;

public enum BarColour {
	Green(SWT.COLOR_GREEN), DarkGreen(SWT.COLOR_DARK_GREEN), Yellow(SWT.COLOR_YELLOW), Red(SWT.COLOR_RED), Blue(SWT.COLOR_BLUE);

	private BarColour(int colourId) {
		this.colourId = colourId;
	}

	private final int colourId;

	public Color create(GC gc) {
		String key = PreferencesConstants.EDITOR_TABLE_HEALTH_CHART_COLOR_PREFIX + toString();
		String colorString = PreferenceManager.getInstance().loadPreferenceAsString(key);
		if (Strings.isBlank(colorString)) {
			Display display = Shells.getDisplay();
			Color c = display.getSystemColor(colourId);
			return new Color(gc.getDevice(), c.getRGB());
		}
		int r = 0, g = 0, b = 0;
		StringTokenizer strTok = new StringTokenizer(colorString, ",");
		int i = 0;
		while (strTok.hasMoreTokens()) {
			String tok = strTok.nextToken();
			switch (i) {
			case 0: 	r = Integer.parseInt(tok);
			break;
			case 1:		g = Integer.parseInt(tok);
			break;
			case 2:		b = Integer.parseInt(tok);
			break;
			default:	// do nothing
			}
			i++;
		}
		return new Color(gc.getDevice(), r, g, b);
	}

}
