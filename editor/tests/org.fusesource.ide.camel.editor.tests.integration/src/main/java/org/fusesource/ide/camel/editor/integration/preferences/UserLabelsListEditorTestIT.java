/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.integration.preferences;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.preferences.UserLabelsListEditor;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class UserLabelsListEditorTestIT {

	private static Shell shell;
	private ListEditor listEditor;

	@BeforeClass
	public static void createShell() {
		shell = new Shell();
	}

	@Before
	public void createListEditor() {
		listEditor = new UserLabelsListEditor(PreferencesConstants.EDITOR_USER_LABELS, "", shell);
		listEditor.setPreferenceStore(PreferenceManager.getInstance().getUnderlyingStorage());
	}

	@Test
	public void testParsingEmptyLabel() {
		setComponentLabels("");
		listEditor.load();
		assertItems(listEditor);
	}

	@Test
	public void testParsingOneLabel() {
		setComponentLabels("abc.xyz");
		listEditor.load();
		assertItems(listEditor, "abc.xyz");
	}

	@Test
	public void testParsingMoreLabels() {
		setComponentLabels("abc.xyz;abc2.xyz2;");
		listEditor.load();
		assertItems(listEditor, "abc.xyz", "abc2.xyz2");
	}

	@Test
	public void testParsingIncorrectLabels() {
		setComponentLabels("abc.xyz;abc2xyz2;");
		listEditor.load();
		assertItems(listEditor, "abc.xyz", "abc2xyz2");
	}

	private static void assertItems(ListEditor listEditor, String... expectedItems) {
		String[] actualItems = listEditor.getListControl(shell).getItems();
		assertArrayEquals(expectedItems, actualItems);
	}

	private static void setComponentLabels(String componentLabels) {
		PreferenceManager.getInstance().savePreference(PreferencesConstants.EDITOR_USER_LABELS, componentLabels);
	}
}
