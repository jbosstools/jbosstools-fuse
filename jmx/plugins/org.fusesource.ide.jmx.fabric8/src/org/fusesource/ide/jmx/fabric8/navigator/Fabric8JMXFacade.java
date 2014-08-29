/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.navigator;

import io.fabric8.api.ProfileStatus;
import io.fabric8.api.jmx.FabricManagerMBean;
import io.fabric8.insight.log.LogEvent;
import io.fabric8.insight.log.LogResults;
import io.fabric8.insight.log.support.LogQuerySupportMBean;
import io.fabric8.service.JmxTemplateSupport;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.utils.IFabric8JsonConstants;
import org.fusesource.ide.jmx.fabric8.utils.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public class Fabric8JMXFacade {
	
	/**
	 * A callback API for working with FabricManagerMBean
	 */
	public interface FabricManagerMBeanCallback<T> {

		T doWithFabricManagerMBean(FabricManagerMBean mbean) throws Exception;
	}
	
	/**
	 * A callback API for working with LogQuerySupportMBean
	 */
	public interface LogQuerySupportMBeanCallback<T> {

		T doWithLogQuerySupportMBean(LogQuerySupportMBean mbean) throws Exception;
	}
	
	private final JmxPluginJmxTemplate template;
	private ObjectName fabricObjectName;
	private ObjectName insightObjectName;

	/**
	 * 
	 * @param template
	 * @throws MalformedObjectNameException
	 */
	public Fabric8JMXFacade(JmxPluginJmxTemplate template) throws MalformedObjectNameException {
		this.template = template;
		fabricObjectName = new ObjectName("io.fabric8:type=Fabric,*");
		insightObjectName = new ObjectName("io.fabric8.insight:type=LogQuery,*");
	}
	
	
	/**
	 * Executes a JMX operation on a FabricManagerMBean
	 */
	public <T> T execute(final FabricManagerMBeanCallback<T> callback) {
		return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
			@Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {
					if (!ex.getMessage().contentEquals("Connection closed"))
						Fabric8JMXPlugin.getLogger().warning(ex);
					return null;
				}
				final Set<ObjectName> queryNames = connection.queryNames(fabricObjectName, null);
				for (ObjectName fabric8ObjectName : queryNames) {
					FabricManagerMBean fabricMBean = MBeanServerInvocationHandler.newProxyInstance(connection, fabric8ObjectName, FabricManagerMBean.class, true);
					return callback.doWithFabricManagerMBean(fabricMBean);
				}
				return null;
			}
		});
	}
	
	/**
	 * Executes a JMX operation on a LogQuerySupportMBean
	 */
	public <T> T execute(final LogQuerySupportMBeanCallback<T> callback) {
		return template.execute(new JmxTemplateSupport.JmxConnectorCallback<T>() {
			@Override
			public T doWithJmxConnector(JMXConnector connector) throws Exception {
				MBeanServerConnection connection = null;
				try { 
					// No need to report a closed or not yet established connection.
					if ((connection = connector.getMBeanServerConnection()) == null)
						return null;
				} catch (IOException ex) {
					if (!ex.getMessage().contentEquals("Connection closed"))
						Fabric8JMXPlugin.getLogger().warning(ex);
					return null;
				}
				final Set<ObjectName> queryNames = connection.queryNames(insightObjectName, null);
				for (ObjectName insightObjectName : queryNames) {
					LogQuerySupportMBean logQuerySupportMBean = MBeanServerInvocationHandler.newProxyInstance(connection, insightObjectName, LogQuerySupportMBean.class, true);
					return callback.doWithLogQuerySupportMBean(logQuerySupportMBean);
				}
				return null;
			}
		});
	}
	
	/**
	 * Lists the versions
	 */
	public List<Map<String, Object>> listVersions() throws Exception {
		return execute(new FabricManagerMBeanCallback<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> doWithFabricManagerMBean(FabricManagerMBean bean) throws Exception {
				return bean.versions();
			}
		});
	}
	
	/**
	 * Lists the containers
	 */
	public List<Map<String, Object>> listContainers() throws Exception {
		return execute(new FabricManagerMBeanCallback<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> doWithFabricManagerMBean(FabricManagerMBean bean) throws Exception {
				return bean.containers();
			}
		});
	}
	
	/**
	 * queries a profile
	 */
	public Map<String, Object> queryProfile(final String id, final String version) throws Exception {
		return execute(new FabricManagerMBeanCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doWithFabricManagerMBean(FabricManagerMBean bean) throws Exception {
				return bean.getProfile(version, id);
			}
		});
	}
	
	/**
	 * queries the web console url
	 */
	public String queryWebConsoleUrl() throws Exception {
		return execute(new FabricManagerMBeanCallback<String>() {
			@Override
			public String doWithFabricManagerMBean(FabricManagerMBean bean) throws Exception {
				return bean.webConsoleUrl();
			}
		});
	}
	
	/**
	 * queries the web console url
	 */
	public Collection<ProfileStatus> queryProfileStatusMap() throws Exception {
		return execute(new FabricManagerMBeanCallback<Collection<ProfileStatus>>() {
			@Override
			public Collection<ProfileStatus> doWithFabricManagerMBean(FabricManagerMBean bean) throws Exception {
				return bean.fabricStatus().getProfileStatusMap().values();
			}
		});
	}
	
	/**
	 * queries the logs
	 */
	public Collection<LogEvent> queryLogs(final int count) throws Exception {
		return execute(new LogQuerySupportMBeanCallback<Collection<LogEvent>>() {
			@Override
			public Collection<LogEvent> doWithLogQuerySupportMBean(LogQuerySupportMBean bean) throws Exception {
				String json = bean.getLogEvents(count);
				final ModelNode rootNode = JsonHelper.getModelNode(json);
				final String host = JsonHelper.getAsString(rootNode, IFabric8JsonConstants.PROPERTY_LOGRESULTS_HOST);
				final Long from = JsonHelper.getAsLong(rootNode, IFabric8JsonConstants.PROPERTY_LOGRESULTS_FROM);
				final Long to = JsonHelper.getAsLong(rootNode, IFabric8JsonConstants.PROPERTY_LOGRESULTS_TO);
				
				LogResults res = new LogResults();
				res.setHost(host);
				res.setFromTimestamp(from);
				res.setToTimestamp(to);
				
				final List<ModelNode> events = JsonHelper.getAsList(rootNode, IFabric8JsonConstants.PROPERTY_LOGRESULTS_EVENTS);
				for (ModelNode ev : events) {
					String className = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_CLASS);
					String container = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_CONTAINER);
					String[] exception = JsonHelper.getAsStringArray(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_EXCEPTION);
					String fileName = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_FILE);
					String eventhost = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_HOST);
					String logLevel = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_LOGLEVEL);
					String lineNo = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_LINE);
					String logger = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_LOGGER);
					String logMessage = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_MESSAGE);
					String methodName = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_METHOD);
					Long seq = JsonHelper.getAsLong(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_SEQ);
					String threadName = JsonHelper.getAsString(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_THREAD);
					Date eventTimestamp = new Date(JsonHelper.getAsLong(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_TIMESTAMP));
					Map<String, String> propertiesMap = JsonHelper.getAsPropertiesMap(ev, IFabric8JsonConstants.PROPERTY_LOGEVENT_PROPERTIES);
					
					LogEvent event = new LogEvent();
					event.setClassName(className);
					event.setContainerName(container);
					event.setException(exception);
					event.setFileName(fileName);
					event.setHost(Strings.isBlank(eventhost) ? host : eventhost);
					event.setLevel(logLevel);
					event.setLineNumber(lineNo);
					event.setLogger(logger);
					event.setMessage(logMessage);
					event.setMethodName(methodName);
					event.setSeq(seq);
					event.setThread(threadName);
					event.setTimestamp(eventTimestamp);
					event.setProperties(propertiesMap);
					
					res.addEvent(event);
				}
				
				return res.getEvents();
			}
		});
	}
}
