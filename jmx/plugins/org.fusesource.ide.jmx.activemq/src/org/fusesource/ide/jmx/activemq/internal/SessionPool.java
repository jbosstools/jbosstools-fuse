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

import java.util.LinkedList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author lhein
 *
 */
public class SessionPool {

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private LinkedList<Session> sessions = new LinkedList<Session>();

    public Connection getConnection() throws JMSException {
        if (checkConnection()) {
            return connection;
        }

        synchronized (this) {
            connection = getConnectionFactory().createConnection();
            connection.start();
            return connection;
        }
    }

    private boolean checkConnection() {
        if (connection == null) {
            return false;
        }

        try {
            connection.getMetaData();
            return true;
        } catch (JMSException e) {
            return false;
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            throw new IllegalStateException("No ConnectionFactory has been set for the session pool");
        }
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Session borrowSession() throws JMSException {
        Session answer;
        synchronized (sessions) {
            if (sessions.isEmpty()) {
                answer = createSession();
            } else {
                answer = sessions.removeLast();
            }
        }
        return answer;
    }

    public void returnSession(Session session) {
        if (session != null) {
            synchronized (sessions) {
                sessions.add(session);
            }
        }
    }

    protected Session createSession() throws JMSException {
        return getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

}
