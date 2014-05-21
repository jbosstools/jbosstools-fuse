/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.imports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

public class ImportUtils {

	private static final String VERSION_STRING_DELIMITER = "\\."; //$NON-NLS-1$
	private static final String DROPINS_FOLDER_NAME = "dropins"; //$NON-NLS-1$
	private static final String JCO_MIN_SUPPORTED_VERSION = "3.0.11"; //$NON-NLS-1$
	private static final int JCO_MIN_SUPPORTED_MAJOR_VERSION = 3;
	private static final int JCO_MIN_SUPPORTED_MINOR_VERSION = 0;
	private static final int JCO_MIN_SUPPORTED_MICRO_VERSION = 11;
	
	private static final String IDOC_MIN_SUPPORTED_VERSION = "3.0.10"; //$NON-NLS-1$
	private static final int IDOC_MIN_SUPPORTED_MAJOR_VERSION = 3;
	private static final int IDOC_MIN_SUPPORTED_MINOR_VERSION = 0;
	private static final int IDOC_MIN_SUPPORTED_MICRO_VERSION = 10;
	
	/**
	 * Default Execution Environment assigned to plug-ins.
	 */
	public static final String DEFAULT_EXECUTION_ENVIRONMENT = "JavaSE-1.6"; //$NON-NLS-1$
	
	public static class UnsupportedVersionException extends Exception {

		private static final long serialVersionUID = 1L;
		
		public UnsupportedVersionException() {
			super();
		}

		public UnsupportedVersionException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public UnsupportedVersionException(String arg0) {
			super(arg0);
		}

		public UnsupportedVersionException(Throwable arg0) {
			super(arg0);
		}
	}

	public static String[] getExecutionEnvironments() {
		List<String> result = new ArrayList<String>();
		IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
		IExecutionEnvironment[] environments = manager.getExecutionEnvironments();
		for (IExecutionEnvironment environment : environments) {
			result.add(environment.getId());
		}
		return result.toArray(new String[0]);
	}
	
	public static String getExecutionEnvironment(int index) {
		String[] environments = getExecutionEnvironments();
		if (index < environments.length) {
			return environments[index];
		}
		return null;
	}
	
	public static int getExecutionEnvironmentIndex(String executionEnvironment) {
		int result = -1;
		String[] environments = getExecutionEnvironments();
		for (int index = 0; index < environments.length; index++) {
			String environment = environments[index];
			if (environment.equals(executionEnvironment)) {
				result = index;
				break;
			}
		}
		return result;
	}

	public static String getDefaultDeployLocation() {
		return Platform.getInstallLocation().getURL().getFile() + DROPINS_FOLDER_NAME;
	}
	
	public static void isJCoArchiveVersionSupported(String archiveVersion) throws UnsupportedVersionException {
		isArchiveVersionSupported(archiveVersion, JCO_MIN_SUPPORTED_VERSION, JCO_MIN_SUPPORTED_MAJOR_VERSION, JCO_MIN_SUPPORTED_MINOR_VERSION, JCO_MIN_SUPPORTED_MICRO_VERSION);
	}

	public static void isIDocArchiveVersionSupported(String archiveVersion) throws UnsupportedVersionException {
		isArchiveVersionSupported(archiveVersion, IDOC_MIN_SUPPORTED_VERSION, IDOC_MIN_SUPPORTED_MAJOR_VERSION, IDOC_MIN_SUPPORTED_MINOR_VERSION, IDOC_MIN_SUPPORTED_MICRO_VERSION);
	}

	public static void isArchiveVersionSupported(String archiveVersion, String supportedVersion, int supportedMajorVersion, int supportedMinorVersion, int supportedMicroVersion) throws UnsupportedVersionException {
		try {
			if (archiveVersion == null) {
				throw new UnsupportedVersionException(Messages.ImportUtils_ArchiveVersionNotFound);
			}
			String[] version = archiveVersion.split(VERSION_STRING_DELIMITER);
			if (version.length >= 3) {
				int archiveMajorVersion = Integer.parseInt(version[0]);
				int archiveMinorVersion = Integer.parseInt(version[1]);
				int archiveMicroVersion = Integer.parseInt(version[2]);
				if (archiveMajorVersion > supportedMajorVersion) {
					return;
				} else if (archiveMajorVersion == supportedMajorVersion) {
					if (archiveMinorVersion > supportedMinorVersion) {
						return;
					} else if (archiveMinorVersion == supportedMinorVersion) {
						if (archiveMicroVersion >= supportedMicroVersion) {
							return;
						}
					}
				} 
			}
		} catch (NumberFormatException e) {
			throw new UnsupportedVersionException(MessageFormat.format(Messages.ImportUtils_ArchiveVersionIsInvalid, archiveVersion));
		}
		throw new UnsupportedVersionException(MessageFormat.format(Messages.ImportUtils_ArchiveVersionIsNotSupported, archiveVersion, supportedVersion));
	}
}
