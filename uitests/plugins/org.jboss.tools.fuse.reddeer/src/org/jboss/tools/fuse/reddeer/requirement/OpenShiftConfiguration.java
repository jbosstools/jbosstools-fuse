/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.requirement;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

/**
 * Requirement configuration for OpenShift properties.
 * 
 * @author tsedmik
 */
public class OpenShiftConfiguration implements RequirementConfiguration {

	private String host;
	private String port;
	private String oc;
	private String username;
	private String password;
	private String protocol;
	private boolean overrideOC;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getOc() {
		return oc;
	}

	public void setOc(String oc) {
		this.oc = oc;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean getOverrideOC() {
		return overrideOC;
	}

	public void setOverrideOC(boolean overrideOC) {
		this.overrideOC = overrideOC;
	}

	@Override
	public String getId() {
		return host + ":" + port;
	}
}
