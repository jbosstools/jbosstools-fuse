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
package org.fusesource.ide.launcher.debug.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.launcher.Activator;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;

/**
 * Connect to Camel Debugger via JMX
 */
public class JMXCamelConnectJob extends Job {
	
	private static final int TIME_WAIT_BETWEEN_RETRY = 500;
	public static final Object JMX_CONNECT_JOB_FAMILY = new Object();

	private final CamelDebugTarget camelDebugTarget;
	private JMXConnector jmxc = null;

	private MBeanServerConnection mBeanServerConnection;

	private String jmxUser;
	private String jmxPass;
	
	private static final long CONNECTION_TIMEOUT_IN_MILLIS = 1000 * 60 * 5L; // 5 minutes timeout
	
	public JMXCamelConnectJob(CamelDebugTarget camelDebugTarget) {
		super("Connect to Camel Debugger...");
		this.camelDebugTarget = camelDebugTarget;
		setSystem(false);
	}

	public JMXCamelConnectJob(CamelDebugTarget camelDebugTarget, String jmxUser, String jmxPass) {
		this(camelDebugTarget);
		this.jmxUser = jmxUser;
		this.jmxPass = jmxPass;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Connect to Camel VM...", 1);
		
		final long startTime = System.currentTimeMillis();
		boolean connected = connect(monitor, startTime);
		
		if (!connected) {
			abortConnection();
		}
		
		monitor.done();
		
		return connected ? Status.OK_STATUS : Status.CANCEL_STATUS;
	}

	private void abortConnection() {
		try {
			camelDebugTarget.abort("Unable to connect to Camel VM", new Exception("Unable to connect to Camel Debugger"));
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}

	private boolean connect(IProgressMonitor monitor, final long startTime) {
		boolean connected = false;
		
		// run until connected or timed out
		while (!connected && System.currentTimeMillis()-startTime <= CONNECTION_TIMEOUT_IN_MILLIS && !monitor.isCanceled()) {
			try {
				connected = tryToConnect();
			} catch (Exception ex) {
				Activator.getLogger().error(ex);
				// runtime not yet up...wait a bit and retry
				try {
					Thread.sleep(TIME_WAIT_BETWEEN_RETRY);
				} catch (InterruptedException e) {
					monitor.setCanceled(true);
				}
			}
		}
		return connected;
	}

	private boolean tryToConnect() throws MalformedObjectNameException, IOException {
		boolean connected = false;
		if (connectToVM()) {
			// connected to the camel vm
			CamelDebugFacade debugger = new CamelDebugFacade(camelDebugTarget, mBeanServerConnection, camelDebugTarget.getCamelContextId());
			camelDebugTarget.setDebugger(debugger);
			connected = true;
			camelDebugTarget.started(true);
			if (!debugger.isEnabled()){
				debugger.enableDebugger();
			}
		}
		return connected;
	}
	
	/**
	 * establish JMX connection to process
	 * 
	 * @return
	 */
	boolean connectToVM() {
		try {
			IConnectionWrapper jmxConnectionWrapper = camelDebugTarget.getJmxConnectionWrapper();
			if(jmxConnectionWrapper != null){
				mBeanServerConnection = createMBeanServerConnection(jmxConnectionWrapper);
			} else {
				mBeanServerConnection = connectToVMUsingCredentials(camelDebugTarget.getJmxUri(), jmxUser, jmxPass);
			}
			return true;
		} catch (IOException | JMXException ex) {
			return false;
		}
	}

	private MBeanServerConnection createMBeanServerConnection(IConnectionWrapper jmxConnectionWrapper) throws IOException, JMXException {
		if(!jmxConnectionWrapper.isConnected()){
			jmxConnectionWrapper.connect();
		}
		MBeanSerConnectionJMXRunnableRetriever connectionRetriever = new MBeanSerConnectionJMXRunnableRetriever();
		jmxConnectionWrapper.run(connectionRetriever);
		return connectionRetriever.getConnection();
	}

	private MBeanServerConnection connectToVMUsingCredentials(String jmxUri, String jmxUser, String jmxPass) throws IOException {
		JMXServiceURL url = new JMXServiceURL(jmxUri);
		if (!Strings.isBlank(jmxUser)) {
			// credentials defined - so use them
			Map<String, Object> envMap = new HashMap<>();
			envMap.put("jmx.remote.credentials", new String[] { jmxUser, jmxPass });
			jmxc = JMXConnectorFactory.connect(url, envMap); 
		} else {
			// no need for using credentials if no user is defined
			jmxc = JMXConnectorFactory.connect(url); 
		}
		return jmxc.getMBeanServerConnection();
	}
	
	private final class MBeanSerConnectionJMXRunnableRetriever implements IJMXRunnable {
		MBeanServerConnection connection = null;

		@Override
		public void run(MBeanServerConnection connection) throws Exception {
			this.connection = connection;
		}

		MBeanServerConnection getConnection(){
			return connection;
		}
	}
	
	@Override
	protected void canceling() {
		clean();
		super.canceling();
	}

	private void clean() {
		try {
			if(jmxc != null) {
				jmxc.close();
			}
		} catch (IOException e) {
			Activator.getLogger().error(e);
		}
		if(camelDebugTarget != null){
			try {
				camelDebugTarget.terminate();
			} catch (DebugException e) {
				Activator.getLogger().error(e);
			}
		}
	}
	
	@Override
	public boolean belongsTo(Object family) {
		return JMX_CONNECT_JOB_FAMILY.equals(family) || super.belongsTo(family);
	}

	public MBeanServerConnection getMBeanConnection() {
		return mBeanServerConnection;
	}
	
}