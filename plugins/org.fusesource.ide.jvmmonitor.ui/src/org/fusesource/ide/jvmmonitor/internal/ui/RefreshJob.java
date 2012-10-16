/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * The job to refresh model and UI.
 */
abstract public class RefreshJob extends Job {

    /** The job ID. */
    private String jobId;

    /**
     * The constructor.
     * 
     * @param name
     *            The job name
     * @param jobId
     *            The job ID
     */
    public RefreshJob(String name, String jobId) {
        super(name);
        Assert.isNotNull(jobId);

        setPriority(SHORT);
        this.jobId = jobId;
    }

    /*
     * @see Job#run(IProgressMonitor)
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        refreshModel(monitor);

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                refreshUI();
            }
        });

        return Status.OK_STATUS;
    }

    /*
     * @see Job#belongsTo(Object)
     */
    @Override
    public boolean belongsTo(Object family) {
        return jobId.equals(family.toString());
    }

    /*
     * @see Job#shouldSchedule()
     */
    @Override
    public boolean shouldSchedule() {
        Job[] runningJobs = Job.getJobManager().find(jobId);
        for (Job runningJob : runningJobs) {
            if (runningJob instanceof RefreshJob
                    && belongsTo(((RefreshJob) runningJob).getJobId())) {
                return false;
            }
        }
        return true;
    }

    /*
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RefreshJob) {
            RefreshJob job = (RefreshJob) obj;
            return job.getJobId().equals(jobId);
        }
        return false;
    }

    /*
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() | jobId.hashCode();
    }

    /**
     * Refreshes the model.
     * 
     * @param monitor
     *            The progress monitor
     */
    protected void refreshModel(IProgressMonitor monitor) {
        // do nothing
    }

    /**
     * Refreshes the UI.
     */
    protected void refreshUI() {
        // do nothing
    }

    /**
     * Gets the job ID.
     * 
     * @return The job ID
     */
    private String getJobId() {
        return jobId;
    }
}