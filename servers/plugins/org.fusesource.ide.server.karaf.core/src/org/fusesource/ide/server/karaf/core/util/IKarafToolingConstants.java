/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.server.karaf.core.util;

/**
 * Keep track of the server and runtime id's for the karaf servers
 */
public interface IKarafToolingConstants {
	public static final String KARAF_VERSION_2X = "2.";
	public static final String KARAF_VERSION_3X = "3.";
	public static final String KARAF_VERSION_4X = "4.";
	
	public static final String RUNTIME_KARAF_PREFIX = "org.fusesource.ide.karaf.runtime.";
	
	public static final String RUNTIME_KARAF_22 = RUNTIME_KARAF_PREFIX + "22";
	public static final String RUNTIME_KARAF_23 = RUNTIME_KARAF_PREFIX + "23";
	public static final String RUNTIME_KARAF_24 = RUNTIME_KARAF_PREFIX + "24";
	public static final String RUNTIME_KARAF_30 = RUNTIME_KARAF_PREFIX + "30";
	public static final String RUNTIME_KARAF_40 = RUNTIME_KARAF_PREFIX + "40";
	public static final String RUNTIME_KARAF_41 = RUNTIME_KARAF_PREFIX + "41";
		
	public static final String[] ALL_KARAF_RUNTIME_TYPES = new String[]{
		RUNTIME_KARAF_22, RUNTIME_KARAF_23, RUNTIME_KARAF_24, RUNTIME_KARAF_30, RUNTIME_KARAF_40, RUNTIME_KARAF_41
	};	

	public static final String SERVER_KARAF_PREFIX = "org.fusesource.ide.karaf.server.";
		
	public static final String SERVER_KARAF_22 = SERVER_KARAF_PREFIX + "22";
    public static final String SERVER_KARAF_23 = SERVER_KARAF_PREFIX + "23";
    public static final String SERVER_KARAF_24 = SERVER_KARAF_PREFIX + "24";
	public static final String SERVER_KARAF_30 = SERVER_KARAF_PREFIX + "30";
	public static final String SERVER_KARAF_40 = SERVER_KARAF_PREFIX + "40";
	public static final String SERVER_KARAF_41 = SERVER_KARAF_PREFIX + "41";
	
	public static final String[] ALL_KARAF_SERVER_TYPES = new String[]{
		SERVER_KARAF_22, SERVER_KARAF_23, SERVER_KARAF_24, SERVER_KARAF_30, SERVER_KARAF_40, SERVER_KARAF_41
	};
}
