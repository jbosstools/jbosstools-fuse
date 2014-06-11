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
import org.fusesource.ide.jvmmonitor.internal.ui.actions.AbstractClearAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.CpuSection;

/**
 * The action to clear CPU profiling data.
 */
public class ClearCpuProfilingDataAction extends AbstractClearAction {

    /** The CPU section. */
    private CpuSection cpuSection;

    /**
     * The constructor.
     * 
     * @param cpuSection
     *            The CPU section
     */
    public ClearCpuProfilingDataAction(CpuSection cpuSection) {
        setText(Messages.clearCpuProfilingDataLabel);
        this.cpuSection = cpuSection;
    }

    /*
     * @see AbstractClearAction#doRun(IProgressMonitor)
     */
    @Override
    protected IStatus doRun(IProgressMonitor monitor) {
        cpuSection.clear();
        return Status.OK_STATUS;
    }

    /*
     * @see AbstractClearAction#getJobName()
     */
    @Override
    protected String getJobName() {
        return Messages.clearCpuProfilingDataLabel;
    }
}
