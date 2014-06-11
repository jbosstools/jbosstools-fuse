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
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions.ConfigureCpuProfilerAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The hot spots tab page.
 */
public class HotSpotsTabPage extends AbstractTabPage {

    /** The hot spots filtered tree. */
    HotSpotsFilteredTree filteredTree;

    /** The hot spots image. */
    private Image hotSpotsImage;

    /**
     * The constructor.
     * 
     * @param cpuSection
     *            The CPU section
     * @param tabFolder
     *            The tab folder
     */
    public HotSpotsTabPage(CpuSection cpuSection, CTabFolder tabFolder) {
        super(cpuSection, tabFolder);

        Composite composite = new Composite(viewForm, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_BLACK));

        createHotSpotsViewer(composite);

        viewForm.setContent(composite);

        CTabItem tabItem = cpuSection.getWidgetFactory().createTabItem(
                tabFolder, SWT.NONE);
        tabItem.setText(Messages.hotSpotsTabLabel);
        tabItem.setImage(getHotSpotsImage());
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
        if (hotSpotsImage != null) {
            hotSpotsImage.dispose();
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
     * Creates the hot spots viewer.
     * 
     * @param composite
     *            The parent composite
     */
    private void createHotSpotsViewer(Composite composite) {
        PropertySheet propertySheet = (PropertySheet) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart();
        filteredTree = new HotSpotsFilteredTree(composite, propertySheet
                .getViewSite().getActionBars()) {
            @Override
            protected void addMenus(IMenuManager manager) {
                manager.add(new Separator());
                manager.add(new ConfigureCpuProfilerAction(cpuSection));
            }
        };
        TreeViewer hotSpotsViewer = filteredTree.getViewer();
        hotSpotsViewer.setContentProvider(new AbstractContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof ICpuModel) {
                    return ((ICpuModel) inputElement).getHotSpotRoots();
                }
                return new Object[0];
            }
        });
        hotSpotsViewer
                .setLabelProvider(new HotSpotsLabelProvider(filteredTree));
        hotSpotsViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        filteredTree
                                .updateStatusLine((IStructuredSelection) event
                                        .getSelection());
                    }
                });
        filteredTree.sortColumn(hotSpotsViewer.getTree().getColumn(1));
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
     * Gets the hot spots image.
     * 
     * @return The hot spots image
     */
    private Image getHotSpotsImage() {
        if (hotSpotsImage == null || hotSpotsImage.isDisposed()) {
            hotSpotsImage = Activator.getImageDescriptor(
                    ISharedImages.HOT_SPOTS_IMG_PATH).createImage();
        }
        return hotSpotsImage;
    }
}
