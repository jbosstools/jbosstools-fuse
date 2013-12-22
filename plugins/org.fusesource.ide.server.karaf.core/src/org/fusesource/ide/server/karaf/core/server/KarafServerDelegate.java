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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.fusesource.ide.server.karaf.core.Activator;


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
	
	private static final String DEFAULT_DUMMY = null;
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getHostName()
	 */
	public String getHostName() {
		return getAttribute(IServerConfiguration.HOST_NAME,	DEFAULT_DUMMY);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfiguration#getPassword()
	 */
	public String getPassword() {
		return getAttribute(IServerConfiguration.PASSWORD, DEFAULT_DUMMY);
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
		return getAttribute(IServerConfiguration.USER_ID, DEFAULT_DUMMY);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setHostName(java.lang.String)
	 */
	public void setHostName(String hostName) {
		setAttribute(IServerConfiguration.HOST_NAME, hostName);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.IServerConfigurationWorkingCopy#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		setAttribute(IServerConfiguration.PASSWORD, password);
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
		if (remove != null && remove.length > 0) {
			for (IModule mod : remove) {
				try {
					removeModule(mod);
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							"IOException for module " + mod.getName(), e));
				}
			}
		}
	}
	
	private void removeModule(IModule mod) throws CoreException, IOException {
		Activator.getLogger().debug("Remove Module: " + mod.getId());
//		IFuseESBModule esbModule = (IFuseESBModule) mod.loadAdapter(
//				IFuseESBModule.class, null);
//		if (esbModule != null) {
//			if (esbModule.isPublishedUsingAnt()) {
//				removeAntPublishedContent(esbModule);
//			} else {
//				removeUsingProjectName(mod);
//				// removeUsingJMX(mod);
//			}
//			esbModule.clearModuleProperties();
//		} else {
//			removeUsingProjectName(mod);
//		}
//		try{
//			getServerConfiguration().getDeployManager().removeDeployArtifact(mod.getProject());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
