/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;

/**
 * The MBean filtered tree.
 */
public class MBeanFilteredTree extends FilteredTree {

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param section
     *            The property section
     */
    protected MBeanFilteredTree(Composite parent,
            AbstractJvmPropertySection section) {
        super(parent, SWT.MULTI | SWT.FULL_SELECTION, new PatternFilter(), true);
        createContextMenu(section);
        setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    }

    /*
     * @see FilteredTree#createControl(Composite, int)
     */
    @Override
    protected void createControl(Composite composite, int treeStyle) {
        super.createControl(composite, treeStyle);

        // adjust the indentation of filter composite
        GridData data = (GridData) filterComposite.getLayoutData();
        data.horizontalIndent = 2;
        data.verticalIndent = 2;
        filterComposite.setLayoutData(data);
    }

    /**
     * Creates the context menu.
     * 
     * @param section
     *            The property section
     */
    private void createContextMenu(final AbstractJvmPropertySection section) {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                SubscribeAction subscribeAction = new SubscribeAction(
                        getViewer().getSelection(), section);
                manager.add(subscribeAction);
            }
        });

        Menu menu = menuMgr.createContextMenu(getViewer().getControl());
        getViewer().getControl().setMenu(menu);
    }
}
