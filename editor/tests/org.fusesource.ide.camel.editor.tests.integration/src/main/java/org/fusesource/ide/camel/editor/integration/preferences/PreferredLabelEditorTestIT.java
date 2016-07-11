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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.editor.preferences.PreferredLabelEditor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class PreferredLabelEditorTestIT {

	public static final String PREFERENCE_KEY = "test";
	
	private IPreferenceStore store;

	private PreferredLabelEditor editor;

	@Before
	public void prepareEditor() {
		editor = new PreferredLabelEditor(PREFERENCE_KEY, "Test", new Shell());
		store = Mockito.mock(IPreferenceStore.class);
		editor.setPreferenceStore(store);
	}

	@Test
	public void testSavingEmptyPreferredLabels() {
		editor.store();

		verify(store).setValue(PREFERENCE_KEY, "");
	}

	@Test
	public void testSavingPreferredLabels() {
		editor.addRow("log", "message");
		editor.addRow("setHeader", "headerName");
		editor.store();

		verify(store).setValue(PREFERENCE_KEY, "log.message;setHeader.headerName");
	}

	@Test
	public void testUpdatingPreferredLabels() {
		editor.addRow("log", "message");
		editor.addRow("setHeader", "headerName");
		editor.updateRow(0, "log", "id");
		editor.store();

		verify(store).setValue(PREFERENCE_KEY, "log.id;setHeader.headerName");
	}

	@Test
	public void testLoadingPreferredLabels() {
		when(store.getString(PREFERENCE_KEY)).thenReturn("log.id;setHeader.id");
		editor.load();

		assertEquals(2, editor.getPreferredLabels().size());
		assertEquals("log.id", editor.getPreferredLabels().get(0));
		assertEquals("setHeader.id", editor.getPreferredLabels().get(1));
	}

	@Test
	public void testLoadingPartialPreferredLabels() {
		when(store.getString(PREFERENCE_KEY)).thenReturn("log.message;setHeader");
		editor.load();

		assertEquals(2, editor.getPreferredLabels().size());
		assertEquals("log.message", editor.getPreferredLabels().get(0));
		assertEquals("setHeader.", editor.getPreferredLabels().get(1));
	}

	@Test
	public void testLoadingEmptyPreferredLabels() {
		when(store.getString(PREFERENCE_KEY)).thenReturn("");
		editor.load();

		assertTrue(editor.getPreferredLabels().isEmpty());
	}

	@Test
	public void testRemovingPreferredLabels() {
		editor.addRow("log", "id");
		editor.addRow("setHeader", "headerName");
		editor.removeRows(0);
		editor.store();

		verify(store).setValue(PREFERENCE_KEY, "setHeader.headerName");
	}

}