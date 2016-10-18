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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.preferences.initializer.StagingRepositoriesPreferenceInitializer;

public class StagingRepositoriesFieldEditor extends StringFieldEditor {

	public StagingRepositoriesFieldEditor(Composite fieldEditorParent) {
		super(StagingRepositoriesPreferenceInitializer.STAGING_REPOSITORIES, Messages.StagingRepositoriesFieldEditor_PageTitle, fieldEditorParent);
	}

	@Override
	protected boolean doCheckState() {
		String valueToCheck = getStringValue();
		if(valueToCheck != null && !valueToCheck.isEmpty()){
			String[] repos = valueToCheck.split(StagingRepositoriesPreferenceInitializer.REPO_SEPARATOR);
			for (String repo : repos) {
				if(!repo.contains(StagingRepositoriesPreferenceInitializer.NAME_URL_SEPARATOR)){
					setErrorMessage(Messages.StagingRepositoriesFieldEditor_ErrorMessageWrongPattern);
					return false;
				}
			}
			List<String> listOfRepoName = Arrays.asList(repos).stream()
			.map(repoWithName -> repoWithName.split(StagingRepositoriesPreferenceInitializer.NAME_URL_SEPARATOR)[0]).collect(Collectors.toList());
			for (String repoName : listOfRepoName) {
				if(Collections.frequency(listOfRepoName, repoName) > 1){
					setErrorMessage(Messages.StagingRepositoriesFieldEditor_ErrorMessageDuplicateRepoId+ repoName);
					return false;
				}
			}
			
		}
		return super.doCheckState();
	}
}