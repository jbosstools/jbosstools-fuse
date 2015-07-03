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

package org.fusesource.ide.jmx.servicemix.internal;

import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.apache.servicemix.nmr.management.ManagedEndpointMBean;
import org.fusesource.ide.jmx.commons.JmxTemplateSupport;

/**
 * @author lhein
 *
 */
public class JmxTemplateServiceMixFacade implements ServiceMixFacade {
    private final JmxTemplateSupport template;

    public JmxTemplateServiceMixFacade(JmxTemplateSupport template) {
        this.template = template;
    }

    /**
     * Executes a JMX operation on a BrokerFacade
     */
    public <T> T execute(final ServiceMixFacadeCallback<T> callback) {
        return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            public T doWithJmxConnector(JMXConnector connector) throws Exception {
                MBeanServerConnection connection = connector.getMBeanServerConnection();
                ServiceMixFacade facade = new RemoteJMXServiceMixFacade(connection);
                return callback.doWithServiceMixFacade(facade);
            }
        });
    }

    public List<ManagedEndpointMBean> getEndpoints() throws Exception {
        return execute(new ServiceMixFacadeCallback<List<ManagedEndpointMBean>>() {
           public List<ManagedEndpointMBean> doWithServiceMixFacade(ServiceMixFacade facade) throws Exception {
                return facade.getEndpoints();
            }
        });
    }

}