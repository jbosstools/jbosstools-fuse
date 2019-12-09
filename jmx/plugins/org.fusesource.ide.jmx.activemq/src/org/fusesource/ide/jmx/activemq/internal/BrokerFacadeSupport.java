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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.apache.activemq.broker.jmx.ConnectionViewMBean;
import org.apache.activemq.broker.jmx.ConnectorViewMBean;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.broker.jmx.DurableSubscriptionViewMBean;
import org.apache.activemq.broker.jmx.JobSchedulerViewMBean;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.jmx.NetworkBridgeViewMBean;
import org.apache.activemq.broker.jmx.NetworkConnectorViewMBean;
import org.apache.activemq.broker.jmx.ProducerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;

/**
 * A useful base class for an implementation of {@link BrokerFacade}
 * 
 * 
 */
public abstract class BrokerFacadeSupport implements BrokerFacade {
    public abstract ManagementContext getManagementContext();
    public abstract Set queryNames(ObjectName name, QueryExp query) throws Exception;
    public abstract Object newProxyInstance( ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) throws Exception;

    static public <T> T proxy(Class<T> ic, final Object target, final String id) throws Exception {
        return ic.cast(Proxy.newProxyInstance(ic.getClassLoader(), new Class[]{ic}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (method.getName() == "getId" && method.getParameterTypes().length == 0) {
                    return id;
                }
                return method.invoke(target, objects);
            }
        }));
    }

    @Override
	public Collection<QueueViewFacade> getQueues() throws Exception {
        BrokerViewFacade broker = getBrokerAdmin();
        if (broker == null) {
            return Collections.emptyList();
        }
        ObjectName[] queues = broker.getQueues();
        return getManagedObjects(queues, QueueViewMBean.class, QueueViewFacade.class);
    }

    @Override
	public Collection<TopicViewFacade> getTopics() throws Exception {
        BrokerViewFacade broker = getBrokerAdmin();
        if (broker == null) {
            return Collections.emptyList();
        }
        ObjectName[] queues = broker.getTopics();
        return getManagedObjects(queues, TopicViewMBean.class, TopicViewFacade.class);
    }

    @Override
	public Collection<DurableSubscriptionViewFacade> getDurableTopicSubscribers() throws Exception {
        BrokerViewFacade broker = getBrokerAdmin();
        if (broker == null) {
            return Collections.emptyList();
        }
        ObjectName[] queues = broker.getDurableTopicSubscribers();
        return getManagedObjects(queues, DurableSubscriptionViewMBean.class, DurableSubscriptionViewFacade.class);
    }

    @Override
	public Collection<DurableSubscriptionViewFacade> getInactiveDurableTopicSubscribers() throws Exception {
        BrokerViewFacade broker = getBrokerAdmin();
        if (broker == null) {
            return Collections.emptyList();
        }
        ObjectName[] queues = broker.getInactiveDurableTopicSubscribers();
        return getManagedObjects(queues, DurableSubscriptionViewMBean.class, DurableSubscriptionViewFacade.class);
    }

    @Override
	public QueueViewFacade getQueue(String name) throws Exception {
        return (QueueViewFacade) getDestinationByName(getQueues(), name);
    }

    @Override
	public TopicViewFacade getTopic(String name) throws Exception {
        return (TopicViewFacade) getDestinationByName(getTopics(), name);
    }

    protected DestinationViewMBean getDestinationByName(Collection<? extends DestinationViewMBean> collection,
            String name) {
        Iterator<? extends DestinationViewMBean> iter = collection.iterator();
        while (iter.hasNext()) {
            DestinationViewMBean destinationViewFacade = iter.next();
            if (name.equals(destinationViewFacade.getName())) {
                return destinationViewFacade;
            }
        }
        return null;
    }

    protected <T> Collection<T> getManagedObjects(ObjectName[] names, Class<?> type, Class<T> type2) throws Exception {
        List<T> answer = new ArrayList<T>();
        for (int i = 0; i < names.length; i++) {
            ObjectName name = names[i];
            Object value = newProxyInstance(name, type, true);
            if (value != null) {
                answer.add(proxy(type2, value, name.getCanonicalName()));
            }
        }
        return answer;
    }

    @Override
	@SuppressWarnings("unchecked")
    public Collection<ConnectionViewFacade> getConnections() throws Exception {
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",connector=clientConnectors,connectorName=*,connectionViewType=clientId,connectionName=*");

        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), ConnectionViewMBean.class, ConnectionViewFacade.class);
    }

    @Override
	@SuppressWarnings("unchecked")
    public Collection<String> getConnections(String connectorName) throws Exception {
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",connector=clientConnectors,connectorName=" + connectorName + ",connectionViewType=clientId,connectionName=*");
        Set<ObjectName> queryResult = queryNames(query, null);
        Collection<String> result = new ArrayList<String>(queryResult.size());
        for (ObjectName on : queryResult) {
            String name = on.getKeyProperty("connectionName").replace('_', ':');
            result.add(name);
        }
        return result;
    }

    @Override
	@SuppressWarnings("unchecked")
    public ConnectionViewFacade getConnection(String connectionName) throws Exception {
        connectionName = connectionName.replace(':', '_');
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",connector=clientConnectors,*,connectionName=" + connectionName);
        Set<ObjectName> queryResult = queryNames(query, null);
        if (queryResult.isEmpty())
            return null;
        ObjectName objectName = queryResult.iterator().next();
        Object rc = newProxyInstance(objectName, ConnectionViewMBean.class, true);
        return proxy(ConnectionViewFacade.class, rc, objectName.getCanonicalName());
    }

    @Override
	@SuppressWarnings("unchecked")
    public Collection<String> getConnectors() throws Exception {
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",connector=clientConnectors,connectorName=*");
        Set<ObjectName> queryResult = queryNames(query, null);
        Collection<String> result = new ArrayList<String>(queryResult.size());
        for (ObjectName on : queryResult)
            result.add(on.getKeyProperty("connectorName"));
        return result;
    }

    @Override
	public ConnectorViewFacade getConnector(String name) throws Exception {
        String brokerName = getBrokerName();
        ObjectName objectName = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",connector=clientConnectors,connectorName=" + name);
        Object rc = newProxyInstance(objectName, ConnectorViewMBean.class, true);
        return proxy(ConnectorViewFacade.class, rc, objectName.getCanonicalName());
    }

    @Override
	@SuppressWarnings("unchecked")
    public Collection<NetworkConnectorViewFacade> getNetworkConnectors() throws Exception {
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",connector=networkConnectors,networkConnectorName=*");
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), NetworkConnectorViewMBean.class,
                NetworkConnectorViewFacade.class);
    }

    @Override
	public Collection<NetworkBridgeViewFacade> getNetworkBridges() throws Exception {
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",connector=networkConnectors,networkConnectorName=*,networkBridge=*");
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]),
                NetworkBridgeViewMBean.class, NetworkBridgeViewFacade.class);
    }

    @Override
	@SuppressWarnings("unchecked")
    public Collection<SubscriptionViewFacade> getQueueConsumers(String queueName) throws Exception {
        String brokerName = getBrokerName();
        queueName = queueName.replace("\"", "_");
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",destinationType=Queue,destinationName=" + queueName + ",endpoint=Consumer,*");
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), SubscriptionViewMBean.class, SubscriptionViewFacade.class);
    }

    @Override
    public Collection<SubscriptionViewFacade> getTopicConsumers(String topicName) throws Exception {
        String brokerName = getBrokerName();
        topicName = topicName.replace('\"', '_');
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",destinationType=Topic,destinationName=" + topicName + ",endpoint=Consumer,*");
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), SubscriptionViewMBean.class, SubscriptionViewFacade.class);
    }

    @Override
    public Collection<DurableSubscriptionViewFacade> getTopicDurableConsumers(String topicName) throws Exception {
        String brokerName = getBrokerName();
        topicName = topicName.replace('\"', '_');
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName +  ",destinationType=Topic,destinationName=" + topicName + ",endpoint=Consumer,consumerId=Durable(*),*");
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), DurableSubscriptionViewMBean.class, DurableSubscriptionViewFacade.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ProducerViewFacade> getQueueProducers(String queueName) throws Exception {
        String brokerName = getBrokerName();
        queueName = queueName.replace('\"', '_');
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",destinationType=Queue,destinationName=" + queueName + ",endpoint=Producer,*");
        Set<ObjectName> queryResult = queryNames(query, null);
        Collection<ProducerViewFacade> producers = getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), ProducerViewMBean.class, ProducerViewFacade.class);

        // Now look for dynamic ones.
        query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",endpoint=dynamicProducer,*");
        queryResult = queryNames(query, null);
        Collection<ProducerViewFacade> dynamics = getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), ProducerViewMBean.class, ProducerViewFacade.class);
        for (ProducerViewFacade dynamicProducer : dynamics) {
            if (queueName.equals(dynamicProducer.getDestinationName())) {
                producers.add(dynamicProducer);
            }
        }

        return producers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ProducerViewFacade> getTopicProducers(String topicName) throws Exception {
        String brokerName = getBrokerName();
        topicName = topicName.replace('\"', '_');
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",destinationType=Queue,destinationName=" + topicName + ",endpoint=Producer,*");
        Set<ObjectName> queryResult = queryNames(query, null);
        Collection<ProducerViewFacade> producers = getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), ProducerViewMBean.class, ProducerViewFacade.class);

        // Now look for dynamic ones.
        query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",endpoint=dynamicProducer,*");
        queryResult = queryNames(query, null);
        Collection<ProducerViewFacade> dynamics = getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), ProducerViewMBean.class, ProducerViewFacade.class);
        for (ProducerViewFacade dynamicProducer : dynamics) {
            if (topicName.equals(dynamicProducer.getDestinationName())) {
                producers.add(dynamicProducer);
            }
        }

        return producers;
    }

    @Override
	@SuppressWarnings("unchecked")
    public Collection<SubscriptionViewFacade> getConsumersOnConnection(String connectionName) throws Exception {
        connectionName = connectionName.replace(':', '_');
        String brokerName = getBrokerName();
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName
                + ",*,endpoint=Consumer,clientId=" + connectionName);
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), SubscriptionViewMBean.class,  SubscriptionViewFacade.class);
    }

    @Override
	public JobSchedulerViewFacade getJobScheduler() throws Exception {
        ObjectName name = getBrokerAdmin().getJMSJobScheduler();
        Object rc = newProxyInstance(name, JobSchedulerViewMBean.class, true);
        return proxy(JobSchedulerViewFacade.class, rc, name.getCanonicalName());
    }

    @Override
	public Collection<JobFacade> getScheduledJobs() throws Exception {
        JobSchedulerViewFacade jobScheduler = getJobScheduler();
        List<JobFacade> result = new ArrayList<JobFacade>();
        TabularData table = jobScheduler.getAllJobs();
        for (Object object : table.values()) {
            CompositeData cd = (CompositeData) object;
            JobFacade jf = new JobFacade(cd);
            result.add(jf);
        }
        return result;
    }


    @Override
	public boolean isJobSchedulerStarted() {
        try {
            getJobScheduler();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
