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
package org.fusesource.ide.launcher.tests.integration.remote.debug;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.jolokia.JolokiaConnectionWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class RemoteCamelLaunchConfigurationDelegateOverJolokiaIT extends AbstractRemoteCamelLaunchConfigurationDelegate {
	
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	private final static String JOLOKIA_PROVIDER_ID = "org.jboss.tools.jmx.jolokia.JolokiaConnectionProvider";
	private IConnectionWrapper jolokiaConnection;

	private Path jolokiaAgentPath;
	private final static int JOLOKIA_PORT = SocketUtil.findFreePort();
	private final static String JOLOKIA_URL = "http://localhost:"+JOLOKIA_PORT+"/jolokia/";
	
	@Before
	@Override
	public void setup() throws Exception {
		createJBossToolsJolokiaConnectionWrapper();
		
		String jarName = "jolokia-jvm-1.3.6-agent.jar";
		jolokiaAgentPath = new File(tmp.getRoot(), jarName).toPath();
		Files.copy(RemoteCamelLaunchConfigurationDelegateOverJolokiaIT.class.getResourceAsStream("/lib/"+jarName), jolokiaAgentPath, StandardCopyOption.REPLACE_EXISTING);
		
		super.setup();
		
		waitForJolokia();
	}
	
	protected void createJBossToolsJolokiaConnectionWrapper() throws CoreException {
		IConnectionProvider provider = ExtensionManager.getProvider(JOLOKIA_PROVIDER_ID);
		Map<String,Object> map = new HashMap<>();
		map.put(JolokiaConnectionWrapper.ID, RemoteCamelLaunchConfigurationDelegateOverJolokiaIT.class.getName());
		map.put(JolokiaConnectionWrapper.URL, JOLOKIA_URL);
		map.put(JolokiaConnectionWrapper.GET_OR_POST, "POST");
		map.put(JolokiaConnectionWrapper.IGNORE_SSL_ERRORS, true);
		map.put(JolokiaConnectionWrapper.HEADERS, Collections.EMPTY_MAP);
		jolokiaConnection = provider.createConnection(map);
		provider.getConnections();//workaround waiting for JBoss Tools 4.4.4
		provider.addConnection(jolokiaConnection);
	}
	
	@Override
	protected ProjectWithDebugAvailableDeployedHelper createProjectHelper() {
		return new ProjectWithDebugAvailableDeployedHelper(RemoteCamelLaunchConfigurationDelegateOverJolokiaIT.class.getSimpleName()) {
			@Override
			protected void addExtraAttributesToLocalProjectLaunch(ILaunchConfigurationWorkingCopy configuration) throws CoreException, IOException {
				super.addExtraAttributesToLocalProjectLaunch(configuration);
				String vmArguments = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
				configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-javaagent:\""+jolokiaAgentPath.toFile().getCanonicalPath()+"=port="+JOLOKIA_PORT+"\" " + vmArguments);
			}
			
			@Override
			protected String getMavenLaunchOutPutFileName(IProject project) {
				return "MavenLaunchOutput-"+project.getName()+"-jolokia.txt";
			}
		};
	}
	
	private void waitForJolokia() throws IOException, InterruptedException {
		URLConnection openConnection = new URL(JOLOKIA_URL).openConnection();
		waitConnection(openConnection);
	}

	private void waitConnection(URLConnection openConnection) throws InterruptedException {
		int tryNumber = 0;
		boolean connected = false;
		while(!connected && tryNumber < 10){
			try{
				openConnection.connect();
				connected = true;
			} catch (IOException ioe) {
				//wait for connection availability
				System.out.println("failed attempt "+tryNumber);
				System.out.println(ioe);
				Thread.sleep(500);
				tryNumber++;
			}
		}
	}

	@Override
	protected void configureConnection(ILaunchConfigurationWorkingCopy configuration) throws Exception {
		configuration.setAttribute(ICamelDebugConstants.ATTR_JMX_CONNECTION_WRAPPER_PROVIDER_ID, JOLOKIA_PROVIDER_ID);
		configuration.setAttribute(ICamelDebugConstants.ATTR_JMX_CONNECTION_WRAPPER_CONNECTION_NAME, ExtensionManager.getProvider(JOLOKIA_PROVIDER_ID).getName(jolokiaConnection));
	}

}
