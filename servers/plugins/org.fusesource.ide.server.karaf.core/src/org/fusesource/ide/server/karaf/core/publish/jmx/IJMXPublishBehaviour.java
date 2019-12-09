/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.publish.jmx;

import javax.management.MBeanServerConnection;

/**
 * common methods for all fuse jmx publisher
 * 
 * @author lhein
 */
public interface IJMXPublishBehaviour {
	
	/**
	 * returns the id of the bundle in the server. it tries to find the bundle
	 * with the same bundle symbolic name and version. If no version is supplied
	 * it will return the first id of a bundle with the same symbolic name.
	 * 
	 * @param mbsc
	 * @param bundleSymbolicName
	 * @param version
	 * @return	the bundle id or -1 if not found
	 */
	public long getBundleId(MBeanServerConnection mbsc, String bundleSymbolicName, String version);
	
	/**
	 * installs and starts the bundle from the given bundle path
	 * 
	 * @param mbsc
	 * @param bundlePath
	 * @return	the bundle id of the installed bundle or -1 on failure
	 */
	public long installBundle(MBeanServerConnection mbsc, String bundlePath);
	
	/**
	 * re-installs the bundle with the given bundle id and path
	 * 
	 * @param mbsc
	 * @param bundleId
	 * @param bundlePath
	 * @return	true on success
	 */
	public boolean updateBundle(MBeanServerConnection mbsc, long bundleId, String bundlePath);
	
	/**
	 * removes the bundle with the given id from the server
	 * 
	 * @param mbsc
	 * @param bundleId
	 * @return	true on success
	 */
	public boolean uninstallBundle(MBeanServerConnection mbsc, long bundleId);
	
	/**
	 * checks if the behaviour class can handle a given jmx connection
	 * 
	 * @param mbsc
	 * @return	true if the behaviour can be used
	 */
	public boolean canHandle(MBeanServerConnection mbsc);
	
	/**
	 * returns the bundle status of the bundle with the given id
	 * 
	 * @param mbsc
	 * @param bundleId	the bundle id
	 * @return	the status as IServer.STATE_xxx
	 */
	public int getBundleStatus(MBeanServerConnection mbsc, long bundleId);
}
