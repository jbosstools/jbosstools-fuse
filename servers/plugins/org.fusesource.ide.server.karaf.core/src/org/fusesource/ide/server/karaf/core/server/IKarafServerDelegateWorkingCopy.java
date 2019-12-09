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
public interface IKarafServerDelegateWorkingCopy extends IKarafServerDelegate {
	
	/**
	 * sets the password
	 * 
	 * @param password
	 */
	void setPassword(String password);

	/**
	 * sets the port number
	 * 
	 * @param portNo
	 */
	void setPortNumber(int portNo);

	/**
	 * sets the user name
	 * 
	 * @param userName
	 */
	void setUserName(String userName);
}
