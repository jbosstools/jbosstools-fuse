/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerEvent;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.server.karaf.core.server.IServerConfiguration;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListener;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListenerManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafUIPlugin extends AbstractUIPlugin {

	public static final String TERMINAL_VIEW_ID = "org.fusesource.ide.server.view.TerminalView";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.server.karaf.ui";

	// The shared instance
	private static KarafUIPlugin plugin;
	
	private UnitedServerListener serverStartingListener;
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static KarafUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * The constructor
	 */
	public KarafUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// Add a server listener to respond to the server being marked as 'starting'
		serverStartingListener = getServerStartingListener();
		
		// Temporary workaround... under recent tests, if the listener is added 
		// too early, the server never ever fires events (ie the listener is ignored
		// or clobbered or something).  Will test again asap. 
		new Thread() {
			public void run() {
				try{
					Thread.sleep(1000);
				} catch(InterruptedException ie){}
				UnitedServerListenerManager.getDefault().addListener(serverStartingListener);
			}
		}.start();
	}

	private UnitedServerListener getServerStartingListener() {
		return new UnitedServerListener(){
			public boolean canHandleServer(IServer server) {
				return isKarafServer(server);
			}
			
			public boolean isKarafServer(IServer server) {
				return (IServerConfiguration)server.loadAdapter(
						IServerConfiguration.class, new NullProgressMonitor()) != null;
			}

			public void serverChanged(ServerEvent event) {
				if( serverSwitchesToState(event, IServer.STATE_STARTING)) {
					// We already know it's a karaf server from canHandleServer(IServer)
					IServer s = event.getServer();
					fireConnectorJob(s);
				}
			}
			
			private void fireConnectorJob(final IServer server) {
				new Job("Connecting to " + server.getName()) {
					protected IStatus run(IProgressMonitor arg0) {
						if( server.getServerState() == IServer.STATE_STARTING) {
							SshConnector c = new SshConnector(server);
							c.start();
							return Status.OK_STATUS;
						}
						return Status.CANCEL_STATUS;
					}
				}.schedule(7000);
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		KarafSharedImages.instance().cleanup();
		if( serverStartingListener != null ) {
			UnitedServerListenerManager.getDefault().removeListener(serverStartingListener);
		}
	}
	/**
	 * opens the properties view if not already open
	 */
	public static IViewPart openTerminalView() {
		final IViewPart[] ret = new IViewPart[1];
		ret[0] = null;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				if (wb != null) {
					IWorkbenchWindow activeWindow = wb.getActiveWorkbenchWindow();
					if (activeWindow != null) {
						IWorkbenchPage activePage = activeWindow.getActivePage();
						if (activePage != null) {
							try { 
								ret[0] = activePage.showView(TERMINAL_VIEW_ID);
							} catch (CoreException ex) {
								getLogger().error("Unable to create the terminal view!", ex);
							}
						}
					}
				}
			}
		});
		return ret[0];
	}
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}
