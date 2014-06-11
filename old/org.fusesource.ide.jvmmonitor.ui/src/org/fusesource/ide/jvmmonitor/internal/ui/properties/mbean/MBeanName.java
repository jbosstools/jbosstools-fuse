/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.ObjectName;

import org.fusesource.ide.jvmmonitor.core.IActiveJvm;


/**
 * The MBean name.
 */
public class MBeanName {

    /** The object name. */
    private ObjectName objectName;

    /** The state indicating if notification is supported. */
    private boolean isNotificationSupported;

    /** The JVM. */
    private IActiveJvm jvm;

    /**
     * The constructor.
     * 
     * @param objectName
     *            The object name
     * @param jvm
     *            The JVM
     * @param isNotificationSupported
     *            <tt>true</tt> if notification is supported
     */
    public MBeanName(ObjectName objectName, IActiveJvm jvm,
            boolean isNotificationSupported) {
        this.objectName = objectName;
        this.jvm = jvm;
        this.isNotificationSupported = isNotificationSupported;
    }

    /**
     * Gets the object name.
     * 
     * @return the object name
     */
    public ObjectName getObjectName() {
        return objectName;
    }

    /**
     * Gets the state indicating if notification is supported.
     * 
     * @return <tt>true</tt> if notification is supported
     */
    protected boolean isNotificationSubsctibed() {
        return isNotificationSupported
                && jvm.getMBeanServer().getMBeanNotification()
                        .isSubscribed(objectName);
    }

    /**
     * Gets the JVM.
     * 
     * @return the JVM
     */
    protected IActiveJvm getJvm() {
        return jvm;
    }
}
