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
package org.fusesource.ide.server.fuse.core.util;

/**
 * Keep track of the server and runtime id's for the fuse servers
 */
public class FuseToolingConstants {
	public static final String FUSE_VERSION_6X = "6.";
	public static final String FUSE_VERSION_7X = "7.";

	public static final String RUNTIME_FUSE_60 = "org.fusesource.ide.fuseesb.runtime.60";
	public static final String RUNTIME_FUSE_61 = "org.fusesource.ide.fuseesb.runtime.61";
    public static final String RUNTIME_FUSE_62 = "org.fusesource.ide.fuseesb.runtime.62";
    public static final String RUNTIME_FUSE_63 = "org.fusesource.ide.fuseesb.runtime.63";
	
    public static final String RUNTIME_FUSE_70 = "org.fusesource.ide.fuseesb.runtime.70";
    
	public static final String[] ALL_FUSE_RUNTIME_TYPES = new String[]{
		RUNTIME_FUSE_60, RUNTIME_FUSE_61, RUNTIME_FUSE_62, RUNTIME_FUSE_63, RUNTIME_FUSE_70
	};	

	public static final String SERVER_FUSE_60 = "org.fusesource.ide.fuseesb.server.60";
	public static final String SERVER_FUSE_61 = "org.fusesource.ide.fuseesb.server.61";
    public static final String SERVER_FUSE_62 = "org.fusesource.ide.fuseesb.server.62";
    public static final String SERVER_FUSE_63 = "org.fusesource.ide.fuseesb.server.63";
	
    public static final String SERVER_FUSE_70 = "org.fusesource.ide.fuseesb.server.70";
    
	public static final String[] ALL_FUSE_SERVER_TYPES = new String[]{
		SERVER_FUSE_60, SERVER_FUSE_61, SERVER_FUSE_62, SERVER_FUSE_63, SERVER_FUSE_70
	};
	
	private FuseToolingConstants() {
		// util class
	}
}
