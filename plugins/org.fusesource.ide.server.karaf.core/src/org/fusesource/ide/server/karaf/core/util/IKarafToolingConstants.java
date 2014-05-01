/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.server.karaf.core.util;

/**
 * Keep track of the server and runtime id's for the karaf servers
 */
public interface IKarafToolingConstants {
	public static final String KARAF_VERSION_2x = "2.";
	public static final String KARAF_VERSION_3x = "3.";
	
	public static final String RUNTIME_KARAF_22 = "org.fusesource.ide.karaf.runtime.22";
	public static final String RUNTIME_KARAF_23 = "org.fusesource.ide.karaf.runtime.23";
	public static final String RUNTIME_KARAF_30 = "org.fusesource.ide.karaf.runtime.30";
		
	public static final String[] ALL_KARAF_RUNTIME_TYPES = new String[]{
		RUNTIME_KARAF_22, RUNTIME_KARAF_23, RUNTIME_KARAF_30
	};	

	public static final String SERVER_KARAF_22 = "org.fusesource.ide.karaf.server.22";
	public static final String SERVER_KARAF_23 = "org.fusesource.ide.karaf.server.23";
	public static final String SERVER_KARAF_30 = "org.fusesource.ide.karaf.server.30";
	
	public static final String[] ALL_KARAF_SERVER_TYPES = new String[]{
		SERVER_KARAF_22, SERVER_KARAF_23, SERVER_KARAF_30
	};
}
