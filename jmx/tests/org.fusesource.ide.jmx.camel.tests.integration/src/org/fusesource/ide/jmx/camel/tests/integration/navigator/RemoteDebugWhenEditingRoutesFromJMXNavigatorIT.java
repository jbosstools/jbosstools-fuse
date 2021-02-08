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
package org.fusesource.ide.jmx.camel.tests.integration.navigator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.fusesource.ide.jmx.camel.jmx.content.navigator.providers.CamelNodeContentProvider;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;
import org.fusesource.ide.jmx.camel.navigator.CamelContextsNode;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.tests.integration.remote.debug.ProjectWithDebugAvailableDeployedHelper;
import org.fusesource.ide.launcher.tests.integration.remote.debug.RemoteCamelDebugTester;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.providers.DefaultConnectionProvider;
import org.jboss.tools.jmx.core.providers.DefaultConnectionWrapper;
import org.jboss.tools.jmx.core.providers.MBeanServerConnectionDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RemoteDebugWhenEditingRoutesFromJMXNavigatorIT {
	
	private ProjectWithDebugAvailableDeployedHelper projectWithDebugAvailableDeployedHelper;

	private DefaultConnectionWrapper jmxConnection;
	
	@Before
	public void setup() throws Exception {
		projectWithDebugAvailableDeployedHelper = new ProjectWithDebugAvailableDeployedHelper(RemoteDebugWhenEditingRoutesFromJMXNavigatorIT.class.getSimpleName());
		projectWithDebugAvailableDeployedHelper.start();
	}
	
	@After
	public void tearDown() throws Exception {
		if(jmxConnection != null){
			jmxConnection.disconnect();
			ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID).removeConnection(jmxConnection);
		}
		projectWithDebugAvailableDeployedHelper.clean();
	}
	
	@Test
	public void testRemoteDebugScenario() throws Exception {
		initializeConnection();
		CamelNodeContentProvider camelNodeContentProvider = new CamelNodeContentProvider();
		CamelContextsNode camelContextsNode = (CamelContextsNode)camelNodeContentProvider.getChildren(jmxConnection)[0];
		Object[] contextsChildren = camelNodeContentProvider.getChildren(camelContextsNode);
		assertThat(Arrays.asList(contextsChildren).stream().map(Object::toString).collect(Collectors.toList())).containsExactly("contextToTestJMX");
		CamelContextNode camelContextNode = (CamelContextNode)camelNodeContentProvider.getChildren(camelContextsNode)[0];
		
		ILaunch remoteDebuglaunch = camelContextNode.editRoutes();
		String camelFilePath = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(remoteDebuglaunch.getLaunchConfiguration());
		IFile contextFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(camelFilePath));
		new RemoteCamelDebugTester(remoteDebuglaunch, contextFile.getProject(), contextFile).test();
	}

	private DefaultConnectionWrapper initializeConnection() throws IOException, CoreException {
		MBeanServerConnectionDescriptor descriptor = new MBeanServerConnectionDescriptor("JMX Connection for Remote Debug scenario test", ICamelDebugConstants.DEFAULT_JMX_URI, null, null);
		jmxConnection = new DefaultConnectionWrapper(descriptor);
		IConnectionProvider provider = ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID);
		provider.getConnections();
		provider.addConnection(jmxConnection);
		jmxConnection.connect();
		jmxConnection.loadRoot(new NullProgressMonitor());
		return jmxConnection;
	}

}
