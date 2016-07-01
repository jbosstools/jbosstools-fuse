/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.preferences.initializer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;

/**
 * @author lhein
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = PreferenceManager.getInstance().getUnderlyingStorage();

		store.setDefault(PreferencesConstants.EDITOR_DEFAULT_LANGUAGE, "simple");
		store.setDefault(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION, PositionConstants.SOUTH);
		store.setDefault(PreferencesConstants.EDITOR_GRID_VISIBILITY, true);
		store.setDefault(PreferencesConstants.EDITOR_GRID_COLOR, "227,238,249");
		store.setDefault(PreferencesConstants.EDITOR_CONNECTION_COLOR, "0,0,0");
		store.setDefault(PreferencesConstants.EDITOR_FIGURE_BG_COLOR, "ED,F5,FC"); // E2,E5,E9
		store.setDefault(PreferencesConstants.EDITOR_FIGURE_FG_COLOR, "128,128,128");
		store.setDefault(PreferencesConstants.EDITOR_TEXT_COLOR, "0,0,0");

		Display.getDefault().syncExec( () -> {
			Color c = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
			store.setDefault(PreferencesConstants.EDITOR_TABLE_CHART_BG_COLOR, String.format("%d,%d,%d", c.getRed(), c.getGreen(), c.getBlue()));				
		});
	}
}
