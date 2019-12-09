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

package org.fusesource.ide.jmx.activemq.internal;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;

/**
 * @author lhein
 *
 */
public class JMXConnectorBrokerFacade extends RemoteBrokerFacade {

    private final JMXConnector connector;
    private MBeanServerConnection mbeanServerConnection;

    public JMXConnectorBrokerFacade(JMXConnector connector) {
        super(null);
        this.connector = connector;
    }

    public JMXConnector getConnector() {
        return connector;
    }

    @Override
    protected MBeanServerConnection getMBeanServerConnection() throws Exception {
        if (mbeanServerConnection == null) {
            mbeanServerConnection = connector.getMBeanServerConnection();
        }
        return mbeanServerConnection;
    }

    public synchronized void closeConnection() {
        if (connector != null) {
            try {
                ActiveMQJMXPlugin.getLogger().debug("Closing a connection to a broker (" + connector.getConnectionId() + ")");

                connector.close();
            } catch (IOException e) {
                // Ignore the exception, since it most likly won't matter
                // anymore
            }
        }
    }


}