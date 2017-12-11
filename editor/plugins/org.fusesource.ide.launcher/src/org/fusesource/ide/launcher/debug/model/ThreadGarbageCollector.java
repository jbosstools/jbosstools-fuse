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
package org.fusesource.ide.launcher.debug.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.launcher.Activator;

public class ThreadGarbageCollector extends Job {
	
	private static final int TIME_BETWEEN_GARBAGE_COLLECTION = 60000; // 1 minute
	public static final long THREAD_LIFE_DURATION = 10*60*1000L; // 10 minutes
	
	private final CamelDebugTarget camelDebugTarget;
	
	public ThreadGarbageCollector(CamelDebugTarget camelDebugTarget) {
		super("Thread CleanUp Service");
		this.camelDebugTarget = camelDebugTarget;
		setSystem(true);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		while (!camelDebugTarget.isTerminated() && !monitor.isCanceled()) {
			try {
				checkAllThreads();
				Thread.sleep(TIME_BETWEEN_GARBAGE_COLLECTION);
			} catch (InterruptedException | DebugException ex) {
				Activator.getLogger().error(ex);
			}
		}
		return Status.OK_STATUS;
	}

	private void checkAllThreads() throws DebugException {
		for (CamelThread t : camelDebugTarget.getThreads()) {
			// we clean all threads not suspended in the last x seconds and state running
			if (!t.isSuspended() && (System.currentTimeMillis() - t.getLastSuspended()) > THREAD_LIFE_DURATION) {
				t.terminate();
			}
		}
	}
}