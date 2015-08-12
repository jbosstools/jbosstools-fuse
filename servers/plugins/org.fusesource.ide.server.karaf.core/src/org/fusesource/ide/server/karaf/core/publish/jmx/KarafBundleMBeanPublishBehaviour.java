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
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

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
	public long getBundleId(MBeanServerConnection mbsc,
			String bundleSymbolicName, String version) {
		try {
			TabularData	tabData = (TabularData)mbsc.getAttribute(this.objectName, "Bundles"); 
			final Collection<?> rows = tabData.values();
			for (Object row : rows) {
				if (row instanceof CompositeData) {
					CompositeData cd = (CompositeData) row;
					String bsn = cd.get("Name").toString();
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
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return -1;
	}
	
	@Override
	public long installBundle(MBeanServerConnection mbsc, String bundlePath) {
		String bundleUrl = bundlePath;
		try {
			bundleUrl = new File(bundlePath).toURL().toExternalForm();
		} catch(MalformedURLException murle) {
			murle.printStackTrace();		
		}

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
	
	@Override
	public boolean updateBundle(MBeanServerConnection mbsc, long bundleId,
			String bundlePath) {
		try {
			mbsc.invoke(this.objectName, "update", new Object[] { ""+bundleId, bundlePath } , new String[] {String.class.getName(), String.class.getName() }); 
			return true;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}
	
	@Override
	public boolean uninstallBundle(MBeanServerConnection mbsc, long bundleId) {
		try {
			mbsc.invoke(this.objectName, "uninstall", new Object[] { ""+bundleId } , new String[] {String.class.getName() }); 
			return true;
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}
	
	@Override
	public int getBundleStatus(MBeanServerConnection mbsc, long bundleId) {
		try {
			TabularData	tabData = (TabularData)mbsc.getAttribute(this.objectName, "Bundles"); 
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
			this.objectName = new ObjectName(KARAF_BUNDLE_MBEAN);
			
			Set mbeans = mbsc.queryMBeans(this.objectName, null); 	    
		    if (mbeans.size() == 1) {
		    	// remember the mbean
		    	Object oMbean = mbeans.iterator().next();
		    	if (oMbean instanceof ObjectInstance) {
		    		ObjectInstance oi = (ObjectInstance)oMbean;
		    		this.objectName = oi.getObjectName();
		    		return true;
		    	}
		    }
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}

	    return false;
	}
}
