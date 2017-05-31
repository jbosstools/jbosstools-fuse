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
package org.jboss.tools.fuse.qe.reddeer.utils;

import java.io.IOException;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;

import com.jcraft.jsch.JSchException;

/**
 * Performs operations with the <i>Fuse Shell</i>. Command execution is performed via SSH (not
 * via JBoss Fuse Tooling).
 * 
 * @author tsedmik
 */
public class FuseShellSSH {

	private static Logger log = Logger.getLogger(FuseShellSSH.class);

	/**
	 * Types a given command into the Fuse Shell
	 * 
	 * @param command
	 *            command that will be performed
	 */
	public String execute(String command) {

		ShellManager shell = null;
		try {
			shell = new ShellManager("admin", "admin", "0.0.0.0", 8101);
			return shell.execute(command);
		} catch (JSchException e) {
			log.error("Cannot create ShellManager");
		} catch (IOException e) {
			log.error("Reading response to given command error");
		} finally {
			if (shell != null)
				shell.close();
		}

		return null;
	}

	/**
	 * Checks whether JBoss Fuse log contains given text
	 * 
	 * @param text
	 *            Text which presence is checked in JBoss Fuse log
	 * @return true - text is in the log, false - otherwise
	 */
	public boolean containsLog(String text) {

		ShellManager shell = null;
		int attempts = 10;

		try {
			shell = new ShellManager("admin", "admin", "0.0.0.0", 8101);
			while (attempts-- > 0) {
				String tmp = shell.execute("log:display");
				System.out.println(tmp);
				if (tmp.contains(text))
					return true;
				AbstractWait.sleep(TimePeriod.SHORT);
			}
			return false;
		} catch (JSchException e) {
			log.error("Cannot create ShellManager");
		} catch (IOException e) {
			log.error("Problem with creating output");
		} finally {
			if (shell != null)
				shell.close();
		}

		return false;
	}
}
