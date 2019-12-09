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

import javax.management.MBeanServerConnection;

/**
 * @author lhein
 *
 */
public class RemoteBrokerFacade extends RemoteBrokerFacadeSupport {

    private final MBeanServerConnection mbeanServerConnection;

    public RemoteBrokerFacade(MBeanServerConnection mbeanServerConnection) {
        this(mbeanServerConnection, null);
    }

    private RemoteBrokerFacade(MBeanServerConnection mbeanServerConnection, String brokerName) {
        super(brokerName);
        this.mbeanServerConnection = mbeanServerConnection;
    }

    @Override
    public BrokerFacade[] getBrokers() throws Exception {
        String[] brokerNames = getBrokerNames();
        BrokerFacade[] rc = new BrokerFacade[brokerNames.length];
        for (int i = 0; i < rc.length; i++) {
            rc[i] = new RemoteBrokerFacade(getMBeanServerConnection(), brokerNames[i]);
        }
        return rc;
    }

    @Override
    protected MBeanServerConnection getMBeanServerConnection() throws Exception {
        return mbeanServerConnection;
    }
}
