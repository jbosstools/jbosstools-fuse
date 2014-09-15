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

import io.fabric8.api.SshHostConfiguration;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.fusesource.ide.commons.util.Strings;

/**
 * @author lhein
 *
 */
public class CreateSshContainerOptionsDTO extends
		CreateContainerOptionsDTO<CreateSshContainerOptionsDTO> {

	public static final String DEFAULT_PRIVATE_KEY_FILE = System
			.getProperty("user.home")
			+ File.separatorChar
			+ ".ssh"
			+ File.separatorChar + "id_rsa";

	static final int DEFAULT_SSH_RETRIES = 1;
	static final int DEFAULT_SSH_PORT = 22;
	public static final String DEFAULT_PATH = "~/containers/";
	public static final String DEFAULT_USERNAME = "root";

	private final String username;
	private final String password;
	private final String host;
	private final int port;
	private final int sshRetries;
	private final int retryDelay;
	private final String privateKeyFile;
	private final String passPhrase;
	private final String path;
	private final Map<String, String> environmentalVariables; // keep imutable
	private final List<String> fallbackRepositories;
	private final Boolean uploadDistribution;

	/**
	 * 
	 */
	public CreateSshContainerOptionsDTO(String bindAddress, String resolver,
			String globalResolver, String manualIp, BigInteger minimumPort,
			BigInteger maximumPort, Set<String> profiles, String version,
			Map<String, String> dataStoreProperties,
			BigInteger zooKeeperServerPort,
			BigInteger zooKeeperServerConnectionPort, String zookeeperPassword,
			Boolean ensembleStart, Boolean agentEnabled,
			Boolean autoImportEnabled, String importPath,
			Map<String, String> users, String name, String parent,
			String providerType, Boolean ensembleServer,
			String preferredAddress, Map<String, Properties> systemProperties,
			Integer number, URI proxyUri, String zookeeperUrl, String jvmOpts,
			Boolean adminAccess, Boolean clean, String username,
			String password, String host, int port, int sshRetries,
			int retryDelay, String privateKeyFile, String passPhrase,
			String path, Map<String, String> environmentalVariables,
			List<String> fallbackRepositories, Boolean uploadDistribution,
			String jmxUser, String jmxPassword) {
		super(bindAddress, resolver, globalResolver, manualIp, minimumPort,
				maximumPort, profiles, version, dataStoreProperties,
				zooKeeperServerPort, zooKeeperServerConnectionPort,
				zookeeperPassword, ensembleStart, agentEnabled, false,
				(long) 0, autoImportEnabled, importPath, users, name, parent,
				providerType, ensembleServer, preferredAddress,
				systemProperties, number, proxyUri, zookeeperUrl, jvmOpts,
				adminAccess, clean, jmxUser, jmxPassword);
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.sshRetries = sshRetries;
		this.retryDelay = retryDelay;
		this.privateKeyFile = privateKeyFile;
		this.passPhrase = passPhrase;
		this.path = path;
		this.fallbackRepositories = fallbackRepositories;
		this.uploadDistribution = uploadDistribution;
		this.environmentalVariables = Collections
				.unmodifiableMap(new HashMap<String, String>(
						environmentalVariables));
	}

	public CreateContainerOptionsDTO updateCredentials(String newUser,
			String newPassword) {
		return new CreateSshContainerOptionsDTO(getBindAddress(),
				getResolver(), getGlobalResolver(), getManualIp(),
				getMinimumPort(), getMaximumPort(), getProfiles(),
				getVersion(), getDataStoreProperties(),
				getZooKeeperServerPort(), getZooKeeperServerConnectionPort(),
				getZookeeperPassword(), getEnsembleStart(), getAgentEnabled(),
				getAutoImportEnabled(), getImportPath(), getUsers(), getName(),
				getParent(), "ssh", isEnsembleServer(), getPreferredAddress(),
				getSystemProperties(), getNumber(), getProxyUri(),
				getZookeeperUrl(), getJvmOpts(), isAdminAccess(), getClean(),
				newUser != null ? newUser : username,
				newPassword != null ? newPassword : password, host, port,
				sshRetries, retryDelay, privateKeyFile, passPhrase, path,
				environmentalVariables, fallbackRepositories,
				uploadDistribution, getJmxUser(), getJmxPassword());
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getSshRetries() {
		return sshRetries;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getEnvironmentalVariables() {
		return environmentalVariables;
	}

	public Boolean doUploadDistribution() {
		return uploadDistribution;
	}

	public String getPrivateKeyFile() {
		// We check for a parameter first as the privateKeyFile has a default
		// value assigned.
		return privateKeyFile;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public String getHostNameContext() {
		return "none";
	}

	public List<String> getFallbackRepositories() {
		return fallbackRepositories;
	}

	public CreateSshContainerOptionsDTO clone()
			throws CloneNotSupportedException {
		return (CreateSshContainerOptionsDTO) super.clone();
	}

	@Override
	public String toString() {
		return "createSshContainer(" + getUsername() + "@" + getHost() + ":"
				+ getPort() + " " + getPath() + ")";
	}

	public static class Builder extends CreateContainerOptionsDTO.Builder<Builder>{
		private String username;
		private String password;
		private String host;
		private int port = DEFAULT_SSH_PORT;
		private int sshRetries = DEFAULT_SSH_RETRIES;
		private int retryDelay = 1;
		private String privateKeyFile = DEFAULT_PRIVATE_KEY_FILE;
		private String passPhrase;
		private String path = DEFAULT_PATH;
		private Map<String, String> environmentalVariables = new HashMap<String, String>();
		private List<String> fallbackRepositories = new ArrayList<String>();
		private Boolean uploadDistribution = true;

		public Builder username(final String username) {
			this.username = username;
			return this;
		}

		public Builder password(final String password) {
			this.password = password;
			return this;
		}

		public Builder host(final String host) {
			this.host = host;
			return this;
		}

		public Builder port(int port) {
			this.port = port;
			return this;
		}

		public Builder path(final String path) {
			this.path = path;
			return this;
		}

		public Builder environmentalVariables(
				Map<String, String> environmentalVariables) {
			this.environmentalVariables = environmentalVariables;
			return this;
		}

		public Builder environmentalVariable(String key, String value) {
			this.environmentalVariables.put(key, value);
			return this;
		}

		public Builder environmentalVariable(String entry) {
			if (entry.contains("=")) {
				String key = entry.substring(0, entry.indexOf("="));
				String value = entry.substring(entry.indexOf("=") + 1);
				environmentalVariable(key, value);
			}
			return this;
		}

		public Builder environmentalVariable(List<String> entries) {
			if (entries != null) {
				for (String entry : entries) {
					environmentalVariable(entry);
				}
			}
			return this;
		}

		public Builder sshRetries(int sshRetries) {
			this.sshRetries = sshRetries;
			return this;
		}

		public Builder retryDelay(int retryDelay) {
			this.retryDelay = retryDelay;
			return this;
		}

		public Builder privateKeyFile(final String privateKeyFile) {
			this.privateKeyFile = privateKeyFile;
			return this;
		}

		public Builder passPhrase(final String passPhrase) {
			this.passPhrase = passPhrase;
			return this;
		}

		public Builder fallbackRepositories(
				final List<String> fallbackRepositories) {
			this.fallbackRepositories = fallbackRepositories;
			return this;
		}

		public Builder uploadDistribution(final Boolean uploadDistribution) {
			this.uploadDistribution = uploadDistribution;
			return this;
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

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getSshRetries() {
			return sshRetries;
		}

		public void setSshRetries(int sshRetries) {
			this.sshRetries = sshRetries;
		}

		public int getRetryDelay() {
			return retryDelay;
		}

		public void setRetryDelay(int retryDelay) {
			this.retryDelay = retryDelay;
		}

		public String getPrivateKeyFile() {
			return privateKeyFile;
		}

		public void setPrivateKeyFile(String privateKeyFile) {
			this.privateKeyFile = privateKeyFile;
		}

		public String getPassPhrase() {
			return passPhrase;
		}

		public void setPassPhrase(String passPhrase) {
			this.passPhrase = passPhrase;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public void setEnvironmentalVariables(
				Map<String, String> environmentalVariables) {
			this.environmentalVariables = environmentalVariables;
		}

		public List<String> getFallbackRepositories() {
			return fallbackRepositories;
		}

		public void setFallbackRepositories(List<String> fallbackRepositories) {
			this.fallbackRepositories = fallbackRepositories;
		}

		public Boolean getUploadDistribution() {
			return uploadDistribution;
		}

		public void setUploadDistribution(Boolean uploadDistribution) {
			this.uploadDistribution = uploadDistribution;
		}

		public CreateSshContainerOptionsDTO build() {
			return new CreateSshContainerOptionsDTO(getBindAddress(),
					getResolver(), getGlobalResolver(), getManualIp(),
					getMinimumPort(), getMaximumPort(), getProfiles(),
					getVersion(), getDataStoreProperties(),
					getZooKeeperServerPort(),
					getZooKeeperServerConnectionPort(), getZookeeperPassword(),
					getEnsembleStart(), getAgentEnabled(),
					getAutoImportEnabled(), getImportPath(), getUsers(),
					getName(), getParent(), "ssh", isEnsembleServer(),
					getPreferredAddress(), getSystemProperties(), getNumber(),
					getProxyUri(), getZookeeperUrl(), getJvmOpts(),
					isAdminAccess(), false, username, password, host, port,
					sshRetries, retryDelay, privateKeyFile, passPhrase, path,
					environmentalVariables, fallbackRepositories,
					uploadDistribution, getJmxUser(), getJmxPassword());
		}

		/**
		 * Configures the builder from the requirements and chosen host
		 * configuration
		 */
		public void configure(SshHostConfiguration sshHostConfig,
				RequirementsDTO requirements,
				ProfileRequirementsDTO profileRequirements) {
			SshConfigurationDTO sshHosts = requirements.getSshConfiguration();
			host = sshHostConfig.getHostName();
			if (Strings.isBlank(host)) {
				throw new IllegalArgumentException(
						"Missing host property in the ssh configuration: "
								+ sshHostConfig);
			}
			String preferredAddress = getPreferredAddress();
			if (Strings.isBlank(preferredAddress)) {
				preferredAddress = sshHostConfig.getPreferredAddress();
				if (Strings.isBlank(preferredAddress)) {
					preferredAddress = host;
				}
				preferredAddress(preferredAddress);
			}
			path = sshHostConfig.getPath();
			if (Strings.isBlank(path)) {
				if (sshHosts != null) {
					path = sshHosts.getDefaultPath();
				}
				if (Strings.isBlank(path)) {
					path = DEFAULT_PATH;
				}
			}
			Integer portValue = sshHostConfig.getPort();
			if (portValue == null) {
				if (sshHosts != null) {
					portValue = sshHosts.getDefaultPort();
				}
			}
			port = portValue != null ? portValue : DEFAULT_SSH_PORT;
			username = sshHostConfig.getUsername();
			if (Strings.isBlank(username)) {
				if (sshHosts != null) {
					username = sshHosts.getDefaultUsername();
				}
				if (Strings.isBlank(username)) {
					username = DEFAULT_USERNAME;
				}
			}
			password = sshHostConfig.getPassword();
			if (Strings.isBlank(password)) {
				if (sshHosts != null) {
					password = sshHosts.getDefaultPassword();
				}
			}
			if (sshHosts != null) {
				fallbackRepositories = sshHosts.getFallbackRepositories();
			}
			passPhrase = sshHostConfig.getPassPhrase();
			if (Strings.isBlank(passPhrase)) {
				if (sshHosts != null) {
					passPhrase = sshHosts.getDefaultPassPhrase();
				}
			}
			privateKeyFile = sshHostConfig.getPrivateKeyFile();
			if (Strings.isBlank(privateKeyFile)) {
				if (sshHosts != null) {
					privateKeyFile = sshHosts.getDefaultPrivateKeyFile();
				}
				if (Strings.isBlank(privateKeyFile)) {
					privateKeyFile = DEFAULT_PRIVATE_KEY_FILE;
				}
			}
		}
	}
}
