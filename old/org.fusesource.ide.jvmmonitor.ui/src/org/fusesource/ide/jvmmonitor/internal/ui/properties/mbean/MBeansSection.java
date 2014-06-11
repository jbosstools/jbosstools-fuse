/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.RefreshAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ToggleOrientationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;


/**
 * The MBean section.
 */
public class MBeansSection extends AbstractJvmPropertySection {

    /** The layout menu id. */
    private static final String LAYOUT_MENU_ID = "layout"; //$NON-NLS-1$

    /** The sash form. */
    private MBeanSashForm sashForm;

    /** The action to refresh section. */
    private RefreshAction refreshAction;

    /** The separator. */
    private Separator separator;

    /** The layout menu. */
    private MenuManager layoutMenu;

    /**
     * The constructor.
     */
    public MBeansSection() {
        refreshAction = new RefreshAction(this);
        separator = new Separator();
        layoutMenu = new MenuManager(Messages.layoutLabel, LAYOUT_MENU_ID);
    }

    /*
     * @see AbstractPropertySection#refresh()
     */
    @Override
    public void refresh() {
        if (!isSectionActivated) {
            return;
        }

        IActiveJvm jvm = getJvm();
        boolean isConnected = jvm != null && jvm.isConnected();
        refreshAction.setEnabled(isConnected);

        if (sashForm != null && !sashForm.isDisposed()) {
            refreshBackground(sashForm.getChildren(), isConnected);
            sashForm.refresh();
        }
    }

    /*
     * @see AbstractJvmPropertySection#createControls(Composite)
     */
    @Override
    protected void createControls(Composite parent) {
        sashForm = new MBeanSashForm(parent, this);

        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(parent, IHelpContextIds.MBEANS_PAGE);
    }

    /*
     * @see AbstractJvmPropertySection#setInput(IWorkbenchPart, ISelection,
     * IActiveJvm, IActiveJvm)
     */
    @Override
    protected void setInput(IWorkbenchPart part, ISelection selection,
            IActiveJvm newJvm, IActiveJvm oldJvm) {
        // do nothing
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
     * @see AbstractJvmPropertySection#addLocalMenus(IMenuManager)
     */
    @Override
    protected void addLocalMenus(IMenuManager manager) {
        if (manager.find(layoutMenu.getId()) == null) {
            manager.add(layoutMenu);
            for (ToggleOrientationAction action : sashForm
                    .getOrientationActions()) {
                if (layoutMenu.find(action.getId()) == null) {
                    layoutMenu.add(action);
                }
            }
        }
    }

    /*
     * @see AbstractJvmPropertySection#removeLocalMenus(IMenuManager)
     */
    @Override
    protected void removeLocalMenus(IMenuManager manager) {
        manager.remove(layoutMenu);
    }
    
    /*
     * @see AbstractJvmPropertySection#deactivateSection()
     */
    @Override
    protected void deactivateSection() {
        super.deactivateSection();
        if (sashForm != null && !sashForm.isDisposed()) {
        	sashForm.deactivated();	
        }        
        IToolBarManager manager = getActionBars().getToolBarManager();
        removeToolBarActions(manager);
    }
}
