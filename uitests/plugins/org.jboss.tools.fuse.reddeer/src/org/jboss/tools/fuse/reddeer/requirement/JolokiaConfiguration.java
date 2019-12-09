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
package org.jboss.tools.fuse.reddeer.requirement;

import java.io.File;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

/**
 * Requirement configuration for Jolokia properties.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class JolokiaConfiguration implements RequirementConfiguration {

	private String name;
	private String host;
	private String port;
	private String url;
	private String requestType;
	private boolean verifySSL;
	private File jolokiaJarFile;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host != null ? host : "localhost";
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port != null ? port : "8778";
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUrl() {
		return url != null ? url : "http://" + getHost() + ":" + getPort() + "/jolokia/";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public boolean isVerifySSL() {
		return verifySSL;
	}

	public void setVerifySSL(boolean verifySSL) {
		this.verifySSL = verifySSL;
	}

	public File getJolokiaJarFile() {
		return jolokiaJarFile;
	}

	public void setJolokiaJarFile(File jolokiaJarFile) {
		this.jolokiaJarFile = jolokiaJarFile;
	}

	@Override
	public String getId() {
		return getName();
	}
}
