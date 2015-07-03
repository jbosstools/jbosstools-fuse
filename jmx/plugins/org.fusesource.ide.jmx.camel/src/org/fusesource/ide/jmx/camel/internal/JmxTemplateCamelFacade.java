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

package org.fusesource.ide.jmx.camel.internal;

import org.apache.camel.api.management.mbean.ManagedBacklogTracerMBean;
import org.fusesource.ide.jmx.commons.JmxTemplateSupport;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.util.List;

/**
 * @author lhein
 *
 */
public class JmxTemplateCamelFacade implements CamelFacade {
    private final JmxTemplateSupport template;

    public JmxTemplateCamelFacade(JmxTemplateSupport template) {
        this.template = template;
    }

    /**
     * Executes a JMX operation on a BrokerFacade
     */
    public <T> T execute(final CamelFacadeCallback<T> callback) {
        return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
            public T doWithJmxConnector(JMXConnector connector) throws Exception {
                MBeanServerConnection connection = connector.getMBeanServerConnection();
                CamelFacade camelFacade = new RemoteJMXCamelFacade(connection);
                return callback.doWithCamelFacade(camelFacade);
            }
        });
    }

    public List<CamelContextMBean> getCamelContexts() throws Exception {
        return execute(new CamelFacadeCallback<List<CamelContextMBean>>() {
           public List<CamelContextMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getCamelContexts();
            }
        });
    }

    public CamelContextMBean getCamelContext(final String managementName) {
        return execute(new CamelFacadeCallback<CamelContextMBean>() {
           public CamelContextMBean doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getCamelContext(managementName);
            }
        });
    }

    public CamelFabricTracerMBean getFabricTracer(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<CamelFabricTracerMBean>() {
           public CamelFabricTracerMBean doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getFabricTracer(managementName);
            }
        });
    }

	public ManagedBacklogTracerMBean getCamelTracer(final String managementName) throws Exception {
		return execute(new CamelFacadeCallback<ManagedBacklogTracerMBean>() {
			public ManagedBacklogTracerMBean doWithCamelFacade(CamelFacade camel) throws Exception {
				return camel.getCamelTracer(managementName);
			}
		});
	}

    public List<CamelComponentMBean> getComponents(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelComponentMBean>>() {
           public List<CamelComponentMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getComponents(managementName);
            }
        });
    }

    public List<CamelRouteMBean> getRoutes(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelRouteMBean>>() {
           public List<CamelRouteMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getRoutes(managementName);
            }
        });
    }

    public List<CamelEndpointMBean> getEndpoints(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelEndpointMBean>>() {
           public List<CamelEndpointMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getEndpoints(managementName);
            }
        });
    }

    public List<CamelConsumerMBean> getConsumers(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelConsumerMBean>>() {
           public List<CamelConsumerMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getConsumers(managementName);
            }
        });
    }

    public List<CamelProcessorMBean> getProcessors(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelProcessorMBean>>() {
           public List<CamelProcessorMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getProcessors(managementName);
            }
        });
    }

    public List<CamelThreadPoolMBean> getThreadPools(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelThreadPoolMBean>>() {
           public List<CamelThreadPoolMBean> doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.getThreadPools(managementName);
            }
        });
    }

    public String dumpRoutesStatsAsXml(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<String>() {
            public String doWithCamelFacade(CamelFacade camel) throws Exception {
                return camel.dumpRoutesStatsAsXml(managementName);
            }
        });
    }
}
