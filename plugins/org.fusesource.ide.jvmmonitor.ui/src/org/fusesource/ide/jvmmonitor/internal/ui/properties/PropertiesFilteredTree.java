/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * The tree to show properties having filter widget.
 */
abstract public class PropertiesFilteredTree extends FilteredTree {

    /** The value column. */
    private TreeViewerColumn valueColumn;

    /** The actions. */
    List<Action> actions;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param actionBars
     *            The action bars
     */
    public PropertiesFilteredTree(Composite parent, IActionBars actionBars) {
        super(parent, SWT.MULTI | SWT.FULL_SELECTION, new PatternFilter(), true);
        setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        configureTree();
        createContextMenu(actionBars);
        addListeners();
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
     * Sets the editing support.
     * 
     * @param editingSupport
     *            The editing support
     */
    public void setEditingSupport(EditingSupport editingSupport) {
        valueColumn.setEditingSupport(editingSupport);
    }

    /**
     * Gets the actions.
     * 
     * @param actionBars
     *            The action bars
     * @return The actions
     */
    abstract protected List<Action> createActions(IActionBars actionBars);

    /**
     * Notifies that menu is about to show.
     */
    abstract protected void menuAboutToshow();

    /**
     * Configures the tree.
     */
    private void configureTree() {
        getViewer().getTree().setLinesVisible(true);
        getViewer().getTree().setHeaderVisible(true);

        for (PropertiesColumn column : PropertiesColumn.values()) {
            TreeViewerColumn treeColumn = new TreeViewerColumn(getViewer(),
                    SWT.NONE);
            treeColumn.getColumn().setText(column.label);
            treeColumn.getColumn().setWidth(column.defalutWidth);
            treeColumn.getColumn().setAlignment(column.alignment);
            treeColumn.getColumn().setToolTipText(column.toolTip);
            if (Messages.valueColumnLabel.equals(column.label)) {
                valueColumn = treeColumn;
            }
        }
    }

    /**
     * Creates the context menu.
     * 
     * @param actionBars
     *            The action bars
     */
    private void createContextMenu(IActionBars actionBars) {
        actions = createActions(actionBars);
        
        // create menu manager
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                menuAboutToshow();
                for (Action action : actions) {
                    manager.add(action);
                }
            }
        });

        // create context menu
        Menu menu = menuMgr.createContextMenu(getViewer().getControl());
        getViewer().getControl().setMenu(menu);
    }

    /**
     * Adds the listeners.
     */
    private void addListeners() {
        TreeViewer viewer = getViewer();
        for (Action action : actions) {
            if (action instanceof ISelectionChangedListener) {
                viewer.addSelectionChangedListener((ISelectionChangedListener) action);
            }
        }
    }
}
