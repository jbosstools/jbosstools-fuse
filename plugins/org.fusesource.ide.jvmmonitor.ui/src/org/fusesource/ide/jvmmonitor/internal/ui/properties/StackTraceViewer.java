/*******************************************************************************
 * Copyright (c) 2010-2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.OpenDeclarationAction;

/**
 * The stack trace viewer.
 */
public class StackTraceViewer extends TableViewer {

    /** the open action */
    OpenDeclarationAction openAction;

    /** the copy action */
    CopyAction copyAction;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param actionBars
     *            The action bars
     */
    public StackTraceViewer(Composite parent, IActionBars actionBars) {
        super(parent, SWT.MULTI);
        setContentProvider(new StackTraceContentProvider());
        setLabelProvider(new StackTraceLabelProvider());

        createContextMenu(actionBars);

        addSelectionChangedListener(openAction);
        getControl().addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                removeSelectionChangedListener(copyAction);
            }

            @Override
            public void focusGained(FocusEvent e) {
                addSelectionChangedListener(copyAction);
            }
        });
        addOpenListener(new IOpenListener() {
            @Override
            public void open(OpenEvent event) {
                openAction.run();
            }
        });
    }

    /**
     * Creates the context menu.
     * 
     * @param actionBars
     *            The action bars
     */
    private void createContextMenu(IActionBars actionBars) {

        // create actions
        openAction = OpenDeclarationAction
                .createOpenDeclarationAction(actionBars);
        copyAction = CopyAction.createCopyAction(actionBars);

        // create menu manager
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(openAction);
                manager.add(new Separator());
                manager.add(copyAction);
            }
        });

        // create context menu
        Menu menu = menuMgr.createContextMenu(getControl());
        getControl().setMenu(menu);
    }
}
