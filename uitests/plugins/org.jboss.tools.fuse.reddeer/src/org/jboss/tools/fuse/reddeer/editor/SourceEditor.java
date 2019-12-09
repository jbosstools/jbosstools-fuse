/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.editor;

import java.util.List;

import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;

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
	 *                     position in the editor
	 */
	public void setCursorPosition(int position) {
		editor.selectPosition(position);
	}

	/**
	 * Inserts given text on the cursor position
	 * 
	 * @param text
	 *                 text to be inserted
	 */
	public void insertText(String text) {
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

	public int getPosition(String text) {
		return editor.getPositionOfText(text);
	}

	public void selectText(int from, int to) {
		editor.setSelection(from, to);
	}

	/**
	 * Opens ContentAssistant and return available code completion proposals
	 * 
	 * @return List<String>
	 */
	public List<String> getCompletionProposals() {
		ContentAssistant assistant = openContentAssistant();
		List<String> proposals = assistant.getProposals();
		assistant.close();
		return proposals;
	}
}
