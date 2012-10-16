/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.JvmMonitorPreferences;


/**
 * The wizard to create a new JVM connection.
 */
public class NewJvmConnectionWizard extends Wizard implements INewWizard {

    /** The wizard page for new JVM connection. */
    private NewJvmConnectionWizardPage page;

    /** The tree viewer. */
    private TreeViewer viewer;

    /**
     * The constructor.
     * 
     * @param viewer
     *            The tree viewer
     */
    public NewJvmConnectionWizard(TreeViewer viewer) {
        this.viewer = viewer;
        setDialogSettings(Activator.getDefault().getDialogSettings());
        setWindowTitle(Messages.newJvmConnectionTitle);
    }

    /*
     * @see wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        page = new NewJvmConnectionWizardPage(viewer.getSelection());
        addPage(page);
    }

    /*
     * @see Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        // add active JVM
        IActiveJvm jvm = addActiveJvm();
        if (jvm == null) {
            return false;
        }

        // select item on JVM explorer
        viewer.refresh();
        viewer.setSelection(new StructuredSelection(jvm));

        // show properties view
        StartMonitoringAction action = new StartMonitoringAction();
        action.showPropertiesView(jvm);

        // connect to JVM
        try {
            int period = JvmMonitorPreferences.getJvmUpdatePeriod();
            jvm.connect(period);
        } catch (JvmCoreException e) {
            Activator.log(NLS.bind(Messages.connectJvmFailedMsg, jvm.getPid()),
                    e);
        }

        page.storeDialogSettings();

        return true;
    }

    /*
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // do nothing
    }

    /**
     * Adds the active JVM.
     * 
     * @return The active JVM
     */
    private IActiveJvm addActiveJvm() {
        final boolean isHostAndPortSelected = page.isHostAndPortSelected();
        final String hostName = page.getRemoteHost();
        final int port = page.getPort();
        final String userName = page.getUserName();
        final String password = page.getPassword();
        final String jmxUrl = page.getJmxUrl();

        try {
            final IActiveJvm[] result = new IActiveJvm[1];
            IRunnableWithProgress op = new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    try {
                        int period = JvmMonitorPreferences.getJvmUpdatePeriod();
                        if (isHostAndPortSelected) {
                            IHost host = JvmModel.getInstance().addHost(
                                    hostName);
                            result[0] = host.addRemoteActiveJvm(port, userName,
                                    password, period);
                        } else {
                            result[0] = JvmModel.getInstance().addHostAndJvm(
                                    jmxUrl, userName, password, period);
                        }
                    } catch (JvmCoreException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            };
            new ProgressMonitorDialog(getShell()).run(true, true, op);
            return result[0];
        } catch (InvocationTargetException e) {
            openErrorDialog(e);
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Opens the error dialog.
     * 
     * @param e
     *            The exception
     */
    private void openErrorDialog(final InvocationTargetException e) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog
                        .openError(getShell(), "Error", getErrorMessage(e)); //$NON-NLS-1$
            }
        });
    }

    /**
     * Gets the error message.
     * 
     * @param e
     *            The exception
     * @return The error message
     */
    String getErrorMessage(InvocationTargetException e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null
                && rootCause != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }

        if (rootCause instanceof UnknownHostException) {
            return Messages.determineIpAddressFailedMsg;
        }
        if (rootCause instanceof ConnectException) {
            return Messages.connectionTimedOutMsg;
        }
        return Messages.connectionFailedMsg;
    }
}
