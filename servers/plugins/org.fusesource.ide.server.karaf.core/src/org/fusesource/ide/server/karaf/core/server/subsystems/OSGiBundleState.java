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

import org.eclipse.wst.server.core.IServer;

/**
 * this class maps the osgi bundle states into server state constants for
 * use with module states
 * 
 * @author lhein
 */
public enum OSGiBundleState {
	
	UNKNOWN		("UNKNOWN", 	IServer.STATE_UNKNOWN),		// dummy unknown
	UNINSTALLED	("UNINSTALLED", IServer.STATE_UNKNOWN),		// uninstalled means removed from framework
	INSTALLED	("INSTALLED", 	IServer.STATE_STOPPED),		// installed means not resolved but able to try a start
	RESOLVED	("RESOLVED", 	IServer.STATE_STOPPED),		// resolved means able to start
	STARTING	("STARTING", 	IServer.STATE_STARTING),	// starting
	STOPPING	("STOPPING", 	IServer.STATE_STOPPING),	// stopping
	ACTIVE		("ACTIVE", 		IServer.STATE_STARTED);		// started / running

	private String stateInFramework;
	private int mappedState;
	
	/**
	 * 
	 * @param stateInFramework
	 * @param mappedState
	 */
	private OSGiBundleState(String stateInFramework, int mappedState) {
		this.stateInFramework = stateInFramework;
		this.mappedState = mappedState;
	}
	
	/**
	 * returns the mapped state for a given osgi bundle status
	 * 
	 * @param statusString	the osgi bundle status
	 * @return	the mapped server state or IServer.STATE_UNKNOWN if no match
	 */
	public static int getStatusForString(String statusString) {
		for (OSGiBundleState state : values()) {
			if (state.stateInFramework.equalsIgnoreCase(statusString.trim())) {
				return state.mappedState;
			}
		}
		return IServer.STATE_UNKNOWN;
	}
	
	public static String getStatusText(String status) {
		for (OSGiBundleState state : values()) {
			if (state.stateInFramework.equalsIgnoreCase(status.trim())) {
				return state.name();
			}
		}
		return UNKNOWN.name();
	}
}
