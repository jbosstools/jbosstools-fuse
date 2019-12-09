/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.runtime;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher which decides whether a server is supported or not. The decision is made by a given enumeration of
 * supported server types.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class ServerTypeMatcher extends TypeSafeMatcher<ServerBase> {

	private List<Class<?>> serverTypes;

	/**
	 * Constructs the matcher with a given enumeration of supported server types.
	 * 
	 * @param serverTypes
	 *            supported server types
	 */
	public ServerTypeMatcher(Class<?>... serverTypes) {
		this.serverTypes = Arrays.asList(serverTypes);
	}

	@Override
	public void describeTo(Description description) {

	}

	@Override
	protected boolean matchesSafely(ServerBase server) {
		if (server == null) {
			return false;
		}
		return serverTypes.contains(server.getClass());
	}

}
