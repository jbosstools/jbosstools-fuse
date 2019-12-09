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
            @Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
                MBeanServerConnection connection = connector.getMBeanServerConnection();
                CamelJMXFacade camelFacade = new RemoteJMXCamelFacade(connection);
                return callback.doWithCamelFacade(camelFacade);
            }
        });
    }

    @Override
	public List<CamelContextMBean> getCamelContexts() throws Exception {
        return execute(new CamelFacadeCallback<List<CamelContextMBean>>() {
           @Override
		public List<CamelContextMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getCamelContexts();
            }
        });
    }

    @Override
	public CamelContextMBean getCamelContext(final String managementName) {
        return execute(new CamelFacadeCallback<CamelContextMBean>() {
           @Override
		public CamelContextMBean doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getCamelContext(managementName);
            }
        });
    }

    @Override
	public CamelFabricTracerMBean getFabricTracer(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<CamelFabricTracerMBean>() {
           @Override
		public CamelFabricTracerMBean doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getFabricTracer(managementName);
            }
        });
    }

	@Override
	public CamelBacklogTracerMBean getCamelTracer(final String managementName) throws Exception {
		return execute(new CamelFacadeCallback<CamelBacklogTracerMBean>() {
			@Override
			public CamelBacklogTracerMBean doWithCamelFacade(CamelJMXFacade camel) throws Exception {
				return camel.getCamelTracer(managementName);
			}
		});
	}

    @Override
	public List<CamelComponentMBean> getComponents(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelComponentMBean>>() {
           @Override
		public List<CamelComponentMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getComponents(managementName);
            }
        });
    }

    @Override
	public List<CamelRouteMBean> getRoutes(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelRouteMBean>>() {
           @Override
		public List<CamelRouteMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getRoutes(managementName);
            }
        });
    }

    @Override
	public List<CamelEndpointMBean> getEndpoints(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelEndpointMBean>>() {
           @Override
		public List<CamelEndpointMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getEndpoints(managementName);
            }
        });
    }

    @Override
	public List<CamelConsumerMBean> getConsumers(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelConsumerMBean>>() {
           @Override
		public List<CamelConsumerMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getConsumers(managementName);
            }
        });
    }

    @Override
	public List<CamelProcessorMBean> getProcessors(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelProcessorMBean>>() {
           @Override
		public List<CamelProcessorMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getProcessors(managementName);
            }
        });
    }

    @Override
	public List<CamelThreadPoolMBean> getThreadPools(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<List<CamelThreadPoolMBean>>() {
           @Override
		public List<CamelThreadPoolMBean> doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.getThreadPools(managementName);
            }
        });
    }

    @Override
	public String dumpRoutesStatsAsXml(final String managementName) throws Exception {
        return execute(new CamelFacadeCallback<String>() {
            @Override
			public String doWithCamelFacade(CamelJMXFacade camel) throws Exception {
                return camel.dumpRoutesStatsAsXml(managementName);
            }
        });
    }
}
