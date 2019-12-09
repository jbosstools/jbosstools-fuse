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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 * 
 */
public abstract class RuntimeBase implements RequirementConfiguration {

	protected String name;

	private String version;

	private String home;

	private Properties properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getProperty(String key) {
		return properties != null ? properties.getProperty(key) : null;
	}

	public List<String> getProperties(String key) {
		return properties != null ? properties.getProperties(key) : new ArrayList<String>();
	}

	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return value != null ? value : defaultValue;
	}

	public boolean exists() {
		IRuntime[] runtime = ServerCore.getRuntimes();
		for (int i = 0; i < runtime.length; i++) {
			if (runtime[i].getId().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public abstract void create();

	@Override
	public String getId() {
		return null;
	}
}
