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
package org.fusesource.ide.server.servicemix.core.util;

/**
 * Keep track of the server and runtime id's for the servicemix servers
 */
public interface IServiceMixToolingConstants {
	public static final String SMX_VERSION_4x = "4.";
	public static final String SMX_VERSION_5x = "5.";

	public static final String RUNTIME_SMX_45 = "org.fusesource.ide.servicemix.runtime.45";
	public static final String RUNTIME_SMX_50 = "org.fusesource.ide.servicemix.runtime.50";
	public static final String RUNTIME_SMX_51 = "org.fusesource.ide.servicemix.runtime.51";
	public static final String RUNTIME_SMX_52 = "org.fusesource.ide.servicemix.runtime.52";
		
	public static final String[] ALL_SMX_RUNTIME_TYPES = new String[]{
		RUNTIME_SMX_45, RUNTIME_SMX_50, RUNTIME_SMX_51, RUNTIME_SMX_52
	};	

	public static final String SERVER_SMX_45 = "org.fusesource.ide.servicemix.server.45";
	public static final String SERVER_SMX_50 = "org.fusesource.ide.servicemix.server.50";
	public static final String SERVER_SMX_51 = "org.fusesource.ide.servicemix.server.51";
	public static final String SERVER_SMX_52 = "org.fusesource.ide.servicemix.server.52";
	
	public static final String[] ALL_SMX_SERVER_TYPES = new String[]{
		SERVER_SMX_45, SERVER_SMX_50, SERVER_SMX_51, SERVER_SMX_52
	};
}
