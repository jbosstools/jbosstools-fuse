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
package org.fusesource.ide.fabric8.core.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lhein
 *
 */
public class SshConfigurationDTO {
	private List<SshHostConfigurationDTO> hosts = new ArrayList<>();
	private String defaultPath;
	private Integer defaultPort;
	private String defaultUsername;
	private String defaultPassword;
	private List<String> fallbackRepositories;
	private String defaultPassPhrase;
	private String defaultPrivateKeyFile;

	public SshHostConfigurationDTO getHost(String hostName) {
		if (hosts != null) {
			for (SshHostConfigurationDTO host : hosts) {
				if (hostName.equals(host.getHostName())) {
					return host;
				}
			}
		}
		return null;
	}

	public void addHost(SshHostConfigurationDTO configuration) {
		if (hosts == null) {
			hosts = new ArrayList<>();
		}
		hosts.add(configuration);
	}

	// Fluid API to make configuration easier
	// -------------------------------------------------------------------------
	/**
	 * Returns the host configuration for the given host name; lazily creating a
	 * new one if one does not exist yet
	 */
	public SshHostConfigurationDTO host(String hostName) {
		SshHostConfigurationDTO answer = getHost(hostName);
		if (answer == null) {
			answer = new SshHostConfigurationDTO(hostName);
			addHost(answer);
		}
		return answer;
	}

	public SshConfigurationDTO defaultPort(Integer defaultPort) {
		setDefaultPort(defaultPort);
		return this;
	}

	public SshConfigurationDTO defaultPath(String defaultPath) {
		setDefaultPath(defaultPath);
		return this;
	}

	public SshConfigurationDTO defaultUsername(final String defaultUsername) {
		this.defaultUsername = defaultUsername;
		return this;
	}

	public SshConfigurationDTO defaultPassword(final String defaultPassword) {
		this.defaultPassword = defaultPassword;
		return this;
	}

	public SshConfigurationDTO defaultPassPhrase(final String defaultPassPhrase) {
		this.defaultPassPhrase = defaultPassPhrase;
		return this;
	}

	public SshConfigurationDTO defaultPrivateKeyFile(
			final String defaultPrivateKeyFile) {
		this.defaultPrivateKeyFile = defaultPrivateKeyFile;
		return this;
	}

	public SshConfigurationDTO fallbackRepositories(
			final List<String> fallbackRepositories) {
		this.fallbackRepositories = fallbackRepositories;
		return this;
	}

	public SshConfigurationDTO fallbackRepositories(
			final String... fallbackRepositories) {
		return fallbackRepositories(Arrays.asList(fallbackRepositories));
	}

	// Properties
	// -------------------------------------------------------------------------
	public List<SshHostConfigurationDTO> getHosts() {
		return hosts;
	}

	public void setHosts(List<SshHostConfigurationDTO> hosts) {
		this.hosts = hosts;
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}

	public Integer getDefaultPort() {
		return defaultPort;
	}

	public void setDefaultPort(Integer defaultPort) {
		this.defaultPort = defaultPort;
	}

	public String getDefaultUsername() {
		return defaultUsername;
	}

	public void setDefaultUsername(String defaultUsername) {
		this.defaultUsername = defaultUsername;
	}

	public String getDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public List<String> getFallbackRepositories() {
		return fallbackRepositories;
	}

	public void setFallbackRepositories(List<String> fallbackRepositories) {
		this.fallbackRepositories = fallbackRepositories;
	}

	public String getDefaultPassPhrase() {
		return defaultPassPhrase;
	}

	public void setDefaultPassPhrase(String defaultPassPhrase) {
		this.defaultPassPhrase = defaultPassPhrase;
	}

	public String getDefaultPrivateKeyFile() {
		return defaultPrivateKeyFile;
	}

	public void setDefaultPrivateKeyFile(String defaultPrivateKeyFile) {
		this.defaultPrivateKeyFile = defaultPrivateKeyFile;
	}
}
