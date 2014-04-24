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
package org.fusesource.ide.server.tests.util;

import org.eclipse.core.runtime.IPath;

/**
 * @author lhein
 */
public final class MockServerCreationUtil {
	
	public static boolean createServerMock(String runtimeId, IPath runtimePath) {
		boolean serverCreated = false;
		
		// create the runtime mock structure
		if (MockRuntimeCreationUtil.createRuntimeMock(runtimeId, runtimePath)) {
			// configure the runtime mock
			serverCreated = configureMockRuntime(runtimeId, runtimePath);
		}
		
		return serverCreated;
	}
	
	private static boolean configureMockRuntime(String runtimeId, IPath runtimePath) {
		boolean serverConfigured = true;
		
		// TODO: add config options
		
		return serverConfigured;
	}
}
