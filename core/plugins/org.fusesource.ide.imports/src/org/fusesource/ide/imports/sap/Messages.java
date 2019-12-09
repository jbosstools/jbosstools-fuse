/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at https://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.imports.sap;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.fusesource.ide.imports.sap.l10n.messages"; //$NON-NLS-1$
	public static String ArchivesSelectionPage_ArchiveOSPlatform;
	public static String ArchivesSelectionPage_ArchiveVersion;
	public static String ArchivesSelectionPage_Browse;
	public static String ArchivesSelectionPage_PageName;
	public static String ArchivesSelectionPage_PleaseSelectJCo3Archive;
	public static String ArchivesSelectionPage_IncompatibleJC03ArchiveFileType;
	public static String ArchivesSelectionPage_IncompatibleJCo3ArchiveFileVersion;
	public static String ArchivesSelectionPage_InvalidJCo3ArchiveNameValue;
	public static String ArchivesSelectionPage_UnsupportedJC03ArchiveFile;
	public static String ArchivesSelectionPage_UnsupportedJCo3ArchiveFileFilename;
	public static String ArchivesSelectionPage_JCo3ArchiveFile;
	public static String ArchivesSelectionPage_JCo3ArchivePath_text_message;
	public static String ArchivesSelectionPage_IDoc3ArchiveFile;
	public static String ArchivesSelectionPage_IDoc3ArchivePath;
	public static String ArchivesSelectionPage_IncompatibleIDoc3ArchibeFileVersion;
	public static String ArchivesSelectionPage_InvalidIDoc3ArchiveNameValue;
	public static String ArchivesSelectionPage_PleaseSelectIDoc3Archive;
	public static String ArchivesSelectionPage_SelectArchiveFilesContainingJCo3IDoc3Libs;
	public static String ArchivesSelectionPage_SelectIDoc3ArchiveFile;
	public static String ArchivesSelectionPage_SelectJCo3ArchiveFile;
	public static String ArchivesSelectionPage_SelectJCo3IDoc3ArchiveToImport;
	public static String ArchivesSelectionPage_UnsupportedIDoc3ArchiveFile;
	public static String ArchivesSelectionPage_UnsupportedIDoc3ArchiveFileFilename;

	public static String JCo3Archive_FailedToBuildJCo3Plugin;
	public static String JCo3Archive_FailedToBuildJCo3PluginFragment;
	public static String JCo3Archive_FileMissingFromArchive;
	public static String JCo3Archive_HeaderisMissingFromManifestFile;
	public static String JCo3Archive_OSPlatformIsNotSupported;
	public static String JCo3Archive_UnableToParseArchiveManifestFile;
	
	public static String SAPArchive_InvalidFile;
	public static String SAPToolSuiteImportWizard_WindowTitle;
	
	public static String OverviewPage_DownloadAccountNote;
	public static String OverviewPage_DownloadContinueDirections;
	public static String OverviewPage_DownloadDirections;
	public static String OverviewPage_DownloadIDocVersionNote;
	public static String OverviewPage_DownloadIntro;
	public static String OverviewPage_DownloadJCoVersionNote;
	public static String OverviewPage_PageTitle;
	public static String OverviewPage_DownloadTheSAPJavaConnectorAndSAPJavaIDocClassLibraryArchiveFiles;
	public static String OverviewPage_ErrorOpeningTheSAPDownloadPage;

	public static String IDoc3Archive_FailedToBuildIDoc3Plugin;
	public static String IDoc3Archive_FileIsMissingFromArchive;

	public static String ImportUtils_ArchiveVersionIsInvalid;
	public static String ImportUtils_ArchiveVersionIsNotSupported;
	public static String ImportUtils_ArchiveVersionNotFound;

	public static String SAPToolSuiteImportWizard_RestartEclipseMessage;
	public static String SAPToolSuiteImportWizard_RestartEclipseTitle;
	public static String SAPToolSuiteImportWizard_SAPImportErrorMessage;
	public static String SAPToolSuiteImportWizard_SAPImportErrorTitle;
	public static String SAPToolSuiteImportWizard_SAPImportCancelledMessage;
	public static String SAPToolSuiteImportWizard_SAPImportCancelledTitle;
	public static String SapToolSuiteInstaller_errorDuringInstallationMessage;
	public static String SapToolSuiteInstaller_errorDuringInstallationTitle;
	public static String SapToolSuiteInstaller_InstallingJBossFuseSapToolSuite;
	public static String SapToolSuiteInstaller_JBossFuseSapToolSuiteCouldNotBeInstalled;
	public static String SapToolSuiteInstaller_JBossFuseSAPToolSuiteInstallFailed;
	public static String SapToolSuiteInstaller_UnableToPerformInstallationOfJBossFuseSAPToolSuite;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
