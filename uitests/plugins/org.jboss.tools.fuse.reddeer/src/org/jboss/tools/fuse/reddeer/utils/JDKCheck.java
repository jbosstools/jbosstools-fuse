/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.utils;

import org.eclipse.reddeer.common.condition.WaitCondition;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.preference.InstalledJREs;

/**
 * Checks, if JDK is available.
 * 
 * @author djelinek, fpospisi
 */
public class JDKCheck {

	public static final String JDK_WARNING_MESSAGE = "No Strictly compliant JRE detected";
	public static final String JDK8_REGEX = ".*(jdk8|jdk-1.8|1.8.0).*";
	public static final String JDK11_REGEX = ".*(jdk11|jdk-11|11).*";
	public static final String JDK17_REGEX = ".*(jdk17|jdk-17|17).*";

	/*
	 * Checks, if JDK8 is available.
	 */
	public static boolean isJava8Available() {
		return isJDKAvailable(JDK8_REGEX);
	}

	/*
	 * Checks, if JDK11 is available.
	 */
	public static boolean isJava11Available() {
		return isJDKAvailable(JDK11_REGEX);
	}

	/*
	 * Checks, if JDK17 is available.
	 */
	public static boolean isJava17Available() {
		return isJDKAvailable(JDK17_REGEX);
	}

	/*
	 * Checks, if JDK version defined by regular expression is available in preferences.
	 */
	public static boolean isJDKAvailable(String regex) {
		WorkbenchPreferenceDialog prefs = new WorkbenchPreferenceDialog();
		InstalledJREs jres = new InstalledJREs(prefs);
		prefs.open();
		prefs.select(jres);
		boolean hasJava = jres.containsJreWithName(regex);
		prefs.ok();
		return hasJava;
	}

	/*
	 * Handles missing JDK while creating new project.
	 */
	public static void handleJDKWarningDialog() {
		DefaultShell warningMessage = new DefaultShell(JDK_WARNING_MESSAGE);
		WaitCondition wait = new ShellIsAvailable(warningMessage);
		new WaitUntil(wait, TimePeriod.getCustom(900), false);
		if (wait.getResult() != null) {
			new OkButton(warningMessage).click();
		}
	}

}
