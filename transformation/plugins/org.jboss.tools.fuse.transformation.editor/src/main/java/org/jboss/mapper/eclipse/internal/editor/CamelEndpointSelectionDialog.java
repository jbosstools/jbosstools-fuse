/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.editor;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.eclipse.internal.util.JavaUtil;
import org.jboss.mapper.eclipse.internal.util.Util;

/**
 *
 */
@SuppressWarnings("synthetic-access")
public class CamelEndpointSelectionDialog extends TitleAreaDialog {

    String camelFilePath;
    String endpointID;
    IProject project;
    boolean updateCamelBuilder = false;
    String errMessage;
    CamelConfigBuilder camelConfigBuilder = null;
    ComboViewer endpointCombo;

    /**
     * @param parentShell
     * @param project
     * @param camelFilePath
     */
    public CamelEndpointSelectionDialog(final Shell parentShell,
            final IProject project,
            final String camelFilePath) {
        super(parentShell);
        this.project = project;
        this.camelFilePath = camelFilePath;
        this.endpointID = null;
        this.errMessage = null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar(final Composite parent) {
        final Control rtnControl = super.createButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(validate());
        setErrorMessage(null);
        return rtnControl;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        setTitle("Specify Camel Config and Endpoint ID");
        setMessage("Please specify path to the Camel configuration file "
                + "\nand the ID of the endpoint to update with new transformation details.");
        getShell().setText("Update Camel Endpoint");
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        final int nColumns = 3;

        final GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        // Create camel file path widgets
        Label label = new Label(composite, SWT.NONE);
        label.setText("Camel File path: ");
        label.setToolTipText("Path to the Camel configuration file.");
        final Text camelFilePathText = new Text(composite, SWT.BORDER);
        camelFilePathText.setLayoutData(GridDataFactory.swtDefaults().span(1, 1).grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        camelFilePathText.setToolTipText(label.getToolTipText());
        camelFilePathText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent arg0) {
                camelFilePath = camelFilePathText.getText();
                updateCamelBuilder = true;
                getButton(IDialogConstants.OK_ID).setEnabled(validate());
            }
        });

        final Button camelPathButton = new Button(composite, SWT.NONE);
        camelPathButton.setText("...");
        camelPathButton.setToolTipText("Browse to select an available Camel file.");
        camelPathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {

                final IResource res =
                        Util.selectResourceFromWorkspace(getShell(), ".xml", project);
                if (res != null) {
                    final IPath respath = JavaUtil.getJavaPathForResource(res);
                    final String relpath = respath.makeRelative().toString();
                    try {
                        File camelFile = new File(project.getFile(relpath).getLocationURI());
                        if (!camelFile.exists()) {
                            camelFile =
                                    new File(project.getFile(Util.RESOURCES_PATH + relpath)
                                            .getLocationURI());
                            if (camelFile.exists()) {
                                camelFilePath = relpath;
                                camelFilePathText.setText(relpath);
                                updateCamelBuilder = true;
                                getButton(IDialogConstants.OK_ID).setEnabled(validate());
                            }
                        }
                    } catch (final Exception e) {
                        // swallow
                        e.printStackTrace();
                    }
                }
            }
        });

        // Create ID widgets
        label = new Label(composite, SWT.NONE);
        label.setText("Endpoint:");
        label.setToolTipText("The Endpoint ID used in the Camel configuration");
        endpointCombo = new ComboViewer(composite, SWT.BORDER | SWT.READ_ONLY);
        endpointCombo.getControl().setLayoutData(
                GridDataFactory.swtDefaults().span(2, 1).grab(true, false)
                        .align(SWT.FILL, SWT.CENTER).create());
        endpointCombo.getControl().setToolTipText(label.getToolTipText());
        endpointCombo.setLabelProvider(new EndpointLabelProvider());
        endpointCombo.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent arg0) {
                final ISelection sel = arg0.getSelection();
                if (sel != null && !sel.isEmpty()) {
                    final IStructuredSelection ssel = (IStructuredSelection) sel;
                    endpointID = (String) ssel.getFirstElement();
                }
                getButton(IDialogConstants.OK_ID).setEnabled(validate());
            }
        });

        validate();
        setErrorMessage(null);

        return parent;
    }

    /**
     * @return the Camel configuration file path
     */
    public String getCamelFilePath() {
        return camelFilePath;
    }

    /**
     * @return the Camel end point ID
     */
    public String getEndpointID() {
        return endpointID;
    }

    boolean validate() {
        errMessage = null;

        if (camelFilePath == null || camelFilePath.trim().isEmpty()) {
            errMessage = "A Camel file path must be supplied";
        } else {
            try {
                File testFile = new File(project.getFile(camelFilePath).getLocationURI());
                if (!testFile.exists()) {
                    testFile =
                            new File(project.getFile(Util.RESOURCES_PATH + camelFilePath)
                                    .getLocationURI());
                    if (testFile.exists()) {
                        camelFilePath = Util.RESOURCES_PATH + camelFilePath;
                        final CamelConfigBuilder testBuilder =
                                CamelConfigBuilder.loadConfig(testFile);
                        if (updateCamelBuilder) {
                            camelConfigBuilder = testBuilder;
                        }
                    }
                }
            } catch (final Exception e) {
                errMessage = "The Camel file path must refer to a valid Camel file";
            }
        }
        if (camelConfigBuilder != null && updateCamelBuilder) {
            endpointCombo.setContentProvider(new ArrayContentProvider());
            endpointCombo.setInput(camelConfigBuilder.getTransformEndpointIds());
        }
        updateCamelBuilder = false;
        if (endpointID == null || endpointID.toString().trim().isEmpty()) {
            errMessage = "An endpoint must be selected";
        } else {
            final String id = endpointID.trim();
            if (camelConfigBuilder != null) {
                if (camelConfigBuilder.getEndpoint(id) == null) {
                    errMessage = "An endpoint does not exist with the specified id";
                }
            }
        }

        setErrorMessage(errMessage);
        return (getErrorMessage() == null);
    }

    class EndpointLabelProvider extends LabelProvider {

        @Override
        public String getText(final Object element) {
            if (element instanceof String) {
                return (String) element;
            }
            return super.getText(element);
        }

    }
}
