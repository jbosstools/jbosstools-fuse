/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * A Job that can start another job upon successful completion.
 */
public abstract class ChainedJob extends Job {
	private Job nextJob;
	private IJobChangeListener listener;
	private String family;

	/**
	 * Create a new dependent job.
	 * 
	 * @param name the name of the job
	 * @param server the server to publish to
	 */
	public ChainedJob(String name, String family) {
		super(name);
		this.family = family;
	}

	/**
	 * @see Job#belongsTo(java.lang.Object)
	 */
	public boolean belongsTo(Object family) {
		return family.equals(family);
	}


	/**
	 * Create a listener for when this job finishes.
	 */
	protected void createListener() {
		if (listener != null)
			return;
		
		listener = new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				jobDone(event.getResult());
			}
		};
		
		addJobChangeListener(listener);
	}

	/**
	 * Called when this job is complete.
	 * 
	 * @param status the result of the current job
	 */
	protected void jobDone(IStatus status) {
		if (listener == null)
			return;
		
		removeJobChangeListener(listener);
		listener = null;
		
		if (nextJob != null && status != null && status.getSeverity() != IStatus.ERROR
				&& status.getSeverity() != IStatus.CANCEL)
			nextJob.schedule();
	}

	/**
	 * Set the next job, which should be scheduled if and only if this job completes
	 * successfully. The next job will be run as long as the result of this job is
	 * not an ERROR or CANCEL status.
	 * This method is not thread-safe. However, the next job can be changed anytime
	 * up until the current job completes.
	 * 
	 * @param job the next job that should be scheduled
	 */
	public void setNextJob(Job job) {
		nextJob = job;
		createListener();
	}
}
