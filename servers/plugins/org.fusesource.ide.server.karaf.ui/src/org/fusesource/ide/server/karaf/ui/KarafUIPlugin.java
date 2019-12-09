/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerEvent;
import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegate;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListener;
import org.jboss.ide.eclipse.as.core.server.UnitedServerListenerManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KarafUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.server.karaf.ui";

	public static final String TERMINAL_VIEW_ID = "org.eclipse.tm.terminal.view.ui.TerminalsView";
	public static final String IMG_KARAF_LOGO_LARGE = "karaf-logo_lg.png"; //$NON-NLS-1$
	
	// The shared instance
	private static KarafUIPlugin plugin;
	
	private UnitedServerListener serverListener;
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static KarafUIPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// Add a server listener to respond to the server being marked as 'starting'
		serverListener = getServerListener();
		UnitedServerListenerManager.getDefault().addListener(serverListener);
	}
	
	public boolean isKarafServer(IServer server) {
		return (IKarafServerDelegate)server.loadAdapter(
				IKarafServerDelegate.class, new NullProgressMonitor()) != null;
	}

	private UnitedServerListener getServerListener() {
		return new UnitedServerListener(){
			@Override
			public boolean canHandleServer(IServer server) {
				return isKarafServer(server);
			}
			
			@Override
			public void serverChanged(ServerEvent event) {
				if( serverSwitchesToState(event, IServer.STATE_STARTED)) {
					// We already know it's a karaf server from canHandleServer(IServer)
					IServer s = event.getServer();
					fireConnectorJob(s);
				} 
			}
			
			private void fireConnectorJob(final IServer server) {
				new Job("Connecting to " + server.getName()) {
					@Override
					protected IStatus run(IProgressMonitor arg0) {
						if( server.getServerState() == IServer.STATE_STARTED) {
							SshConnector c = new SshConnector(server);
							c.start();
							return Status.OK_STATUS;
						}
						return Status.CANCEL_STATUS;
					}
				}.schedule();
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if( serverListener != null ) {
			UnitedServerListenerManager.getDefault().removeListener(serverListener);
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
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMG_KARAF_LOGO_LARGE, ImageDescriptor.createFromURL(plugin.getBundle().getEntry("/icons/karaf-logo_lg.png")));  //$NON-NLS-1$
	}
}
