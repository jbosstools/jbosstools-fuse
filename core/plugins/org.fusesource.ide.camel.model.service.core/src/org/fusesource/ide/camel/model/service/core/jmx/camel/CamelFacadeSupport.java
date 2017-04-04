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

package org.fusesource.ide.camel.model.service.core.jmx.camel;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * @author lhein
 *
 */
public abstract class CamelFacadeSupport implements CamelJMXFacade {
    
    protected String camelContextManagementName;
    protected final MBeanServerConnection mBeanServer;

    protected CamelFacadeSupport(String camelContextManagementName, MBeanServerConnection mBeanServer) throws Exception {
        this.mBeanServer = mBeanServer;
        this.camelContextManagementName = camelContextManagementName;
    }

    protected MBeanServerConnection getMBeanServerConnection() throws Exception {
        return mBeanServer;
    }

    protected Set<ObjectInstance> queryNames(ObjectName name, QueryExp query) throws Exception {
        return getMBeanServerConnection().queryMBeans(name, query);
    }

    static public <T> T addGetId(Class<T> ic, final Object target, final String id) throws Exception {
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

	protected Object newProxyInstance(ObjectName objectName, Class<?> interfaceClass, boolean notificationBroadcaster) throws Exception {
        Object jmx_proxy = MBeanServerInvocationHandler.newProxyInstance(getMBeanServerConnection(), objectName, interfaceClass, notificationBroadcaster);
        return addGetId(interfaceClass, jmx_proxy, objectName.getCanonicalName());
    }

    /**
     * Finds all CamelContext's registered on a certain JMX-Server or, if a
     * JMX-BrokerName has been set, the broker with that name.
     *
     * @param connection not <code>null</code>
     * @param managementName to find a specific context by its management name
     * @return Set with ObjectName-elements
     */
    protected Set<ObjectName> findCamelContexts(MBeanServerConnection connection, String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName name;
        if (id != null) {
            name = new ObjectName("org.apache.camel:context=" + managementName + ",type=context,*");
        } else {
            name = new ObjectName("org.apache.camel:context=*,type=context,*");
        }
        Set<ObjectName> camels = connection.queryNames(name, null);
        return camels;
    }

    // CamelFacade
    //---------------------------------------------------------------

    @Override
    public List<CamelContextMBean> getCamelContexts() throws Exception {
        MBeanServerConnection connection = getMBeanServerConnection();
        Set<ObjectName> names = findCamelContexts(connection, null);

        List<CamelContextMBean> answer = new ArrayList<CamelContextMBean>();
        for (ObjectName on : names) {
            CamelContextMBean context = (CamelContextMBean) newProxyInstance(on, CamelContextMBean.class, true);
            answer.add(context);
        }
        return answer;
    }

    @Override
    public CamelContextMBean getCamelContext(String managementName) throws Exception {
        MBeanServerConnection connection = getMBeanServerConnection();

		Set<ObjectName> contexts = findCamelContexts(connection, managementName);
        if (contexts.size() == 0) {
            throw new IOException("No CamelContext could be found in the JMX.");
        }

        // we just take the first CamelContext as it matches the context id
		ObjectName name = contexts.iterator().next();
        CamelContextMBean mbean = (CamelContextMBean) newProxyInstance(name, CamelContextMBean.class, true);
        return mbean;
    }

    @Override
    public CamelFabricTracerMBean getFabricTracer(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=fabric,*");

        Set<ObjectInstance> names = queryNames(query, null);
        for (ObjectInstance on : names) {
            if (on.getClassName().equals("org.apache.camel.fabric.FabricTracer")) {
                CamelFabricTracerMBean tracer = (CamelFabricTracerMBean) newProxyInstance(on.getObjectName(), CamelFabricTracerMBean.class, true);
                return tracer;
            }
        }

        // tracer not found
        return null;
    }

	@Override
	public CamelBacklogTracerMBean getCamelTracer(String managementName) throws Exception {
		String id = managementName != null ? managementName : camelContextManagementName;

		ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=tracer,*");

		Set<ObjectInstance> names = queryNames(query, null);
		for (ObjectInstance on : names) {
			if (on.getClassName().equals("org.apache.camel.management.mbean.ManagedBacklogTracer")) {
				CamelBacklogTracerMBean tracer = (CamelBacklogTracerMBean) newProxyInstance(on.getObjectName(), CamelBacklogTracerMBean.class, true);
				return tracer;
			}
		}

		// tracer not found
		return null;
	}

    @Override
    public List<CamelComponentMBean> getComponents(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=components,*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<CamelComponentMBean> answer = new ArrayList<CamelComponentMBean>();
        for (ObjectInstance on : names) {
            CamelComponentMBean component = (CamelComponentMBean) newProxyInstance(on.getObjectName(), CamelComponentMBean.class, true);
            answer.add(component);
        }
        return answer;
    }

    @Override
    public List<CamelRouteMBean> getRoutes(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=routes,*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<CamelRouteMBean> answer = new ArrayList<CamelRouteMBean>();
        for (ObjectInstance on : names) {
            CamelRouteMBean route;
            if ("org.apache.camel.management.mbean.ManagedSuspendableRoute".equals(on.getClassName())) {
                route = (CamelRouteMBean) newProxyInstance(on.getObjectName(), CamelSuspendableRouteMBean.class, true);
            } else {
                route = (CamelRouteMBean) newProxyInstance(on.getObjectName(), CamelRouteMBean.class, true);
            }
            answer.add(route);
        }
        return answer;
    }

    @Override
    public List<CamelEndpointMBean> getEndpoints(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=endpoints,*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<CamelEndpointMBean> answer = new ArrayList<CamelEndpointMBean>();
        for (ObjectInstance on : names) {
            CamelEndpointMBean endpoint;
            if ("org.apache.camel.management.mbean.ManagedBrowsableEndpoint".equals(on.getClassName()) || 
            	"org.apache.camel.component.seda.SedaEndpoint".equals(on.getClassName())) {
                endpoint = (CamelEndpointMBean) newProxyInstance(on.getObjectName(), CamelBrowsableEndpointMBean.class, true);
            } else if (on.getClassName().startsWith("org.apache.camel.component.jms")) {
                // special for JMS endpoints as they are browsable as well
                endpoint = (CamelEndpointMBean) newProxyInstance(on.getObjectName(), CamelBrowsableEndpointMBean.class, true);
            } else {
                endpoint = (CamelEndpointMBean) newProxyInstance(on.getObjectName(), CamelEndpointMBean.class, true);
            }
            answer.add(endpoint);
        }
        return answer;
    }

    @Override
    public List<CamelConsumerMBean> getConsumers(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=consumers,*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<CamelConsumerMBean> answer = new ArrayList<CamelConsumerMBean>();
        for (ObjectInstance on : names) {
            CamelConsumerMBean consumer;
            if ("org.apache.camel.management.mbean.ManagedScheduledPollConsumer".equals(on.getClassName())) {
                consumer = (CamelConsumerMBean) newProxyInstance(on.getObjectName(), CamelScheduledPollConsumerMBean.class, true);
            } else {
                consumer = (CamelConsumerMBean) newProxyInstance(on.getObjectName(), CamelConsumerMBean.class, true);
            }
            answer.add(consumer);
        }
        return answer;
    }

    @Override
    public List<CamelProcessorMBean> getProcessors(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=processors,*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<CamelProcessorMBean> answer = new ArrayList<CamelProcessorMBean>();
        for (ObjectInstance on : names) {
            CamelProcessorMBean processor;
            if ("org.apache.camel.management.mbean.ManagedSendProcessor".equals(on.getClassName())) {
                processor = (CamelProcessorMBean) newProxyInstance(on.getObjectName(), CamelSendProcessorMBean.class, true);
            } else if ("org.apache.camel.management.mbean.ManagedDelayer".equals(on.getClassName())) {
                processor = (CamelProcessorMBean) newProxyInstance(on.getObjectName(), CamelDelayProcessorMBean.class, true);
            } else if ("org.apache.camel.management.mbean.ManagedThrottler".equals(on.getClassName())) {
                processor = (CamelProcessorMBean) newProxyInstance(on.getObjectName(), CamelThrottleProcessorMBean.class, true);
            } else {
                processor = (CamelProcessorMBean) newProxyInstance(on.getObjectName(), CamelProcessorMBean.class, true);
            }
            answer.add(processor);
        }
        return answer;
    }

    @Override
    public List<CamelThreadPoolMBean> getThreadPools(String managementName) throws Exception {
        String id = managementName != null ? managementName : camelContextManagementName;

        ObjectName query = ObjectName.getInstance("org.apache.camel:context=" + id + ",type=threadpools,*");

        Set<ObjectInstance> names = queryNames(query, null);
        List<CamelThreadPoolMBean> answer = new ArrayList<CamelThreadPoolMBean>();
        for (ObjectInstance on : names) {
            CamelThreadPoolMBean pool = (CamelThreadPoolMBean) newProxyInstance(on.getObjectName(), CamelThreadPoolMBean.class, true);
            answer.add(pool);
        }
        return answer;
    }

    @Override
    public String dumpRoutesStatsAsXml(String managementName) throws Exception {
        CamelContextMBean context = getCamelContext(managementName);
        try {
            return context.dumpRoutesStatsAsXml(false, true);
        } catch (Exception e) {
            // ignore as the method may not be available in older Camel releases
        }

        // fallback and use backwards compatible which is slower
        return CamelBackwardsCompatibleSupport.dumpRoutesStatsAsXml(this, managementName);
    }
}
