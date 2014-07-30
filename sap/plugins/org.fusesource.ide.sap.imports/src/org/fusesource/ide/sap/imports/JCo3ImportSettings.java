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

import java.io.File;

public class JCo3ImportSettings extends SAPImportSettings {
	
	///////////////////////////////////////////////////////
	// Property Names

	public static final String JCO_ARCHIVE = "jcoArchive"; //$NON-NLS-1$

	public static final String ARCHIVE_OS = "archiveOs"; //$NON-NLS-1$

	public static final String JCO3_ARCHIVE_FILENAME = "jco3ArchiveFilename"; //$NON-NLS-1$

	//
	///////////////////////////////////////////////////////

	private static final String FRAGMENT_HOST_BUNDLE_VERSION_PREFIX = ";bundle-version=\""; //$NON-NLS-1$

	private static final String FRAGMENT_HOST_BUNDLE_VERSION_SUFFIX = "\""; //$NON-NLS-1$

	private static final String BUNDLE_JCO_JAR_ENTRY = "sapjco3.jar"; //$NON-NLS-1$

	private static final String NATIVE_DIRECTORY_NAME = "jni"; //$NON-NLS-1$

	private static final String FRAGMENT_BUNDLE_NAME_SUFFIX = " - Native Library for "; //$NON-NLS-1$

	private static final String JAR_EXTENTION = ".jar"; //$NON-NLS-1$

	private static final String UNDERSCORE = "_"; //$NON-NLS-1$

	private String jco3ArchiveFilename;

	private String archiveOs;
	
	private JCo3Archive jco3Archive;
	
	public String getJco3ArchiveFilename() {
		return jco3ArchiveFilename;
	}

	public void setJco3ArchiveFilename(String archiveFilename) {
		firePropertyChange(JCO3_ARCHIVE_FILENAME, this.jco3ArchiveFilename, this.jco3ArchiveFilename = archiveFilename);
	}

	public String getArchiveOs() {
		return archiveOs;
	}
	
	public void setArchiveOs(String archiveOs) {
		firePropertyChange(ARCHIVE_OS, this.archiveOs, this.archiveOs = archiveOs);
	}
	
	public JCo3Archive getJco3Archive() {
		return jco3Archive;
	}

	public void setJco3Archive(JCo3Archive jcoArchive) {
		firePropertyChange(JCO_ARCHIVE, this.jco3Archive, this.jco3Archive = jcoArchive);
		setArchiveOs(jcoArchive.getType().getDescription());
		setArchiveVersion(jcoArchive.getVersion());
		if (getBundleVersion() == null || getBundleVersion().length() == 0) {
			setBundleVersion(jcoArchive.getVersion());
		}
	}

	public String getBundleSymbolicName() {
		return jco3Archive.getBundleName();
	}

	public String getBundleFilename() {
		return bundleDeployLocation + File.separator + jco3Archive.getBundleName() + UNDERSCORE + bundleVersion + JAR_EXTENTION;
	}

	public String getFragmentFilename() {
		return bundleDeployLocation + File.separator + jco3Archive.getType().getFragmentName() + UNDERSCORE + bundleVersion + JAR_EXTENTION;
	}

	public String getFragmentBundleName() {
		return bundleName + FRAGMENT_BUNDLE_NAME_SUFFIX + jco3Archive.getType().getDescription();
	}

	public String getFragmentSymbolicName() {
		return jco3Archive.getType().getFragmentName();
	}
	
	public String getBundleNativeDirEntry() {
		return NATIVE_DIRECTORY_NAME + File.separator;
	}
	
	public String getBundleJCoJarEntry() {
		return BUNDLE_JCO_JAR_ENTRY;
	}

	public String getBundleNativeLibraryEntry() {
		return NATIVE_DIRECTORY_NAME + File.separator + jco3Archive.getType().getNativeArchiveName();
	}

	public String getFragmentHost() {
		return jco3Archive.getType().getPluginName() + FRAGMENT_HOST_BUNDLE_VERSION_PREFIX + bundleVersion + FRAGMENT_HOST_BUNDLE_VERSION_SUFFIX;
	}
	
	public String getPlatformFilter() {
		return jco3Archive.getType().getPlatformFilter();
	}
}
