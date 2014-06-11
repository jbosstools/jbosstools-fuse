/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvmModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent.State;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerState;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;


/**
 * The tree viewer on JVM Monitor view.
 */
public class JvmTreeViewer extends TreeViewer implements
        IJvmModelChangeListener, ISelectionChangedListener,
        IDoubleClickListener {

    /** The start monitoring action. */
    private StartMonitoringAction startMonitoringAction;

    /** The stop monitoring action. */
    private StopMonitoringAction stopMonitoringAction;

    /** The open snapshot action. */
    private OpenSnapshotAction openSnapshotAction;

    /** The delete action. */
    private DeleteAction deleteAction;

    /** The copy action. */
    private CopyAction copyAction;

    /** The rename action. */
    private RenameAction renameAction;

    /** The error image. */
    Image errorImage;

    /** The status line manager. */
    IStatusLineManager statusLineManager;

    /** the status line item showing the state of JVM. */
    StatusLineContributionItem statusLineItem;

    /**
     * The constructor.
     * 
     * @param parent
     *            the parent composite
     * @param style
     *            the style
     * @param actionBars
     *            The action bars
     */
    public JvmTreeViewer(Composite parent, int style, IActionBars actionBars) {
        super(parent, style);
        statusLineManager = actionBars.getStatusLineManager();
        statusLineItem = new StatusLineContributionItem(
                "StatusLineContributionItem"); //$NON-NLS-1$
        statusLineManager.add(statusLineItem);

        setContentProvider(new JvmTreeContentProvider());
        setLabelProvider(new DecoratingStyledCellLabelProvider(
                new JvmTreeLabelProvider(), PlatformUI.getWorkbench()
                        .getDecoratorManager().getLabelDecorator(), null));

        createContextMenu(actionBars);
        addListeners();

        new Job(Messages.initializeJvmExplorer) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                // make sure not to instantiate JVM model in UI thread
                JvmModel.getInstance();

                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        setInput(new Object[0]);
                        JvmModel.getInstance().addJvmModelChangeListener(
                                JvmTreeViewer.this);
                    }
                });
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    /*
     * @see IJvmModelChangeListener#jvmModelChanged(JvmModelEvent)
     */
    @Override
    public void jvmModelChanged(final JvmModelEvent e) {
        if (e.state == State.CpuProfilerConfigChanged) {
            return;
        }

        if (e.state == State.JvmConnected || e.state == State.JvmDisconnected) {
            startMonitoringAction.refresh();
            stopMonitoringAction.refresh();
        }

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (getControl().isDisposed()) {
                    return;
                }

                if (e.state == State.JvmDisconnected) {
                    updateSelection(new StructuredSelection());
                    return;
                }

                if (e.state == State.ShapshotTaken) {
                    if (e.source instanceof ISnapshot) {
                        refresh();
                        setSelection(new StructuredSelection(e.source));
                        return;
                    }
                }

                if (e.state == State.JvmAdded) {
                    IHost host = e.jvm.getHost();
                    setExpandedState(host, true);
                }

                if (getControl().isFocusControl()) {
                    updateStatusLine((IStructuredSelection) getSelection());
                }
                refresh();
            }
        });
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        updateStatusLine((IStructuredSelection) event.getSelection());
    }

    /*
     * @see IDoubleClickListener#doubleClick(DoubleClickEvent)
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        startMonitoringAction.run();
        if (openSnapshotAction.isEnabled()) {
            openSnapshotAction.run();
        }
    }

    /*
     * @see StructuredViewer#updateSelection(ISelection)
     */
    @Override
    public void updateSelection(ISelection selection) {
        super.updateSelection(selection);
    }

    /**
     * Disposes the resources.
     */
    protected void dispose() {
        JvmModel.getInstance().removeJvmModelChangeListener(this);
        removeSelectionChangedListener(this);
        if (errorImage != null) {
            errorImage.dispose();
        }
    }

    /**
     * Adds listeners.
     */
    private void addListeners() {
        addDoubleClickListener(this);
        addSelectionChangedListener(this);
        addSelectionChangedListener(startMonitoringAction);
        addSelectionChangedListener(stopMonitoringAction);
        addSelectionChangedListener(openSnapshotAction);
        addSelectionChangedListener(copyAction);
        addSelectionChangedListener(deleteAction);
        addSelectionChangedListener(renameAction);
    }

    /**
     * Creates the context menu.
     * 
     * @param actionBars
     *            The action bars
     */
    private void createContextMenu(IActionBars actionBars) {
        startMonitoringAction = new StartMonitoringAction();
        stopMonitoringAction = new StopMonitoringAction(this);
        openSnapshotAction = new OpenSnapshotAction();
        copyAction = CopyAction.createCopyAction(actionBars);
        deleteAction = new DeleteAction(this, actionBars);
        renameAction = new RenameAction(this, actionBars);

        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                configureMenus(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(getControl());
        getControl().setMenu(menu);
    }

    /**
     * Configures the menus.
     * 
     * @param manager
     *            The menu manager
     */
    void configureMenus(IMenuManager manager) {
        if (startMonitoringAction.getVisible()) {
            manager.add(startMonitoringAction);
        }
        if (stopMonitoringAction.getVisible()) {
            manager.add(stopMonitoringAction);
            manager.add(new Separator());
        }
        if (openSnapshotAction.getVisible()) {
            manager.add(openSnapshotAction);
            manager.add(new Separator());
        }
        manager.add(copyAction);
        manager.add(deleteAction);
        manager.add(renameAction);
    }

    /**
     * Updates the status line.
     * 
     * @param selection
     *            the selection
     */
    void updateStatusLine(final IStructuredSelection selection) {

        RefreshJob refreshJob = new RefreshJob(
                Messages.refreshStatusLineJobLabel,
                JvmTreeViewer.class.getName()) {

            private ProfilerState state;

            private Object element;

            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                element = selection.getFirstElement();
                if (element instanceof IActiveJvm) {
                    IActiveJvm activeJvm = (IActiveJvm) element;
                    state = activeJvm.getCpuProfiler().getState();
                }
            }

            @Override
            protected void refreshUI() {
                StringBuffer text = new StringBuffer();
                StringBuffer errorText = new StringBuffer();
                errorImage = null;
                if (element instanceof IActiveJvm) {
                    IActiveJvm activeJvm = (IActiveJvm) element;
                    if (activeJvm.isConnected()) {
                        text.append(Messages.connectedMsg);
                        if (state == ProfilerState.RUNNING) {
                            text.append(" ").append( //$NON-NLS-1$
                                    Messages.cpuProfilerRunningMsg);
                        }
                    } else if (!activeJvm.isConnectionSupported()) {
                        String errorMessage = activeJvm.getErrorStateMessage();
                        if (errorMessage != null) {
                            errorText.append(errorMessage);
                        }
                    } else {
                        text.append(Messages.disconnectedMsg);
                    }
                } else if (element instanceof IHost) {
                    IHost host = (IHost) element;
                    if (host.getName().equals(IHost.LOCALHOST)
                            && !JvmModel.getInstance().hasValidJdk()) {
                        errorText.append(Messages.invalidJdkLocationMsg);
                        errorImage = getErrorImage();
                    }
                }

                statusLineManager.setErrorMessage(errorImage,
                        errorText.toString());
                statusLineItem.setText(text.toString());
            }
        };

        refreshJob.schedule();

    }

    /**
     * Gets the error image.
     * 
     * @return The error image
     */
    Image getErrorImage() {
        if (errorImage == null || errorImage.isDisposed()) {
            errorImage = PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK)
                    .createImage();
        }
        return errorImage;
    }
}
