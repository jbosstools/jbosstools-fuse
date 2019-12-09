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
package org.fusesource.ide.server.karaf.core.server.subsystems;

import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ISubsystemController;

/**
 * @author lhein
 */
public interface IServerPortController extends ISubsystemController {
	public static final String SYSTEM_ID = "ports"; //$NON-NLS-1$

	public static final int KEY_JNDI = 100;
	public static final int KEY_WEB = 101;
	public static final int KEY_PORT_OFFSET = 102;
	public static final int KEY_JMX_RMI = 103;
	public static final int KEY_MANAGEMENT_PORT = 104;
	public static final int KEY_SSH_PORT = 105;

	/**
	 * Get the relevant port
	 * 
	 * @param id				the id of the port
	 * @param defaultValue		a default value
	 * @return
	 */
	public int findPort(int id, int defaultValue);
}
