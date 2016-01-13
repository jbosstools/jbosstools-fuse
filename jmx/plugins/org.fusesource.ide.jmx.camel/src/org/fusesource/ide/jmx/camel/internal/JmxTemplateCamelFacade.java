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

import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelBacklogTracerMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelComponentMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelConsumerMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelEndpointMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelFabricTracerMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelFacadeCallback;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelProcessorMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelRouteMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelThreadPoolMBean;
import org.fusesource.ide.jmx.commons.JmxTemplateSupport;

/**
 * @author lhein
 *
 */
public class JmxTemplateCamelFacade implements CamelJMXFacade {
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
                CamelJMXFacade camelFacade = new RemoteJMXCamelFacade(connection);
                return callback.doWithCamelFacade(camelFacade);
            }
        });
    }

    public List<CamelContextMBean> getCamelContexts() throws Exception {
        return execute(new CamelFacadeCallback<List<CamelContextMBean>>() {
           public List<CamelContextMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getCamelContexts();
            }
        });
    }

    public CamelContextMBean getCamelContext(final String managementName) {
        return execute(new CamelFacadeCallback<CamelContextMBean>() {
           public CamelContextMBean doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getCamelContext(managementName);
            }
        });
    }

    public CamelFabricTracerMBean getFabricTracer(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<CamelFabricTracerMBean>() {
           public CamelFabricTracerMBean doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getFabricTracer(managementName);
            }
        });
    }

	public CamelBacklogTracerMBean getCamelTracer(final String managementName) throws Exception {
		return execute(new CamelFacadeCallback<CamelBacklogTracerMBean>() {
			public CamelBacklogTracerMBean doWithCamelFacade(CamelJMXFacade camel) throws Exception {
				return camel.getCamelTracer(managementName);
			}
		});
	}

    public List<CamelComponentMBean> getComponents(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelComponentMBean>>() {
           public List<CamelComponentMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getComponents(managementName);
            }
        });
    }

    public List<CamelRouteMBean> getRoutes(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelRouteMBean>>() {
           public List<CamelRouteMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getRoutes(managementName);
            }
        });
    }

    public List<CamelEndpointMBean> getEndpoints(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelEndpointMBean>>() {
           public List<CamelEndpointMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getEndpoints(managementName);
            }
        });
    }

    public List<CamelConsumerMBean> getConsumers(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelConsumerMBean>>() {
           public List<CamelConsumerMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getConsumers(managementName);
            }
        });
    }

    public List<CamelProcessorMBean> getProcessors(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelProcessorMBean>>() {
           public List<CamelProcessorMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getProcessors(managementName);
            }
        });
    }

    public List<CamelThreadPoolMBean> getThreadPools(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelThreadPoolMBean>>() {
           public List<CamelThreadPoolMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getThreadPools(managementName);
            }
        });
    }

    public String dumpRoutesStatsAsXml(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<String>() {
            public String doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.dumpRoutesStatsAsXml(managementName);
            }
        });
    }
}
