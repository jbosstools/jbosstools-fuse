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

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.preferences.PreferredLabelEditor;
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
public class PreferredLabelEditorTestIT {

	private static Shell shell;
	private PreferredLabelEditor editor;

	@BeforeClass
	public static void createShell() {
		shell = new Shell();
	}

	@Before
	public void createListEditor() {
		editor = new PreferredLabelEditor(PreferencesConstants.EDITOR_PREFERRED_LABEL, "", shell);
		editor.setPreferenceStore(PreferenceManager.getInstance().getUnderlyingStorage());
	}

	@Test
	public void testParsingEmptyLabel() {
		setComponentLabels("");
		editor.load();
		assertItems(editor);
	}

	@Test
	public void testParsingOneLabel() {
		setComponentLabels("abc.xyz");
		editor.load();
		assertItems(editor, "abc.xyz");
	}

	@Test
	public void testParsingMoreLabels() {
		setComponentLabels("abc.xyz;abc2.xyz2;");
		editor.load();
		assertItems(editor, "abc.xyz", "abc2.xyz2");
	}

	@Test
	public void testParsingIncorrectLabels() {
		setComponentLabels("abc.xyz;abc2xyz2;");
		editor.load();
		assertItems(editor, "abc.xyz", "abc2xyz2.");
	}

	private static void assertItems(PreferredLabelEditor editor, String... expectedItems) {
		List<String> actualItems = editor.getPreferredLabels();
		assertArrayEquals(expectedItems, actualItems.toArray(new String[actualItems.size()]));
	}

	private static void setComponentLabels(String componentLabels) {
		PreferenceManager.getInstance().savePreference(PreferencesConstants.EDITOR_PREFERRED_LABEL, componentLabels);
	}
}
