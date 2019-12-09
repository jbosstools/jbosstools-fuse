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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.remote.JMXConnector;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQDestination;


/**
 * @author lhein
 *
 */
public abstract class RemoteBrokerFacadeSupport extends BrokerFacadeSupport {
    private String brokerName;

    public RemoteBrokerFacadeSupport() {
    }

    public RemoteBrokerFacadeSupport(String brokerName) {
        this.brokerName = brokerName;
    }

    @Override
	public String getBrokerName() throws Exception {
        return getBrokerAdmin().getBrokerName();
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    @Override
	public BrokerViewFacade getBrokerAdmin() throws Exception {
        MBeanServerConnection connection = getMBeanServerConnection();

        Set<ObjectName> brokers = findBrokers(connection);
        if (brokers.isEmpty()) {
            throw new IOException("No broker could be found in the JMX.");
        }
        ObjectName name = brokers.iterator().next();
        BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(connection, name, BrokerViewMBean.class, true);
        return proxy(BrokerViewFacade.class, mbean, name.getCanonicalName());
    }

    @Override
    public String getId() throws Exception {
        Set<ObjectName> brokers = findBrokers(getMBeanServerConnection());
        if (brokers.isEmpty()) {
            throw new IOException("No broker could be found in the JMX.");
        }
        ObjectName name = brokers.iterator().next();
        return name.getCanonicalName();
    }

    public String[] getBrokerNames() throws Exception {
        MBeanServerConnection connection = getMBeanServerConnection();
        ObjectName names = new ObjectName("org.apache.activemq:type=Broker,brokerName=*");
        Set<String> rc = new HashSet<>();
        Set<ObjectName> objectNames = connection.queryNames(names, null);
        for(ObjectName name: objectNames) {
            String bn = name.getKeyProperty("brokerName");
            if(bn!=null) {
                rc.add(bn);
            }
        }
        return rc.toArray(new String[rc.size()]);
    }

    protected abstract MBeanServerConnection getMBeanServerConnection() throws Exception;

    /**
     * Finds all ActiveMQ-Brokers registered on a certain JMX-Server or, if a
     * JMX-BrokerName has been set, the broker with that name.
     *
     * @param connection not <code>null</code>
     * @return Set with ObjectName-elements
     * @throws java.io.IOException
     * @throws javax.management.MalformedObjectNameException
     *
     */
    protected Set<ObjectName> findBrokers(MBeanServerConnection connection)
            throws IOException, MalformedObjectNameException {
        ObjectName name;
        if (this.brokerName == null) {
            name = new ObjectName("org.apache.activemq:type=Broker,brokerName=*");
        } else {
            name = new ObjectName("org.apache.activemq:brokerName="
                    + this.brokerName + ",type=Broker");
        }

        Set<ObjectName> brokers = connection.queryNames(name, null);
        return brokers;
    }

    @Override
	public void purgeQueue(ActiveMQDestination destination) throws Exception {
        QueueViewMBean queue = getQueue(destination.getPhysicalName());
        queue.purge();
    }

    @Override
	public ManagementContext getManagementContext() {
        throw new IllegalStateException("not supported");
    }

    protected <T> Collection<T> getManagedObjects(ObjectName[] names,
                                                  Class<T> type) {
        MBeanServerConnection connection;
        try {
            connection = getMBeanServerConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<T> answer = new ArrayList<>();
        if (connection != null) {
            for (int i = 0; i < names.length; i++) {
                ObjectName name = names[i];
                T value = MBeanServerInvocationHandler.newProxyInstance(
                        connection, name, type, true);
                if (value != null) {
                    answer.add(value);
                }
            }
        }
        return answer;
    }

    @Override
    public Set<ObjectName> queryNames(ObjectName name, QueryExp query) throws Exception {
        return getMBeanServerConnection().queryNames(name, query);
    }


    @Override
    public Object newProxyInstance(ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) throws Exception {
        return MBeanServerInvocationHandler.newProxyInstance(getMBeanServerConnection(), objectName, interfaceClass, notificationBroadcaster);
    }

    protected boolean isConnectionActive(JMXConnector connector) {
        if (connector == null) {
            return false;
        }

        try {
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            int brokerCount = findBrokers(connection).size();
            return brokerCount > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
