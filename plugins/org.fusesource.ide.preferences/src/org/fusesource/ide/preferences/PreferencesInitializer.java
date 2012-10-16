package org.fusesource.ide.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author lhein
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferenceManager.getInstance().getUnderlyingStorage();

		store.setDefault(PreferencesConstants.EDITOR_DEFAULT_LANGUAGE, "simple");
		store.setDefault(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL, true);
		store.setDefault(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION, PositionConstants.EAST);
		store.setDefault(PreferencesConstants.EDITOR_GRID_VISIBILITY, true);
		store.setDefault(PreferencesConstants.EDITOR_GRID_COLOR, "227,238,249");
		store.setDefault(PreferencesConstants.EDITOR_CONNECTION_COLOR, "0,0,0");
		store.setDefault(PreferencesConstants.EDITOR_FIGURE_BG_COLOR, "ED,F5,FC"); // E2,E5,E9
		store.setDefault(PreferencesConstants.EDITOR_FIGURE_FG_COLOR, "128,128,128");
		store.setDefault(PreferencesConstants.EDITOR_TEXT_COLOR, "0,0,0");

		Color c = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
		store.setDefault(PreferencesConstants.EDITOR_TABLE_CHART_BG_COLOR, String.format("%d,%d,%d", c.getRed(), c.getGreen(), c.getBlue()));
	}
}
