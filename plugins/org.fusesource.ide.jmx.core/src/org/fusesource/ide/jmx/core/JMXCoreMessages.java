/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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

package org.fusesource.ide.jmx.core;

import org.eclipse.osgi.util.NLS;

public class JMXCoreMessages extends NLS {
	public static String ConnectJob;
	public static String ConnectJobFailed;
	public static String DisconnectJob;
	public static String DeleteConnectionJob;
	public static String DisconnectJobFailed;
	public static String ExtensionManagerError1;
	public static String DefaultConnection_ErrorAdding;
	public static String DefaultConnection_ErrorRemoving;
	public static String DefaultConnection_ErrorLoading;
	public static String DefaultConnection_ErrorRunningJMXCode;
	public static String RefreshJob;
	public static String RefreshJobFailed;
	static {
	    NLS.initializeMessages("org.fusesource.ide.jmx.core.JMXCoreMessages", //$NON-NLS-1$
	                    JMXCoreMessages.class);
	}

}
