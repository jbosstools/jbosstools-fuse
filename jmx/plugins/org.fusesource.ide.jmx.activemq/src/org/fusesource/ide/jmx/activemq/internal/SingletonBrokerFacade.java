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

import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.BrokerService;

/**
 * @author lhein
 *
 */
public class SingletonBrokerFacade extends LocalBrokerFacade {
    public SingletonBrokerFacade() {
        super(findSingletonBroker());
    }

    protected static BrokerService findSingletonBroker() {
        BrokerService broker = BrokerRegistry.getInstance().findFirst();
        if (broker == null) {
            throw new IllegalArgumentException("No BrokerService is registered with the BrokerRegistry. Are you sure there is a configured broker in the same ClassLoader?");
        }
        return broker;
    }
}