/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.preferences.StagingRepositoriesUtils;
import org.fusesource.ide.projecttemplates.actions.ui.UnknownTimeMonitorUpdater;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;

/**
 * @author lheinema
 *
 */
public class SyndesisVersionChecker implements IRunnableWithProgress {
	
	private static final String BOM_GROUPID = "io.syndesis.extension";
	private static final String BOM_ARTIFACTID = "extension-bom";
	private static final String BOM_TYPE = "pom";
	private static final String PROP_SPRINGBOOT_VERSION = "spring-boot.version";
	private static final String PROP_CAMEL_VERSION = "camel.version";
	private static final String FALLBACK_SPRINGBOOT_VERSION = "1.5.8.RELEASE";
	private static final String FALLBACK_CAMEL_VERSION = "2.20.1";
	
	private String syndesisVersionToValidate;
	private String camelVersionRetrieved;
	private String springBootVersionRetrieved;
	private boolean valid = true;
	private boolean isCanceled = false;
	private boolean done;
	private UnknownTimeMonitorUpdater unknownTimeMonitorUpdater;
	private Thread threadCheckingCamelVersion;
	
	public SyndesisVersionChecker(String syndesisVersionToValidate) {
		this.syndesisVersionToValidate = syndesisVersionToValidate;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.validatingSyndesisVersionMessage, syndesisVersionToValidate), 1);
		unknownTimeMonitorUpdater = new UnknownTimeMonitorUpdater(subMonitor);
		try {
			new Thread(unknownTimeMonitorUpdater).start();
			threadCheckingCamelVersion = createThreadCheckingSyndesisVersion(syndesisVersionToValidate, monitor);
			threadCheckingCamelVersion.start();
			while (!unknownTimeMonitorUpdater.shouldTerminate() && threadCheckingCamelVersion.isAlive() && !threadCheckingCamelVersion.isInterrupted()) {
				Thread.sleep(100);
			}
			if (subMonitor.isCanceled()) {
				isCanceled = true;
			}
			if (threadCheckingCamelVersion.isAlive()) {
				threadCheckingCamelVersion.interrupt();
			}
			subMonitor.setWorkRemaining(0);
		} finally {
			unknownTimeMonitorUpdater.finish();
		}
		done = true;
	}
	
	public void cancel() {
		isCanceled = true;
		if (unknownTimeMonitorUpdater != null) {
			unknownTimeMonitorUpdater.cancel();
		}
		if (threadCheckingCamelVersion != null) {
			threadCheckingCamelVersion.interrupt();
		}
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isValid() {
		return valid;
	}

	public Thread createThreadCheckingSyndesisVersion(String syndesisVersion, IProgressMonitor monitor) {
		return new Thread(
				null, 
				() -> valid = isSyndesisVersionExisting(syndesisVersion, monitor),
				"SyndesisVersionChecker " + SyndesisVersionChecker.this.toString());
	}

	private boolean isSyndesisVersionExisting(String syndesisVersion, IProgressMonitor monitor) {
		boolean exists = false;
		
		IMaven maven = MavenPlugin.getMaven();
		List<ArtifactRepository> repos;
		try {
			repos = getRepositoryList(maven);
		} catch (CoreException ex) {
			SyndesisExtensionsUIActivator.pluginLog().logError(ex);
			repos = new ArrayList<>();
		}
		
		try {
			Artifact bom = maven.resolve(BOM_GROUPID, BOM_ARTIFACTID, syndesisVersion, BOM_TYPE, null, repos, monitor);
			if (bom != null) {
				exists = true;
				Model bomModel = maven.readModel(bom.getFile());
				if (bomModel.getProperties().containsKey(PROP_SPRINGBOOT_VERSION)) {
					springBootVersionRetrieved = bomModel.getProperties().getProperty(PROP_SPRINGBOOT_VERSION, FALLBACK_SPRINGBOOT_VERSION);
				}
				if (bomModel.getProperties().containsKey(PROP_CAMEL_VERSION)) {
					camelVersionRetrieved = bomModel.getProperties().getProperty(PROP_CAMEL_VERSION, FALLBACK_CAMEL_VERSION);
				}
				
			}
		} catch (CoreException ex) {
			SyndesisExtensionsUIActivator.pluginLog().logError(ex);
		}
		return exists;
	}
	
	private List<ArtifactRepository> getRepositoryList(IMaven maven) throws CoreException {
		List<ArtifactRepository> repoList = new ArrayList<>();
		List<List<String>> additionRepos = StagingRepositoriesUtils.getAdditionalRepos();
		for (List<String> repo : additionRepos) {
			String name = repo.get(0);
			String url = repo.get(1);
			repoList.add(maven.createArtifactRepository(name, url));
		}
		
		return repoList;
	}
	
	public boolean isCanceled() {
		return isCanceled;
	}
	
	/**
	 * @return the camelVersionRetrieved
	 */
	public String getCamelVersionRetrieved() {
		return this.camelVersionRetrieved;
	}
	
	/**
	 * @return the springBootVersionRetrieved
	 */
	public String getSpringBootVersionRetrieved() {
		return this.springBootVersionRetrieved;
	}
}
