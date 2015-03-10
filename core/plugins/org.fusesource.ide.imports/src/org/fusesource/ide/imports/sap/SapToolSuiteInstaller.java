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
package org.fusesource.ide.imports.sap;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.publisher.eclipse.FeaturesAndBundlesPublisherApplication;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.imports.sap.JCo3Archive.JCoArchiveType;

@SuppressWarnings("restriction")
public class SapToolSuiteInstaller implements IRunnableWithProgress {

	private static final String COMPRESS_FLAG = "-compress"; //$NON-NLS-1$
	private static final String PUBLISH_ARTIFACTS_FLAG = "-publishArtifacts"; //$NON-NLS-1$
	private static final String SOURCE_ARG = "-source"; //$NON-NLS-1$
	private static final String ARTIFACT_REPOSITORY_ARG = "-artifactRepository"; //$NON-NLS-1$
	private static final String METADATA_REPOSITORY_ARG = "-metadataRepository"; //$NON-NLS-1$
	private static final String DOT = "."; //$NON-NLS-1$
	private static final String CONFIGS_ARG = "-configs"; //$NON-NLS-1$
	private static final String APPEND_FLAG = "-append"; //$NON-NLS-1$
	private SapLibrariesFeatureArchive sapLibrariesFeatureArchive;
	private JCo3ImportSettings jco3ImportSettings;
	private IDoc3ImportSettings idoc3ImportSettings;

	public SapToolSuiteInstaller(SapLibrariesFeatureArchive sapLibrariesFeatureArchive, JCo3ImportSettings jco3ImportSettings, IDoc3ImportSettings idoc3ImportSettings) {
		this.sapLibrariesFeatureArchive = sapLibrariesFeatureArchive;
		this.jco3ImportSettings = jco3ImportSettings;
		this.idoc3ImportSettings = idoc3ImportSettings;
	}

	@Override
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
		SubMonitor monitor = SubMonitor.convert(progressMonitor, Messages.SapToolSuiteInstaller_InstallingJBossFuseSapToolSuite, 7); 
		try {
			// Deploy JCo3 Jar Bundle to temporary SAP Libraries Repository
			jco3ImportSettings.getJco3Archive().buildJCoPlugin(jco3ImportSettings);
			monitor.worked(1);
			checkCancelled(monitor);

			// Deploy JCo3 Native Library Bundle to temporary SAP Libraries Repository
			jco3ImportSettings.getJco3Archive().buildJCoNativePlugin(jco3ImportSettings);
			monitor.worked(1);
			checkCancelled(monitor);
			if (monitor.isCanceled())
				throw new InterruptedException();
			
			// Deploy IDoc3 Jar Bundle to temporary SAP Libraries Repository
			idoc3ImportSettings.getIdoc3Archive().buildIDoc3Plugin(idoc3ImportSettings);
			monitor.worked(1);
			checkCancelled(monitor);
			if (monitor.isCanceled()) 
				throw new InterruptedException();
			
			// Deploy  Feature Bundle to temporary SAP Libraries Repository
			sapLibrariesFeatureArchive.buildSAPLibrariesFeature();
			monitor.worked(1);
			checkCancelled(monitor);
			if (monitor.isCanceled()) 
				throw new InterruptedException();
			
			// Generate P2 Meta Data
			URI librariesRepositoryURI = ImportUtils.getTemporySapLibrariesRepository().toUri();
			String librariesRepositoryURL = librariesRepositoryURI.toString();
			String librariesRepositoryPath = ImportUtils.getTemporySapLibrariesRepository().toString();
			FeaturesAndBundlesPublisherApplication fabpa = new FeaturesAndBundlesPublisherApplication();
			JCoArchiveType type = jco3ImportSettings.getJco3Archive().getType();
			fabpa.run(new String[] {APPEND_FLAG, CONFIGS_ARG, type.getEclipseWS() + DOT + type.getEclipseOS() + DOT + type.getEclipseArch(), METADATA_REPOSITORY_ARG,  librariesRepositoryURL, ARTIFACT_REPOSITORY_ARG, librariesRepositoryURL, SOURCE_ARG, librariesRepositoryPath, PUBLISH_ARTIFACTS_FLAG, COMPRESS_FLAG});
			monitor.worked(1);
			checkCancelled(monitor);
			
			// Perform Installation
			IProvisioningAgent provisioningAgent = Activator.getProvisioningAgent();
			// TODO Use deployment site for SAP Tool Suite Repository Archive.
			URI sapToolSuiteRepositoryURI = URI.create("file:/Users/wcollins/Dev/fuseide/sap/site/target/repository/"); //$NON-NLS-1$
			try {
				ProvisioningSession session = new ProvisioningSession(provisioningAgent);

				final ProvisioningUI ui = new ProvisioningUI(session, IProfileRegistry.SELF, new SapToolsPolicy());
				ui.loadArtifactRepository(librariesRepositoryURI, false, monitor);
				ui.loadArtifactRepository(sapToolSuiteRepositoryURI, false, monitor);
				IMetadataRepository librariesMetadataRepository = ui.loadMetadataRepository(librariesRepositoryURI, false, monitor);
				IMetadataRepository sapToolingMetadataRepository = ui.loadMetadataRepository(sapToolSuiteRepositoryURI, false, monitor);
				final Set<IInstallableUnit> toInstall = new HashSet<IInstallableUnit>();
				toInstall.addAll(librariesMetadataRepository.query(QueryUtil.createIUGroupQuery(), monitor).toUnmodifiableSet());
				toInstall.addAll(sapToolingMetadataRepository.query(QueryUtil.createIUGroupQuery(), monitor).toUnmodifiableSet());
				final InstallOperation installOperation = ui.getInstallOperation(toInstall, new URI[] { librariesRepositoryURI, sapToolSuiteRepositoryURI });

				IStatus status = installOperation.resolveModal(monitor);
				monitor.worked(1);
				checkCancelled(monitor);
				if (!status.isOK()) {
					throw new InvocationTargetException(status.getException(), Messages.SapToolSuiteInstaller_JBossFuseSapToolSuiteCouldNotBeInstalled + status.getMessage());
				}

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						ui.openInstallWizard(toInstall, installOperation, null);
					}
				});
			} finally {
				// Remove SAP Tooling and SAP Libraries repository from Repository manager's list of known repositories.
				IArtifactRepositoryManager artifactRepositoryManager = (IArtifactRepositoryManager) provisioningAgent.getService(IArtifactRepositoryManager.SERVICE_NAME);
				IMetadataRepositoryManager metadataRepositoryManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
				artifactRepositoryManager.removeRepository(librariesRepositoryURI);
				metadataRepositoryManager.removeRepository(librariesRepositoryURI);
				artifactRepositoryManager.removeRepository(sapToolSuiteRepositoryURI);
				metadataRepositoryManager.removeRepository(sapToolSuiteRepositoryURI);
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}
	}

	protected void checkCancelled(IProgressMonitor monitor) throws InterruptedException {
		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}
	}

}
