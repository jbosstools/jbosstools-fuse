/*******************************************************************************
 * Copyright (c)2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.commons;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.security.auth.Subject;
import java.io.IOException;
import java.util.Map;

/**
 * @author lhein
 *
 */

public class LocalJMXConnector implements JMXConnector {

    private final MBeanServerConnection mbeanServerConnection;

    public LocalJMXConnector(MBeanServerConnection mbeanServerConnection) {
        this.mbeanServerConnection = mbeanServerConnection;
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public void connect(Map<String, ?> stringMap) throws IOException {
    }

    @Override
    public MBeanServerConnection getMBeanServerConnection() throws IOException {
        return mbeanServerConnection;
    }

    @Override
    public MBeanServerConnection getMBeanServerConnection(Subject subject) throws IOException {
        return mbeanServerConnection;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void addConnectionNotificationListener(NotificationListener notificationListener, NotificationFilter notificationFilter, Object o) {
        // TODO
        // mbeanServerConnection.addNotificationListener(notificationListener, notificationFilter, o);
    }

    @Override
    public void removeConnectionNotificationListener(NotificationListener notificationListener) throws ListenerNotFoundException {
        // TODO

    }

    @Override
    public void removeConnectionNotificationListener(NotificationListener notificationListener, NotificationFilter notificationFilter, Object o) throws ListenerNotFoundException {
        // TODO

    }

    @Override
    public String getConnectionId() throws IOException {
        return mbeanServerConnection.toString();
    }
}
