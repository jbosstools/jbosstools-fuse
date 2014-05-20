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

/**
 * 
 */
public class IDoc3ImportSettings extends SAPImportSettings {

	///////////////////////////////////////////////////////
	// Property Names

	public static final String IDOC3_ARCHIVE = "idoc3Archive"; //$NON-NLS-1$
	public static final String IDOC3_ARCHIVE_FILENAME = "idoc3ArchiveFilename"; //$NON-NLS-1$

	//
	///////////////////////////////////////////////////////

	private static final String JAR_EXTENTION = ".jar"; //$NON-NLS-1$
	private static final String UNDERSCORE = "_"; //$NON-NLS-1$
	private static final String BUNDLE_IDOC3_JAR_ENTRY = "sapidoc3.jar"; //$NON-NLS-1$

	private String idoc3ArchiveFilename;
	private IDoc3Archive idoc3Archive;

	/**
	 * 
	 * @return
	 */
	public String getIdoc3ArchiveFilename() {
		return this.idoc3ArchiveFilename;
	}

	/**
	 * 
	 * @param idoc3ArchiveFilename
	 */
	public void setIdoc3ArchiveFilename(String idoc3ArchiveFilename) {
		firePropertyChange(IDOC3_ARCHIVE_FILENAME, this.idoc3ArchiveFilename, this.idoc3ArchiveFilename = idoc3ArchiveFilename);
	}

	/**
	 * 
	 * @return
	 */
	public IDoc3Archive getIdoc3Archive() {
		return this.idoc3Archive;
	}

	/**
	 * 
	 * @param idoc3Archive
	 */
	public void setIdoc3Archive(IDoc3Archive idoc3Archive) {
		firePropertyChange(IDOC3_ARCHIVE, this.idoc3Archive, this.idoc3Archive = idoc3Archive);
		setArchiveVersion(idoc3Archive.getVersion());
		if (getBundleVersion() == null || getBundleVersion().length() == 0) {
			setBundleVersion(idoc3Archive.getVersion());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBundleSymbolicName() {
		return this.idoc3Archive.getBundleName();
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleFilename() {
		return this.bundleDeployLocation + File.separator + this.idoc3Archive.getBundleName() + UNDERSCORE + this.bundleVersion + JAR_EXTENTION;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBundleIDoc3JarEntry() {
		return BUNDLE_IDOC3_JAR_ENTRY;
	}
}
