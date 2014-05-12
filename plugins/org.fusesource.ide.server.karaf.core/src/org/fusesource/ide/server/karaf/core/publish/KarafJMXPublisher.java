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
package org.fusesource.ide.server.karaf.core.publish;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.server.BaseConfigPropertyProvider;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;

/**
 * this publisher can be used for deploying a local bundle / jar file to a local running karaf 2.x instance
 * 3.x fails due to some security reasons -> TODO: investigate
 * 
 * @author lhein
 */
public class KarafJMXPublisher implements IPublishBehaviour {

	protected JMXServiceURL url;
	protected JMXConnector jmxc;
	protected MBeanServerConnection mbsc;
	protected ObjectName karafBundlesMBeanName;
	protected IServer server;
	
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
			String conUrl = getJMXConnectionURL();
			this.url = new JMXServiceURL(conUrl); 
			this.jmxc = JMXConnectorFactory.connect(this.url, envMap); 
			this.mbsc = this.jmxc.getMBeanServerConnection(); 	
			
			if (determineMBeanNames()) return true;
		} catch (IOException ex) {
			ex.printStackTrace();
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
			ex.printStackTrace();
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
	public boolean publish(IServer server, IModule[] module) {
		// TODO: for now we do the connect each time...very inefficient...try to cache it
		if (this.jmxc == null) connect(server);

		boolean published = false;
		try {
			// first check if there is a bundle installed with that name already
			long bundleId = findModuleInBundleList(module[0].getProject().getName());
			if (bundleId != -1) {
				// if yes - reinstall / update the bundle
				published = reinstallBundle(server, module[0], bundleId);
			} else {
				// if no  - fresh install
				published = installBundle(server, module[0]);
			}			
			if (published) {
				// a final check if the bundle is really installed
				long bid = findModuleInBundleList(module[0].getProject().getName());
				published = bid != -1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect(server);
		}
		return published;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour#uninstall(org.eclipse.wst.server.core.IServer, org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public boolean uninstall(IServer server, IModule[] module) {
		if (this.jmxc == null) connect(server);
		boolean unpublished = false;
		try {
			// first check if there is a bundle installed with that name already
			long bundleId = findModuleInBundleList(module[0].getProject().getName());
			if (bundleId != -1) {
				this.mbsc.invoke(this.karafBundlesMBeanName, "uninstall", new Object[] { ""+bundleId } , new String[] {String.class.getName() }); 
				unpublished = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
	private boolean reinstallBundle(IServer server, IModule module, long bundleId) {
		try {
			String fileUrl = getBundleFilePath(module);
			if (fileUrl != null) {
				this.mbsc.invoke(this.karafBundlesMBeanName, "update", new Object[] { ""+bundleId, fileUrl } , new String[] {String.class.getName(), String.class.getName() }); 
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
	 * installs a bundle
	 * @param server
	 * @param module
	 * @return
	 */
	private boolean installBundle(IServer server, IModule module) {
		try {
			String fileUrl = getBundleFilePath(module);
			if (fileUrl != null) {
				this.mbsc.invoke(this.karafBundlesMBeanName, "install", new Object[] { fileUrl, Boolean.TRUE } , new String[] {String.class.getName(), "boolean" }); 
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private String getBundleFilePath(IModule module) {
		File projectTargetPath = module.getProject().getLocation().append("target").toFile();
		File[] jars = projectTargetPath.listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File pathname) {
				return pathname.exists() && pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar");
			}
		});
		if (jars.length>0) {
			return String.format("wrap:file:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", jars[0].getPath(), module.getProject().getName(), getBundleVersion(module, jars[0]));
		}
		return null;
	}

	private String getBundleVersion(IModule module, File f) {
		String version = null;
		IFile manifest = module.getProject().getFile("target/classes/META-INF/MANIFEST.MF");
		if (!manifest.exists()) {
			manifest = module.getProject().getFile("META-INF/MANIFEST.MF");
		}
		if (manifest.exists()) {
			try {
				Manifest mf = new Manifest(new FileInputStream(manifest.getLocation().toFile()));
				version = mf.getMainAttributes().getValue("Bundle-Version");
			} catch (IOException ex) {
				version = null;
			}
		} else {
			// no OSGi bundle - lets take the project name instead
			version = null;
		}

		// no manifest - so grab it from the file name
		if (version == null) {
			version = "";
			String[] parts = f.getName().split("-");
			for (String part : parts) {
				if (!Character.isDigit(part.charAt(0))) {
					if (version.length()==0) continue;
					version += "." + part;
				}
				version += part.trim();
			}
			version = version.substring(0, version.indexOf(".jar"));
		}
		
		return version;
	}
	
	/**
	 * looks for an existing bundle with that SymbolicName
	 * @param tabData
	 * @param bundleSymbolicName
	 * @return	the bundle id or -1 if not found
	 */
	private long findModuleInBundleList(String bundleSymbolicName) throws Exception {
		TabularData tabData = (TabularData)this.mbsc.invoke(this.karafBundlesMBeanName, "list", null, null); 
		final Collection<?> rows = tabData.values();
		for (Object row : rows) {
			if (row instanceof CompositeData) {
				CompositeData cd = (CompositeData) row;
				String bsn = cd.get("Name").toString();
				String id = cd.get("ID").toString();
				if (bsn.equals(bundleSymbolicName)) {
					try {
						return Long.parseLong(id);
					} catch (NumberFormatException ex) {
						return -1;
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * checks the mbeans
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean determineMBeanNames() throws IOException {
		ObjectName objNameFramework = null;
		
		try {
			objNameFramework = new ObjectName("org.apache.karaf:type=bundles,*");
		} catch (MalformedObjectNameException ex) {
			ex.printStackTrace();
		}
		Set mbeans = mbsc.queryMBeans(objNameFramework, null); 
	    if (mbeans.size() != 1) {
	    	// no mbean for the osgi framework found - can't publish anything
	    	return false;
	    }
	    
	    Object oMbean = mbeans.iterator().next();
    	if (oMbean instanceof ObjectInstance) {
    		ObjectInstance oi = (ObjectInstance)oMbean;
    		this.karafBundlesMBeanName = oi.getObjectName();
    	}
    	
    	return true;
	}
	
	/**
	 * retrieve all needed information to connect to JMX server
	 * @return
	 */
	private String getJMXConnectionURL() {
		String retVal = "";
		BaseConfigPropertyProvider manProv = new BaseConfigPropertyProvider(server.getRuntime().getLocation().append("etc").append("org.apache.karaf.management.cfg").toFile());
		BaseConfigPropertyProvider sysProv = new BaseConfigPropertyProvider(server.getRuntime().getLocation().append("etc").append("system.properties").toFile());
		
		String url = manProv.getConfigurationProperty("serviceUrl");
		if (url == null) return null;
		url = url.trim();
		int pos = -1;
		while ((pos = url.indexOf("${")) != -1) {
			retVal += url.substring(0, pos);
			String placeHolder = url.substring(url.indexOf("${")+2, url.indexOf("}")).trim();
			String replacement = manProv.getConfigurationProperty(placeHolder);
			if (replacement == null) {
				replacement = sysProv.getConfigurationProperty(placeHolder);
			}
			if (replacement == null) {
				return null;
			} else {
				retVal += replacement.trim();
				url = url.substring(pos + placeHolder.length() + 3);
			}
		}
		return retVal;
	}
}
