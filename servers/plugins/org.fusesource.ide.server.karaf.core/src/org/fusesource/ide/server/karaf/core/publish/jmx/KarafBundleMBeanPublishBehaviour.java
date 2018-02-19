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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.InvalidKeyException;
import javax.management.openmbean.TabularData;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.server.subsystems.OSGiBundleState;

/**
 * publisher using the org.apache.karaf:type=bundle mbean
 * 
 * @author lhein
 */
public class KarafBundleMBeanPublishBehaviour implements IJMXPublishBehaviour {
	
	private static final String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=bundle,*";
	
	protected ObjectName objectName;
	
	@Override
	public long getBundleId(MBeanServerConnection mbsc, String bundleSymbolicName, String version) {
		try {
			TabularData	tabData = getTabularData(mbsc);
			return getBundleId(bundleSymbolicName, version, tabData);
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	protected TabularData getTabularData(MBeanServerConnection mbsc) throws MBeanException, AttributeNotFoundException,
			InstanceNotFoundException, ReflectionException, IOException {
		return (TabularData) mbsc.getAttribute(this.objectName, "Bundles");
	}

	protected long getBundleId(String bundleSymbolicName, String version, TabularData tabData) {
		final Collection<?> rows = tabData.values();
		for (Object row : rows) {
			if (row instanceof CompositeData) {
				CompositeData cd = (CompositeData) row;
				String bsn = getBundleSymbolicName(cd);
				if (bsn != null) {
					String id = cd.get("ID").toString();
					String ver = cd.get("Version").toString();
					if (version != null) {
						if (bsn.equals(bundleSymbolicName) && ver.equals(version)) {
							return Long.parseLong(id);
						}
					} else {
						// if we don't have a version we take the first best
						if (bsn.equals(bundleSymbolicName)) {
							return Long.parseLong(id);
						}
					}
				}
			}
		}
		return -1;
	}
	
	@Override
	public long installBundle(MBeanServerConnection mbsc, String bundlePath) {
		String bundleUrl = getBundleUrl(bundlePath);

		try {
			Object retVal = mbsc.invoke(this.objectName, "install", new Object[] { bundleUrl, Boolean.TRUE } , new String[] {String.class.getName(), "boolean" }); 
			if (retVal instanceof Long) {
				return (Long)retVal;
			} else {
				Activator.getLogger().error(retVal.toString());
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}

	private String getBundleUrl(String bundlePath) {
		String bundleUrl = bundlePath;
		try {
			bundleUrl = new File(bundlePath.replaceFirst("file:", "")).toURI().toURL().toExternalForm();
		} catch(MalformedURLException murle) {
			Activator.getLogger().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, murle.getMessage(), murle));
		}
		return bundleUrl;
	}
	
	@Override
	public boolean updateBundle(MBeanServerConnection mbsc, long bundleId,
			String bundlePath) {
		String bundleUrl = getBundleUrl(bundlePath);
		try {
			mbsc.invoke(this.objectName, "update", new Object[] { Long.toString(bundleId), bundleUrl } , new String[] {String.class.getName(), String.class.getName() }); 
			return true;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}
	
	@Override
	public boolean uninstallBundle(MBeanServerConnection mbsc, long bundleId) {
		try {
			mbsc.invoke(this.objectName, "uninstall", new Object[] { Long.toString(bundleId) } , new String[] {String.class.getName() }); 
			return true;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}
	
	@Override
	public int getBundleStatus(MBeanServerConnection mbsc, long bundleId) {
		try {
			TabularData	tabData = getTabularData(mbsc); 
			final Collection<?> rows = tabData.values();
			for (Object row : rows) {
				if (row instanceof CompositeData) {
					CompositeData cd = (CompositeData) row;
					String id = cd.get("ID").toString();
					String state = cd.get("State").toString();
					long longID = Long.parseLong(id); 
					if (bundleId == longID) {
						return OSGiBundleState.getStatusForString(state);
					}	
				}
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return IServer.STATE_UNKNOWN;
	}
	
	@Override
	public boolean canHandle(MBeanServerConnection mbsc) {
		try {
			this.objectName = getQueryObjectName();
			
			Set<ObjectInstance> mbeans = mbsc.queryMBeans(this.objectName, null); 	    
			if (mbeans.size() == 1) {
				// remember the mbean
				ObjectInstance oMbean = mbeans.iterator().next();
				this.objectName = oMbean.getObjectName();
				return true;
			}
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}

	    return false;
	}

	protected ObjectName getQueryObjectName() throws MalformedObjectNameException {
		return new ObjectName(KARAF_BUNDLE_MBEAN);
	}
	
	protected String getBundleSymbolicName(CompositeData cd) {
		String bsn = null;
		try {
			bsn = cd.get("Symbolic Name").toString();
		} catch (InvalidKeyException ex) {
			Object name = cd.get("Name");
			if (name != null) {
				bsn = name.toString();
			}
		}
		return bsn;
	}
}
