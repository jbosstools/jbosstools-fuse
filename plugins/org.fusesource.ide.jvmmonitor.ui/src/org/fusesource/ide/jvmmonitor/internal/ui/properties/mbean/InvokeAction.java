/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;


/**
 * The action to invoke MBean method.
 */
public class InvokeAction extends Action implements ISelectionChangedListener {

    /** The object name. */
    private ObjectName objectName;

    /** The table viewer. */
    private TableViewer tableViewer;

    /** The MBean operation info. */
    private MBeanOperationInfo info;

    /** The property section */
    private AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param tableViewer
     *            The table viewer
     * @param section
     *            The property section
     */
    public InvokeAction(TableViewer tableViewer,
            AbstractJvmPropertySection section) {
        this.tableViewer = tableViewer;
        this.section = section;
        setText(Messages.invokeLabel);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        IActiveJvm jvm = section.getJvm();
        if (jvm != null && objectName != null && info != null) {
            new InvokeDialog(tableViewer.getTable().getShell(), jvm,
                    objectName, info).open();
        }
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        info = null;

        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection)
                    .getFirstElement();
            if (element instanceof MBeanOperationInfo) {
                info = (MBeanOperationInfo) element;
            }
        }

        setEnabled(info != null);
    }

    /**
     * Notifies that the selection has been changed on MBean tree.
     * 
     * @param name
     *            The object name
     */
    protected void selectionChanged(ObjectName name) {
        this.objectName = name;
    }
}
