/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.RefreshAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ShowInTimelineAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.PropertiesFilteredTree;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline.MBeanAttribute;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The overview section.
 */
public class OverviewSection extends AbstractJvmPropertySection {

    /** The dialog settings section name. */
    private static final String sectionName = "OverviewSectionTreeExpansionState"; //$NON-NLS-1$

    /** The action to refresh section. */
    RefreshAction refreshAction;

    /** The separator. */
    private Separator separator;

    /** The overview properties. */
    OverviewProperties overviewProperties;

    /** The properties viewer. */
    TreeViewer viewer;

    /**
     * The constructor.
     */
    public OverviewSection() {
        refreshAction = new RefreshAction(this);
        separator = new Separator();
        overviewProperties = new OverviewProperties();
    }

    /*
     * @see AbstractJvmPropertySection#createControls(Composite)
     */
    @Override
    protected void createControls(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);

        IWorkbenchPart view = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart();
    	IActionBars actionBars = null;
        if (view instanceof IViewPart) {
        	IViewPart viewPart = (IViewPart) view;
			actionBars = viewPart.getViewSite().getActionBars();
        }
        viewer = new PropertiesFilteredTree(composite, actionBars) {
 
            private ShowInTimelineAction showInTimelineAction;

            @Override
            protected List<Action> createActions(IActionBars actionBars) {
                List<Action> actions = new ArrayList<Action>();
                CopyAction copyAction = CopyAction.createCopyAction(actionBars);
                actions.add(copyAction);
                showInTimelineAction = new MyShowInTimelineAction(
                        OverviewSection.this);
                actions.add(showInTimelineAction);
                return actions;
            }

            @Override
            public void menuAboutToshow() {
                // do nothing
            }
        }.getViewer();
        viewer.setContentProvider(new OverviewContentProvider(
                overviewProperties));
        viewer.setLabelProvider(new OverviewLabelProvider());
        ((Tree) viewer.getControl()).addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                storeTreeExpansionState();
            }
        });
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(parent, IHelpContextIds.OVERVIEW_PAGE);
    }

    /*
     * @see AbstractJvmPropertySection#setInput(IWorkbenchPart, ISelection,
     * IActiveJvm, IActiveJvm)
     */
    @Override
    protected void setInput(IWorkbenchPart part, ISelection selection,
            final IActiveJvm newJvm, IActiveJvm oldJvm) {
        viewer.setInput(newJvm);
    }

    /*
     * @see AbstractPropertySection#refresh()
     */
    @Override
    public void refresh() {
        if (!isSectionActivated) {
            return;
        }

        new RefreshJob(NLS.bind(Messages.refreshOverviewSectionJobLabel,
                getJvm().getPid()), toString()) {
            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                IActiveJvm jvm = getJvm();
                if (jvm != null && jvm.isConnected() && !isRefreshSuspended()) {
                    overviewProperties.refresh(jvm);
                }
            }

            @Override
            protected void refreshUI() {
                IActiveJvm jvm = getJvm();
                boolean isConnected = jvm != null && jvm.isConnected();
                refreshAction.setEnabled(isConnected);

                if (!viewer.getControl().isDisposed()) {
                    refreshBackground(viewer.getControl(),
                            jvm != null && jvm.isConnected());
                    viewer.refresh();
                }
            }
        }.schedule();
    }

    /*
     * @see AbstractJvmPropertySection#addToolBarActions(IToolBarManager)
     */
    @Override
    protected void addToolBarActions(IToolBarManager manager) {
        manager.insertAfter("defaults", separator); //$NON-NLS-1$
        if (manager.find(refreshAction.getId()) == null) {
            manager.insertAfter("defaults", refreshAction); //$NON-NLS-1$
        }
    }

    /*
     * @see AbstractJvmPropertySection#removeToolBarActions(IToolBarManager)
     */
    @Override
    protected void removeToolBarActions(IToolBarManager manager) {
        manager.remove(separator);
        manager.remove(refreshAction.getId());
    }

    /*
     * @see AbstractJvmPropertySection#activateSection()
     */
    @Override
    protected void activateSection() {
        super.activateSection();
        restoreTreeExpansionState();
    }

    /*
     * @see AbstractJvmPropertySection#deactivateSection()
     */
    @Override
    protected void deactivateSection() {
        super.deactivateSection();
        storeTreeExpansionState();
        Job.getJobManager().cancel(toString());
        IToolBarManager manager = getActionBars().getToolBarManager();
        removeToolBarActions(manager);
    }

    /**
     * Restores the tree expansion state.
     */
    private void restoreTreeExpansionState() {
        TreeItem[] items = ((Tree) viewer.getControl()).getItems();
        restoreTreeExpansionState(items);
    }

    /**
     * Restores the tree expansion state of given tree items.
     * 
     * @param items
     *            The tree items
     */
    private void restoreTreeExpansionState(TreeItem[] items) {
        IDialogSettings settings = getDialogSettings();
        for (TreeItem item : items) {
            if (item.getItems().length > 0) {
                boolean expanded = settings.getBoolean(item.getText());
                item.setExpanded(expanded);
            }
        }
    }

    /**
     * Stores the tree expansion state.
     */
    void storeTreeExpansionState() {
        Tree tree = (Tree) viewer.getControl();
        if (tree.isDisposed()) {
            return;
        }

        storeTreeExpansionState(tree.getItems());
    }

    /**
     * Stores the tree expansion state of given tree items.
     * 
     * @param items
     *            The tree items
     */
    private void storeTreeExpansionState(TreeItem[] items) {
        IDialogSettings settings = getDialogSettings();
        for (TreeItem item : items) {
            if (item.getItems().length > 0) {
                settings.put(item.getText(), item.getExpanded());
                storeTreeExpansionState(item.getItems());
            }
        }
    }

    /**
     * Gets the dialog settings for the tree expansion state.
     * 
     * @return The dialog settings
     */
    private IDialogSettings getDialogSettings() {
        String id = sectionName;
        IActiveJvm jvm = getJvm();
        if (jvm != null) {
            id += jvm.getPid();
        }

        IDialogSettings settings = Activator.getDefault().getDialogSettings();
        IDialogSettings section = settings.getSection(id);
        if (section == null) {
            section = settings.addNewSection(id);
        }
        return section;
    }

    /**
     * The action to show the attribute in timeline.
     */
    private static class MyShowInTimelineAction extends ShowInTimelineAction {

        /**
         * The constructor.
         * 
         * @param section
         *            The property section
         */
        public MyShowInTimelineAction(AbstractJvmPropertySection section) {
            super(section);
        }

        /*
         * @see ShowInTimelineAction#getMBeanAttribute(Object)
         */
        @Override
        public MBeanAttribute getMBeanAttribute(Object element) {
            if (!(element instanceof OverviewProperty)) {
                return null;
            }

            OverviewProperty property = (OverviewProperty) element;

            ObjectName objectName;
            try {
                objectName = new ObjectName(property.getObjectName());
            } catch (MalformedObjectNameException e) {
                Activator
                        .log(IStatus.ERROR, Messages.getObjectNameFailedMsg, e);
                return null;
            } catch (NullPointerException e) {
                Activator
                        .log(IStatus.ERROR, Messages.getObjectNameFailedMsg, e);
                return null;
            }
            String attributeName = property.getAttributeName();
            return new MBeanAttribute(objectName, attributeName,
                    getRGB(attributeName));
        }

        /*
         * @see ShowInTimelineAction#getEnabled(Object)
         */
        @Override
        protected boolean getEnabled(Object element) {
            if (!(element instanceof OverviewProperty)) {
                return false;
            }

            OverviewProperty property = (OverviewProperty) element;
            return property.isTimelineSupported();
        }

        /**
         * Gets the arbitrary RGB with given string.
         * 
         * @param string
         *            The string to determine RGB
         * @return The RGB
         */
        private RGB getRGB(String string) {
            int hashCode = string.hashCode();
            int r = (hashCode >> 3) % 256;
            int g = (hashCode >> 1) % 256;
            int b = hashCode % 256;
            return new RGB(Math.abs(r), Math.abs(g), Math.abs(b));
        }
    }
}
