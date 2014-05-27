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

import java.util.Collection;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * publisher using the org.apache.karaf:type=bundles mbean
 * 
 * @author lhein
 */
public class KarafBundlesMBeanPublishBehaviour extends
		KarafBundleMBeanPublishBehaviour {
	
	private static final String KARAF_BUNDLES_MBEAN = "org.apache.karaf:type=bundles,*";
	
	@Override
	public long getBundleId(MBeanServerConnection mbsc,
			String bundleSymbolicName, String version) {
		try {
			TabularData	tabData = (TabularData)mbsc.invoke(this.objectName, "list", null, null);
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
			// ignore
		}
		return -1;
	}

	@Override
	public String getBundleStatus(MBeanServerConnection mbsc, long bundleId) {
		try {
			TabularData	tabData = (TabularData)mbsc.invoke(this.objectName, "list", null, null);
			final Collection<?> rows = tabData.values();
			for (Object row : rows) {
				if (row instanceof CompositeData) {
					CompositeData cd = (CompositeData) row;
					String id = cd.get("ID").toString();
					String state = cd.get("State").toString();
					long longID = Long.parseLong(id); 
					if (bundleId == longID) {
						return state;
					}	
				}
			}
		} catch (Exception ex) {
			// ignore
		}
		return null;
	}
	
	@Override
	public boolean canHandle(MBeanServerConnection mbsc) {
		try {
			this.objectName = new ObjectName(KARAF_BUNDLES_MBEAN);
			
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
			// ignore
		}

	    return false;
	}
}
