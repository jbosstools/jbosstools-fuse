/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.ObjectName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to subscribe notification.
 */
public class SubscribeAction extends Action {

    /** The object name. */
    ObjectName objectName;

    /** The property section */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param section
     *            The property section
     */
    public SubscribeAction(AbstractJvmPropertySection section) {
        super(Messages.subscribeLabel, IAction.AS_CHECK_BOX);
        this.section = section;
    }

    /**
     * The constructor.
     * 
     * @param selection
     *            The selection
     * @param section
     *            The property section
     */
    public SubscribeAction(ISelection selection,
            AbstractJvmPropertySection section) {
        this(section);
        setSelection(selection);
        refresh();
    }

    /**
     * Refreshes.
     */
    private void refresh() {
        new Job(Messages.refreshSubscribeActionStateJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                boolean enabled = jvm != null
                        && jvm.isConnected()
                        && objectName != null
                        && jvm.getMBeanServer().getMBeanNotification()
                                .isSupported(objectName);
                setEnabled(enabled);
                if (enabled && jvm != null) {
                    setChecked(jvm.getMBeanServer().getMBeanNotification()
                            .isSubscribed(objectName));
                }
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        new Job(Messages.subscribeNotificationJobLabel) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                if (jvm == null) {
                    return Status.CANCEL_STATUS;
                }

                try {
                    if (jvm.getMBeanServer().getMBeanNotification()
                            .isSubscribed(objectName)) {
                        jvm.getMBeanServer().getMBeanNotification()
                                .unsubscribe(objectName);
                    } else {
                        jvm.getMBeanServer().getMBeanNotification()
                                .subscribe(objectName);
                    }
                } catch (JvmCoreException e) {
                    Activator.log(Messages.subscribeNotificationFailedMsg, e);
                }
                return Status.OK_STATUS;
            }

        }.schedule();

    }

    /**
     * Sets the selection.
     * 
     * @param objectName
     *            The object name
     */
    protected void setSelection(ObjectName objectName) {
        this.objectName = objectName;
        refresh();
    }

    /**
     * Sets the selection.
     * 
     * @param selection
     *            The selection
     */
    private void setSelection(ISelection selection) {
        objectName = null;

        if (selection instanceof ITreeSelection) {
            ITreeSelection new_name = (ITreeSelection) selection;
            Object element = new_name.getFirstElement();
            if (element instanceof MBeanType) {
                MBeanName[] mBeanNames = ((MBeanType) element).getMBeanNames();
                if (mBeanNames != null && mBeanNames.length == 1) {
                    objectName = mBeanNames[0].getObjectName();
                }
            } else if (element instanceof MBeanName) {
                objectName = ((MBeanName) element).getObjectName();
            }
        }
    }
}
