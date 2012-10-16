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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheet;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.ICallTreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ConfigureCpuProfilerAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The call tree tab page.
 */
public class CallTreeTabPage extends AbstractTabPage {

    /** The call tree filtered tree. */
    CallTreeFilteredTree filteredTree;

    /** The call tree image. */
    private Image callTreeImage;

    /**
     * The constructor.
     * 
     * @param cpuSection
     *            The CPU section
     * @param tabFolder
     *            The tab folder
     */
    public CallTreeTabPage(CpuSection cpuSection, CTabFolder tabFolder) {
        super(cpuSection, tabFolder);

        Composite composite = new Composite(viewForm, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_BLACK));

        createCallTreeViewer(composite);

        viewForm.setContent(composite);

        CTabItem tabItem = cpuSection.getWidgetFactory().createTabItem(
                tabFolder, SWT.NONE);
        tabItem.setText(Messages.callTreeTabLabel);
        tabItem.setImage(getCallTreeImage());
        tabItem.setControl(this);

        cpuModelChangeListener = new ICpuModelChangeListener() {
            @Override
            public void modelChanged(CpuModelEvent event) {
                if (event.state == CpuModelState.FocusedMethodChanged) {
                    refresh();
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
        filteredTree.getViewer().setInput(jvm.getCpuProfiler().getCpuModel());
    }

    /*
     * @see AbstractTabPage#refresh()
     */
    @Override
    protected void refresh() {
        if (!filteredTree.getViewer().getControl().isDisposed()) {
            filteredTree.getViewer().refresh();
            filteredTree.updateStatusLine((IStructuredSelection) filteredTree
                    .getViewer().getSelection());
        }

        refreshContentDescription();
    }

    /*
     * @see AbstractTabPage#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (callTreeImage != null) {
            callTreeImage.dispose();
        }
    }

    /*
     * @see AbstractTabPage#getFilteredTrees()
     */
    @Override
    protected List<AbstractFilteredTree> getFilteredTrees() {
        List<AbstractFilteredTree> trees = new ArrayList<AbstractFilteredTree>();
        trees.add(filteredTree);
        return trees;
    }

    /**
     * Creates the call tree viewer.
     * 
     * @param composite
     *            The parent composite
     */
    private void createCallTreeViewer(Composite composite) {
        PropertySheet propertySheet = (PropertySheet) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart();
        filteredTree = new CallTreeFilteredTree(composite, propertySheet
                .getViewSite().getActionBars()) {
            @Override
            protected void addMenus(IMenuManager manager) {
                super.addMenus(manager);
                manager.add(new Separator());
                manager.add(new ConfigureCpuProfilerAction(cpuSection));
            }
        };
        TreeViewer callTreeViewer = filteredTree.getViewer();
        callTreeViewer.setContentProvider(new CallTreeContentProvider());
        callTreeViewer.setLabelProvider(new CallTreeLabelProvider(
                callTreeViewer));
        callTreeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        filteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
    }

    /**
     * Refreshes the content description.
     */
    private void refreshContentDescription() {
        if (jvm == null) {
            return;
        }

        ICallTreeNode focusedNode = jvm.getCpuProfiler().getCpuModel()
                .getFocusTarget();
        StringBuilder description = new StringBuilder();
        if (focusedNode != null) {
            description.append(
                    NLS.bind(Messages.focusTargetIndicator,
                            focusedNode.getNonqualifiedName()))
                    .append(NLS.bind(Messages.threadIndicator,
                            focusedNode.getThread()));
        }
        setContentDescription(description.toString());
    }

    /**
     * Gets the call tree image.
     * 
     * @return The call tree image
     */
    private Image getCallTreeImage() {
        if (callTreeImage == null || callTreeImage.isDisposed()) {
            callTreeImage = Activator.getImageDescriptor(
                    ISharedImages.CALL_TREE_IMG_PATH).createImage();
        }
        return callTreeImage;
    }
}
