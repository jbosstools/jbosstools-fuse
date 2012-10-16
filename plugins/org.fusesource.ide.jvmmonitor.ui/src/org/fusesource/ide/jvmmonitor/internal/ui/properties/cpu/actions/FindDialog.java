/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.ITreeNode;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractFilteredTree.ViewerType;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The find dialog.
 */
public class FindDialog extends Dialog {

    /** The dialog settings key for find text history. */
    private static final String FIND_HISTORY_KEY = Activator.getDefault()
            .getBundle().getBundleId()
            + ".find"; //$NON-NLS-1$

    /** The find text field. */
    private Combo findText;

    /** The forward check box. */
    private Button forwardButton;

    /** The find button. */
    private Button findButton;

    /** The state indicating if search is started. */
    private boolean startSearch;

    /** The tree viewer. */
    private TreeViewer viewer;

    /** The viewer type. */
    private ViewerType viewerType;

    /**
     * The constructor.
     * 
     * @param viewer
     *            The tree viewer
     * @param viewerType
     *            The viewer type
     */
    public FindDialog(TreeViewer viewer, ViewerType viewerType) {
        super(viewer.getTree().getShell());
        this.viewer = viewer;
        this.viewerType = viewerType;
        setShellStyle(getShellStyle() ^ SWT.APPLICATION_MODAL);
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(Messages.findTitle);
        validate();
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.FIND_DIALOG);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, true));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addFindTextField(composite);
        addDirectionSelection(composite);

        applyDialogFont(composite);

        return composite;
    }

    /*
     * @see Dialog#createButtonsForButtonBar(Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        findButton = createButton(parent, IDialogConstants.CLIENT_ID,
                Messages.findButtonLabel, true);
        findButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doFind();
            }
        });

        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CLOSE_LABEL, false);
    }

    /*
     * @see Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Adds the find text field.
     * 
     * @param parent
     *            The parent composite
     */
    private void addFindTextField(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.findTextLabel);

        findText = new Combo(composite, SWT.BORDER);
        String[] items = Activator.getDefault()
                .getDialogSettings(getClass().getName())
                .getArray(FIND_HISTORY_KEY);
        if (items != null) {
            findText.setItems(items);
            findText.select(0);
        }
        findText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        findText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
    }

    /**
     * Adds the direction selection.
     * 
     * @param parent
     *            The parent composite
     */
    private void addDirectionSelection(Composite parent) {
        Group group = new Group(parent, SWT.NULL);
        group.setLayout(new GridLayout(1, true));
        group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        group.setText(Messages.directionLabel);

        forwardButton = new Button(group, SWT.RADIO);
        forwardButton.setText(Messages.forwardLabel);
        forwardButton.setSelection(true);
        Button backwardButton = new Button(group, SWT.RADIO);
        backwardButton.setText(Messages.backwardLabel);
    }

    /**
     * Validates the currently specified text.
     */
    void validate() {
        if (findText.getText().isEmpty()) {
            findButton.setEnabled(false);
        } else {
            findButton.setEnabled(true);
        }
    }

    /**
     * Adds the search text to history.
     * 
     * @param searchText
     *            The search text.
     */
    private void addHistory(String searchText) {
        if (searchText.isEmpty()) {
            return;
        }
        IDialogSettings dialogSettings = Activator.getDefault()
                .getDialogSettings(getClass().getName());
        String[] items = dialogSettings.getArray(FIND_HISTORY_KEY);
        if (items == null) {
            items = new String[] { searchText };
        } else {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, items);
            if (list.contains(searchText)) {
                list.remove(searchText);
            }
            Collections.reverse(list);
            list.add(searchText);
            Collections.reverse(list);
            items = list.toArray(new String[0]);
        }

        dialogSettings.put(FIND_HISTORY_KEY, items);
        findText.setItems(items);
        findText.select(0);
    }

    /**
     * Finds the item with string specified in text field.
     */
    void doFind() {
        String searchText = findText.getText();
        addHistory(searchText);

        ITreeNode treeNode = null;
        ITreeNode[] nodes = null;

        ICpuModel cpuModel = (ICpuModel) viewer.getInput();
        if (viewerType == ViewerType.CallTree) {
            nodes = cpuModel.getCallTreeRoots();
        } else if (viewerType == ViewerType.HotSpots) {
            nodes = cpuModel.getHotSpotRoots();
        } else if (viewerType == ViewerType.Caller) {
            nodes = cpuModel.getCallers();
        } else if (viewerType == ViewerType.Callee) {
            nodes = cpuModel.getCallees();
        }
        if (nodes == null) {
            return;
        }

        ITreeNode selectedNode = getSelectedNode();
        startSearch = (selectedNode == null);

        if (forwardButton.getSelection()) {
            treeNode = searchTreeNodeInForward(nodes, selectedNode, searchText);
        } else {
            treeNode = searchTreeNodeInBackward(nodes, selectedNode, searchText);
        }
        if (treeNode != null) {
            ISelection newSelection = new StructuredSelection(treeNode);
            viewer.setSelection(newSelection);
        }
        findButton.forceFocus();
    }

    /**
     * Gets the selected node.
     * 
     * @return The selected node
     */
    private ITreeNode getSelectedNode() {
        ISelection selection = viewer.getSelection();
        if (selection instanceof TreeSelection) {
            TreeSelection treeSelection = (TreeSelection) selection;
            Object element = treeSelection.getFirstElement();
            if (element instanceof ITreeNode) {
                return (ITreeNode) element;
            }
        }
        return null;
    }

    /**
     * Searches the tree node containing the given string in the given nodes in
     * forward.
     * 
     * @param nodes
     *            The tree nodes
     * @param selectedNode
     *            The selected node
     * @param searchText
     *            The search text
     * @return The tree node
     */
    private ITreeNode searchTreeNodeInForward(ITreeNode[] nodes,
            ITreeNode selectedNode, String searchText) {
        ITreeNode[] sortedNodes = getSortedNodes(nodes);

        for (ITreeNode treeNode : sortedNodes) {
            if (startSearch && treeNode.getName().contains(searchText)) {
                return treeNode;
            }

            if (treeNode.equals(selectedNode)) {
                startSearch = true;
            }

            if (treeNode.hasChildren()) {
                ITreeNode foundTreeNode = searchTreeNodeInForward(treeNode
                        .getChildren().toArray(new ITreeNode[0]), selectedNode,
                        searchText);
                if (foundTreeNode != null) {
                    return foundTreeNode;
                }
            }
        }
        return null;
    }

    /**
     * Searches the tree node containing the given string in the given list in
     * backward.
     * 
     * @param nodes
     *            The tree nodes
     * @param selectedNode
     *            The selected node
     * @param searchText
     *            The search text
     * @return The tree node
     */
    private ITreeNode searchTreeNodeInBackward(ITreeNode[] nodes,
            ITreeNode selectedNode, String searchText) {
        ITreeNode[] sortedNodes = getSortedNodes(nodes);

        for (int i = sortedNodes.length - 1; i >= 0; i--) {
            ITreeNode treeNode = sortedNodes[i];

            if (treeNode.hasChildren()) {
                ITreeNode foundTreeNode = searchTreeNodeInBackward(treeNode
                        .getChildren().toArray(new ITreeNode[0]), selectedNode,
                        searchText);
                if (foundTreeNode != null) {
                    return foundTreeNode;
                }
            }

            if (startSearch && treeNode.getName().contains(searchText)) {
                return treeNode;
            }

            if (treeNode.equals(selectedNode)) {
                startSearch = true;
            }
        }
        return null;
    }

    /**
     * Gets the sorted tree nodes.
     * 
     * @param nodes
     *            The tree nodes
     * @return The sorted tree nodes
     */
    private ITreeNode[] getSortedNodes(ITreeNode[] nodes) {
        ViewerComparator comparator = viewer.getComparator();
        if (comparator != null) {
            comparator.sort(viewer, nodes);
        }
        return nodes;
    }
}
