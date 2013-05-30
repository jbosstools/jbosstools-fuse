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
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public class RiderColorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private ColorFieldEditor gridColorEditor;
	private ColorFieldEditor textColorEditor;
	private ColorFieldEditor connectionColorEditor;
	private ColorFieldEditor figureBackgroundColorEditor;
	private ColorFieldEditor figureForegroundColorEditor;
	private ColorFieldEditor tableChartBackgroundColorEditor;
	
	/**
	 * 
	 */
	public RiderColorPreferencePage() {
		super(GRID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		this.gridColorEditor = new ColorFieldEditor(
				PreferencesConstants.EDITOR_GRID_COLOR,
				EditorMessages.colorPreferencePageGridColorSetting,
				getFieldEditorParent());
		
		addField(this.gridColorEditor);
		
		this.connectionColorEditor = new ColorFieldEditor(
				PreferencesConstants.EDITOR_CONNECTION_COLOR,
				EditorMessages.colorPreferencePageConnectionColorSetting,
				getFieldEditorParent());
		
		addField(this.connectionColorEditor);
		
		this.textColorEditor = new ColorFieldEditor(
				PreferencesConstants.EDITOR_TEXT_COLOR,
				EditorMessages.colorPreferencePageTextColorSetting,
				getFieldEditorParent());
		
		addField(this.textColorEditor);
		
		this.figureBackgroundColorEditor = new ColorFieldEditor(
				PreferencesConstants.EDITOR_FIGURE_BG_COLOR,
				EditorMessages.colorPreferencePageFigureBGColorSetting,
				getFieldEditorParent());
		
		addField(this.figureBackgroundColorEditor);
		
		this.figureForegroundColorEditor = new ColorFieldEditor(
				PreferencesConstants.EDITOR_FIGURE_FG_COLOR,
				EditorMessages.colorPreferencePageFigureFGColorSetting,
				getFieldEditorParent());
		
		addField(this.figureForegroundColorEditor);
		
		this.tableChartBackgroundColorEditor = new ColorFieldEditor(
				PreferencesConstants.EDITOR_TABLE_CHART_BG_COLOR,
				EditorMessages.colorPreferencePageTableChartBGColorSetting,
				getFieldEditorParent());
		
		addField(this.tableChartBackgroundColorEditor);
		
		// Sets up the context sensitive help for this page
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getFieldEditorParent(), "org.fusesource.ide.camel.editor.editorConfig");
	}

	/* (non-Javadoc)
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(PreferenceManager.getInstance().getUnderlyingStorage());
		setDescription(EditorMessages.colorPreferencePageDescription);
	}	
}
