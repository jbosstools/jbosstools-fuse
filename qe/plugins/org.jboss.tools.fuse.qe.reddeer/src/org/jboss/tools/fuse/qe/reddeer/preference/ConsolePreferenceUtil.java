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
package org.jboss.tools.fuse.qe.reddeer.preference;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.direct.preferences.Preferences;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class ConsolePreferenceUtil {

	private static final Logger log = Logger.getLogger(ConsolePreferenceUtil.class);

	public static final String CONSOLE_PLUGIN = "org.eclipse.debug.ui";
	public static final String CONSOLE_OPEN_ON_ERR_KEY = "DEBUG.consoleOpenOnErr";
	public static final String CONSOLE_OPEN_ON_OUT_KEY = "DEBUG.consoleOpenOnOut";

	/**
	 * Decides whether the console opens on error
	 * 
	 * @return true if the console opens on error, false otherwise
	 */
	public static boolean isConsoleOpenOnError() {
		return "true".equalsIgnoreCase(Preferences.get(CONSOLE_PLUGIN, CONSOLE_OPEN_ON_ERR_KEY));
	}

	/**
	 * Decides whether the console opens on standard output
	 * 
	 * @return true if the console opens on standard output, false otherwise
	 */
	public static boolean isConsoleOpenOnOutput() {
		return "true".equalsIgnoreCase(Preferences.get(CONSOLE_PLUGIN, CONSOLE_OPEN_ON_OUT_KEY));
	}

	/**
	 * Sets the console open on standard output
	 */
	public static void setConsoleOpenOnError(boolean openOnError) {
		log.info("Sets the console open on error to '" + openOnError + "'");
		Preferences.set(CONSOLE_PLUGIN, CONSOLE_OPEN_ON_ERR_KEY, String.valueOf(openOnError));
	}

	/**
	 * Sets the console open on standard output
	 */
	public static void setConsoleOpenOnOutput(boolean openOnOutput) {
		log.info("Sets the console open on error to '" + openOnOutput + "'");
		Preferences.set(CONSOLE_PLUGIN, CONSOLE_OPEN_ON_OUT_KEY, String.valueOf(openOnOutput));
	}
}
