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
package org.jboss.tools.fuse.reddeer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.eclipse.ui.views.log.LogMessage;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;

/**
 * Utilizes access to Error Log View
 * 
 * @author tsedmik
 */
public class LogGrapper {

	/**
	 * Retrieves all error logs about given plugin
	 * 
	 * @param plugin name of plugin or substring of it
	 * @return List of errors in the plugin. In case of no error occurred, an empty List is returned
	 */
	public static List<LogMessage> getPluginErrors(String plugin) {

		List<LogMessage> fuseErrors = new ArrayList<LogMessage>();
		LogView log = new LogView();
		log.open();
		List<LogMessage> allErrors = log.getErrorMessages();
		for (LogMessage message : allErrors) {
			if (message.getPlugin().toLowerCase().contains(plugin)) {
				fuseErrors.add(message);
			}
		}
		return fuseErrors;
	}
}
