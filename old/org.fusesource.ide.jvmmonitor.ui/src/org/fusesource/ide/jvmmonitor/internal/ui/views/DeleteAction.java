/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;
import org.fusesource.ide.jvmmonitor.core.ITerminatedJvm;
import org.fusesource.ide.jvmmonitor.core.JvmModel;

/**
 * The action to delete items on JVM Explorer view.
 */
public class DeleteAction extends Action implements ISelectionChangedListener {

    /** The terminated JVMs. */
    private List<ITerminatedJvm> jvms;

    /** The remote hosts. */
    private List<IHost> remoteHosts;

    /** The snapshots. */
    private List<ISnapshot> snapshots;

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /** The selected first element. */
    private Object firstElement;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     * @param actionBars
     *            The action bars
     */
    public DeleteAction(TreeViewer treeViewer, IActionBars actionBars) {
        this.treeViewer = treeViewer;
        jvms = new ArrayList<ITerminatedJvm>();
        remoteHosts = new ArrayList<IHost>();
        snapshots = new ArrayList<ISnapshot>();

        setText(Messages.deleteLabel);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), this);
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof TreeSelection)) {
            return;
        }

        jvms.clear();
        remoteHosts.clear();
        snapshots.clear();

        firstElement = ((TreeSelection) event.getSelection()).getFirstElement();

        for (Object element : ((TreeSelection) event.getSelection()).toArray()) {
            if (element instanceof ITerminatedJvm) {
                jvms.add((ITerminatedJvm) element);
            } else if (element instanceof ISnapshot) {
                snapshots.add((ISnapshot) element);
            } else if (element instanceof IHost) {
                if (!((IHost) element).isLocalHost()) {
                    remoteHosts.add((IHost) element);
                }
            } else {
                jvms.clear();
                remoteHosts.clear();
                snapshots.clear();
                break;
            }
        }

        setEnabled(jvms.size() > 0 || snapshots.size() > 0
                || remoteHosts.size() > 0);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (!confirm()) {
            return;
        }

        // delete JVMs
        deleteJvms();

        // delete remote hosts
        for (IHost host : remoteHosts) {
            jvms = host.getTerminatedJvms();
            deleteJvms();
        }
        for (IHost host : remoteHosts) {
            JvmModel.getInstance().removeHost(host);
        }

        // delete snapshots
        for (ISnapshot snapshot : snapshots) {
            closeEditor(snapshot.getFileStore());
            snapshot.getJvm().deleteSnapshot(snapshot);
        }

        treeViewer.refresh();
    }

    /**
     * Confirms if proceeding.
     * 
     * @return True if proceeding
     */
    private boolean confirm() {
        int size = jvms.size() + remoteHosts.size() + snapshots.size();

        String message;
        if (size == 1) {
            String elementName = firstElement.toString();
            if (elementName.isEmpty()) {
                message = Messages.confirmDeleteSelectedElementMsg;
            } else {
                message = NLS.bind(Messages.confirmDeleteElementMsg,
                        elementName);
            }
        } else {
            message = NLS.bind(Messages.confirmDeleteElementsMsg,
                    String.valueOf(size));
        }

        return MessageDialog.openConfirm(treeViewer.getControl().getShell(),
                Messages.confirmDeleteTitle, message);
    }

    /**
     * Deletes the JVMs.
     */
    private void deleteJvms() {
        for (ITerminatedJvm jvm : jvms) {

            // close editors
            for (ISnapshot snapshot : jvm.getShapshots()) {
                closeEditor(snapshot.getFileStore());
            }

            // remove terminated JVM from model
            jvm.getHost().removeJvm(jvm.getPid());
        }
    }

    /**
     * Closes the text editor.
     * 
     * @param fileStore
     *            The file store
     */
    private void closeEditor(final IFileStore fileStore) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                IEditorInput input = new FileStoreEditorInput(fileStore);
                IWorkbenchPage page = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                IEditorPart editor = page.findEditor(input);
                if (editor != null) {
                    page.closeEditor(editor, false);
                }
            }
        });
    }
}
