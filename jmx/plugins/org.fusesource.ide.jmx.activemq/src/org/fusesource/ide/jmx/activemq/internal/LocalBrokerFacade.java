/*******************************************************************************
 * Copyright (c)2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.activemq.internal;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerView;
import org.apache.activemq.broker.jmx.ManagedRegionBroker;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQDestination;

import javax.management.ObjectName;
import javax.management.QueryExp;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lhein
 *
 */
public class LocalBrokerFacade extends BrokerFacadeSupport {
	private BrokerService brokerService;

    public LocalBrokerFacade(BrokerService brokerService) {
		this.brokerService = brokerService;
	}

	public BrokerService getBrokerService() {
		return brokerService;
	}

    @Override
    public String getId() throws Exception {
        return brokerService.getBrokerName();
    }

    public BrokerFacade[] getBrokers() throws Exception {
        return new BrokerFacade[]{this};
    }

	public String getBrokerName() throws Exception {
		return brokerService.getBrokerName();
	}
	public Broker getBroker() throws Exception {
		return brokerService.getBroker();
	}
	public ManagementContext getManagementContext() {
		return brokerService.getManagementContext();
	}
	public BrokerViewFacade getBrokerAdmin() throws Exception {
		return proxy(BrokerViewFacade.class, brokerService.getAdminView(), brokerService.getBrokerName());
	}
	public ManagedRegionBroker getManagedBroker() throws Exception {
		BrokerView adminView = brokerService.getAdminView();
		if (adminView == null) {
			return null;
		}
		return adminView.getBroker();
	}

    public void purgeQueue(ActiveMQDestination destination) throws Exception {
        Set destinations = getManagedBroker().getQueueRegion().getDestinations(destination);
        for (Iterator i = destinations.iterator(); i.hasNext();) {
            Destination dest = (Destination) i.next();
            if (dest instanceof Queue) {
                Queue regionQueue = (Queue) dest;
                regionQueue.purge();
            }
        }
    }

    @Override
    public Set queryNames(ObjectName name, QueryExp query) throws Exception {
        return getManagementContext().queryNames(name, query);
    }

    @Override
    public Object newProxyInstance(ObjectName objectName, Class interfaceClass, boolean notificationBroadcaster) {
        return getManagementContext().newProxyInstance(objectName, interfaceClass, notificationBroadcaster);
    }
}

