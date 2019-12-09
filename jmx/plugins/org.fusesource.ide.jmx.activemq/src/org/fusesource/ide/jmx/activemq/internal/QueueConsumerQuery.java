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

import javax.jms.JMSException;
import java.util.Collection;

/**
 * @author lhein
 *
 */
public class QueueConsumerQuery extends DestinationFacade {

	public QueueConsumerQuery(BrokerFacade brokerFacade) throws JMSException {
		super(brokerFacade);
		setJMSDestinationType("queue");
	}

	public Collection<SubscriptionViewFacade> getConsumers() throws Exception {
		return getBrokerFacade().getQueueConsumers(getJMSDestination());
	}

	public void destroy() {
		// empty
	}
}
