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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.util.LanguageUtils;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */
public class CamelEditorPreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage, IWorkbenchPropertyPage {

	public CamelEditorPreferencePage() {
		super(GRID);
	}

	@Override
	public void createFieldEditors() {
		String[][] namesAndValues = LanguageUtils.nameAndLanguageArray();

		ComboFieldEditor defaultLanguageEditor = new ComboFieldEditor(
				PreferencesConstants.EDITOR_DEFAULT_LANGUAGE,
				UIMessages.editorPreferencePageDefaultLanguageSetting,
				namesAndValues, getFieldEditorParent());

		addField(defaultLanguageEditor);

		BooleanFieldEditor preferIdAsLabelEditor = new BooleanFieldEditor(
				PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL,
				UIMessages.editorPreferencePagePreferIdAsLabelSetting,
				getFieldEditorParent());

		addField(preferIdAsLabelEditor);

		namesAndValues = new String[][] {
				{ UIMessages.editorPreferencePageLayoutOrientationEAST,  String.valueOf(PositionConstants.EAST) },
				{ UIMessages.editorPreferencePageLayoutOrientationSOUTH, String.valueOf(PositionConstants.SOUTH) }
		};

		ComboFieldEditor layoutOrientationEditor = new ComboFieldEditor(
				PreferencesConstants.EDITOR_LAYOUT_ORIENTATION,
				UIMessages.editorPreferencePageLayoutOrientationSetting,
				namesAndValues, getFieldEditorParent());

		addField(layoutOrientationEditor);

		BooleanFieldEditor gridVisibilityEditor = new BooleanFieldEditor(
				PreferencesConstants.EDITOR_GRID_VISIBILITY,
				UIMessages.editorPreferencePageGridVisibilitySetting,
				getFieldEditorParent());

		addField(gridVisibilityEditor);

		BooleanFieldEditor restPageVisibilityEditor = new BooleanFieldEditor(
				PreferencesConstants.EDITOR_SHOW_REST_PAGE,
				UIMessages.editorPreferencePageTechPreviewRESTEditorPageSetting,
				getFieldEditorParent());

		addField(restPageVisibilityEditor);

		// Sets up the context sensitive help for this page
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getFieldEditorParent(), "org.fusesource.ide.camel.editor.editorConfig");
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(PreferenceManager.getInstance().getUnderlyingStorage());
		setDescription(UIMessages.editorPreferencePageDescription);
	}


	// IWorkbenchPropertyPage API
	@Override
	public IAdaptable getElement() {
		return null;
	}

	/**
	 * The element passed in is the current selection DiagramEditPart in the diagram if opened via File -> Properties
	 */
	@Override
	public void setElement(IAdaptable element) {
		setPreferenceStore(PreferenceManager.getInstance().getUnderlyingStorage());
		setDescription(UIMessages.editorPreferencePageDescription);
	}

}