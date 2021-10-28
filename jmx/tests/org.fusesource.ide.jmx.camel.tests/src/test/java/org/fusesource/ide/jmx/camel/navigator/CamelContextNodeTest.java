/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.camel.navigator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.providers.DefaultConnectionProvider;
import org.jboss.tools.jmx.core.tree.Root;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelContextNodeTest {
	
	@Mock
	private CamelJMXFacade facade;
	private CamelContextMBean camelContext1;
	private CamelContextMBean camelContext2;
	private IConnectionWrapper connection1;
	private IConnectionWrapper connection2;
	@Mock
	private IConnectionProvider provider;
	
	@Before
	public void setup() throws CoreException{
		String connectionName1 = "Test Connection 1";
		String connectionName2 = "Test Connection 2";
		connection1 = createConnection(connectionName1);
		connection2 = createConnection(connectionName2);
		doReturn(provider).when(connection1).getProvider();
		doReturn(provider).when(connection2).getProvider();
		doReturn(connectionName1).when(provider).getName(connection1);
		doReturn(connectionName2).when(provider).getName(connection2);
		
		camelContext1 = new CamelContextMBean() {
			
			@Override
			public void suspend() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void stop() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void start() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTracing(Boolean tracing) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTimeout(long timeout) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTimeUnit(TimeUnit timeUnit) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setShutdownNowOnTimeout(boolean shutdownNowOnTimeout) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void sendStringBody(String endpointUri, String body) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void sendBodyAndHeaders(String endpointUri, Object body, Map<String, Object> headers) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void sendBody(String endpointUri, Object body) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void resume() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Object requestStringBody(String endpointUri, String body) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object requestBodyAndHeaders(String endpointUri, Object body, Map<String, Object> headers) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object requestBody(String endpointUri, Object body) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int removeEndpoints(String pattern) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean isShutdownNowOnTimeout() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getUptime() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Boolean getTracing() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long getTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public TimeUnit getTimeUnit() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getState() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, String> getProperties() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getManagementName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Integer getInflightExchanges() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getCamelVersion() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getCamelId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String dumpRoutesStatsAsXml(boolean fullStats, boolean includeProcessors) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String dumpRoutesAsXml() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean createEndpoint(String uri) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void addOrUpdateRoutesFromXml(String xml) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	private IConnectionWrapper createConnection(String connectionName) throws CoreException {
		Map<String, String> map = new HashMap<>();
		map.put(DefaultConnectionProvider.ID, connectionName);
		map.put(DefaultConnectionProvider.URL, "service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
		map.put(DefaultConnectionProvider.USERNAME, "");
		map.put(DefaultConnectionProvider.PASSWORD, "");
		return spy(new DefaultConnectionProvider().createConnection(map));
	}

	@Test
	public void testEquals() throws Exception {
		CamelContextNode camelContextNode1 = new CamelContextNode(new CamelContextsNode(new Root(connection1), facade), facade, camelContext1);
		CamelContextNode camelContextNode2 = new CamelContextNode(new CamelContextsNode(new Root(connection1), facade), facade, camelContext1);
		assertThat(camelContextNode1).isEqualTo(camelContextNode2);
	}
	
	@Test
	public void testNotEqualsInDifferentConnectionprovider() throws Exception {
		CamelContextNode camelContextNode1 = new CamelContextNode(new CamelContextsNode(new Root(connection1), facade), facade, camelContext1);
		CamelContextNode camelContextNode2 = new CamelContextNode(new CamelContextsNode(new Root(connection2), facade), facade, camelContext1);
		assertThat(camelContextNode1).isNotEqualTo(camelContextNode2);
	}

}
