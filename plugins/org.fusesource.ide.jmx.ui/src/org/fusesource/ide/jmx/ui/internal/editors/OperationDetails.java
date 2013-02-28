/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.editors;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.fusesource.ide.jmx.core.MBeanOperationInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanUtils;
import org.fusesource.ide.jmx.core.util.StringUtils;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.dialogs.OperationInvocationResultDialog;


public class OperationDetails extends AbstractFormPart implements IDetailsPage {

    private FormToolkit toolkit;

    private Composite container;

    private MBeanOperationInfoWrapper opInfoWrapper;

    private Section section;

    public OperationDetails(IFormPart masterSection) {
    }

    public void createContents(Composite parent) {
        TableWrapLayout layout = new TableWrapLayout();
        parent.setLayout(layout);

        toolkit = getManagedForm().getToolkit();

        section = toolkit.createSection(parent, Section.TITLE_BAR | SWT.WRAP
                | Section.DESCRIPTION);
        section.marginWidth = 10;
        section.setText(Messages.OperationDetails_title);
        section.setDescription(""); //$NON-NLS-1$
        section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

        container = toolkit.createComposite(section);
        section.setClient(container);
        GridLayout glayout = new GridLayout();
        glayout.marginWidth = glayout.marginHeight = 0;
        glayout.numColumns = 2;
        glayout.makeColumnsEqualWidth = false;
        container.setLayout(glayout);
    }

    public void selectionChanged(IFormPart part, ISelection selection) {
        if (!(selection instanceof IStructuredSelection))
            return;

        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (obj instanceof MBeanOperationInfoWrapper) {
            MBeanOperationInfoWrapper wrapper = (MBeanOperationInfoWrapper) obj;
            if (wrapper == opInfoWrapper) {
                return;
            }
            // update the currently selected contribution to the one to be
            // displayed, if null
            // the controls displayed are still disposed, this is to reflect
            // a removed contribution
            opInfoWrapper = wrapper;
            drawInvocationDetails(wrapper);
        } else {
            clear();
        }
    }

    public void clear() {
        drawInvocationDetails(null);
    }

    protected void drawInvocationDetails(MBeanOperationInfoWrapper wrapper) {
        if (container != null && !container.isDisposed()) {
            // remove any controls created from prior selections
            Control[] childs = container.getChildren();
            if (childs.length > 0) {
                for (int i = 0; i < childs.length; i++) {
                    childs[i].dispose();
                }
            }
        }
        if (wrapper == null) {
            return;
        }
        MBeanOperationInfo opInfo = wrapper.getMBeanOperationInfo();
        String desc = opInfo.getDescription();
        // FIX issue #27: the MBean operation description can be null
        if (desc != null) {
            section.setDescription(desc);
        }
        // composite for method signature [ return type | method button | ( |
        // Composite(1..n parameters) | ) ]
        Composite c = toolkit.createComposite(container, SWT.NONE);
        c.setLayout(new GridLayout(5, false));
        // return type
        Label returnTypeLabel = toolkit.createLabel(c,
                opInfo.getReturnType() != null ? StringUtils.toString(opInfo
                        .getReturnType()) : "void"); //$NON-NLS-1$
        returnTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
                false, false));
        // method name
        InvokeOperationButton invocationButton = new InvokeOperationButton(c,
                SWT.PUSH);
        Label leftParenthesis = toolkit.createLabel(c, "("); //$NON-NLS-1$
        leftParenthesis.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
                false, false));

        // parameters
        final MBeanParameterInfo[] params = opInfo.getSignature();
        Text[] textParams = null;
        if (params.length > 0) {
            Composite paramsComposite = toolkit.createComposite(c, SWT.NONE);
            paramsComposite.setLayout(new GridLayout(1, false));
            paramsComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BOTTOM,
                    false, false));
            textParams = new Text[params.length];
            for (int j = 0; j < params.length; j++) {
                MBeanParameterInfo param = params[j];
                textParams[j] = new Text(paramsComposite, SWT.SINGLE
                        | SWT.BORDER);
                textParams[j].setText(StringUtils.toString(param.getType()));
                textParams[j].setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM,
                        true, true));
            }
            paramsComposite.pack();
        }
        Label rightParenthesis = toolkit.createLabel(c, ")"); //$NON-NLS-1$
        rightParenthesis.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
                false, false));

        invocationButton.setTextParams(textParams);
        container.pack();
        container.layout();
    }

    private class InvokeOperationButton extends SelectionAdapter {

        private Text[] textParams;

        private Button button;

        public InvokeOperationButton(Composite parent, int style) {
            button = toolkit.createButton(parent, opInfoWrapper
                    .getMBeanOperationInfo().getName(), style);
            button.addSelectionListener(this);
            button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                    false));
        }

        void setTextParams(Text[] textParams) {
            this.textParams = textParams;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            try {
                MBeanParameterInfo[] paramInfos = opInfoWrapper
                        .getMBeanOperationInfo().getSignature();
                Object[] paramList = null;
                if (textParams != null) {
                    String[] strs = new String[textParams.length];
                    for (int i = 0; i < strs.length; i++) {
                        strs[i] = textParams[i].getText();
                    }
                    paramList = MBeanUtils.getParameters(strs, paramInfos);
                }
                MBeanServerConnection mbsc = opInfoWrapper
                        .getMBeanServerConnection();
                ObjectName objectName = opInfoWrapper.getObjectName();
                String methodName = opInfoWrapper.getMBeanOperationInfo()
                        .getName();
                Object result;
                if (paramList != null) {
                    String[] paramSig = new String[paramInfos.length];
                    for (int i = 0; i < paramSig.length; i++) {
                        paramSig[i] = paramInfos[i].getType();
                    }
                    result = mbsc.invoke(objectName, methodName, paramList,
                            paramSig);
                } else {
                    result = mbsc.invoke(objectName, methodName, new Object[0],
                            new String[0]);
                }
                if ("void".equals(opInfoWrapper.getMBeanOperationInfo() //$NON-NLS-1$
                        .getReturnType())) {
                    MessageDialog.openInformation(container.getShell(),
                            Messages.OperationDetails_invocationResult,
                            Messages.OperationDetails_invocationSuccess);
                    return;
                } else {
                    OperationInvocationResultDialog.open(container.getShell(), result);
                }
            } catch (Exception e) {
                String message = e.getLocalizedMessage();
                if (message == null) {
                    message = e.getClass().getName();
                }
                JMXUIActivator.log(IStatus.ERROR, e.getClass().getName(), e);
                // if the exception has a cause, it is likely more interesting
                // since it may be the exception thrown by the mbean
                // implementation
                // rather than the exception thrown by the mbean server
                // connection
                if (e.getCause() != null) {
                    message = e.getCause().getLocalizedMessage();
                }
                MessageDialog.openError(container.getShell(),
                        Messages.OperationDetails_invocationError, message);
            }
        }
    }
}