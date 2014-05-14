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

	public String getIdoc3ArchiveFilename() {
		return idoc3ArchiveFilename;
	}

	public void setIdoc3ArchiveFilename(String idoc3ArchiveFilename) {
		firePropertyChange(IDOC3_ARCHIVE_FILENAME, this.idoc3ArchiveFilename, this.idoc3ArchiveFilename = idoc3ArchiveFilename);
	}

	public IDoc3Archive getIdoc3Archive() {
		return idoc3Archive;
	}

	public void setIdoc3Archive(IDoc3Archive idoc3Archive) {
		firePropertyChange(IDOC3_ARCHIVE, this.idoc3Archive, this.idoc3Archive = idoc3Archive);
		setArchiveVersion(idoc3Archive.getVersion());
		if (getBundleVersion() == null || getBundleVersion().length() == 0) {
			setBundleVersion(idoc3Archive.getVersion());
		}
	}
	
	public String getBundleSymbolicName() {
		return idoc3Archive.getBundleName();
	}

	public String getBundleFilename() {
		return bundleDeployLocation + File.separator + idoc3Archive.getBundleName() + UNDERSCORE + bundleVersion + JAR_EXTENTION;
	}
	
	public String getBundleIDoc3JarEntry() {
		return BUNDLE_IDOC3_JAR_ENTRY;
	}
}
