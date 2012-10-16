/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent.State;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerState;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerType;
import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.OpenDeclarationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ClearCpuProfilingDataAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.DumpCpuProfilingDataAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.FindAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ResumeCpuProfilingAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.SuspendCpuProfilingAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The CPU section.
 */
public class CpuSection extends AbstractJvmPropertySection {

    /** The default profiler sampling period. */
    static final Integer DEFAULT_SAMPLING_PERIOD = 50;

    /** The default profiler type. */
    private static final ProfilerType DEFAULT_PROFILER_TYPE = ProfilerType.SAMPLING;

    /** The call tree. */
    CallTreeTabPage callTree;

    /** The hot spots. */
    HotSpotsTabPage hotSpots;

    /** The caller and callee. */
    CallerCalleeTabPage callerCallee;

    /** The action to resume CPU profiler. */
    ResumeCpuProfilingAction resumeCpuProfilingAction;

    /** The action to suspend CPU profiler. */
    SuspendCpuProfilingAction suspendCpuProfilingAction;

    /** The action to clear CPU profiling data. */
    ClearCpuProfilingDataAction clearCpuProfilingDataAction;

    /** The action to dump CPU profiling data. */
    DumpCpuProfilingDataAction dumpCpuProfilingDataAction;

    /** The separator. */
    private Separator separator;

    /** The CPU model change listener. */
    private ICpuModelChangeListener cpuModelChangeListener;

    /**
     * The constructor.
     */
    public CpuSection() {
        suspendCpuProfilingAction = new SuspendCpuProfilingAction(this);
        resumeCpuProfilingAction = new ResumeCpuProfilingAction(this);
        clearCpuProfilingDataAction = new ClearCpuProfilingDataAction(this);
        dumpCpuProfilingDataAction = new DumpCpuProfilingDataAction(this);
        separator = new Separator();

        cpuModelChangeListener = new ICpuModelChangeListener() {
            @Override
            public void modelChanged(CpuModelEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        refreshViewers();
                    }
                });
            }
        };
    }

    /*
     * @see AbstractPropertySection#createControls(Composite,
     * TabbedPropertySheetPage)
     */
    @Override
    public void createControls(Composite parent) {
        contributeToActionBars();

        // hide the highlight margin with SWT.FLAT
        final CTabFolder tabFolder = getWidgetFactory().createTabFolder(parent,
                SWT.BOTTOM | SWT.FLAT);

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabSelectionChanged(tabFolder.getSelection());
            }
        });

        callTree = new CallTreeTabPage(this, tabFolder);
        hotSpots = new HotSpotsTabPage(this, tabFolder);
        callerCallee = new CallerCalleeTabPage(this, tabFolder);

        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(parent, IHelpContextIds.CPU_PAGE);
    }

    /*
     * @see AbstractPropertySection#refresh()
     */
    @Override
    public void refresh() {
        if (!isSectionActivated) {
            return;
        }
        refresh(false);
    }

    private void refresh(final boolean initConfig) {
        new RefreshJob(NLS.bind(Messages.refeshCpuSectionJobLabel, getJvm()
                .getPid()), toString()) {

            private boolean isCpuProfilerReady;
            private boolean isPackageSpecified;
            private boolean isCpuProfilerRunning;

            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                isCpuProfilerReady = isCpuProfilerReady();
                isPackageSpecified = isPackageSpecified();
                isCpuProfilerRunning = isCpuProfilerRunning();

                if (initConfig) {
                    setProfiledPackages();
                    setProfilerSamplingPeriod();
                    setProfilerType();
                }

                IActiveJvm jvm = getJvm();
                if (jvm == null || !isCpuProfilerRunning) {
                    return;
                }

                try {
                    jvm.getCpuProfiler().refreshBciProfileCache(monitor);
                    jvm.getCpuProfiler().getCpuModel().refreshMaxValues();
                } catch (JvmCoreException e) {
                    Activator.log(Messages.refreshCpuProfileDataFailedMsg, e);
                }
            }

            @Override
            protected void refreshUI() {
                IActiveJvm jvm = getJvm();
                boolean isConnected = jvm != null && jvm.isConnected();
                updatePage(isPackageSpecified);

                suspendCpuProfilingAction.setEnabled(isCpuProfilerRunning
                        && isCpuProfilerReady && isConnected);
                resumeCpuProfilingAction.setEnabled(!isCpuProfilerRunning
                        && isCpuProfilerReady && isPackageSpecified
                        && isConnected);
                clearCpuProfilingDataAction.setEnabled(isCpuProfilerReady
                        && isPackageSpecified && isConnected);
                dumpCpuProfilingDataAction.setEnabled(!hasErrorMessage());

                if (!isDisposed()) {
                    refreshBackground(callTree.getChildren(), isConnected);
                    refreshBackground(hotSpots.getChildren(), isConnected);
                    refreshBackground(callerCallee.getChildren(), isConnected);
                    refreshViewers();
                }
            }
        }.schedule();
    }

    /*
     * @see AbstractJvmPropertySection#jvmModelChanged(JvmModelEvent)
     */
    @Override
    public void jvmModelChanged(JvmModelEvent event) {
        super.jvmModelChanged(event);

        IActiveJvm jvm = getJvm();
        if (jvm == null || event.jvm == null
                || event.jvm.getPid() != jvm.getPid()
                || !(event.jvm instanceof IActiveJvm)) {
            return;
        }

        if (event.state == State.JvmConnected) {
            refresh(true);
        }
    }

    /*
     * @see AbstractJvmPropertySection#setInput(IWorkbenchPart, ISelection,
     * IActiveJvm, IActiveJvm)
     */
    @Override
    protected void setInput(IWorkbenchPart part, ISelection selection,
            IActiveJvm newJvm, IActiveJvm oldJvm) {
        if (oldJvm != null) {
            oldJvm.getCpuProfiler().getCpuModel()
                    .removeModelChangeListener(cpuModelChangeListener);
        }
        newJvm.getCpuProfiler().getCpuModel()
                .addModelChangeListener(cpuModelChangeListener);

        if (newJvm.isConnected()) {
            refresh(true);
        }

        callTree.setInput(newJvm);
        hotSpots.setInput(newJvm);
        callerCallee.setInput(newJvm);
    }

    /*
     * @see AbstractJvmPropertySection#addToolBarActions(IToolBarManager)
     */
    @Override
    protected void addToolBarActions(IToolBarManager manager) {
        suspendCpuProfilingAction.setEnabled(false);
        resumeCpuProfilingAction.setEnabled(false);
        clearCpuProfilingDataAction.setEnabled(false);
        manager.insertAfter("defaults", separator); //$NON-NLS-1$
        if (manager.find(clearCpuProfilingDataAction.getId()) == null) {
            manager.insertAfter("defaults", clearCpuProfilingDataAction); //$NON-NLS-1$
        }
        if (manager.find(suspendCpuProfilingAction.getId()) == null) {
            manager.insertAfter("defaults", suspendCpuProfilingAction); //$NON-NLS-1$
        }
        if (manager.find(resumeCpuProfilingAction.getId()) == null) {
            manager.insertAfter("defaults", resumeCpuProfilingAction); //$NON-NLS-1$
        }
        if (manager.find(dumpCpuProfilingDataAction.getId()) == null) {
            manager.insertAfter("defaults", dumpCpuProfilingDataAction); //$NON-NLS-1$
        }
    }

    /*
     * @see AbstractJvmPropertySection#removeToolBarActions(IToolBarManager)
     */
    @Override
    protected void removeToolBarActions(IToolBarManager manager) {
        manager.remove(suspendCpuProfilingAction.getId());
        manager.remove(resumeCpuProfilingAction.getId());
        manager.remove(clearCpuProfilingDataAction.getId());
        manager.remove(dumpCpuProfilingDataAction.getId());
        manager.remove(separator);
    }

    /*
     * @see AbstractJvmPropertySection#deactivateSection()
     */
    @Override
    protected void deactivateSection() {
        super.deactivateSection();
        Job.getJobManager().cancel(toString());
    }

    /**
     * Clears the CPU profile data.
     */
    public void clear() {
        IActiveJvm jvm = getJvm();
        if (jvm != null) {
            try {
                jvm.getCpuProfiler().clear();
            } catch (JvmCoreException e) {
                Activator.log(Messages.clearCpuProfileDataFailedMsg, e);
            }
        }
    }

    /**
     * Notifies that tab selection has been changed.
     * 
     * @param tabItem
     *            The tab item
     */
    protected void tabSelectionChanged(CTabItem tabItem) {
        clearStatusLine();

        AbstractTabPage page = (AbstractTabPage) tabItem.getControl();
        AbstractFilteredTree filteredTree = page.getFilteredTrees().get(0);
        FindAction findAction = (FindAction) getActionBars()
                .getGlobalActionHandler(ActionFactory.FIND.getId());
        if (findAction != null) {
            findAction.setViewer(filteredTree.getViewer(),
                    filteredTree.getViewerType());
        }
    }

    /**
     * Update the page.
     * 
     * @param isPackageSpecified
     *            True if packages are specified
     */
    void updatePage(boolean isPackageSpecified) {
        if (!callTree.isDisposed() && !hotSpots.isDisposed()
                && !callerCallee.isDisposed()) {
            callTree.updatePage(isPackageSpecified);
            hotSpots.updatePage(isPackageSpecified);
            callerCallee.updatePage(isPackageSpecified);
        }
    }

    /**
     * Refreshes the viewers.
     */
    void refreshViewers() {
        if (isSectionActivated && !isDisposed()) {
            callTree.refresh();
            hotSpots.refresh();
            callerCallee.refresh();
        }
    }

    /**
     * Gets the state indicating if pages are disposed.
     * 
     * @return <tt>true</tt> if pages are disposed
     */
    boolean isDisposed() {
        return callTree == null || hotSpots == null || callerCallee == null
                || callTree.isDisposed() || hotSpots.isDisposed()
                || callerCallee.isDisposed();
    }

    /**
     * Contributes to action bars.
     */
    private void contributeToActionBars() {
        IActionBars actionBars = getActionBars();

        OpenDeclarationAction.createOpenDeclarationAction(actionBars);
        actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
                new FindAction());
        CopyAction.createCopyAction(actionBars);
    }

    /**
     * Sets the profiled packages to CPU profiler.
     */
    void setProfiledPackages() {
        IActiveJvm jvm = getJvm();
        if (jvm == null) {
            return;
        }
        Set<String> packages = new LinkedHashSet<String>();
        String packagesString = Activator.getDefault()
                .getDialogSettings(CpuSection.class.getName())
                .get(IConstants.PACKAGES_KEY);
        if (packagesString != null) {
            if (packagesString.contains(",")) { //$NON-NLS-1$
                for (String item : packagesString.split(",")) { //$NON-NLS-1$
                    packages.add(item);
                }
            } else if (!packagesString.isEmpty()) {
                packages.add(packagesString);
            }
            try {
                jvm.getCpuProfiler().setProfiledPackages(packages);
            } catch (JvmCoreException e) {
                Activator.log(Messages.setProfiledPackagesFailedMsg, e);
            }
        }
    }
    
    /**
     * Sets the profiler sampling period.
     */
    void setProfilerSamplingPeriod() {
        IActiveJvm jvm = getJvm();
        if (jvm == null) {
            return;
        }
        
        Integer period = null;
        String periodString = Activator.getDefault()
        .getDialogSettings(CpuSection.class.getName())
        .get(IConstants.PROFILER_SAMPLING_PERIOD_KEY);
        if (periodString != null) {
            try {
                period = Integer.valueOf(periodString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        if (period == null) {
            period = DEFAULT_SAMPLING_PERIOD;
        }
        jvm.getCpuProfiler().setSamplingPeriod(period);
    }

    /**
     * Sets the profiler type.
     */
    void setProfilerType() {
        IActiveJvm jvm = getJvm();
        if (jvm == null) {
            return;
        }

        ProfilerType type = null;
        String typeString = Activator.getDefault()
                .getDialogSettings(CpuSection.class.getName())
                .get(IConstants.PROFILER_TYPE_KEY);
        if (typeString != null) {
            for (ProfilerType profilerType : ProfilerType.values()) {
                if (profilerType.name().equals(typeString)) {
                    type = profilerType;
                    break;
                }
            }
        }
        if (type == null) {
            type = DEFAULT_PROFILER_TYPE;
        }

        jvm.getCpuProfiler().setProfilerType(type);
    }

    /**
     * Gets the state indicating if CPU profiler is ready.
     * 
     * @return True if CPU profiler is ready
     */
    boolean isCpuProfilerReady() {
        IActiveJvm jvm = getJvm();
        return jvm != null
                && jvm.isConnected()
                && (jvm.getCpuProfiler().getState() == ProfilerState.READY || jvm
                        .getCpuProfiler().getState() == ProfilerState.RUNNING);
    }

    /**
     * Gets the state indicating if CPU profiler is running.
     * 
     * @return True if CPU profiler is running
     */
    boolean isCpuProfilerRunning() {
        IActiveJvm jvm = getJvm();
        return jvm != null && jvm.isConnected()
                && jvm.getCpuProfiler().getState() == ProfilerState.RUNNING;
    }

    /**
     * Gets the state indicating if the profiled packages are specified.
     * 
     * @return True if the profiled packages are specified
     */
    boolean isPackageSpecified() {
        IActiveJvm jvm = getJvm();
        if (jvm == null
                || jvm.getCpuProfiler().getState() == ProfilerState.AGENT_NOT_LOADED) {
            return false;
        }

        try {
            Set<String> packages = jvm.getCpuProfiler().getProfiledPackages();
            return packages != null && !packages.isEmpty();
        } catch (JvmCoreException e) {
            Activator.log(Messages.getProfiledPackagesFailedMsg, e);
            return false;
        }
    }
}
