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

import org.apache.activemq.command.ActiveMQDestination;
import org.fusesource.ide.jmx.commons.JmxTemplateSupport;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.util.Collection;

/**
 * @author lhein
 *
 */
public class JmxTemplateBrokerFacade implements BrokerFacade {
    private final JmxTemplateSupport template;

    public JmxTemplateBrokerFacade(JmxTemplateSupport template) {
        this.template = template;
    }


    /**
     * Executes a JMX operation on a BrokerFacade
     */
    public <T> T execute(final BrokerFacadeCallback<T> callback) {
        return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            @Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
                MBeanServerConnection connection = connector.getMBeanServerConnection();
                BrokerFacade brokerFacade = new RemoteBrokerFacade(connection);
                return callback.doWithBrokerFacade(brokerFacade);
            }
        });
    }

    @Override
    public String getId() throws Exception {
        return execute(new BrokerFacadeCallback<String>() {
            @Override
			public String doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getId();
            }
        });
    }

    @Override
    public BrokerFacade[] getBrokers() throws Exception {
        return execute(new BrokerFacadeCallback<BrokerFacade[]>() {
            @Override
			public BrokerFacade[] doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getBrokers();
            }
        });
    }

    @Override
	public BrokerViewFacade getBrokerAdmin() throws Exception {
        return execute(new BrokerFacadeCallback<BrokerViewFacade>() {
            @Override
			public BrokerViewFacade doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getBrokerAdmin();
            }
        });
    }

    @Override
	public String getBrokerName() throws Exception {
        return execute(new BrokerFacadeCallback<String>() {
            @Override
			public String doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getBrokerName();
            }
        });
    }

    @Override
	public Collection<QueueViewFacade> getQueues() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<QueueViewFacade>>() {
            @Override
			public Collection<QueueViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getQueues();
            }
        });
    }

    @Override
	public Collection<TopicViewFacade> getTopics() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<TopicViewFacade>>() {
            @Override
			public Collection<TopicViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getTopics();
            }
        });
    }

    @Override
	public Collection<SubscriptionViewFacade> getQueueConsumers(final String queueName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<SubscriptionViewFacade>>() {
            @Override
			public Collection<SubscriptionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getQueueConsumers(queueName);
            }
        });
    }

    @Override
    public Collection<SubscriptionViewFacade> getTopicConsumers(final String topicName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<SubscriptionViewFacade>>() {
            @Override
			public Collection<SubscriptionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getTopicConsumers(topicName);
            }
        });
    }

    @Override
    public Collection<DurableSubscriptionViewFacade> getTopicDurableConsumers(final String topicName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<DurableSubscriptionViewFacade>>() {
            @Override
			public Collection<DurableSubscriptionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getTopicDurableConsumers(topicName);
            }
        });
    }

    @Override
	public Collection<DurableSubscriptionViewFacade> getDurableTopicSubscribers() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<DurableSubscriptionViewFacade>>() {
            @Override
			public Collection<DurableSubscriptionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getDurableTopicSubscribers();
            }
        });
    }

    @Override
	public Collection<DurableSubscriptionViewFacade> getInactiveDurableTopicSubscribers() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<DurableSubscriptionViewFacade>>() {
            @Override
			public Collection<DurableSubscriptionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getInactiveDurableTopicSubscribers();
            }
        });
    }

    @Override
    public Collection<ProducerViewFacade> getQueueProducers(final String queueName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<ProducerViewFacade>>() {
            @Override
			public Collection<ProducerViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getQueueProducers(queueName);
            }
        });
    }

    @Override
    public Collection<ProducerViewFacade> getTopicProducers(final String topicName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<ProducerViewFacade>>() {
            @Override
			public Collection<ProducerViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getTopicProducers(topicName);
            }
        });
    }

    @Override
	public Collection<String> getConnectors() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<String>>() {
            @Override
			public Collection<String> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getConnectors();
            }
        });
    }

    @Override
	public ConnectorViewFacade getConnector(final String name) throws Exception {
        return execute(new BrokerFacadeCallback<ConnectorViewFacade>() {
            @Override
			public ConnectorViewFacade doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getConnector(name);
            }
        });
    }

    @Override
	public Collection<ConnectionViewFacade> getConnections() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<ConnectionViewFacade>>() {
            @Override
			public Collection<ConnectionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getConnections();
            }
        });
    }

    @Override
	public Collection<String> getConnections(final String connectorName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<String>>() {
            @Override
			public Collection<String> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getConnections(connectorName);
            }
        });
    }

    @Override
	public ConnectionViewFacade getConnection(final String connectionName) throws Exception {
        return execute(new BrokerFacadeCallback<ConnectionViewFacade>() {
            @Override
			public ConnectionViewFacade doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getConnection(connectionName);
            }
        });
    }

    @Override
	public Collection<SubscriptionViewFacade> getConsumersOnConnection(final String connectionName) throws Exception {
        return execute(new BrokerFacadeCallback<Collection<SubscriptionViewFacade>>() {
            @Override
			public Collection<SubscriptionViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getConsumersOnConnection(connectionName);
            }
        });
    }

    @Override
	public Collection<NetworkConnectorViewFacade> getNetworkConnectors() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<NetworkConnectorViewFacade>>() {
            @Override
			public Collection<NetworkConnectorViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getNetworkConnectors();
            }
        });
    }

    @Override
	public Collection<NetworkBridgeViewFacade> getNetworkBridges() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<NetworkBridgeViewFacade>>() {
            @Override
			public Collection<NetworkBridgeViewFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getNetworkBridges();
            }
        });
    }

    @Override
	public void purgeQueue(final ActiveMQDestination destination) throws Exception {
        execute(new BrokerFacadeCallback() {
            @Override
			public Object doWithBrokerFacade(BrokerFacade broker) throws Exception {
                broker.purgeQueue(destination);
                return null;
            }
        });
    }

    @Override
	public QueueViewFacade getQueue(final String name) throws Exception {
        return execute(new BrokerFacadeCallback<QueueViewFacade>() {
            @Override
			public QueueViewFacade doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getQueue(name);
            }
        });
    }

    @Override
	public TopicViewFacade getTopic(final String name) throws Exception {
        return execute(new BrokerFacadeCallback<TopicViewFacade>() {
            @Override
			public TopicViewFacade doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getTopic(name);
            }
        });
    }

    @Override
	public JobSchedulerViewFacade getJobScheduler() throws Exception {
        return execute(new BrokerFacadeCallback<JobSchedulerViewFacade>() {
            @Override
			public JobSchedulerViewFacade doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getJobScheduler();
            }
        });
    }

    @Override
	public Collection<JobFacade> getScheduledJobs() throws Exception {
        return execute(new BrokerFacadeCallback<Collection<JobFacade>>() {
            @Override
			public Collection<JobFacade> doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.getScheduledJobs();
            }
        });
    }

    @Override
	public boolean isJobSchedulerStarted() {
        return execute(new BrokerFacadeCallback<Boolean>() {
            @Override
			public Boolean doWithBrokerFacade(BrokerFacade broker) throws Exception {
                return broker.isJobSchedulerStarted();
            }
        });
    }
}
