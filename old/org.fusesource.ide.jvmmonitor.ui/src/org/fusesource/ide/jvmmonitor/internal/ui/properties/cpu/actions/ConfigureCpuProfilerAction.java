/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerState;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerType;
import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.CpuSection;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to configure CPU profiler.
 */
public class ConfigureCpuProfilerAction extends Action {

    /** The Java packages that CPU profiler profiles. */
    Set<String> packages;

    /** The CPU section. */
    CpuSection cpuSection;

    /**
     * The constructor.
     * 
     * @param cpuSection
     *            The CPU section
     */
    public ConfigureCpuProfilerAction(CpuSection cpuSection) {
        this.cpuSection = cpuSection;
        setText(Messages.configureCpuProfilerLabel);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {

        // get the profiled packages
        final Job getProfiledPackagesJob = new Job(
                Messages.getProfiledPackagesJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm jvm = cpuSection.getJvm();
                if (jvm == null) {
                    return Status.CANCEL_STATUS;
                }
                try {
                    packages = jvm.getCpuProfiler().getProfiledPackages();
                } catch (JvmCoreException e) {
                    Activator.log(Messages.getProfiledPackagesFailedMsg, e);
                }
                return Status.OK_STATUS;
            }
        };
        getProfiledPackagesJob.schedule();

        // open CPU profiler configuration dialog
        Job openDialogJob = new Job(Messages.openDialogJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    getProfiledPackagesJob.join();
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            openDialog();
                        }
                    });
                } catch (InterruptedException e) {
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        openDialogJob.schedule();
    }

    /**
     * Opens the dialog.
     */
    void openDialog() {
        IActiveJvm jvm = cpuSection.getJvm();
        if (jvm == null) {
            return;
        }

        final ConfigurationDialog dialog = new ConfigurationDialog(cpuSection
                .getPart().getSite().getShell(), jvm.getCpuProfiler()
                .getProfilerType(), jvm.getCpuProfiler().getSamplingPeriod(),
                jvm.getCpuProfiler().getState(ProfilerType.BCI), packages);

        if (dialog.open() != Window.OK) {
            return;
        }

        new Job(Messages.configureProfilerJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return doConfigure(monitor, dialog);
            }
        }.schedule();
    }

    /**
     * Configures the profiler.
     * 
     * @param monitor
     *            The progress monitor
     * @param dialog
     *            The configuration dialog
     * @return The status
     */
    IStatus doConfigure(IProgressMonitor monitor, ConfigurationDialog dialog) {
        IActiveJvm jvm = cpuSection.getJvm();
        if (jvm == null) {
            return Status.CANCEL_STATUS;
        }

        IDialogSettings dialogSettings = Activator.getDefault()
                .getDialogSettings(CpuSection.class.getName());

        ProfilerType type = dialog.getProfilerType();
        if (jvm.getCpuProfiler().getProfilerType() != type) {
            if (jvm.getCpuProfiler().getState() == ProfilerState.RUNNING) {
                new SuspendCpuProfilingAction(cpuSection).run();
            }
            new ClearCpuProfilingDataAction(cpuSection).run();
            jvm.getCpuProfiler().setProfilerType(type);
        }
        if (type == ProfilerType.SAMPLING) {
            int samplingPeriod = dialog.getSamplingPeriod();
            jvm.getCpuProfiler().setSamplingPeriod(samplingPeriod);
            dialogSettings.put(IConstants.PROFILER_SAMPLING_PERIOD_KEY,
                    samplingPeriod);
        }
        String packageString = setPackages(dialog.getPackages(), monitor);
        dialogSettings.put(IConstants.PACKAGES_KEY, packageString);
        dialogSettings.put(IConstants.PROFILER_TYPE_KEY, type.name());

        return Status.OK_STATUS;
    }

    /**
     * Sets the packages.
     * 
     * @param packages
     *            The packages
     * @param monitor
     *            The progress monitor
     * @return The packages string with delimiter ','
     */
    String setPackages(Set<String> packages, IProgressMonitor monitor) {
        IActiveJvm jvm = cpuSection.getJvm();
        if (jvm != null) {
            try {
                jvm.getCpuProfiler().setProfiledPackages(packages);
                if (jvm.getCpuProfiler().getProfilerType() == ProfilerType.BCI
                        && jvm.getCpuProfiler().getState() == ProfilerState.RUNNING) {
                    jvm.getCpuProfiler().transformClasses(monitor);
                }
            } catch (JvmCoreException e) {
                Activator.log(Messages.setProfiledPackagesFailedMsg, e);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        StringBuffer buffer = new StringBuffer();
        for (String item : packages) {
            if (buffer.length() > 0) {
                buffer.append(',');
            }
            buffer.append(item);
        }
        return buffer.toString();
    }
}
