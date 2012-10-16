/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;

/**
 * The action to copy the selected item clipboard.
 */
public class CopyAction extends Action implements ISelectionChangedListener {

    /** The text data to be transfered. */
    private String textData;

    /** The file data to be transfered. */
    private List<String> filesData;

    /**
     * The constructor.
     */
    public CopyAction() {
        setText(Messages.copyLabel);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setEnabled(false);
        setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);

        filesData = new ArrayList<String>();
    }

    /**
     * Creates copy action. If copy action is found in the given action bars,
     * the found action will be returned, otherwise copy action will be newly
     * created and set to the given action bars as a global action.
     * 
     * @param actionBars
     *            The action bars.
     * @return The copy action
     */
    public static CopyAction createCopyAction(IActionBars actionBars) {
        IAction action = actionBars.getGlobalActionHandler(ActionFactory.COPY
                .getId());
        if (action instanceof CopyAction) {
            return (CopyAction) action;
        }

        CopyAction copyAction = new CopyAction();
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
                copyAction);
        return copyAction;
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof StructuredSelection)) {
            return;
        }

        StringBuffer buffer = new StringBuffer();
        filesData.clear();
        for (Object element : ((StructuredSelection) event.getSelection())
                .toArray()) {
            if (buffer.length() > 0) {
                buffer.append('\n');
            }
            String string = getString(element);
            buffer.append(string);
            if (element instanceof ISnapshot) {
                filesData.add(string);
            }
        }
        textData = buffer.toString();

        setEnabled(textData.length() > 0);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (textData == null || textData.length() == 0) {
            return;
        }

        Clipboard clipboard = new Clipboard(Display.getDefault());
        if (filesData.size() > 0) {
            Object[] clipboardData = new Object[] {
                    filesData.toArray(new String[0]), textData };
            Transfer[] clipboardDataTypes = new Transfer[] {
                    FileTransfer.getInstance(), TextTransfer.getInstance() };
            clipboard.setContents(clipboardData, clipboardDataTypes);
        } else {
            clipboard.setContents(new Object[] { textData },
                    new Transfer[] { TextTransfer.getInstance() });
        }
    }

    /**
     * Gets the string for the given element.
     * 
     * @param element
     *            The element
     * @return The string
     */
    protected String getString(Object element) {
        if (element instanceof ISnapshot) {
            return ((ISnapshot) element).getFileStore().toURI().getPath();
        }
        return element.toString();
    }
}
