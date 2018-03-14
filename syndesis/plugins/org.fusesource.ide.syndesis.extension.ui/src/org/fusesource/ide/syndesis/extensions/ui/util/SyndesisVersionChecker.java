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
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.projecttemplates.actions.ui.UnknownTimeMonitorUpdater;
import org.fusesource.ide.syndesis.extensions.core.util.SyndesisVersionUtil;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;

/**
 * @author lheinema
 *
 */
public class SyndesisVersionChecker implements IRunnableWithProgress {
	
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
				() -> {
					Map<String, String> versions = SyndesisVersionUtil.checkSyndesisVersionExisting(syndesisVersion, monitor);
					valid = versions.containsKey(SyndesisVersionUtil.PROP_SYNDESIS_VERSION);
					springBootVersionRetrieved = versions.get(SyndesisVersionUtil.PROP_SPRINGBOOT_VERSION);
					camelVersionRetrieved = versions.get(SyndesisVersionUtil.PROP_CAMEL_VERSION);
				},"SyndesisVersionChecker " + SyndesisVersionChecker.this.toString());
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
