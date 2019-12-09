/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.jobs;

import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;


public class Jobs {

	public static void cancel(Job... jobs) {
		for (Job job : jobs) {
			if (job != null) {
				job.cancel();
			}
		}
	}

	public static void schedule(Job... jobs) {
		for (Job job : jobs) {
			if (job != null) {
				job.setUser(true);
				job.schedule();
			}
		}
	}

	public static void schedule(final String message, final Runnable task) {
		schedule(new Job(message) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					task.run();
					return Status.OK_STATUS;
				} catch (Throwable e) {
					return new Status(Status.ERROR, FoundationUIActivator.PLUGIN_ID, "Failed to " + message, e);
				}
			}
		});
	}

	public static <T> void schedule(final String message, final Callable<T> task) {
		schedule(new Job(message) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					task.call();
					return Status.OK_STATUS;
				} catch (Throwable e) {
					return new Status(Status.ERROR, FoundationUIActivator.PLUGIN_ID, "Failed to " + message, e);
				}
			}
		});
	}

	/**
	 * Schedules the job for execution after the given delay if its > 0
	 */
	public static void schedule(long time, Job job) {
		if (time > 0) {
			job.schedule(time);
		}
	}

}
