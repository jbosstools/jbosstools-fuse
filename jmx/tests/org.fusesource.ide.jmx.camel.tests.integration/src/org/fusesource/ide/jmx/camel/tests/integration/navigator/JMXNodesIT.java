/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.camel.tests.integration.navigator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.jmx.camel.jmx.content.navigator.providers.CamelNodeContentProvider;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;
import org.fusesource.ide.jmx.camel.navigator.CamelContextsNode;
import org.fusesource.ide.jmx.camel.navigator.EndpointNode;
import org.fusesource.ide.jmx.camel.navigator.EndpointSchemeNode;
import org.fusesource.ide.jmx.camel.navigator.EndpointsNode;
import org.fusesource.ide.jmx.camel.navigator.ProcessorNode;
import org.fusesource.ide.jmx.camel.navigator.RouteNode;
import org.fusesource.ide.jmx.camel.navigator.RoutesNode;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.tests.integration.remote.debug.ProjectWithDebugAvailableDeployedHelper;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.providers.DefaultConnectionProvider;
import org.jboss.tools.jmx.core.providers.DefaultConnectionWrapper;
import org.jboss.tools.jmx.core.providers.MBeanServerConnectionDescriptor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class JMXNodesIT {

	private static ProjectWithDebugAvailableDeployedHelper projectWithDebugAvailableDeployedHelper;
	private static String initialSwitchPerspectiveValue;
	private Set<DefaultConnectionWrapper> jmxConnections = new HashSet<>();
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		initialSwitchPerspectiveValue = DebugUIPlugin.getDefault().getPreferenceStore().getString(IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE);
		DebugUIPlugin.getDefault().getPreferenceStore().setValue(IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE, "always");
		
		projectWithDebugAvailableDeployedHelper = new ProjectWithDebugAvailableDeployedHelper(JMXNodesIT.class.getSimpleName());
		projectWithDebugAvailableDeployedHelper.start();
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		projectWithDebugAvailableDeployedHelper.clean();
		DebugUIPlugin.getDefault().getPreferenceStore().setValue(IInternalDebugUIConstants.PREF_SWITCH_TO_PERSPECTIVE, initialSwitchPerspectiveValue);
	}
	
	@Before
	public void setup(){
		jmxConnections.clear();
	}
	
	@After
	public void tearDown() throws IOException{
		for(IConnectionWrapper jmxConnection : jmxConnections){
			jmxConnection.disconnect();
			ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID).removeConnection(jmxConnection);
		}
	}
	
	@Test
	public void testAllNodesAvailable() throws Exception {
		DefaultConnectionWrapper jmxConnection = initializeConnection("JMX Connection for Remote connection test all nodes available");
		CamelNodeContentProvider camelNodeContentProvider = new CamelNodeContentProvider();
		List<NodeSupport> traverseNodes = traverseNodes(jmxConnection, camelNodeContentProvider);
		
		checkEqualsAndHashCodeAreFine(traverseNodes);
	}

	private void checkEqualsAndHashCodeAreFine(List<NodeSupport> traverseNodes) {
		assertThat(traverseNodes.stream().distinct().count()).isEqualTo(traverseNodes.size());
		assertThat(traverseNodes.stream().map(Object::hashCode).distinct().count()).isEqualTo(traverseNodes.size());
	}

	private List<NodeSupport> traverseNodes(DefaultConnectionWrapper jmxConnection, CamelNodeContentProvider camelNodeContentProvider) {
		List<NodeSupport> traversedNodes = new ArrayList<>();
		CamelContextsNode camelContextsNode = (CamelContextsNode)camelNodeContentProvider.getChildren(jmxConnection)[0];
		traversedNodes.add(camelContextsNode);
		CamelContextNode camelContextNode = (CamelContextNode)camelNodeContentProvider.getChildren(camelContextsNode)[0];
		traversedNodes.add(camelContextNode);
		assertThat(camelContextNode.toString()).isEqualTo("contextToTestJMX");
		EndpointsNode endpointsNode = (EndpointsNode)camelNodeContentProvider.getChildren(camelContextNode)[0];
		traversedNodes.add(endpointsNode);
		EndpointSchemeNode fileEndpointSchemeNode = (EndpointSchemeNode)camelNodeContentProvider.getChildren(endpointsNode)[0];
		assertThat(fileEndpointSchemeNode.toString()).isEqualTo("file");
		traversedNodes.add(fileEndpointSchemeNode);
		EndpointNode fileEndpointNode = (EndpointNode)camelNodeContentProvider.getChildren(fileEndpointSchemeNode)[0];
		assertThat(fileEndpointNode.toString()).isEqualTo("directoryName");
		traversedNodes.add(fileEndpointNode);
		RoutesNode routesNode = (RoutesNode)camelNodeContentProvider.getChildren(camelContextNode)[1];
		traversedNodes.add(routesNode);
		RouteNode routeNode = (RouteNode)camelNodeContentProvider.getChildren(routesNode)[0];
		assertThat(routeNode.toString()).isEqualTo("routeToTestJMX");
		traversedNodes.add(routeNode);
		ProcessorNode fileProcessorNode = (ProcessorNode) camelNodeContentProvider.getChildren(routeNode)[0];
		traversedNodes.add(fileProcessorNode);
		assertThat(fileProcessorNode.toString()).isEqualTo("file:directoryName");
		ProcessorNode logProcessorNode = (ProcessorNode) camelNodeContentProvider.getChildren(fileProcessorNode)[0];
		traversedNodes.add(logProcessorNode);
		assertThat(logProcessorNode.toString()).isEqualTo("Log logToTestJMX");
		return traversedNodes;
	}
	
	@Test
	public void testRefreshprovidesEqualityOnDifferentObjects() throws Exception {
		DefaultConnectionWrapper jmxConnection = initializeConnection("JMX Connection for Remote connection test refresh");
		CamelNodeContentProvider camelNodeContentProvider = new CamelNodeContentProvider();
		List<NodeSupport> traversedNodes = traverseNodes(jmxConnection, camelNodeContentProvider);
		
		CamelContextsNode camelContextsNode = (CamelContextsNode) traversedNodes.get(0);
		camelContextsNode.refresh();
		
		List<NodeSupport> traversedNodesAfterRefresh = traverseNodes(jmxConnection, camelNodeContentProvider);
		
		for(int i = 0; i < traversedNodes.size() ; i++){
			checkEqualityWithDifferentReferences(traversedNodes.get(i), traversedNodesAfterRefresh.get(i));
		}
		
	}
	
	@Test
	public void testNotEqualOnDifferentConnection() throws Exception {
		DefaultConnectionWrapper jmxConnection1 = initializeConnection("JMX Connection for Remote connection test 1");
		DefaultConnectionWrapper jmxConnection2 = initializeConnection("JMX Connection for Remote connection test 2");
		CamelNodeContentProvider camelNodeContentProvider = new CamelNodeContentProvider();
		List<NodeSupport> traversedNodes1 = traverseNodes(jmxConnection1, camelNodeContentProvider);
		List<NodeSupport> traversedNodes2 = traverseNodes(jmxConnection2, camelNodeContentProvider);
		
		for(int i = 0; i < traversedNodes1.size() ; i++){
			checkNotEqualityWithDifferentReferences(traversedNodes1.get(i), traversedNodes2.get(i));
		}
	}

	private void checkNotEqualityWithDifferentReferences(NodeSupport node1, NodeSupport node2) {
		assertThat(node1).isNotEqualTo(node2);
		assertThat(node1 != node2).isTrue();
		
	}

	private void checkEqualityWithDifferentReferences(Object node, Object nodeAfterRefresh) {
		assertThat(nodeAfterRefresh).isEqualTo(node);
		assertThat(nodeAfterRefresh != node).isTrue();
	}
	
	private DefaultConnectionWrapper initializeConnection(String connectioName) throws MalformedURLException, IOException, CoreException {
		MBeanServerConnectionDescriptor descriptor = new MBeanServerConnectionDescriptor(connectioName, ICamelDebugConstants.DEFAULT_JMX_URI, null, null);
		DefaultConnectionWrapper jmxConnection = new DefaultConnectionWrapper(descriptor);
		IConnectionProvider provider = ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID);
		provider.getConnections();
		provider.addConnection(jmxConnection);
		jmxConnection.connect();
		jmxConnection.loadRoot(new NullProgressMonitor());
		jmxConnections.add(jmxConnection);
		return jmxConnection;
	}
	
}
