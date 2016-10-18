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

package org.fusesource.ide.camel.editor.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class CamelEditorColorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public CamelEditorColorPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		addColorField(PreferencesConstants.EDITOR_GRID_COLOR,           UIMessages.colorPreferencePageGridColorSetting);
		addColorField(PreferencesConstants.EDITOR_CONNECTION_COLOR,     UIMessages.colorPreferencePageConnectionColorSetting);
		addColorField(PreferencesConstants.EDITOR_TEXT_COLOR,           UIMessages.colorPreferencePageTextColorSetting);
		addColorField(PreferencesConstants.EDITOR_FIGURE_BG_COLOR,      UIMessages.colorPreferencePageFigureBGColorSetting);
		addColorField(PreferencesConstants.EDITOR_FIGURE_FG_COLOR,      UIMessages.colorPreferencePageFigureFGColorSetting);
		addColorField(PreferencesConstants.EDITOR_TABLE_CHART_BG_COLOR, UIMessages.colorPreferencePageTableChartBGColorSetting);
		
		// Sets up the context sensitive help for this page
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getFieldEditorParent(), "org.fusesource.ide.camel.editor.editorConfig");
	}
	
	private void addColorField(String preferenceName, String label){
		addField(new ColorFieldEditor(preferenceName, label, getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(PreferenceManager.getInstance().getUnderlyingStorage());
		setDescription(UIMessages.colorPreferencePageDescription);
	}	
}
