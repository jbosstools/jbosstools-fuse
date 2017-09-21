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
package org.jboss.tools.fuse.qe.reddeer.tests.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Scanner;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.core.matcher.WithTooltipTextMatcher;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.XPathEvaluator;
import org.jboss.tools.fuse.qe.reddeer.tests.Activator;

/**
 * Support static methods manipulates with Text Editor
 * 
 * @author tsedmik
 */
public class EditorManipulator {

	private static Logger log = Logger.getLogger(EditorManipulator.class);

	/**
	 * Replaces content of a file opened in active text editor with content of the file <i>source</i>
	 * 
	 * @param source
	 *            Path to the source file
	 */
	public static void copyFileContent(String source) {

		TextEditor editor = new TextEditor();
		editor.setText(getFileContent(source));
		editor.save();
	}

	/**
	 * Replaces content of a file opened in active XML editor of the CamelEditor with content of the file <i>source</i>
	 * 
	 * @param source
	 *            Path to the source file
	 */
	public static void copyFileContentToCamelXMLEditor(String source) {

		new DefaultStyledText().setText(EditorManipulator.getFileContent(source));
		new DefaultToolItem(new WorkbenchShell(), 0, new WithTooltipTextMatcher(new RegexMatcher("Save.*"))).click();

		// FIXME temporary added due to https://issues.jboss.org/browse/FUSETOOLS-1208
		try {
			log.debug("Check whether 'Could not parse your changes to the XML' dialog is appeared");
			new WaitUntil(new ShellWithTextIsAvailable("Could not parse your changes to the XML"), TimePeriod.SHORT);
			new DefaultShell("Could not parse your changes to the XML");
			new PushButton("OK").click();
		} catch (Exception e) {
			log.debug("Dialog 'Could not parse your changes to the XML' didn't appeared");
		}
	}

	/**
	 * Gets content of a given file
	 * 
	 * @param source
	 *            the source file
	 * @return content of the file, in case of some error - empty string
	 */
	public static String getFileContent(String source) {

		File testFile = new File(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, source));
		String text = "";

		try (Scanner scanner = new Scanner(testFile)){
			scanner.useDelimiter("\\Z");
			text = scanner.next();
		} catch (FileNotFoundException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + source + ")!");
		}

		log.info("Text in active text editor was replaced with content of the file: " + source + ".");

		return text;
	}

	/**
	 * Compares content of the active XML editor of the Camel Editor with content of the given file
	 * 
	 * @param file
	 *            path to the file
	 * @return true - content of the file and the text editor is the same, false - otherwise
	 */
	public static boolean isEditorContentEqualsFile(String file) {

		String editorText = new DefaultStyledText().getText();
		if (file.equals("resources/camel-context-all.xml")) {
			XPathEvaluator xpath = new XPathEvaluator(new StringReader(editorText));
			if ((xpath.evaluateBoolean("/beans/camelContext/route/*[1]/@uri = 'file:src/data?noop=true'"))
					&& (xpath.evaluateString("/beans/camelContext/route/choice/*[1]/*[1][text()]")
							.equals("/person/city = 'London'"))
					&& (xpath.evaluateBoolean("/beans/camelContext/route/choice/*[1]/*[2]/@message = 'UK message'"))
					&& (xpath.evaluateBoolean(
							"/beans/camelContext/route/choice/*[1]/*[3]/@uri = 'file:target/messages/uk'"))
					&& (xpath.evaluateBoolean("/beans/camelContext/route/choice/*[2]/*[1]/@message = 'Other message'"))
					&& (xpath.evaluateBoolean(
							"/beans/camelContext/route/choice/*[2]/*[2]/@uri = 'file:target/messages/others'")))
				return true;
			return false;
		} else {
			String fileText = getFileContent(file);
			return editorText.equals(fileText);
		}
	}
}
