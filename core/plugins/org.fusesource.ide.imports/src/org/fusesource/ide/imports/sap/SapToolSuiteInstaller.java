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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.publisher.eclipse.FeaturesAndBundlesPublisherApplication;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
import org.fusesource.ide.imports.sap.JCo3Archive.JCoArchiveType;
import org.jboss.tools.foundation.core.properties.IPropertiesProvider;
import org.jboss.tools.foundation.core.properties.PropertiesHelper;

/**
 * SAP Tool Suite Installer
 * 
 * @author William Collins punkhornsw@gmail.com
 *
 */
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
	private static final String FUSE_EXTRAS_URL_PROP = "jboss.fuse.extras.site.url"; //$NON-NLS-1$
	private static final String FUSE_EXTRAS_FEATURE_PROP = "jboss.fuse.extras.site.feature"; //$NON-NLS-1$
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
			final URI librariesRepositoryURI = ImportUtils.getTemporySapLibrariesRepository().toUri();
			String librariesRepositoryURL = librariesRepositoryURI.toString();
			String librariesRepositoryPath = ImportUtils.getTemporySapLibrariesRepository().toString();
			FeaturesAndBundlesPublisherApplication fabpa = new FeaturesAndBundlesPublisherApplication();
			JCoArchiveType type = jco3ImportSettings.getJco3Archive().getType();
			fabpa.run(new String[] {APPEND_FLAG, CONFIGS_ARG, type.getEclipseWS() + DOT + type.getEclipseOS() + DOT + type.getEclipseArch(), METADATA_REPOSITORY_ARG,  librariesRepositoryURL, ARTIFACT_REPOSITORY_ARG, librariesRepositoryURL, SOURCE_ARG, librariesRepositoryPath, PUBLISH_ARTIFACTS_FLAG, COMPRESS_FLAG});
			monitor.worked(1);
			checkCancelled(monitor);
			
			// Perform Installation
			final IProvisioningAgent provisioningAgent = Activator.getProvisioningAgent();
			final URI sapToolingSuiteRepositoryURI = getSapToolingUpdateSiteUrl();
			String sapToolingSuiteFeature = getSapToolingFeature();

			ProvisioningSession session = new ProvisioningSession(provisioningAgent);

			final ProvisioningUI ui = new ProvisioningUI(session, IProfileRegistry.SELF, new SapToolsPolicy());
			ui.loadArtifactRepository(librariesRepositoryURI, false, monitor);
			ui.loadArtifactRepository(sapToolingSuiteRepositoryURI, false, new NullProgressMonitor());
			IMetadataRepository librariesMetadataRepository = ui.loadMetadataRepository(librariesRepositoryURI, false, monitor);
			IMetadataRepository sapToolingMetadataRepository = ui.loadMetadataRepository(sapToolingSuiteRepositoryURI, false, new NullProgressMonitor());
			final Set<IInstallableUnit> toInstall = new HashSet<>();
			toInstall.addAll(librariesMetadataRepository.query(QueryUtil.createIUGroupQuery(), monitor).toUnmodifiableSet());
			toInstall.addAll(sapToolingMetadataRepository.query(QueryUtil.createIUQuery(sapToolingSuiteFeature), monitor).toUnmodifiableSet());
			final InstallOperation installOperation = ui.getInstallOperation(toInstall, new URI[] { librariesRepositoryURI, sapToolingSuiteRepositoryURI });

			IStatus status = installOperation.resolveModal(monitor);
			monitor.worked(1);
			checkCancelled(monitor);
			if (!status.isOK()) {
				ErrorDialog.openError(Display.getDefault().getActiveShell(), Messages.SapToolSuiteInstaller_JBossFuseSAPToolSuiteInstallFailed, Messages.SapToolSuiteInstaller_UnableToPerformInstallationOfJBossFuseSAPToolSuite, status);
				throw new InvocationTargetException(status.getException(), Messages.SapToolSuiteInstaller_JBossFuseSapToolSuiteCouldNotBeInstalled + status.getMessage());
			}

			final ProvisioningJob job = installOperation.getProvisioningJob(new NullProgressMonitor());
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					// Remove Temporary SAP Libraries repository from Repository manager's list of known repositories.
					IArtifactRepositoryManager artifactRepositoryManager = (IArtifactRepositoryManager) provisioningAgent.getService(IArtifactRepositoryManager.SERVICE_NAME);
					IMetadataRepositoryManager metadataRepositoryManager = (IMetadataRepositoryManager) provisioningAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
					artifactRepositoryManager.removeRepository(librariesRepositoryURI);
					metadataRepositoryManager.removeRepository(librariesRepositoryURI);
					
					// Delete SAP Library repository
					ImportUtils.deleteTemporarySapLibrariesRepository();
					
					final IStatus jobResult = job.getResult();
					if(IStatus.ERROR == jobResult.getSeverity()){
						ErrorDialog.openError(Display.getDefault().getActiveShell(), Messages.SapToolSuiteInstaller_errorDuringInstallationTitle, Messages.SapToolSuiteInstaller_errorDuringInstallationMessage, jobResult);
					}
				}
			});
			ui.schedule(job, StatusManager.SHOW | StatusManager.LOG);
		} catch (Exception e) {
			throw new InvocationTargetException(e, e.getMessage());
		} finally {
			monitor.done();
		}
	}
	
	public URI getSapToolingUpdateSiteUrl() throws InterruptedException {
		String sapToolingUpdateSite = System.getProperty(FUSE_EXTRAS_URL_PROP, null);
		if (sapToolingUpdateSite == null) {
			IPropertiesProvider propertiesProvider = PropertiesHelper.getPropertiesProvider();
			sapToolingUpdateSite = propertiesProvider.getValue(FUSE_EXTRAS_URL_PROP);
		}
		if (sapToolingUpdateSite == null) {
			throw new InterruptedException(String.format("No URL set for discovery catalog. Property %s is missing!", FUSE_EXTRAS_URL_PROP)); //$NON-NLS-1$
		}
		
		return URI.create(sapToolingUpdateSite);
	}

	public String getSapToolingFeature() throws InterruptedException {
		String sapToolingUpdateSite = System.getProperty(FUSE_EXTRAS_FEATURE_PROP, null);
		if (sapToolingUpdateSite == null) {
			IPropertiesProvider propertiesProvider = PropertiesHelper.getPropertiesProvider();
			sapToolingUpdateSite = propertiesProvider.getValue(FUSE_EXTRAS_FEATURE_PROP);
		}
		if (sapToolingUpdateSite == null) {
			throw new InterruptedException(String.format("No feature set for discovery catalog. Property %s is missing!", FUSE_EXTRAS_FEATURE_PROP)); //$NON-NLS-1$
		}
		
		return sapToolingUpdateSite;
	}

	protected void checkCancelled(IProgressMonitor monitor) throws InterruptedException {
		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}
	}

}
