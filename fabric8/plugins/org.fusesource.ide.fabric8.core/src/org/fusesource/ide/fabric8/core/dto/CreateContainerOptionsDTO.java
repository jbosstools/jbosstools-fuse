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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.fusesource.ide.commons.util.Strings;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public class CreateContainerOptionsDTO<T extends CreateContainerOptionsDTO> {

	private static final String JSON_ZK_SERVER_PORT = "zooKeeperServerPort";
	private static final String JSON_ZK_SERVER_CONNECTION_PORT = "zooKeeperServerConnectionPort";
	private static final String JSON_ZK_SERVER_TICK = "zooKeeperServerTickTime";
	private static final String JSON_ZK_SERVER_INIT_LIMIT = "zooKeeperServerInitLimit";
	private static final String JSON_ZK_SERVER_SYNC_LIMIT = "zooKeeperServerSyncLimit";
	private static final String JSON_ZK_SERVER_DATA_DIR = "zooKeeperServerDataDir";
	private static final String JSON_ZK_PASSWORD = "zookeeperPassword";
	private static final String JSON_ENSEMBLE_START = "ensembleStart";
	private static final String JSON_AGENT_ENABLED = "agentEnabled";
	private static final String JSON_WAIT_FOR_PROVISION = "waitForProvision";
	private static final String JSON_BOOTSTRAP_TIMEOUT = "bootstrapTimeout";
	private static final String JSON_MIGRATION_TIMEOUT = "migrationTimeout";
	private static final String JSON_AUTO_IMPORT_ENABLED = "autoImportEnabled";
	private static final String JSON_IMPORT_PATH = "importPath";
	private static final String JSON_CLEAN = "clean";
	private static final String JSON_USERS = "users";
	private static final String JSON_JMX_USER = "jmxUser";
	private static final String JSON_JMX_PASSWORD = "jmxPassword";
	private static final String JSON_NAME = "name";
	private static final String JSON_PARENT = "parent";
	private static final String JSON_PROVIDER_TYPE = "providerType";
	private static final String JSON_ENSEMBLE_SERVER = "ensembleServer";
	private static final String JSON_PREFERRED_ADDRESS = "preferredAddress";
	private static final String JSON_NUMBER = "number";
	private static final String JSON_PROXY_URI = "proxyUri";
	private static final String JSON_ZK_URL = "zookeeperUrl";
	private static final String JSON_JVM_OPTS = "jvmOpts";
	private static final String JSON_ADMIN_ACCESS = "adminAccess";
	private static final String JSON_SYSTEM_PROPERTIES = "systemProperties";
	private static final String JSON_META_DATA_MAP = "metadataMap";
	private static final String JSON_BIND_ADDRESS = "bindAddress";
	private static final String JSON_RESOLVER = "resolver";
	private static final String JSON_GLOBAL_RESOLVER = "globalResolver";
	private static final String JSON_MANUAL_IP = "manualIp";
	private static final String JSON_MIN_PORT = "minimumPort";
	private static final String JSON_MAX_PORT = "maximumPort";
	private static final String JSON_VERSION = "version";
	private static final String JSON_PROFILES = "profiles";
	private static final String JSON_DATA_STORE_PROPS = "dataStoreProperties";

	public static final String AGENT_AUTOSTART = "agent.auto.start";
	public static final String ENSEMBLE_AUTOSTART = "ensemble.auto.start";
	public static final String PROFILES_AUTOIMPORT = "profiles.auto.import";
	public static final String PROFILES_AUTOIMPORT_PATH = "profiles.auto.import.path";
	public static final String DEFAULT_IMPORT_PATH = "fabric"
			+ File.separatorChar + "import";
	public static final String ZOOKEEPER_PASSWORD = "zookeeper.password";
	public static final String ZOOKEEPER_SERVER_PORT = "zookeeper.server.port";
	public static final String ZOOKEEPER_SERVER_CONNECTION_PORT = "zookeeper.server.connection.port";
	public static final String ROLE_DELIMITER = ",";
	public static final long DEFAULT_MIGRATION_TIMEOUT = 120000L;
	public static final int DEFAULT_TICKTIME = 2000;
	public static final int DEFAULT_INIT_LIMIT = 10;
	public static final int DEFAULT_SYNC_LIMIT = 5;
	public static final String DEFAULT_DATA_DIR = "data/zookeeper";
	public static final String BIND_ADDRESS = "bind.address";
	public static final String PROFILES = "profiles";
	public static final String VERSION = "version";
	public static final String DEFAULT_VERSION = "1.0";

	private BigInteger zooKeeperServerPort;
	private BigInteger zooKeeperServerConnectionPort;
	private BigInteger zooKeeperServerTickTime;
	private BigInteger zooKeeperServerInitLimit;
	private BigInteger zooKeeperServerSyncLimit;
	private String zooKeeperServerDataDir;
	private String zookeeperPassword;
	private Boolean ensembleStart;
	private Boolean agentEnabled;
	private Boolean waitForProvision;
	private Long bootstrapTimeout;
	private Long migrationTimeout;
	private Boolean autoImportEnabled;
	private String importPath;
	private Boolean clean;
	private Map<String, String> users; // keep immutable
	private String jmxUser;
	private String jmxPassword;
	private String name;
	private String parent;
	private String providerType;
	private Boolean ensembleServer;
	private String preferredAddress;
	private Integer number;
	private URI proxyUri;
	private String zookeeperUrl;
	private String jvmOpts;
	private Boolean adminAccess;
	private Map<String, Properties> systemProperties;
	private Map<String, CreateContainerMetadataDTO> metadataMap = new HashMap<String, CreateContainerMetadataDTO>();
	private String bindAddress;
	private String resolver;
	private String globalResolver;
	private String manualIp;
	private BigInteger minimumPort;
	private BigInteger maximumPort;
	private String version;
	private Set<String> profiles;
	private Map<String, String> dataStoreProperties;

	/**
	 * 
	 * @param bindAddress
	 * @param resolver
	 * @param globalResolver
	 * @param manualIp
	 * @param minimumPort
	 * @param maximumPort
	 * @param profiles
	 * @param version
	 * @param dataStoreProperties
	 * @param getZooKeeperServerPort
	 * @param zooKeeperServerConnectionPort
	 * @param zookeeperPassword
	 * @param ensembleStart
	 * @param agentEnabled
	 * @param waitForProvision
	 * @param provisionTimeout
	 * @param autoImportEnabled
	 * @param importPath
	 * @param users
	 * @param name
	 * @param parent
	 * @param providerType
	 * @param ensembleServer
	 * @param preferredAddress
	 * @param systemProperties
	 * @param number
	 * @param proxyUri
	 * @param zookeeperUrl
	 * @param jvmOpts
	 * @param adminAccess
	 * @param clean
	 */
	public CreateContainerOptionsDTO(String bindAddress, String resolver,
			String globalResolver, String manualIp, BigInteger minimumPort,
			BigInteger maximumPort, Set<String> profiles, String version,
			Map<String, String> dataStoreProperties,
			BigInteger getZooKeeperServerPort,
			BigInteger zooKeeperServerConnectionPort, String zookeeperPassword,
			Boolean ensembleStart, Boolean agentEnabled,
			Boolean waitForProvision, Long provisionTimeout,
			Boolean autoImportEnabled, String importPath,
			Map<String, String> users, String name, String parent,
			String providerType, Boolean ensembleServer,
			String preferredAddress, Map<String, Properties> systemProperties,
			Integer number, URI proxyUri, String zookeeperUrl, String jvmOpts,
			Boolean adminAccess, Boolean clean, String jmxUser,
			String jmxPassword) {
		this.bindAddress = bindAddress;
		this.resolver = resolver;
		this.globalResolver = globalResolver;
		this.manualIp = manualIp;
		this.minimumPort = minimumPort;
		this.maximumPort = maximumPort;
		this.version = version;
		this.profiles = profiles;
		this.dataStoreProperties = dataStoreProperties != null ? dataStoreProperties : Collections.<String, String> emptyMap();
		this.zooKeeperServerPort = getZooKeeperServerPort;
		this.zooKeeperServerConnectionPort = zooKeeperServerConnectionPort;
		this.zooKeeperServerTickTime = BigInteger.valueOf(DEFAULT_TICKTIME);
		this.zooKeeperServerInitLimit = BigInteger.valueOf(DEFAULT_INIT_LIMIT);
		this.zooKeeperServerSyncLimit = BigInteger.valueOf(DEFAULT_SYNC_LIMIT);
		this.zooKeeperServerDataDir = DEFAULT_DATA_DIR;
		this.zookeeperPassword = zookeeperPassword;
		this.ensembleStart = ensembleStart;
		this.agentEnabled = agentEnabled;
		this.waitForProvision = waitForProvision;
		this.bootstrapTimeout = provisionTimeout;
		this.migrationTimeout = DEFAULT_MIGRATION_TIMEOUT;
		this.autoImportEnabled = autoImportEnabled;
		this.importPath = importPath;
		this.users = (users != null) ? Collections
				.unmodifiableMap(new HashMap<String, String>(users))
				: new HashMap<String, String>();
		this.clean = clean;
		this.name = name;
		this.parent = parent;
		this.providerType = providerType;
		this.ensembleServer = ensembleServer;
		this.preferredAddress = preferredAddress;
		this.number = number;
		this.proxyUri = proxyUri;
		this.zookeeperUrl = zookeeperUrl;
		this.jvmOpts = jvmOpts;
		this.adminAccess = adminAccess;
		this.systemProperties = systemProperties;
		this.jmxUser = jmxUser;
		this.jmxPassword = jmxPassword;
	}

	public String getProviderType() {
		return providerType;
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return parent;
	}

	public Boolean isEnsembleServer() {
		return ensembleServer;
	}

	public String getPreferredAddress() {
		return preferredAddress;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public String getResolver() {
		return resolver;
	}

	public String getManualIp() {
		return manualIp;
	}

	public BigInteger getMinimumPort() {
		return minimumPort;
	}

	public BigInteger getMaximumPort() {
		return maximumPort;
	}

	public Map<String, Properties> getSystemProperties() {
		return systemProperties;
	}

	public Integer getNumber() {
		return number;
	}

	public URI getProxyUri() {
		return proxyUri;
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public String getZookeeperPassword() {
		return zookeeperPassword;
	}

	public String getJvmOpts() {
		return jvmOpts;
	}

	public Boolean isAdminAccess() {
		return adminAccess;
	}

	public Map<String, CreateContainerMetadataDTO> getMetadataMap() {
		return metadataMap;
	}

	public String getVersion() {
		return version;
	}

	public Set<String> getProfiles() {
		return profiles;
	}

	public String getJmxUser() {
		return jmxUser;
	}

	public String getJmxPassword() {
		return jmxPassword;
	}

	/**
	 * @return the zooKeeperServerPort
	 */
	public BigInteger getZooKeeperServerPort() {
		return this.zooKeeperServerPort;
	}

	/**
	 * @return the zooKeeperServerConnectionPort
	 */
	public BigInteger getZooKeeperServerConnectionPort() {
		return this.zooKeeperServerConnectionPort;
	}

	/**
	 * @return the ensembleStart
	 */
	public Boolean getEnsembleStart() {
		return this.ensembleStart;
	}

	/**
	 * @return the agentEnabled
	 */
	public Boolean getAgentEnabled() {
		return this.agentEnabled;
	}

	/**
	 * @return the waitForProvision
	 */
	public Boolean getWaitForProvision() {
		return this.waitForProvision;
	}

	/**
	 * @return the bootstrapTimeout
	 */
	public Long getBootstrapTimeout() {
		return this.bootstrapTimeout;
	}

	/**
	 * @return the autoImportEnabled
	 */
	public Boolean getAutoImportEnabled() {
		return this.autoImportEnabled;
	}

	/**
	 * @return the importPath
	 */
	public String getImportPath() {
		return this.importPath;
	}

	/**
	 * @return the clean
	 */
	public Boolean getClean() {
		return this.clean;
	}

	/**
	 * @return the users
	 */
	public Map<String, String> getUsers() {
		return this.users;
	}

	/**
	 * @return the ensembleServer
	 */
	public Boolean getEnsembleServer() {
		return this.ensembleServer;
	}

	/**
	 * @return the globalResolver
	 */
	public String getGlobalResolver() {
		return this.globalResolver;
	}

	/**
	 * @return the dataStoreProperties
	 */
	public Map<String, String> getDataStoreProperties() {
		return this.dataStoreProperties;
	}

	/**
	 * @return the zooKeeperServerDataDir
	 */
	public String getZooKeeperServerDataDir() {
		return this.zooKeeperServerDataDir;
	}

	/**
	 * @return the zooKeeperServerTickTime
	 */
	public BigInteger getZooKeeperServerTickTime() {
		return this.zooKeeperServerTickTime;
	}

	/**
	 * @return the zooKeeperServerInitLimit
	 */
	public BigInteger getZooKeeperServerInitLimit() {
		return this.zooKeeperServerInitLimit;
	}

	/**
	 * @return the zooKeeperServerSyncLimit
	 */
	public BigInteger getZooKeeperServerSyncLimit() {
		return this.zooKeeperServerSyncLimit;
	}

	/**
	 * @return the migrationTimeout
	 */
	public Long getMigrationTimeout() {
		return this.migrationTimeout;
	}

	/**
	 * @return the adminAccess
	 */
	public Boolean getAdminAccess() {
		return this.adminAccess;
	}

	public CreateContainerOptionsDTO updateCredentials(String newJmxUser,
			String newJmxPassword) {
		return new CreateContainerOptionsDTO(bindAddress, resolver,
				globalResolver, manualIp, minimumPort, maximumPort, profiles,
				version, dataStoreProperties, zooKeeperServerPort,
				zooKeeperServerConnectionPort, zookeeperPassword,
				ensembleStart, agentEnabled, waitForProvision,
				bootstrapTimeout, autoImportEnabled, importPath, users, name,
				parent, "child", ensembleServer, preferredAddress,
				systemProperties, number, proxyUri, zookeeperUrl, jvmOpts,
				adminAccess, clean, newJmxUser != null ? newJmxUser : jmxUser,
				newJmxPassword != null ? newJmxPassword : jmxPassword);
	}

	public static Builder<? extends Builder<?>> builder() {
		return new Builder();
	}

	public ModelNode toJson() {
		ModelNode root = new ModelNode();

		if (!Strings.isBlank(getName()))
			root.get(JSON_NAME).set(getName());
		if (!Strings.isBlank(getParent()))
			root.get(JSON_PARENT).set(getParent());
		if (!Strings.isBlank(getVersion()))
			root.get(JSON_VERSION).set(getVersion());
		if (!Strings.isBlank(getZookeeperUrl()))
			root.get(JSON_ZK_URL).set(getZookeeperUrl());
		if (!Strings.isBlank(getZookeeperPassword()))
			root.get(JSON_ZK_PASSWORD).set(getZookeeperPassword());
		if (!Strings.isBlank(getJmxUser()))
			root.get(JSON_JMX_USER).set(getJmxUser());
		if (!Strings.isBlank(getJmxPassword()))
			root.get(JSON_JMX_PASSWORD).set(getJmxPassword());
		if (getProxyUri() != null)
			root.get(JSON_PROXY_URI).set(getProxyUri().toString());
		if (getZooKeeperServerTickTime() != null)
			root.get(JSON_ZK_SERVER_TICK).set(getZooKeeperServerTickTime());
		if (getZooKeeperServerInitLimit() != null)
			root.get(JSON_ZK_SERVER_INIT_LIMIT).set(
					getZooKeeperServerInitLimit());
		if (getZooKeeperServerSyncLimit() != null)
			root.get(JSON_ZK_SERVER_SYNC_LIMIT).set(
					getZooKeeperServerSyncLimit());
		if (!Strings.isBlank(getZooKeeperServerDataDir()))
			root.get(JSON_ZK_SERVER_DATA_DIR).set(getZooKeeperServerDataDir());
		if (getMigrationTimeout() != null)
			root.get(JSON_MIGRATION_TIMEOUT).set(getMigrationTimeout());
		if (!Strings.isBlank(getProviderType()))
			root.get(JSON_PROVIDER_TYPE).set(getProviderType());
		if (getAdminAccess() != null)
			root.get(JSON_ADMIN_ACCESS).set(getAdminAccess());

		ModelNode listNode = root.get(JSON_PROFILES).setEmptyList();
		for (String entry : getProfiles()) {
			listNode.add(entry);
		}

		return root;
	}

	/**
	 * returns the object values as a map
	 * 
	 * @return
	 */
	public Map<String, Object> asMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

	    BeanInfo info = Introspector.getBeanInfo(this.getClass());
	    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
	    	if (pd.getName().equalsIgnoreCase("class")) continue;
	        Method reader = pd.getReadMethod();
	        if (reader != null) {
	        	Object val = reader.invoke(this);
	            if (val != null) map.put(pd.getName(), val);
	        }
	    }

		return map;
	}

	public static class Builder<B extends Builder<?>> {
		private BigInteger zooKeeperServerPort;
		private BigInteger zooKeeperServerConnectionPort;
		private BigInteger zooKeeperServerTickTime;
		private BigInteger zooKeeperServerInitLimit;
		private BigInteger zooKeeperServerSyncLimit;
		private String zooKeeperServerDataDir;
		private String zookeeperPassword;
		private Boolean ensembleStart;
		private Boolean agentEnabled;
		private Boolean waitForProvision;
		private Long bootstrapTimeout;
		private Long migrationTimeout;
		private Boolean autoImportEnabled;
		private String importPath;
		private Boolean clean;
		private Map<String, String> users; // keep immutable
		private String jmxUser;
		private String jmxPassword;
		private String name;
		private String parent;
		private String providerType;
		private Boolean ensembleServer;
		private String preferredAddress;
		private Integer number;
		private URI proxyUri;
		private String zookeeperUrl;
		private String jvmOpts;
		private Boolean adminAccess;
		private Map<String, Properties> systemProperties;
		private Map<String, CreateContainerMetadataDTO> metadataMap = new HashMap<String, CreateContainerMetadataDTO>();
		private String bindAddress;
		private String resolver;
		private String globalResolver;
		private String manualIp;
		private BigInteger minimumPort;
		private BigInteger maximumPort;
		private String version;
		private Set<String> profiles;
		private Map<String, String> dataStoreProperties;

		/**
		 * @return the zooKeeperServerConnectionPort
		 */
		public BigInteger getZooKeeperServerConnectionPort() {
			return this.zooKeeperServerConnectionPort;
		}

		/**
		 * @return the zooKeeperServerTickTime
		 */
		public BigInteger getZooKeeperServerTickTime() {
			return this.zooKeeperServerTickTime;
		}

		/**
		 * @return the zooKeeperServerInitLimit
		 */
		public BigInteger getZooKeeperServerInitLimit() {
			return this.zooKeeperServerInitLimit;
		}

		/**
		 * @return the zooKeeperServerSyncLimit
		 */
		public BigInteger getZooKeeperServerSyncLimit() {
			return this.zooKeeperServerSyncLimit;
		}

		/**
		 * @return the zooKeeperServerDataDir
		 */
		public String getZooKeeperServerDataDir() {
			return this.zooKeeperServerDataDir;
		}

		/**
		 * @return the ensembleStart
		 */
		public Boolean getEnsembleStart() {
			return this.ensembleStart;
		}

		/**
		 * @return the agentEnabled
		 */
		public Boolean getAgentEnabled() {
			return this.agentEnabled;
		}

		/**
		 * @return the waitForProvision
		 */
		public Boolean getWaitForProvision() {
			return this.waitForProvision;
		}

		/**
		 * @return the bootstrapTimeout
		 */
		public Long getBootstrapTimeout() {
			return this.bootstrapTimeout;
		}

		/**
		 * @return the migrationTimeout
		 */
		public Long getMigrationTimeout() {
			return this.migrationTimeout;
		}

		/**
		 * @return the autoImportEnabled
		 */
		public Boolean getAutoImportEnabled() {
			return this.autoImportEnabled;
		}

		/**
		 * @return the importPath
		 */
		public String getImportPath() {
			return this.importPath;
		}

		/**
		 * @return the clean
		 */
		public Boolean getClean() {
			return this.clean;
		}

		/**
		 * @return the users
		 */
		public Map<String, String> getUsers() {
			return this.users;
		}

		/**
		 * @return the ensembleServer
		 */
		public Boolean getEnsembleServer() {
			return this.ensembleServer;
		}

		/**
		 * @return the adminAccess
		 */
		public Boolean getAdminAccess() {
			return this.adminAccess;
		}

		/**
		 * @return the bindAddress
		 */
		public String getBindAddress() {
			return this.bindAddress;
		}

		/**
		 * @return the resolver
		 */
		public String getResolver() {
			return this.resolver;
		}

		/**
		 * @return the globalResolver
		 */
		public String getGlobalResolver() {
			return this.globalResolver;
		}

		/**
		 * @return the manualIp
		 */
		public String getManualIp() {
			return this.manualIp;
		}

		/**
		 * @return the minimumPort
		 */
		public BigInteger getMinimumPort() {
			return this.minimumPort;
		}

		/**
		 * @return the maximumPort
		 */
		public BigInteger getMaximumPort() {
			return this.maximumPort;
		}

		/**
		 * @return the dataStoreProperties
		 */
		public Map<String, String> getDataStoreProperties() {
			return this.dataStoreProperties;
		}

		public B preferredAddress(final String preferredAddress) {
			this.preferredAddress = preferredAddress;
			return (B) this;
		}

		public B ensembleServer(final boolean ensembleServer) {
			this.ensembleServer = ensembleServer;
			return (B) this;
		}

		public B number(final int number) {
			this.number = number;
			return (B) this;
		}

		public B name(final String name) {
			this.name = name;
			return (B) this;
		}

		public B parent(final String parent) {
			this.parent = parent;
			return (B) this;
		}

		public B providerType(final String providerType) {
			this.providerType = providerType;
			return (B) this;
		}

		public B zookeeperUrl(final String zookeeperUrl) {
			this.zookeeperUrl = zookeeperUrl;
			return (B) this;
		}

		public B proxyUri(final URI proxyUri) {
			this.proxyUri = proxyUri;
			return (B) this;
		}

		public B proxyUri(final String proxyUri) throws URISyntaxException {
			this.proxyUri = new URI(proxyUri);
			return (B) this;
		}

		public B jvmOpts(final String jvmOpts) {
			this.jvmOpts = jvmOpts;
			return (B) this;
		}

		public B adminAccess(final boolean adminAccess) {
			this.adminAccess = adminAccess;
			return (B) this;
		}

		public String getName() {
			return name;
		}

		public String getParent() {
			return parent;
		}

		public String getProviderType() {
			return providerType;
		}

		public Boolean isEnsembleServer() {
			return ensembleServer;
		}

		public String getPreferredAddress() {
			return preferredAddress;
		}

		public Map<String, Properties> getSystemProperties() {
			return systemProperties;
		}

		public Integer getNumber() {
			return number;
		}

		public URI getProxyUri() {
			return proxyUri;
		}

		public String getZookeeperUrl() {
			return zookeeperUrl;
		}

		public String getJvmOpts() {
			return jvmOpts;
		}

		public Boolean isAdminAccess() {
			return adminAccess;
		}

		public Map<String, CreateContainerMetadataDTO> getMetadataMap() {
			return metadataMap;
		}

		public B jmxUser(String jmxUser) {
			this.jmxUser = jmxUser;
			return (B) this;
		}

		public B jmxPassword(String jmxPassword) {
			this.jmxPassword = jmxPassword;
			return (B) this;
		}

		public String getJmxUser() {
			return jmxUser;
		}

		public String getJmxPassword() {
			return jmxPassword;
		}

		/**
		 * @return the zooKeeperServerPort
		 */
		public BigInteger getZooKeeperServerPort() {
			return this.zooKeeperServerPort;
		}

		public B version(String version) {
			this.version = version;
			return (B) this;
		}

		/**
		 * @return the version
		 */
		public String getVersion() {
			return this.version;
		}

		public B profiles(Set<String> profiles) {
			this.profiles = profiles;
			return (B) this;
		}

		/**
		 * @return the profiles
		 */
		public Set<String> getProfiles() {
			return this.profiles;
		}

		/**
		 * @return the zookeeperPassword
		 */
		public String getZookeeperPassword() {
			return this.zookeeperPassword;
		}

		public B zookeeperPassword(String password) {
			this.zookeeperPassword = password;
			return (B) this;
		}

		public B resolver(String resolver) {
			this.resolver = resolver;
			return (B) this;
		}
		
		public CreateContainerOptionsDTO build() {
			return new CreateContainerOptionsDTO(bindAddress, resolver,
					globalResolver, manualIp, minimumPort, maximumPort,
					profiles, version, dataStoreProperties,
					zooKeeperServerPort, zooKeeperServerConnectionPort,
					zookeeperPassword, ensembleStart, agentEnabled,
					waitForProvision, bootstrapTimeout, autoImportEnabled,
					importPath, users, name, parent, "child", ensembleServer,
					preferredAddress, systemProperties, number, proxyUri,
					zookeeperUrl, jvmOpts, true, clean, jmxUser, jmxPassword);
		}
	}
}
