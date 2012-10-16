/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The abstract class for clear action.
 */
abstract public class AbstractClearAction extends Action {

    /**
     * The constructor.
     */
    public AbstractClearAction() {
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.CLEAR_IMG_PATH));
        setDisabledImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISABLED_CLEAR_IMG_PATH));
        setId(getClass().getName());
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        new Job(getJobName()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return doRun(monitor);
            }
        }.schedule();
    }

    /**
     * Runs the action.
     * 
     * @param monitor
     *            The progress monitor
     * @return The status
     */
    abstract protected IStatus doRun(IProgressMonitor monitor);

    /**
     * Gets the job name.
     * 
     * @return The job name
     */
    abstract protected String getJobName();
}
