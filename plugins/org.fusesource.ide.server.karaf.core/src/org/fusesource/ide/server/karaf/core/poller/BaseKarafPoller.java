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
package org.fusesource.ide.server.karaf.core.poller;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.Messages;
import org.fusesource.ide.server.karaf.core.server.subsystems.IServerPortController;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller2;
import org.jboss.ide.eclipse.as.core.server.IServerStatePollerType;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;

/**
 * @author lhein
 */
public class BaseKarafPoller implements IServerStatePoller2 {
	public static final String KEY_POLLER = "karafPoller";	
	
	private IServer server;
	private IServerStatePollerType type;
	private String host;
	private int port;
	private boolean canceled, done;
	private boolean state;
	private boolean expectedState;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#beginPolling(org.eclipse.wst.server.core.IServer, boolean)
	 */
	@Override
	public void beginPolling(IServer server, boolean expectedState) {
		this.server = server;
		this.canceled = done = false;
		this.expectedState = expectedState;
		this.state = !expectedState;
		determineServerInfo();
		launchThread();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#getPollerType()
	 */
	@Override
	public IServerStatePollerType getPollerType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#setPollerType(org.jboss.ide.eclipse.as.core.server.IServerStatePollerType)
	 */
	@Override
	public void setPollerType(IServerStatePollerType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerProvider#getServer()
	 */
	@Override
	public IServer getServer() {
		return server;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#isComplete()
	 */
	@Override
	public boolean isComplete() throws PollingException, RequiresInfoException {
		return done;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#getState()
	 */
	@Override
	public boolean getState() throws PollingException, RequiresInfoException {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#cleanup()
	 */
	@Override
	public void cleanup() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.INeedCredentials#getRequiredProperties()
	 */
	@Override
	public List<String> getRequiredProperties() {
		return new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.INeedCredentials#provideCredentials(java.util.Properties)
	 */
	@Override
	public void provideCredentials(Properties properties) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#cancel(int)
	 */
	@Override
	public void cancel(int type) {
		canceled = true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#getTimeoutBehavior()
	 */
	@Override
	public int getTimeoutBehavior() {
		return TIMEOUT_BEHAVIOR_FAIL;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller2#getCurrentStateSynchronous(org.eclipse.wst.server.core.IServer)
	 */
	@Override
	public IStatus getCurrentStateSynchronous(IServer server) {
		URI serverURI = determineServerInfo(server);
		boolean b = onePing(serverURI.getHost(), serverURI.getPort());
		Status s;
		if( b ) {
			s = new Status(IStatus.OK, Activator.PLUGIN_ID, 
					NLS.bind(Messages.KarafPollerServerFound, serverURI.getHost(), serverURI.getPort()));
		} else {
			s = new Status(IStatus.INFO, Activator.PLUGIN_ID, 
				NLS.bind(Messages.KarafPollerServerNotFound, serverURI.getHost(), serverURI.getPort()));
		}
		return s;
	}
	
	/**
	 * creates a polling thread and starts it
	 */
	protected void launchThread() {
		Thread t = new Thread(new Runnable(){
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				pollerRun();
			}
		}, "Karaf Poller"); //$NON-NLS-1$
		t.start();
	}
	
	/**
	 * starts the polling loop
	 */
	private void pollerRun() {
		done = false;
		int cnt = 0;
		while(!canceled && !done) {
			boolean up = onePing(this.host, this.port);
			if( up == expectedState ) {
				state = expectedState;
				done = true;				
			}
			try {
				Thread.sleep(100);
			} catch(InterruptedException ie) {} // ignore
			cnt++;
			// after 10 failed tries we re-read the port info
			if (cnt > 10) {
				determineServerInfo();
				cnt = 0;
			}
		}
	}
	
	/**
	 * determines the host and port
	 */
	private void determineServerInfo() {
		this.host = getServer().getHost();
		this.port = getManagementPort();
	}
	
	/**
	 * determines the host and port
	 */
	private URI determineServerInfo(IServer s) {
		String host = s.getHost();
		int port = getManagementPort(s);
		try {
			return new URI(null, null, host, port, null, null, null);	
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * retrieves the management port used for polling
	 * 
	 * @return	the port to use or -1 if not found
	 */
	private int getManagementPort() {
		try {
			ControllableServerBehavior csb = (ControllableServerBehavior)getServer().loadAdapter(ControllableServerBehavior.class, null);
			if (csb != null) {
				IServerPortController ctrl = (IServerPortController)csb.getController("port");
				return ctrl.findPort(IServerPortController.KEY_MANAGEMENT_PORT, -1);				
			}			
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * retrieves the management port used for polling
	 * 
	 * @return	the port to use or -1 if not found
	 */
	private int getManagementPort(IServer s) {
		try {
			ControllableServerBehavior csb = (ControllableServerBehavior)s.loadAdapter(ControllableServerBehavior.class, null);
			if (csb != null) {
				IServerPortController ctrl = (IServerPortController)csb.getController("port");
				return ctrl.findPort(IServerPortController.KEY_MANAGEMENT_PORT, -1);			
			}
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * does a single ping on the servers port
	 * 
	 * @return	returns true if server is up, otherwise false
	 */
	private boolean onePing(String host, int port) {
		Socket s = null;
        try {
            s = new Socket(host, port);
            if (s.isBound()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
        	Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Failed to establish a connection to the server...");
			Activator.getDefault().getLog().log(status);
        } finally {
            if (s != null) {
            	try {
            		s.close();
            	} catch (IOException e) {
            		// ignore
            	}
            }
        }
		return false;
	}
}
