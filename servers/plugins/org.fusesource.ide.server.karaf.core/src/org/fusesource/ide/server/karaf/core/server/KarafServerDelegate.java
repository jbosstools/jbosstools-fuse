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

package org.fusesource.ide.server.karaf.core.server;

import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.server.internal.IServerConnectionProvider;
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator;
import org.fusesource.ide.server.karaf.core.util.ServerNamingUtil;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;
import org.jboss.ide.eclipse.as.wtp.core.util.ServerSecureStorageUtil;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionFacade;
import org.jboss.tools.jmx.core.IConnectionWrapper;

/**
 * @author lhein
 */
public class KarafServerDelegate extends ServerDelegate implements
		IKarafServerDelegateWorkingCopy, IConnectionFacade {

	public static final String KARAF_JMX_CONNECTION_PROVIDER_ID = "org.fusesource.ide.jmx.karaf.connection.KarafConnectionProvider";
	
	public static final int    DEFAULT_SSH_PORT = 8101;
	
	public static final String DEFAULT_KARAF_SSH_HOSTNAME = "localhost";
	public static final String DEFAULT_KARAF_SSH_USER = "karaf";
	public static final String DEFAULT_KARAF_SSH_PASSWORD = "karaf";
	
	@Override
	public void setDefaults(IProgressMonitor monitor) {
		super.setDefaults(monitor);
		IRuntime rt = getServer().getRuntime();
		getServerWorkingCopy().setName(ServerNamingUtil.getDefaultServerName(getServer(), rt));
		setAttribute(IKarafServerDelegate.USER_ID, getDefaultUsername());
		// do not set password, will be in secure storage
	}
	
	protected String getDefaultUsername() {
		return DEFAULT_KARAF_SSH_USER;	
	}
	
	protected String getDefaultPassword() {
		return DEFAULT_KARAF_SSH_PASSWORD;	
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getHostName()
	 */
	public String getHostName() {
		return getAttribute(IKarafServerDelegate.HOST_NAME,	getServer().getHost());
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getPortNumber()
	 */
	@Override
	public int getPortNumber() {
		return getAttribute(IKarafServerDelegate.PORT_NUMBER, DEFAULT_SSH_PORT);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getUserName()
	 */
	@Override
	public String getUserName() {
		return getAttribute(IKarafServerDelegate.USER_ID, getDefaultUsername());
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setHostName(java.lang.String)
	 */
	public void setHostName(String hostName) {
		setAttribute(IKarafServerDelegate.HOST_NAME, hostName);
	}
	
	@Override
	public String getPassword() {
		String s = ServerSecureStorageUtil.getFromSecureStorage(Activator.PLUGIN_ID, 
				getServer(), IKarafServerDelegate.PASSWORD);
		if( s == null )
			return getAttribute(IKarafServerDelegate.PASSWORD, getDefaultPassword());
		return s;
	}
	
	@Override
	public void setPassword(String pass) {
		try {
			ServerSecureStorageUtil.storeInSecureStorage(Activator.PLUGIN_ID, 
					getServer(), IKarafServerDelegate.PASSWORD, pass);
        } catch (StorageException|UnsupportedEncodingException e) {
        	Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not save password for server in secure storage.", e)); //$NON-NLS-1$
        }
	}
	

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setPortNumber(int)
	 */
	@Override
	public void setPortNumber(int portNo) {
		setAttribute(IKarafServerDelegate.PORT_NUMBER, portNo);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setUserName(java.lang.String)
	 */
	@Override
	public void setUserName(String userName) {
		setAttribute(IKarafServerDelegate.USER_ID, userName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerDelegate#canModifyModules(org.eclipse.wst.server.core.IModule[], org.eclipse.wst.server.core.IModule[])
	 */
	@Override
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerDelegate#getChildModules(org.eclipse.wst.server.core.IModule[])
	 */
	@Override
	public IModule[] getChildModules(IModule[] module) {
		return module;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerDelegate#getRootModules(org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {
		return new IModule[] { module };
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerDelegate#modifyModules(org.eclipse.wst.server.core.IModule[], org.eclipse.wst.server.core.IModule[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void modifyModules(IModule[] add, IModule[] remove,
			IProgressMonitor monitor) throws CoreException {
		// Do nothing
	}
	
	/**
	 * returns the launch configurator for the server
	 * @return	the launch configurator
	 * @throws CoreException
	 */
	public ILaunchConfigConfigurator getLaunchConfigurator() throws CoreException {
		return new Karaf2xStartupLaunchConfigurator(getServer());
	}
	
	/**
	 * validates the server
	 * 
	 * @return
	 */
	public IStatus validate() {
		// check if the folder exists and the karaf.jar is in place
		if (getServer() != null && getServer().getRuntime() != null) {
			IPath rtLoc = getServer().getRuntime().getLocation();
			IPath karafJar = rtLoc.append("lib").append("karaf.jar");
			if (rtLoc.toFile().exists() && rtLoc.toFile().isDirectory() && karafJar.toFile().exists() && karafJar.toFile().isFile()) {
				return Status.OK_STATUS;	
			}
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public IConnectionWrapper getJMXConnection() {
		IConnectionWrapper wrapper = null;
		
		IServerConnectionProvider provider = (IServerConnectionProvider)ExtensionManager.getProvider(KARAF_JMX_CONNECTION_PROVIDER_ID);
		if (provider != null) {
			wrapper = provider.findConnection(getServer());
		}
		
		return wrapper;
	}
}
