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

/**
 * @author lhein
 */
public interface IKarafServerDelegate {
	static final String HOST_NAME = "sshHost";
	static final String PORT_NUMBER = "sshPort";
	static final String USER_ID = "userId";
	static final String PASSWORD = "password";
	
	static final String SERVER_TYPE_PREFIX_KARAF   = "org.fusesource.ide.karaf.server.";
	static final String SERVER_TYPE_PREFIX_FUSEESB = "org.fusesource.ide.fuseesb.server.";
	
	/**
	 * put in here all server type id's to be supported by this karaf adapter
	 * otherwise those servers launch configurations will not be displayed correctly
	 */
	static final String[] SERVER_IDS_SUPPORTED = new String[] {
	   // KARAF VERSIONS
	     SERVER_TYPE_PREFIX_KARAF   + "22"
	   , SERVER_TYPE_PREFIX_KARAF   + "23"
	   , SERVER_TYPE_PREFIX_KARAF   + "24"
	   , SERVER_TYPE_PREFIX_KARAF   + "30"
	   , SERVER_TYPE_PREFIX_KARAF   + "40"
	   , SERVER_TYPE_PREFIX_KARAF   + "41"
	   
	   // FUSE ESB VERSIONS
	   , SERVER_TYPE_PREFIX_FUSEESB + "6x"
	   , SERVER_TYPE_PREFIX_FUSEESB + "7x"
		
	   // more server type id's to be added here!
	};

	/**
	 * returns the password
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * returns the port number
	 * 
	 * @return
	 */
	int getPortNumber();

	/**
	 * returns the user name
	 * 
	 * @return
	 */
	String getUserName();
}
