/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.preferences.page;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.preferences.initializer.StagingRepositoriesPreferenceInitializer;

public class StagingRepositoryiesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public StagingRepositoryiesPreferencePage() {
		// keep it for extension point reflection instantiation
	}

	public StagingRepositoryiesPreferencePage(int style) {
		super(style);
	}

	public StagingRepositoryiesPreferencePage(String title, int style) {
		super(title, style);
	}

	public StagingRepositoryiesPreferencePage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(ProjectTemplatesActivator.getDefault().getPreferenceStore());
		setDescription(Messages.StagingRepositoryiesPreferencePage_PageDescription);
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(StagingRepositoriesPreferenceInitializer.ENABLE_STAGING_REPOSITORIES, Messages.StagingRepositoryiesPreferencePage_EnableCheckboxLabel, getFieldEditorParent()));
		addField(new StagingRepositoriesFieldEditor(getFieldEditorParent()));
	}
}
