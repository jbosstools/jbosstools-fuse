/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.PropertySheet;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.ICallTreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ConfigureCpuProfilerAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The caller and callee tab page.
 */
public class CallerCalleeTabPage extends AbstractTabPage {

    /** The caller filtered tree. */
    HotSpotsFilteredTree callerFilteredTree;

    /** The callee filtered tree. */
    HotSpotsFilteredTree calleeFilteredTree;

    /** The page book for callers/callees. */
    private PageBook callersCalleesPageBook;

    /** The caller callee image. */
    private Image callerCalleeImage;

    /**
     * The constructor.
     * 
     * @param cpuSection
     *            The CPU section
     * @param tabFolder
     *            The tab folder
     */
    public CallerCalleeTabPage(final CpuSection cpuSection,
            final CTabFolder tabFolder) {
        super(cpuSection, tabFolder);

        callersCalleesPageBook = new PageBook(viewForm, SWT.NONE);
        Label messageLabel = new Label(callersCalleesPageBook, SWT.WRAP);
        messageLabel.setText(Messages.noCallersCalleesMessage);

        SashForm sashForm = new SashForm(callersCalleesPageBook, SWT.NONE);
        sashForm.setOrientation(SWT.VERTICAL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        createCallerViewer(sashForm);
        createCalleeViewer(sashForm);

        callersCalleesPageBook.showPage(sashForm);

        viewForm.setContent(callersCalleesPageBook);

        final CTabItem tabItem = cpuSection.getWidgetFactory().createTabItem(
                tabFolder, SWT.NONE);
        tabItem.setText(Messages.callersCalleesTabLabel);
        tabItem.setImage(getCallerCalleeImage());
        tabItem.setControl(this);

        cpuModelChangeListener = new ICpuModelChangeListener() {
            @Override
            public void modelChanged(CpuModelEvent event) {
                if (event.state == CpuModelState.CallersCalleesTargetChanged) {
                    refresh();
                    if (jvm.getCpuProfiler().getCpuModel()
                            .getCallersCalleesTarget() != null
                            && !tabFolder.isDisposed()) {
                        tabFolder.setSelection(tabItem);

                        // setSelection() doesn't send SelectionEvent
                        cpuSection.tabSelectionChanged(tabItem);
                    }
                }
            }
        };
    }

    /*
     * @see AbstractTabPage#setInput(IActiveJvm)
     */
    @Override
    protected void setInput(IActiveJvm jvm) {
        super.setInput(jvm);
        callerFilteredTree.getViewer().setInput(
                jvm.getCpuProfiler().getCpuModel());
        calleeFilteredTree.getViewer().setInput(
                jvm.getCpuProfiler().getCpuModel());
    }

    /*
     * @see AbstractTabPage#refresh()
     */
    @Override
    protected void refresh() {
        TreeViewer callerViewer = callerFilteredTree.getViewer();
        if (!callerViewer.getControl().isDisposed()) {
            callerViewer.refresh();
            if (callerViewer.getTree().isFocusControl()) {
                callerFilteredTree
                        .updateStatusLine((IStructuredSelection) callerViewer
                                .getSelection());
            }
        }

        TreeViewer calleeViewer = calleeFilteredTree.getViewer();
        if (!calleeViewer.getControl().isDisposed()) {
            calleeViewer.refresh();
            if (calleeViewer.getTree().isFocusControl()) {
                calleeFilteredTree
                        .updateStatusLine((IStructuredSelection) calleeViewer
                                .getSelection());
            }
        }

        refreshCallersCalleesPage();
        refreshContentDescription();
    }

    /*
     * @see AbstractTabPage#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (callerCalleeImage != null) {
            callerCalleeImage.dispose();
        }
    }

    /*
     * @see AbstractTabPage#getFilteredTrees()
     */
    @Override
    protected List<AbstractFilteredTree> getFilteredTrees() {
        List<AbstractFilteredTree> trees = new ArrayList<AbstractFilteredTree>();
        trees.add(callerFilteredTree);
        trees.add(calleeFilteredTree);
        return trees;
    }

    /**
     * Creates the caller viewer.
     * 
     * @param parent
     *            The parent composite
     */
    private void createCallerViewer(Composite parent) {
        PropertySheet propertySheet = (PropertySheet) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart();
        callerFilteredTree = new HotSpotsFilteredTree(parent, propertySheet
                .getViewSite().getActionBars()) {
            @Override
            protected void addMenus(IMenuManager manager) {
                manager.add(new Separator());
                manager.add(new ConfigureCpuProfilerAction(cpuSection));
            }

            @Override
            public ViewerType getViewerType() {
                return ViewerType.Caller;
            }

            @Override
            protected String getMethodColumnName() {
                return Messages.callerColumnLabel;
            }

            @Override
            protected String getMethodColumnToolTip() {
                return Messages.calleeColumnToolTip;
            }
        };
        TreeViewer callerViewer = callerFilteredTree.getViewer();
        callerViewer.setContentProvider(new AbstractContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ICpuModel) {
                    return ((ICpuModel) inputElement).getCallers();
                }
                return new Object[0];
            }
        });
        callerViewer.setLabelProvider(new HotSpotsLabelProvider(
                callerFilteredTree));
        callerViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        callerFilteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        callerFilteredTree.sortColumn(callerViewer.getTree().getColumn(1));
    }

    /**
     * Creates the callee viewer.
     * 
     * @param parent
     *            The parent composite
     */
    private void createCalleeViewer(Composite parent) {
        PropertySheet propertySheet = (PropertySheet) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart();
        calleeFilteredTree = new HotSpotsFilteredTree(parent, propertySheet
                .getViewSite().getActionBars()) {
            @Override
            protected void addMenus(IMenuManager manager) {
                manager.add(new Separator());
                manager.add(new ConfigureCpuProfilerAction(cpuSection));
            }

            @Override
            public ViewerType getViewerType() {
                return ViewerType.Callee;
            }

            @Override
            protected String getMethodColumnName() {
                return Messages.calleeColumnLabel;
            }

            @Override
            protected String getMethodColumnToolTip() {
                return Messages.calleeColumnToolTip;
            }
        };
        TreeViewer calleeViewer = calleeFilteredTree.getViewer();
        calleeViewer.setContentProvider(new AbstractContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ICpuModel) {
                    return ((ICpuModel) inputElement).getCallees();
                }
                return new Object[0];
            }
        });
        calleeViewer.setLabelProvider(new HotSpotsLabelProvider(
                calleeFilteredTree));
        calleeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        calleeFilteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        calleeFilteredTree.sortColumn(calleeViewer.getTree().getColumn(1));
    }

    /**
     * Refreshes the content description.
     */
    private void refreshContentDescription() {
        if (jvm == null) {
            return;
        }

        StringBuilder description = new StringBuilder();

        IMethodNode callersCalleesTarget = jvm.getCpuProfiler().getCpuModel()
                .getCallersCalleesTarget();
        if (callersCalleesTarget != null) {
            description.append(NLS.bind(Messages.callersCalleesTargetIndicator,
                    callersCalleesTarget.getName()));
        }

        ICpuModel cpuModel = jvm.getCpuProfiler().getCpuModel();
        ICallTreeNode focusedNode = cpuModel.getFocusTarget();
        if (focusedNode != null) {
            if (callersCalleesTarget != null) {
                description.append(NLS.bind(Messages.focusTargetIndicator,
                        focusedNode.getNonqualifiedName()));
            }
        }

        String thread = null;
        if (focusedNode != null) {
            thread = focusedNode.getThread();
        } else if (callersCalleesTarget != null) {
            thread = callersCalleesTarget.getThread();
        }

        if (description.length() > 0 && thread != null) {
            description.append(NLS.bind(Messages.threadIndicator, thread));
        }

        setContentDescription(description.toString());
    }

    /**
     * Refreshes the callers/callees page.
     */
    private void refreshCallersCalleesPage() {
        if (jvm == null || callersCalleesPageBook.isDisposed()) {
            return;
        }

        Label label = null;
        SashForm sashForm = null;
        for (Control control : callersCalleesPageBook.getChildren()) {
            if (control instanceof Label) {
                label = (Label) control;
            } else if (control instanceof SashForm) {
                sashForm = (SashForm) control;
            }
        }

        if (label == null || sashForm == null) {
            throw new IllegalStateException("label and sashform cannot be null"); //$NON-NLS-1$
        }

        Control control = (jvm.getCpuProfiler().getCpuModel()
                .getCallersCalleesTarget() == null) ? label : sashForm;
        callersCalleesPageBook.showPage(control);
    }

    /**
     * Gets the caller callee image.
     * 
     * @return The caller callee image
     */
    private Image getCallerCalleeImage() {
        if (callerCalleeImage == null || callerCalleeImage.isDisposed()) {
            callerCalleeImage = Activator.getImageDescriptor(
                    ISharedImages.CALLER_CALLEE_IMG_PATH).createImage();
        }
        return callerCalleeImage;
    }
}
