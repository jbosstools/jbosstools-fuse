/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.tests.util;

import java.io.File;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;

public class JDKInstaller {

	private static final String JDK_8_LOCATION = "JDK_8_LOCATION";
	private static final String JDK8_VMINSTALL_NAME = "jdk1.8-programmaticallyadded";
	private static String OS = System.getProperty("os.name").toLowerCase();

	public IVMInstall installJDK8(IVMInstallType vmInstallType) {
		IVMInstall vmInstall = vmInstallType.createVMInstall(JDK8_VMINSTALL_NAME);
		vmInstall.setName(JDK8_VMINSTALL_NAME);
		File jdk8home = findJDK8VM();
		vmInstall.setInstallLocation(jdk8home);
		return vmInstall;
	}

	private File findJDK8VM() {
		String userDefinedLocation = System.getProperty(JDK_8_LOCATION);
		if (userDefinedLocation != null) {
			return new File(userDefinedLocation);
		} else if (isWindows()) {
			File parentJVMFolder = new File("C:\\Program Files\\Java");
			for(File jvmFolder : parentJVMFolder.listFiles()) {
				if (jvmFolder.getName().startsWith("jdk1.8")) {
					return jvmFolder;
				}
			}
		} else if (isUnix()) {
			File jdk8home = new File("/usr/lib/jvm/java-1.8.0");
			if (jdk8home.exists()) {
				return jdk8home;
			}
		}
		throw new IllegalStateException("No JDK 8 has been found.\n"
				+ "JDK 8 is required to launch a part of the test suite.\n"
				+ "Please install it and/or provide JDK_8_LOCATION as system property.");
	}

	public static boolean isWindows() {
		return OS.contains("win");
	}

	public static boolean isUnix() {
		return OS.contains("nux");
	}

}
