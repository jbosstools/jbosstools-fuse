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
package org.jboss.tools.fuse.qe.reddeer.editor;

import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;

/**
 * Represents 'Source' tab of the Camel Route Editor
 * 
 * @author tsedmik
 */
public class SourceEditor extends DefaultEditor {

	private DefaultStyledText editor = new DefaultStyledText();

	/**
	 * Sets position of the cursor to the specified position
	 * 
	 * @param position
	 *            position in the editor
	 */
	public void setCursorPosition(int position) {

		editor.selectPosition(position);
	}

	/**
	 * Inserts given text on the cursor position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	public void insertTest(String text) {

		editor.insertText(text);
	}

	/**
	 * Returns text in the editor
	 * 
	 * @return text in the editor
	 */
	public String getText() {

		return editor.getText();
	}
}
