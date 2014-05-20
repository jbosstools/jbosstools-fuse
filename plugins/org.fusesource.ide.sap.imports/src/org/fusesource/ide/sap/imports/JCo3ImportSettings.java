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

	/**
	 * 
	 * @return
	 */
	public String getJco3ArchiveFilename() {
		return this.jco3ArchiveFilename;
	}

	/**
	 * 
	 * @param archiveFilename
	 */
	public void setJco3ArchiveFilename(String archiveFilename) {
		firePropertyChange(JCO3_ARCHIVE_FILENAME, this.jco3ArchiveFilename, this.jco3ArchiveFilename = archiveFilename);
	}

	/**
	 * 
	 * @return
	 */
	public String getArchiveOs() {
		return this.archiveOs;
	}
	
	/**
	 * 
	 * @param archiveOs
	 */
	public void setArchiveOs(String archiveOs) {
		firePropertyChange(ARCHIVE_OS, this.archiveOs, this.archiveOs = archiveOs);
	}
	
	/**
	 * 
	 * @return
	 */
	public JCo3Archive getJco3Archive() {
		return this.jco3Archive;
	}

	/**
	 * 
	 * @param jcoArchive
	 */
	public void setJco3Archive(JCo3Archive jcoArchive) {
		firePropertyChange(JCO_ARCHIVE, this.jco3Archive, this.jco3Archive = jcoArchive);
		setArchiveOs(jcoArchive.getType().getDescription());
		setArchiveVersion(jcoArchive.getVersion());
		if (getBundleVersion() == null || getBundleVersion().length() == 0) {
			setBundleVersion(jcoArchive.getVersion());
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleSymbolicName() {
		return this.jco3Archive.getBundleName();
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleFilename() {
		return this.bundleDeployLocation + File.separator + this.jco3Archive.getBundleName() + UNDERSCORE + this.bundleVersion + JAR_EXTENTION;
	}

	/**
	 * 
	 * @return
	 */
	public String getFragmentFilename() {
		return this.bundleDeployLocation + File.separator + this.jco3Archive.getType().getFragmentName() + UNDERSCORE + this.bundleVersion + JAR_EXTENTION;
	}

	/**
	 * 
	 * @return
	 */
	public String getFragmentBundleName() {
		return this.bundleName + FRAGMENT_BUNDLE_NAME_SUFFIX + this.jco3Archive.getType().getDescription();
	}

	/**
	 * 
	 * @return
	 */
	public String getFragmentSymbolicName() {
		return this.jco3Archive.getType().getFragmentName();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBundleNativeDirEntry() {
		return NATIVE_DIRECTORY_NAME + File.separator;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBundleJCoJarEntry() {
		return BUNDLE_JCO_JAR_ENTRY;
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleNativeLibraryEntry() {
		return NATIVE_DIRECTORY_NAME + File.separator + this.jco3Archive.getType().getNativeArchiveName();
	}

	/**
	 * 
	 * @return
	 */
	public String getFragmentHost() {
		return this.jco3Archive.getType().getPluginName() + FRAGMENT_HOST_BUNDLE_VERSION_PREFIX + this.bundleVersion + FRAGMENT_HOST_BUNDLE_VERSION_SUFFIX;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPlatformFilter() {
		return this.jco3Archive.getType().getPlatformFilter();
	}
}
