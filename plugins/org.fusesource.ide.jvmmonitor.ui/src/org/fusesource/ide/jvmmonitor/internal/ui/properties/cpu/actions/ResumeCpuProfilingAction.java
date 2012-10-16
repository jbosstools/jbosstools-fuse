/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerType;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to resume CPU profiling.
 */
public class ResumeCpuProfilingAction extends Action {

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public ResumeCpuProfilingAction(AbstractJvmPropertySection section) {
        setText(Messages.resumeCpuProfilingLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.RESUME_IMG_PATH));
        setDisabledImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISABLED_RESUME_IMG_PATH));
        setId(getClass().getName());

        this.section = section;
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        new Job(Messages.resumeCpuProfilingJob) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                if (jvm == null) {
                    return Status.CANCEL_STATUS;
                }

                if (jvm.getCpuProfiler().getProfilerType() == ProfilerType.BCI) {
                    try {
                        jvm.getCpuProfiler().transformClasses(monitor);
                    } catch (JvmCoreException e) {
                        Activator.log(Messages.resumeCpuProfilingFailedMsg, e);
                    } catch (InterruptedException e) {
                        return Status.CANCEL_STATUS;
                    }
                }

                try {
                    jvm.getCpuProfiler().resume();
                } catch (JvmCoreException e) {
                    Activator.log(Messages.resumeCpuProfilingFailedMsg, e);
                }
                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
