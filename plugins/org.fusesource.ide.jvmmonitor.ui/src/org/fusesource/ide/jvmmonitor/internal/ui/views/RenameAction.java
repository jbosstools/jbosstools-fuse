/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to rename snapshot on JVM Explorer view.
 */
public class RenameAction extends Action implements ISelectionChangedListener {

    /** The snapshot. */
    private ISnapshot snapshot;

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     * @param actionBars
     *            The action bars
     */
    public RenameAction(TreeViewer treeViewer, IActionBars actionBars) {
        this.treeViewer = treeViewer;
        setText(Messages.renameLabel);
        setActionDefinitionId(IWorkbenchCommandConstants.FILE_RENAME);
        actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), this);
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof TreeSelection)) {
            return;
        }

        snapshot = null;

        List<ISnapshot> snapshots = new ArrayList<ISnapshot>();

        for (Object element : ((TreeSelection) event.getSelection()).toArray()) {
            if (element instanceof ISnapshot) {
                snapshots.add((ISnapshot) element);
            } else {
                snapshots.clear();
                break;
            }
        }

        boolean enabled = snapshots.size() == 1;
        setEnabled(enabled);
        if (enabled) {
            snapshot = snapshots.get(0);
        }
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        String newName = getNewName();

        if (newName != null) {
            closeEditor();
            try {
                snapshot.rename(newName);
            } catch (JvmCoreException e) {
                ErrorDialog.openError(treeViewer.getTree().getShell(),
                        Messages.errorDialogTitle, Messages.renameFailedMsg,
                        e.getStatus());
            }
            treeViewer.refresh();
        }
    }

    /**
     * Closes the editor.
     */
    private void closeEditor() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] references = page.getEditorReferences();
        for (IEditorReference reference : references) {
            if (reference.getTitle().equals(snapshot.getFileStore().getName())) {
                IEditorPart editor = reference.getEditor(false);
                if (editor != null) {
                    page.closeEditor(editor, false);
                }
            }
        }
    }

    /**
     * Gets the new file name.
     * 
     * @return The new file name
     */
    private String getNewName() {
        IFileStore fileStore = snapshot.getFileStore();
        List<String> oldNames = new ArrayList<String>();
        try {
            for (String oldName : fileStore.getParent().childNames(EFS.NONE,
                    null)) {
                oldNames.add(removeSuffix(oldName));
            }
        } catch (CoreException e) {
            Activator
                    .log(IStatus.ERROR,
                            NLS.bind(Messages.accessFileFailedMsg,
                                    fileStore.getName()), e);
            return null;
        }
        String oldName = fileStore.getName();

        IInputValidator validator = new InputValidator(oldNames, oldName);
        InputDialog dialog = new InputDialog(
                treeViewer.getControl().getShell(), Messages.renameTitle,
                Messages.newNameLabel, removeSuffix(oldName), validator);
        if (dialog.open() == Window.OK) {
            return oldName.replace(removeSuffix(oldName), dialog.getValue());
        }
        return null;
    }

    /**
     * Removes the suffix from the given file name.
     * 
     * @param fileName
     *            The file name with suffix
     * @return The file name without suffix
     */
    private String removeSuffix(String fileName) {
        return fileName.substring(0, fileName.indexOf('.'));
    }

    /**
     * The input validator.
     */
    private static class InputValidator implements IInputValidator {

        /** The invalid characters. */
        private static final char[] INVALID_CHARACTERS = new char[] { '\\',
                '/', ':', '*', '?', '"', '<', '>', '|' };

        /**
         * The file names that exist in the same directory as the file to be
         * renamed.
         */
        private List<String> oldNames;

        /** The file name to be renamed. */
        private String oldName;

        /**
         * The constructor.
         * 
         * @param oldNames
         *            The file names that exist in the same directory as the
         *            file to be renamed
         * @param oldName
         *            The file name to be renamed
         */
        public InputValidator(List<String> oldNames, String oldName) {
            this.oldNames = oldNames;
            this.oldName = oldName;
        }

        /*
         * @see IInputValidator#isValid(String)
         */
        @Override
        public String isValid(String newText) {

            // check if text is empty
            if (newText.isEmpty()) {
                return ""; //$NON-NLS-1$
            }

            // check if there are duplicated name
            for (String name : oldNames) {
                if (name.equals(newText) && !name.equals(oldName)) {
                    return Messages.fileAlreadyExistsMsg;
                }
            }

            // check if invalid characters are contained
            for (char c : INVALID_CHARACTERS) {
                if (newText.indexOf(c) != -1) {
                    return Messages.fileContainsInvalidCharactersMsg;
                }
            }

            return null;
        }
    }
}
