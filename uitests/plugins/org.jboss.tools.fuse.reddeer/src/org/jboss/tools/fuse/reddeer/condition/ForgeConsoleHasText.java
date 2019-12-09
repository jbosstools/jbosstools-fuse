/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.tools.fuse.reddeer.view.ForgeConsoleView;

/**
 * Returns true if the console contains a given text
 * 
 * @author psrna
 * 
 */
public class ForgeConsoleHasText extends AbstractWaitCondition {
	
	private String text;

	/**
	 * Construct the condition with a given text.
	 * 
	 * @param text Text
	 */
	public ForgeConsoleHasText(String text) {
		this.text = text;
	}

	@Override
	public boolean test() {
		String consoleText = getConsoleText();
		return consoleText.contains(text);
	}

	@Override
	public String description() {
		String consoleText = getConsoleText();
		return "console contains '" + text + "'\n" + consoleText;
	}

	private static String getConsoleText() {
		ForgeConsoleView forgeConsoleView = new ForgeConsoleView();
		forgeConsoleView.open();
		return forgeConsoleView.getConsoleText();
	}
}