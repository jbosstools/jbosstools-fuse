/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.jmx.karaf.connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegate;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionProviderListener;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;
import org.jboss.tools.jmx.core.tree.ErrorRoot;
import org.jboss.tools.jmx.core.tree.NodeUtils;
import org.jboss.tools.jmx.core.tree.Root;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.jvmmonitor.core.IJvmFacade;
import org.jboss.tools.jmx.jvmmonitor.core.JvmCoreException;

public class KarafServerConnection implements IConnectionWrapper, IServerListener, 
			IConnectionProviderListener, IJvmFacade, IAdaptable {
	private IServer server;
	private Root root;
	private boolean isConnected;
	private boolean isLoading;
	
	private JMXConnector jmxc;
	private MBeanServerConnection activeConnection;
	private KarafActiveJvm customJvm;
	
	public KarafServerConnection(IServer server) {
		this.server = server;
		this.isConnected = false;
		this.isLoading = false;
		connectViaJmxIfRequired(server); // prime the state
		((AbstractKarafJMXConnectionProvider)getProvider()).addListener(this);
		server.addServerListener(this);
	}
	
	@Override
	public void connect() throws IOException {
		// re-connect
		connectToStartedServer();
	}

	@Override
	public void disconnect() throws IOException {
		// close
		if( activeConnection != null ) {
			cleanupConnection(server, activeConnection);
		}
		if( customJvm != null ) {
			customJvm.disconnect();
			customJvm = null;
		}
		activeConnection = null;
		root = null;
		isConnected = false;
		((AbstractKarafJMXConnectionProvider)getProvider()).fireChanged(KarafServerConnection.this);
	}

	@Override
	public IConnectionProvider getProvider() {
		return ExtensionManager.getProvider(KarafConnectionProvider.ID);
	}
	
	@Override
	public Root getRoot() {
		return root;
	}
	
	@Override
	public void loadRoot(IProgressMonitor monitor) {
		if( isConnected() && !isLoading) {
			isLoading = true;
			try {
				if( root == null ) {
					root = NodeUtils.createObjectNameTree(this, monitor);
				}
			} catch( CoreException ce ) {
				IStatus status = new Status(IStatus.ERROR, KarafJMXPlugin.PLUGIN_ID, ce.getMessage(), ce);
				KarafJMXPlugin.getDefault().getLog().log(status);
				root = new ErrorRoot();
			} finally {
				isLoading = false;
			}
		}
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}


	/**
	 * Some workspaces some previous versions may include server adapters with
	 * null values for username and password. In some of these situations, providing 
	 * default credentials will allow authorization to jmx operations. 
	 * 
	 * Subclasses for server versions that should not use default credentials in the event 
	 * of no credentials being passed in should override this method and return false. 
	 * 
	 * @return boolean whether to use default credentials as provided by the server implementation
	 */
	protected boolean shouldUseDefaultCredentials() {
		return true;
	}
	
	@Override
	public void run(IJMXRunnable runnable) throws JMXException {
		run(runnable, new HashMap<String, String>());
	}
	
	// Potential api upstream in jmx ?
	@Override
	public void run(IJMXRunnable runnable, HashMap<String, String> prefs) throws JMXException {
		run(runnable, prefs, false);
	}
	
	public void run(IJMXRunnable runnable, Map<String, String> prefs, boolean saveActiveConnection) throws JMXException {
		if (getServer().getAdapter(IKarafServerDelegate.class) != null) {
			IKarafServerDelegate kserver = (IKarafServerDelegate) getServer().getAdapter(IKarafServerDelegate.class);
			String user = kserver.getUserName();
			String pass = kserver.getPassword();
			run(server, runnable, user, pass, saveActiveConnection);			
		}
	}
	
	protected void run(IServer s, IJMXRunnable r, String user, String pass) throws JMXException {
		run(s,r,user,pass,false);
	}
	
	protected void run(IServer s, IJMXRunnable r, String user, String pass, boolean saveActiveConnection) throws JMXException {
		try {
			MBeanServerConnection c = null;
			if( activeConnection == null ) {
				c = createConnection(s, user, pass);
				if( saveActiveConnection ) {
					activeConnection = c;
				}
			} else {
				c = activeConnection;
			}
			r.run(c);
		} catch(Exception e) {
			KarafJMXPlugin.getLogger().error(e);
		}
	}

	protected MBeanServerConnection createConnection(IServer s, String user, String pass) throws IOException {
		Map<String, Object> envMap = new HashMap<>();
		envMap.put("jmx.remote.credentials", new String[] { user, pass });
		String conUrl = KarafUtils.getJMXConnectionURL(s);
		JMXServiceURL url = new JMXServiceURL(conUrl); 
		jmxc = JMXConnectorFactory.connect(url, envMap); 
		return jmxc.getMBeanServerConnection(); 
	}
	
	protected void cleanupConnection(IServer server, MBeanServerConnection connection) {
		try {
			if (jmxc != null) {
				jmxc.close();
			}
		} catch (Exception ex) {
			KarafJMXPlugin.getLogger().error(ex);
		}
	}
	
	public String getName() {
		return server.getName();
	}

	
	/* **************
	 *  If there's a change in the server state, then set my connection
	 *  state properly.   If there's been a change then fire to the listeners
	 */
	@Override
	public void serverChanged(ServerEvent event) {
		int eventKind = event.getKind();
		if ((eventKind & ServerEvent.SERVER_CHANGE) != 0) {
			// server change event
			if ((eventKind & ServerEvent.STATE_CHANGE) != 0) {
				// server state has changed. If it's changed to started, let's connect to jmx
				connectViaJmxIfRequired(event.getServer());
			}
		}
	}
	
	protected void connectViaJmxIfRequired(IServer server) {
		if( !shouldConnect(server)) {
			try {
				disconnect();
			} catch(IOException ioe) {
				KarafJMXPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, KarafJMXPlugin.PLUGIN_ID, 
						"Unable to cleanly disconnect jmx connection", ioe));
			}
		}
	}
	
	/**
	 * Should we connect to this server now? 
	 * 
	 * @param server
	 * @return
	 */
	protected boolean shouldConnect(IServer server) {
		boolean started = server.getServerState() == IServer.STATE_STARTED;
		// We require a runtime to launch jmx, because we load those client jars onto the classpath. 
		boolean hasRuntime = server.getRuntime() != null;
		if( started && hasRuntime ) {
			return true;
		}
		return false;
	}
	
	protected void launchConnectionJob(final IServer server) {
		new Job("Connecting to " + server.getName() + " via JMX") { //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if( server.getServerState() == IServer.STATE_STARTED)
					// Since this job runs late, it's possible server already stopped by user
					connectToStartedServer();
				return Status.OK_STATUS;
			} 
		}.schedule(5000);
	}

	protected void connectToStartedServer() {
		try {
			IJMXRunnable run = new IJMXRunnable() {
				@Override
				public void run(MBeanServerConnection connection)
						throws Exception {
					// Do nothing, just see if the connection worked
				} 
			};
			
			HashMap<String, String> map = new HashMap<>();
			map.put("force", Boolean.toString(true));
			run(run, map, true); // save this nw connection as the active one

			
			if( !isConnected ) {
				isConnected = true;
				((AbstractKarafJMXConnectionProvider)getProvider()).fireChanged(KarafServerConnection.this);
			}
		} catch( Exception jmxe ) {
			IStatus status = new Status(IStatus.ERROR, KarafJMXPlugin.PLUGIN_ID, "Error connecting to jmx for server "+server.getName(), jmxe);
			KarafJMXPlugin.getDefault().getLog().log(status);
			// I thought i was connected but I'm not. 
			if( isConnected ) {
				isConnected = false;
				((AbstractKarafJMXConnectionProvider)getProvider()).fireChanged(KarafServerConnection.this);
			}
		}
	}
	
	
	/* *************
	 * The following three methods are just here so that this class
	 * is removed as a listener to the server if it is removed
	 */
	
	@Override
	public void connectionAdded(IConnectionWrapper connection) {
		// ignore
	}

	@Override
	public void connectionChanged(IConnectionWrapper connection) {
		// ignore
	}

	@Override
	public void connectionRemoved(IConnectionWrapper connection) {
		if( connection == this )
			server.removeServerListener(this);
	}

	@Override
	public boolean canControl() {
		return server.getServerState() == IServer.STATE_STARTED && server.getRuntime() != null;
	}

	@Override
	public IActiveJvm getActiveJvm() {
		if( server.getServerState() == IServer.STATE_STARTED && isConnected ) {
			if( customJvm == null ) {
				IActiveJvm active = KarafJVMFacadeUtility.findJvmForServer(server);
				try {
					customJvm = new KarafActiveJvm(this, active);
				} catch(JvmCoreException jvmce) {
					KarafJMXPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, KarafJMXPlugin.PLUGIN_ID, jvmce.getMessage(), jvmce));
				}
			}
			return customJvm;
		}
		return null;
	}
	

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		ITabbedPropertySheetPageContributor contributor = new ITabbedPropertySheetPageContributor() {
			@Override
			public String getContributorId() {
				return "org.jboss.tools.jmx.jvmmonitor.ui.JvmExplorer";
			}
		};
		if (adapter == IPropertySheetPage.class) {
			return new TabbedPropertySheetPage(contributor);
		} else if (adapter == ITabbedPropertySheetPageContributor.class) {
			return contributor;
		}
		return null;
	}
	
	public MBeanServerConnection getActiveConnection() {
		// get an active connection if it exists
		return activeConnection;
	}
	
	public IServer getServer() {
		return server;
	}
}
