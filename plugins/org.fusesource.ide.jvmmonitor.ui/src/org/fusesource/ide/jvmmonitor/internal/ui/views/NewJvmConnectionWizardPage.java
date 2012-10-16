/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvm;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The wizard page to create a new JVM connection.
 */
public class NewJvmConnectionWizardPage extends WizardPage {

    /** The page name. */
    private static final String PAGE_NAME = "newJvmConnectionWizardPage"; //$NON-NLS-1$

    /** The dialog settings key for remote host history. */
    private static final String REMOTE_HOST_HISTORY_KEY = Activator
            .getDefault().getBundle().getBundleId()
            + ".remoteHost"; //$NON-NLS-1$

    /** The dialog settings key for port history. */
    private static final String PORT_HISTORY_KEY = Activator.getDefault()
            .getBundle().getBundleId()
            + ".port"; //$NON-NLS-1$

    /** The dialog settings key for user name history. */
    private static final String USER_NAME_HISTORY_KEY = Activator.getDefault()
            .getBundle().getBundleId()
            + ".userName"; //$NON-NLS-1$

    /** The dialog settings key for URL history. */
    private static final String URL_HISTORY_KEY = Activator.getDefault()
            .getBundle().getBundleId()
            + ".url"; //$NON-NLS-1$

    /** The header of JMX URL. */
    private static final String JMX_URL_HEADER = "service:jmx:"; //$NON-NLS-1$

    /** The remote host text field. */
    Combo remoteHostText;

    /** The port text field. */
    Combo portText;

    /** The user name text field. */
    private Combo userNameText;

    /** The password text field. */
    private Text passwordText;

    /** The URL text field. */
    Combo urlText;

    /** The host and port radio button. */
    Button hostAndPortButton;

    /** The selection. */
    private ISelection selection;

    /**
     * The constructor.
     * 
     * @param selection
     *            The selection
     */
    public NewJvmConnectionWizardPage(ISelection selection) {
        super(PAGE_NAME);

        this.selection = selection;
        setTitle(Messages.newJvmConnectionPageTitle);
        setDescription(Messages.createNewJvmConnectionMsg);
        ImageDescriptor image = Activator
                .getImageDescriptor(ISharedImages.NEW_JVM_CONNECTION_IMG_PATH);
        setImageDescriptor(image);
        setPageComplete(false);
    }

    /*
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createConnectionGroup(composite);
        createAuthenticateGroup(composite);

        Dialog.applyDialogFont(composite);

        setControl(composite);

        PlatformUI
                .getWorkbench()
                .getHelpSystem()
                .setHelp(composite,
                        IHelpContextIds.NEW_JVM_CONNECTION_WIZARD_PAGE);
    }

    /**
     * Gets the remote host.
     * 
     * @return The remote host
     */
    protected String getRemoteHost() {
        return remoteHostText.getText();
    }

    /**
     * Gets the port.
     * 
     * @return The port
     */
    protected int getPort() {
        try {
            return Integer.parseInt(portText.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the user name.
     * 
     * @return The user name
     */
    protected String getUserName() {
        return userNameText.getText();
    }

    /**
     * Gets the password.
     * 
     * @return The password
     */
    protected String getPassword() {
        return passwordText.getText();
    }

    /**
     * Gets the JXM URL.
     * 
     * @return The JXM URL
     */
    protected String getJmxUrl() {
        return urlText.getText();
    }

    /**
     * Gets the state indicating if the host and port radio button is selected.
     * 
     * @return The state indicating if the host and port radio button is
     *         selected
     */
    protected boolean isHostAndPortSelected() {
        return hostAndPortButton.getSelection();
    }

    /**
     * Stores the dialog settings.
     */
    protected void storeDialogSettings() {
        if (isHostAndPortSelected()) {
            addItemToDialogSettings(REMOTE_HOST_HISTORY_KEY, getRemoteHost());
            addItemToDialogSettings(PORT_HISTORY_KEY, String.valueOf(getPort()));
        } else {
            addItemToDialogSettings(URL_HISTORY_KEY,
                    String.valueOf(getJmxUrl()));
        }
        addItemToDialogSettings(USER_NAME_HISTORY_KEY,
                String.valueOf(getUserName()));
    }

    /**
     * Creates connection group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createConnectionGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.connectionGroupLabel);
        group.setLayout(new GridLayout(2, false));

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(layoutData);

        createHostAndPortPanel(group);
        createUrlPanel(group);
    }

    /**
     * Creates authenticate group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createAuthenticateGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.authenticateGroupLabel);
        group.setLayout(new GridLayout(2, false));

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(layoutData);

        userNameText = addComboTextField(group, Messages.userNameTextLabel,
                USER_NAME_HISTORY_KEY);
        passwordText = addTextField(group, Messages.passwordTextLabel);
        passwordText.setEchoChar('*');
    }

    /**
     * Creates the host and port panel.
     * 
     * @param parent
     *            The parent composite
     */
    private void createHostAndPortPanel(Composite parent) {
        hostAndPortButton = new Button(parent, SWT.RADIO);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        hostAndPortButton.setLayoutData(layoutData);
        hostAndPortButton.setText(Messages.connectWithHostAndPort);
        hostAndPortButton.setSelection(true);
        hostAndPortButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!hostAndPortButton.getSelection()) {
                    return;
                }

                urlText.setEnabled(false);
                remoteHostText.setEnabled(true);
                remoteHostText.setFocus();
                portText.setEnabled(true);
                validate();
            }
        });

        remoteHostText = addComboTextField(parent,
                Messages.remoteHostTextLabel, REMOTE_HOST_HISTORY_KEY);
        List<IHost> hosts = JvmModel.getInstance().getHosts();
        for (IHost host : hosts) {
            String hostName = host.getName();
            if (!hostName.equals(IHost.LOCALHOST)
                    && remoteHostText.indexOf(hostName) == -1) {
                remoteHostText.add(hostName);
            }
        }
        remoteHostText.setFocus();
        String hostName = getSelectedHost();
        if (hostName != null && !IHost.LOCALHOST.equals(hostName)) {
            remoteHostText.setText(hostName);
        }

        portText = addComboTextField(parent, Messages.portTextLabel,
                PORT_HISTORY_KEY);
    }

    /**
     * Creates the URL panel.
     * 
     * @param parent
     *            The parent composite
     */
    private void createUrlPanel(Composite parent) {
        Button urlButton = new Button(parent, SWT.RADIO);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        urlButton.setLayoutData(layoutData);
        urlButton.setText(Messages.connectWithJmxUrl);
        urlButton.setSelection(false);
        urlButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hostAndPortButton.getSelection()) {
                    return;
                }

                remoteHostText.setEnabled(false);
                portText.setEnabled(false);
                urlText.setEnabled(true);
                if (urlText.getText().isEmpty()) {
                    urlText.setText(JMX_URL_HEADER);
                }
                urlText.setFocus();
                validate();
            }
        });

        urlText = addComboTextField(parent, Messages.jmxUrlTextLabel,
                URL_HISTORY_KEY);
        urlText.setEnabled(false);
    }

    /**
     * Gets the selected host.
     * 
     * @return The selected host, or <tt>null</tt> if not selected
     */
    private String getSelectedHost() {
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection)
                    .getFirstElement();
            if (element instanceof IHost) {
                return ((IHost) element).getName();
            } else if (element instanceof IJvm) {
                return (((IJvm) element).getHost()).getName();
            }
        }
        return null;
    }

    /**
     * Adds the given item to dialog settings with the given key.
     * 
     * @param key
     *            The dialog settings key
     * @param item
     *            The item to be added
     */
    private void addItemToDialogSettings(String key, String item) {
        String[] items = getDialogSettings().getArray(key);
        if (items == null) {
            items = new String[] { item };
        } else {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, items);
            if (list.contains(item)) {
                list.remove(item);
            }
            Collections.reverse(list);
            list.add(item);
            Collections.reverse(list);
            items = list.toArray(new String[0]);
        }
        getDialogSettings().put(key, items);
    }

    /**
     * Adds the combo text field.
     * 
     * @param composite
     *            The composite
     * @param labelString
     *            The label string
     * @param historyKey
     *            The history key
     * @return The combo widget
     */
    private Combo addComboTextField(Composite composite, String labelString,
            String historyKey) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(labelString);

        Combo combo = new Combo(composite, SWT.BORDER);
        String[] items = getDialogSettings().getArray(historyKey);
        if (items != null) {
            combo.setItems(items);
        }

        combo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        combo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
        return combo;
    }

    /**
     * Adds the text field.
     * 
     * @param composite
     *            The composite
     * @param labelString
     *            The label string
     * @return The text widget
     */
    private Text addTextField(Composite composite, String labelString) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(labelString);

        Text text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });
        return text;
    }

    /**
     * Validates the specified text.
     */
    void validate() {
        if (hostAndPortButton == null || portText == null) {
            return; // not yet completed creating controls
        }

        String errorMsg = null;
        if (hostAndPortButton.getSelection()) {
            if (getRemoteHost().isEmpty()) {
                errorMsg = Messages.emptyRemoteHostNameMsg;
            } else if (portText.getText().isEmpty()) {
                errorMsg = Messages.emptyPortMsg;
            } else {
                try {
                    if (getPort() < 0) {
                        errorMsg = Messages.invalidPortMsg;
                    }
                } catch (NumberFormatException e) {
                    errorMsg = Messages.invalidPortMsg;
                }
            }
        } else {
            if (getJmxUrl().isEmpty()) {
                errorMsg = Messages.emptyJmxUrlMsg;
            } else if (!getJmxUrl().startsWith(JMX_URL_HEADER)) {
                errorMsg = Messages.invalidJmxUrlHeaderMsg;
            } else if (getJmxUrl().equals(JMX_URL_HEADER)) {
                errorMsg = Messages.invalidJmxUrlMsg;
            }
        }

        setErrorMessage(errorMsg);
        setPageComplete(errorMsg == null);
    }
}
