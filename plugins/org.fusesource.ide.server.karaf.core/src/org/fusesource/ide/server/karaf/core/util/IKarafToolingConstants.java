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
	public static final String RUNTIME_KARAF_20 = "org.fusesource.ide.karaf.runtime.20";
	public static final String RUNTIME_KARAF_21 = "org.fusesource.ide.karaf.runtime.21";
	public static final String RUNTIME_KARAF_22 = "org.fusesource.ide.karaf.runtime.22";
	public static final String RUNTIME_KARAF_23 = "org.fusesource.ide.karaf.runtime.23";
	
	public static final String[] ALL_KARAF_RUNTIME_TYPES = new String[]{
		RUNTIME_KARAF_20, RUNTIME_KARAF_21, RUNTIME_KARAF_22, RUNTIME_KARAF_23
	};
	

	public static final String SERVER_KARAF_20 = "org.fusesource.ide.karaf.server.20";
	public static final String SERVER_KARAF_21 = "org.fusesource.ide.karaf.server.21";
	public static final String SERVER_KARAF_22 = "org.fusesource.ide.karaf.server.22";
	public static final String SERVER_KARAF_23 = "org.fusesource.ide.karaf.server.23";
	
	public static final String[] ALL_KARAF_SERVER_TYPES = new String[]{
		SERVER_KARAF_20, SERVER_KARAF_21, SERVER_KARAF_22, SERVER_KARAF_23
	};

}
