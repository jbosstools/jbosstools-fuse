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
package org.fusesource.ide.server.karaf.core.publish.jmx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;

/**
 * this publisher can be used for deploying a local bundle / jar file to a local running karaf instance
 * 
 * @author lhein
 */
public class KarafJMXPublisher implements IPublishBehaviour {
	
	// the list of known JMX publish behaviours
	// each of those behaviours handle the deployment of modules via a specific mbean
	private static final ArrayList<IJMXPublishBehaviour> KNOWN_JMX_BEHAVIOURS;
	static {
		KNOWN_JMX_BEHAVIOURS = new ArrayList<IJMXPublishBehaviour>();
		KNOWN_JMX_BEHAVIOURS.add(new KarafBundleMBeanPublishBehaviour());
		KNOWN_JMX_BEHAVIOURS.add(new KarafBundlesMBeanPublishBehaviour());
		KNOWN_JMX_BEHAVIOURS.add(new OSGIMBeanPublishBehaviour());
	}
	
	protected JMXServiceURL url;
	protected JMXConnector jmxc;
	protected MBeanServerConnection mbsc;
	protected IServer server;
	protected IJMXPublishBehaviour jmxPublisher;
	
	/**
	 * connect to the given server via JMX
	 * 
	 * @param server
	 * @return
	 */
	protected boolean connect(IServer server) {
		this.server = server;
		
		KarafServerDelegate del = (KarafServerDelegate)server.loadAdapter(KarafServerDelegate.class, new NullProgressMonitor());
		Map<String, Object> envMap = new HashMap<String, Object>();
		envMap.put("jmx.remote.credentials", new String[] { del.getUserName(), del.getPassword() });
		try {
			String conUrl = KarafUtils.getJMXConnectionURL(server);
			this.url = new JMXServiceURL(conUrl); 
			this.jmxc = JMXConnectorFactory.connect(this.url, envMap); 
			this.mbsc = this.jmxc.getMBeanServerConnection(); 	
			
			for (IJMXPublishBehaviour pb : KNOWN_JMX_BEHAVIOURS) {
				if (pb.canHandle(mbsc)) {
					this.jmxPublisher = pb;
					break;
				}
			}
			return this.jmxPublisher != null;
		} catch (IOException ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}

	/**
	 * disconnect from the server
	 * 
	 * @param server
	 * @return
	 */
	protected boolean disconnect(IServer server) {
		try {
			if (this.jmxc != null) {
				this.jmxc.close();
			}
			return true;
		} catch (IOException ex) {
			Activator.getLogger().error(ex);
		} finally {
			this.jmxc = null;
			this.mbsc = null;
			this.url = null;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour#publish(org.eclipse.wst.server.core.IServer, org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public int publish(IServer server, IModule[] module, String symbolicName, String version, IPath file) {
		// TODO: for now we do the connect each time...very inefficient...try to cache it
		if (this.jmxc == null) connect(server);

		try {
			// first check if there is a bundle installed with that name already
			long bundleId = this.jmxPublisher.getBundleId(mbsc, symbolicName, version);
			if (bundleId != -1) {
				// if yes - reinstall / update the bundle
				reinstallBundle(server, bundleId, file);
			} else {
				// if no  - fresh install
				bundleId = installBundle(server, file);
			}			
			
			if (bundleId != -1) {
				// a final check if the bundle is really installed
				return this.jmxPublisher.getBundleStatus(mbsc, bundleId);
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		} finally {
			disconnect(server);
		}
		return IServer.STATE_UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour#uninstall(org.eclipse.wst.server.core.IServer, org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public boolean uninstall(IServer server, IModule[] module, String symbolicName, String version) {
		if (this.jmxc == null) connect(server);
		boolean unpublished = false;
		try {
			// insert a project refresh here
			IProject project = module[0].getProject();
			project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			
//			String version = KarafUtils.getBundleVersion(module[0], null);
//			String version2 = KarafUtils.getBundleVersion(module[0], null);
//			String symbolicName2 = KarafUtils.getBundleSymbolicName(module[0]);
			// first check if there is a bundle installed with that name already
			long bundleId = this.jmxPublisher.getBundleId(mbsc, symbolicName, version);
			if (bundleId != -1) {
				unpublished = this.jmxPublisher.uninstallBundle(mbsc, bundleId);
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		} finally {
			disconnect(server);
		}
		return unpublished;
	}
	
	/**
	 * reinstalls a bundle
	 * @param server
	 * @param module
	 * @param bundleId
	 * @return
	 */
	private boolean reinstallBundle(IServer server, long bundleId, IPath file) throws CoreException {
		if (file != null) {
			return this.jmxPublisher.updateBundle(mbsc, bundleId, file.toOSString());
		}
		return false;
	}
	
	/**
	 * installs a bundle
	 * @param server
	 * @param module
	 * @return
	 */
	private long installBundle(IServer server, IPath file) throws CoreException {
		if (file != null) {
			long bundleId = this.jmxPublisher.installBundle(mbsc, file.toOSString());
			return bundleId;
		}
		return -1;
	}
}
