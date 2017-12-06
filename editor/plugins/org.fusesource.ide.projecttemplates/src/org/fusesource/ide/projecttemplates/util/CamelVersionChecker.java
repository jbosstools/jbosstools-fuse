/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.projecttemplates.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.projecttemplates.internal.Messages;

/**
 * @author lheinema
 *
 */
public class CamelVersionChecker implements IRunnableWithProgress {

	private String camelVersionToValidate;
	private Thread thread;
	private IProgressMonitor monitor;
	private boolean valid;
	private boolean done;
	
	public CamelVersionChecker(String camelVersionToValidate) {
		this.camelVersionToValidate = camelVersionToValidate;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		this.monitor = monitor;
		monitor.beginTask(Messages.newProjectWizardRuntimePageResolveDependencyStatus, IProgressMonitor.UNKNOWN);
		this.thread = createThread();
		this.thread.start();
		while (this.thread.isAlive() && !this.thread.isInterrupted() && !monitor.isCanceled()) {
			// wait
			Thread.sleep(100);
		}
		done = true;
	}
	
	public void cancel() {
		this.thread.interrupt();
		monitor.setCanceled(true);
	}
	
	private Thread createThread() {
		return new Thread( () -> {
				valid = isCamelVersionValid(camelVersionToValidate);
				if (!monitor.isCanceled()) {
					monitor.done();
				}
		});
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public synchronized boolean isCamelVersionValid(String camelVersion) {
		boolean versionValid = false;
		if (camelVersion != null) {
			versionValid = CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(camelVersion);
		}
		return versionValid;
	}
}
