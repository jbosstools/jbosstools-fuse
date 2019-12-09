/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.projecttemplates.actions.ui.UnknownTimeMonitorUpdater;
import org.fusesource.ide.projecttemplates.internal.Messages;

/**
 * @author lheinema
 *
 */
public class CamelVersionChecker implements IRunnableWithProgress {

	private String camelVersionToValidate;
	private boolean valid = true;
	private boolean isCanceled = false;
	private boolean done;
	private UnknownTimeMonitorUpdater unknownTimeMonitorUpdater;
	private Thread threadCheckingCamelVersion;
	
	public CamelVersionChecker(String camelVersionToValidate) {
		this.camelVersionToValidate = camelVersionToValidate;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.validatingCamelVersionMessage, camelVersionToValidate), 1);
		unknownTimeMonitorUpdater = new UnknownTimeMonitorUpdater(subMonitor);
		try {
			new Thread(unknownTimeMonitorUpdater).start();
			threadCheckingCamelVersion = createThreadCheckingCamelVersion(camelVersionToValidate);
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

	public Thread createThreadCheckingCamelVersion(String camelVersion) {
		return new Thread(
				null, 
				() -> valid = CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(camelVersion),
				"CamelVersionChecker "+CamelVersionChecker.this.toString());
	}

	public boolean isCanceled() {
		return isCanceled;
	}
}
