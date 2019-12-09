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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

/**
 * Field editor for the list of staging repositories. Based on the PathEditor in
 * Eclipse.
 * 
 * @author brianf
 */
public class StagingRepositoryListEditor extends ListEditor {

	/**
	 * Creates a new repository list field editor
	 */
	protected StagingRepositoryListEditor() {
	}

	/**
	 * Creates a staging repository field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public StagingRepositoryListEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected String createList(String[] items) {
		StringBuilder repositories = new StringBuilder("");//$NON-NLS-1$

		for (int i = 0; i < items.length; i++) {
			repositories.append(items[i]);
			repositories.append(StagingRepositoriesConstants.REPO_SEPARATOR);
		}
		return repositories.toString();
	}
	
	public String getItemList() {
		return createList(super.getList().getItems());
	}
	
	public boolean isListEnabled() {
		return getList().isEnabled();
	}

	@Override
	protected String getNewInputObject() {

		StagingRepositoryDialog dialog = new StagingRepositoryDialog(getShell());
		dialog.setUniquenessList(getItemList());
		int rtn = dialog.open();
		if (rtn == Window.OK && dialog.getURL() != null && dialog.getName() != null) {
			String outUrl = dialog.getURL().trim();
			String outName = dialog.getName().trim();
			if (outUrl.isEmpty() || outName.isEmpty()) {
				return null;
			}
			return outName + StagingRepositoriesConstants.NAME_URL_SEPARATOR + outUrl;
		}
		return null;
	}

	@Override
	protected String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList,
				StagingRepositoriesConstants.REPO_SEPARATOR + "\n\r");//$NON-NLS-1$
		
		List<String> v = new ArrayList<>();
		while (st.hasMoreTokens()) {
			v.add(st.nextToken());
		}
		return v.toArray(new String[v.size()]);
	}

}
