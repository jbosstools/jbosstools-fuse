/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.mbean;

import javax.management.Notification;
import javax.management.ObjectName;

import org.fusesource.ide.jvmmonitor.core.JvmCoreException;


/**
 * The MBean notification.
 */
public interface IMBeanNotification {

    /**
     * Subscribes the notification of given object name.
     * 
     * @param objectName
     *            The object name
     * @throws JvmCoreException
     */
    void subscribe(ObjectName objectName) throws JvmCoreException;

    /**
     * Unsubscribes the notification of given object name.
     * 
     * @param objectName
     *            The object name
     * @throws JvmCoreException
     */
    void unsubscribe(ObjectName objectName) throws JvmCoreException;

    /**
     * Gets the notifications.
     * 
     * @param objectName
     *            The object name
     * @return The notifications
     */
    Notification[] getNotifications(ObjectName objectName);

    /**
     * Clears the notifications.
     * 
     * @param objectName
     *            The object name
     */
    void clear(ObjectName objectName);

    /**
     * Gets the state indicating if the notification is subscribed for the given
     * object name.
     * 
     * @param objectName
     *            The object name
     * @return True if the notification is subscribed for the given object name
     */
    boolean isSubscribed(ObjectName objectName);

    /**
     * Gets the state indicating if the notification is supported for the given
     * object name.
     * 
     * @param objectName
     *            The object name
     * @return True if the notification is supported for the given object name
     */
    boolean isSupported(ObjectName objectName);
}
