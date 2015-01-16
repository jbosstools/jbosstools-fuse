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
package org.fusesource.ide.server.fabric8.core.util;

/**
 * Keep track of the server and runtime id's for the fuse servers
 */
public interface IFabric8ToolingConstants {
	public static final String FABRIC8_VERSION_1x = "1.";

    public static final String RUNTIME_FABRIC8_11 = "org.fusesource.ide.fabric8.runtime.11";
    public static final String RUNTIME_FABRIC8_12 = "org.fusesource.ide.fabric8.runtime.12";
		
	public static final String[] ALL_FABRIC8_RUNTIME_TYPES = new String[]{
		RUNTIME_FABRIC8_11, RUNTIME_FABRIC8_12
	};	

    public static final String SERVER_FABRIC8_11 = "org.fusesource.ide.fabric8.server.11";
    public static final String SERVER_FABRIC8_12 = "org.fusesource.ide.fabric8.server.12";
	
	public static final String[] ALL_FABRIC8_SERVER_TYPES = new String[]{
		SERVER_FABRIC8_11, SERVER_FABRIC8_12
	};
}
