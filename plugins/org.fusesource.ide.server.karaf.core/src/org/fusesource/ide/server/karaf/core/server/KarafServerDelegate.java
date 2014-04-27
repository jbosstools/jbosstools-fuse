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

package org.fusesource.ide.server.karaf.core.server;

import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.util.ServerNamingUtil;
import org.jboss.ide.eclipse.as.wtp.core.util.ServerSecureStorageUtil;

/**
 * @author lhein
 */
public class KarafServerDelegate extends ServerDelegate implements
		IServerConfigurationWorkingCopy {

	public static final int DEFAULT_SSH_PORT = 8101;
	public static final String DEFAULT_KARAF_SSH_HOSTNAME = "localhost";
	public static final String DEFAULT_KARAF_SSH_USER = "karaf";
	public static final String DEFAULT_KARAF_SSH_PASSWORD = "karaf";
	public static final String DEFAULT_SMX_SSH_USER = "smx";
	public static final String DEFAULT_SMX_SSH_PASSWORD = "smx";
	public static final String DEFAULT_FUSEESB_SSH_USER = "admin";
	public static final String DEFAULT_FUSEESB_SSH_PASSWORD = "admin";
	
	public void setDefaults(IProgressMonitor monitor) {
		super.setDefaults(monitor);
		IRuntime rt = getServer().getRuntime();
		getServerWorkingCopy().setName(ServerNamingUtil.getDefaultServerName(getServer(), rt));
		setAttribute(IServerConfiguration.USER_ID, getDefaultUsername());
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
		return getAttribute(IServerConfiguration.HOST_NAME,	getServer().getHost());
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getPortNumber()
	 */
	public int getPortNumber() {
		return getAttribute(IServerConfiguration.PORT_NUMBER, DEFAULT_SSH_PORT);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getUserName()
	 */
	public String getUserName() {
		return getAttribute(IServerConfiguration.USER_ID, getDefaultUsername());
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setHostName(java.lang.String)
	 */
	public void setHostName(String hostName) {
		setAttribute(IServerConfiguration.HOST_NAME, hostName);
	}
	
	@Override
	public String getPassword() {
		String s = ServerSecureStorageUtil.getFromSecureStorage(Activator.PLUGIN_ID, 
				getServer(), IServerConfiguration.PASSWORD);
		if( s == null )
			return getAttribute(IServerConfiguration.PASSWORD, getDefaultPassword());
		return s;
	}
	
	public void setPassword(String pass) {
		try {
			ServerSecureStorageUtil.storeInSecureStorage(Activator.PLUGIN_ID, 
					getServer(), IServerConfiguration.PASSWORD, pass);
        } catch (StorageException e) {
        	Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not save password for server in secure storage.", e)); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
        	Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not save password for server in secure storage.", e)); //$NON-NLS-1$	
        }
	}
	

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setPortNumber(int)
	 */
	public void setPortNumber(int portNo) {
		setAttribute(IServerConfiguration.PORT_NUMBER, portNo);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setUserName(java.lang.String)
	 */
	public void setUserName(String userName) {
		setAttribute(IServerConfiguration.USER_ID, userName);
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
}
