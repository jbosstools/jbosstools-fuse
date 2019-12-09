/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.preferences;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class StagingRepositoriesPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage, IWorkbenchPropertyPage {

	private BooleanFieldEditor enableStagingRepositorieslEditor;
	private StagingRepositoryListEditor stagingRepositoriesListEditor;

	/**
	 * Constructor
	 */
	public StagingRepositoriesPreferencePage() {
		super(GRID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		enableStagingRepositorieslEditor = new BooleanFieldEditor(
				StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES,
				Messages.enableStagingRepositoriesField, getFieldEditorParent());

		addField(enableStagingRepositorieslEditor);

		stagingRepositoriesListEditor = new StagingRepositoryListEditor(
				StagingRepositoriesConstants.STAGING_REPOSITORIES, Messages.stagingRepositoriesListField,
				getFieldEditorParent());

		addField(stagingRepositoriesListEditor);

		stagingRepositoriesListEditor.setEnabled(
				getPreferenceStore().getBoolean(StagingRepositoriesConstants.ENABLE_STAGING_REPOSITORIES),
				getFieldEditorParent());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		stagingRepositoriesListEditor.setEnabled(enableStagingRepositorieslEditor.getBooleanValue(), getFieldEditorParent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.stagingRepositoriesPreferencePageDescription);
	}

	@Override
	public IAdaptable getElement() {
		return null;
	}

	/**
	 * The element passed in is the current selection DiagramEditPart in the
	 * diagram if opened via File -> Properties
	 */
	@Override
	public void setElement(IAdaptable element) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.stagingRepositoriesPreferencePageDescription);
	}

	public BooleanFieldEditor getEnableStagingRepositoriesEditor() {
		return this.enableStagingRepositorieslEditor;
	}
	
	public StagingRepositoryListEditor getStagingRepositoryListEditor() {
		return this.stagingRepositoriesListEditor;
	}
	
}