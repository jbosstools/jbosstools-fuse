/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.ISnapshot.SnapshotType;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerState;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerType;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.internal.ui.views.OpenSnapshotAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to dump heap as hprof file.
 */
public class DumpHprofAction extends Action {

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public DumpHprofAction(AbstractJvmPropertySection section) {
        setText(Messages.dumpHprofLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.TAKE_HPROF_DUMP_IMG_PATH));
        setDisabledImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISABLED_TAKE_HPROF_DUMP_IMG_PATH));
        setId(getClass().getName());

        this.section = section;
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        IActiveJvm jvm = section.getJvm();
        if (jvm == null) {
            return;
        }

        // get file name for remote host
        final String fileName[] = new String[1];
        final boolean transfer[] = new boolean[] { false };

        try {
            if (jvm.isRemote()) {
                final FileNameInputDialog dialog = new FileNameInputDialog(
                        section.getPart().getSite().getShell(),
                        getInitialFileName(jvm), isAgentLoaded(jvm));

                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.open() == Window.OK) {
                            fileName[0] = dialog.getFileName();
                            transfer[0] = dialog.isFileTransfered();
                        }
                    }
                });

                if (fileName[0] == null) {
                    return;
                }
            }
        } catch (JvmCoreException e) {
            Activator.log(Messages.dumpHeapDataFailedMsg, e);
            return;
        }

        dumpHprof(fileName[0], transfer[0]);
    }

    /**
     * Dumps the heap data as hprof file.
     * 
     * @param fileName
     *            The file name
     * @param transfer
     *            <tt>true</tt> to transfer file to local host
     */
    private void dumpHprof(final String fileName, final boolean transfer) {
        new Job(Messages.dumpHprofDataJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm activeJvm = section.getJvm();
                if (activeJvm == null) {
                    return Status.CANCEL_STATUS;
                }

                IFileStore fileStore = null;
                try {
                    fileStore = activeJvm.getMBeanServer().dumpHprof(fileName,
                            transfer, monitor);
                } catch (JvmCoreException e) {
                    Activator.log(Messages.dumpHeapDataFailedMsg, e);
                    return Status.CANCEL_STATUS;
                }

                if (isMemoryAnalyzerInstalled() && fileStore != null) {
                    section.setPinned(true);
                    OpenSnapshotAction.openEditor(fileStore);
                }

                return Status.OK_STATUS;
            }
        }.schedule();
    }

    /**
     * Gets the state indicating if Memory Analyzer is installed.
     * 
     * @return <tt>true</tt> if Memory Analyzer is installed
     */
    boolean isMemoryAnalyzerInstalled() {
        return Platform.getBundle("org.eclipse.mat.ui") != null; //$NON-NLS-1$
    }

    /**
     * Gets the initial file name.
     * 
     * @param jvm
     *            The active JVM
     * @return The file name, or <tt>null</tt> if file name is specified
     * @throws JvmCoreException
     */
    String getInitialFileName(IActiveJvm jvm) throws JvmCoreException {

        ObjectName objectName;
        try {
            objectName = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
        } catch (MalformedObjectNameException e) {
            throw new JvmCoreException(IStatus.ERROR, e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new JvmCoreException(IStatus.ERROR, e.getMessage(), e);
        }

        TabularData initialName = (TabularData) jvm.getMBeanServer()
                .getAttribute(objectName, "SystemProperties"); //$NON-NLS-1$
        CompositeData compisiteData = initialName
                .get(new Object[] { "user.home" }); //$NON-NLS-1$
        String home = compisiteData.values().toArray(new String[0])[1];
        StringBuffer initialFileName = new StringBuffer(home);
        initialFileName.append(File.separator).append(new Date().getTime())
                .append('.').append(SnapshotType.Hprof.getExtension());
        return initialFileName.toString();
    }

    /**
     * Gets the state indicating if agent is loaded.
     * 
     * @param jvm
     *            The JVM
     * @return <tt>true</tt> if agent is loaded
     */
    boolean isAgentLoaded(IActiveJvm jvm) {
        ProfilerState state = jvm.getCpuProfiler().getState(ProfilerType.BCI);
        return state == ProfilerState.READY || state == ProfilerState.RUNNING;
    }

    /**
     * The dialog to input file name.
     */
    private static class FileNameInputDialog extends Dialog {

        /** The dialog settings key for transfer. */
        private static final String TRANSFER_KEY = "transfer"; //$NON-NLS-1$

        /** The invalid characters. */
        private static final char[] INVALID_CHARACTERS = new char[] { '*', '?',
                '"', '<', '>', '|' };

        /** <tt>true</tt> to transfer hprof file to local host. */
        boolean isFileTransfered;

        /** The file name text field. */
        Text fileNameField;

        /** The message label. */
        private Label messageLabel;

        /** The file name. */
        String fileName;

        /** The state indicating if agent is loaded. */
        private boolean isAgentLoaded;

        /** The label to show info/error image. */
        private Label imageLabel;

        /**
         * The constructor.
         * 
         * @param initialFileName
         *            The initial file name
         * @param isAgentLoaded
         */
        public FileNameInputDialog(Shell shell, String initialFileName,
                boolean isAgentLoaded) {
            super(shell);
            this.fileName = initialFileName;
            this.isAgentLoaded = isAgentLoaded;
            isFileTransfered = Activator.getDefault()
                    .getDialogSettings(getClass().getName())
                    .getBoolean(TRANSFER_KEY);
        }

        /*
         * @see Dialog#create()
         */
        @Override
        public void create() {
            super.create();
            getShell().setText(Messages.dumpHprofTitle);
            validate();
        }

        /*
         * @see InputDialog#createDialogArea(Composite)
         */
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);

            addFileNameField(composite);
            addTransferFileButton(composite);
            addMessageLabel(composite);

            applyDialogFont(composite);

            return composite;
        }

        /*
         * @see Dialog#isResizable()
         */
        @Override
        protected boolean isResizable() {
            return true;
        }

        /*
         * @see Dialog#okPressed()
         */
        @Override
        protected void okPressed() {
            Activator.getDefault().getDialogSettings(getClass().getName())
                    .put(TRANSFER_KEY, isFileTransfered);
            super.okPressed();
        }

        /**
         * Gets the file name.
         * 
         * @return The file name
         */
        protected String getFileName() {
            return fileName;
        }

        /**
         * Adds the file name text field.
         * 
         * @param parent
         *            The parent composite
         */
        private void addFileNameField(Composite parent) {
            Composite composite = new Composite(parent, SWT.NULL);
            composite.setLayout(new GridLayout(1, false));
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            Label label = new Label(composite, SWT.NONE);
            label.setText(Messages.hprofFileLabel);

            fileNameField = new Text(composite, SWT.BORDER);
            GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
            gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            fileNameField.setLayoutData(gridData);

            fileNameField.setText(fileName);
            fileNameField.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    if (validate()) {
                        fileName = fileNameField.getText();
                    }
                }
            });
        }

        /**
         * Adds the transfer file button.
         * 
         * @param parent
         *            The parent composite
         */
        private void addTransferFileButton(Composite parent) {
            Composite composite = new Composite(parent, SWT.NULL);
            composite.setLayout(new GridLayout(1, false));
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            
            final Button button = new Button(composite, SWT.CHECK);
            button.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    isFileTransfered = button.getSelection();
                }
            });
            button.setText(Messages.transferHprofFileLabel);
            button.setSelection(isFileTransfered);
            button.setEnabled(isAgentLoaded);
        }

        /**
         * Adds the message label.
         * 
         * @param parent
         *            The parent composite
         */
        private void addMessageLabel(Composite parent) {
            Composite composite = new Composite(parent, SWT.NULL);
            composite.setLayout(new GridLayout(2, false));
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            imageLabel = new Label(composite, SWT.NONE);
            imageLabel.setImage(JFaceResources
                    .getImage(Dialog.DLG_IMG_MESSAGE_INFO));

            messageLabel = new Label(composite, SWT.NONE);
            messageLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        /**
         * Gets the state indicating if file is transfered to local host.
         * 
         * @return <tt>true</tt> if file is transfered to local host
         */
        protected boolean isFileTransfered() {
            return isFileTransfered;
        }

        /**
         * Validates the entered file name.
         * 
         * @return <tt>true</tt> if the entered file name is valid
         */
        boolean validate() {

            // check if agent is loaded
            String message = Util.ZERO_LENGTH_STRING;
            Image image = null;
            if (!isAgentLoaded) {
                message = Messages.transferingHprofFileNotSupportedMsg;
                image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
            }

            // check if text is empty
            String newText = fileNameField.getText();
            if (newText.isEmpty()) {
                message = Messages.fileNameEmptyMsg;
                image = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
            }

            // check if invalid characters are contained
            for (char c : INVALID_CHARACTERS) {
                if (fileNameField.getText().indexOf(c) != -1) {
                    message = Messages.pathContainsInvalidCharactersMsg;
                    image = JFaceResources
                            .getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
                    break;
                }
            }

            imageLabel.setImage(image);
            messageLabel.setText(message);

            getButton(IDialogConstants.OK_ID).setEnabled(message.isEmpty());
            return true;
        }
    }
}
