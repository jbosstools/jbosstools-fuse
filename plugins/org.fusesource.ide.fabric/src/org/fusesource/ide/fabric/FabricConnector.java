package org.fusesource.ide.fabric;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.ui.statushandlers.StatusManager;
import org.fusesource.fabric.api.Container;
import org.fusesource.fabric.api.ContainerProvider;
import org.fusesource.fabric.api.FabricService;
import org.fusesource.fabric.api.Version;
import org.fusesource.fabric.service.FabricServiceImpl;
import org.fusesource.fabric.service.jclouds.JcloudsContainerProvider;
import org.fusesource.fabric.service.jclouds.firewall.internal.Ec2FirewallSupport;
import org.fusesource.fabric.service.jclouds.firewall.internal.FirewallManagerFactoryImpl;
import org.fusesource.fabric.service.jclouds.modules.ZookeeperCredentialStore;
import org.fusesource.fabric.service.ssh.SshContainerProvider;
import org.fusesource.fabric.zookeeper.spring.ZKClientFactoryBean;
import org.fusesource.ide.commons.Bundles;
import org.fusesource.ide.fabric.actions.FabricDetails;
import org.fusesource.ide.fabric.actions.jclouds.JClouds;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.fabric.zookeeper.IZKClient;
import org.fusesource.fabric.zookeeper.internal.ZKClient;
import org.linkedin.zookeeper.client.LifecycleListener;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;


public class FabricConnector {
	private final Fabric fabric;
	private final FabricDetails details;
	private final String url;
	private FabricService fabricService;
	private AtomicBoolean initialised = new AtomicBoolean(false);
	private AtomicBoolean connected = new AtomicBoolean(false);
	private boolean blueprintFabricServiceDoesNotWork = true;
	private IZKClient zooKeeper;
	private ZKClientFactoryBean factory;
	private Watcher watcher = new Watcher() {

		@Override
		public void process(WatchedEvent event) {
			onWatchEvent(event);
		}
	};


	public FabricConnector(Fabric fabric) {
		this.fabric = fabric;
		this.details = fabric.getDetails();
		this.url = details.getUrls();
	}

	protected void initialised() throws Exception {
		// lets make sure we've started the fabric bundle!
		BundleContext context = getBundleContext();

		// lets force the fabric bundle to start
		Bundles.startBundle(context, "org.fusesource.fabric.fabric-core-agent-ssh");
		Bundles.startBundle(context, "org.fusesource.fabric.fabric-core-agent-jclouds");
		Bundles.startBundle(context, "org.fusesource.fabric.fabric-core");
		Bundles.startBundle(context, "org.fusesource.fabric");
		Bundles.startBundle(context, "org.eclipse.osgi.services");
		Bundles.startBundle(context, "org.eclipse.equinox.cm");

		FabricPlugin.getLogger().debug("Starting to connect Fabric on: " + url);
		// lets try find it in OSGi
		if (fabricService == null) {
			fabricService = Bundles.lookupService(context, FabricService.class);
			if (fabricService != null && blueprintFabricServiceDoesNotWork) {
				// TODO Dirty hack!!!!
				// for some reason the blueprint version of the service doesnt' work!
				fabricService = null;
			}
		}
		if (fabricService == null) {
			FabricServiceImpl impl = new FabricServiceImpl();
			zooKeeper = createZooKeeper();
			impl.setZooKeeper(zooKeeper);
			fabricService = impl;
		}
		if (fabricService != null) {
			if (fabricService instanceof FabricServiceImpl) {
				FabricServiceImpl impl = (FabricServiceImpl) fabricService;
				zooKeeper = impl.getZooKeeper();
				if (zooKeeper == null) {
					System.out.println("=================== no ZK service so setting it...");
					zooKeeper = createZooKeeper();
					impl.setZooKeeper(zooKeeper);
				}
				ConfigurationAdmin configurationAdmin = impl.getConfigurationAdmin();
				if (configurationAdmin == null) {
					configurationAdmin = Bundles.lookupService(context, ConfigurationAdmin.class);
					if (configurationAdmin != null) {
						impl.setConfigurationAdmin(configurationAdmin);

						Configuration config = configurationAdmin.getConfiguration("org.fusesource.fabric.zookeeper", null);
						if (config == null) {
							config = configurationAdmin.createFactoryConfiguration("org.fusesource.fabric.zookeeper");
						}
						Dictionary properties = config.getProperties();
						if (properties == null) {
							properties = new Hashtable();
						}
						String zooKeeperUrl = (String) properties.get("zookeeper.url");
						if (true || zooKeeperUrl == null) {
							String u = getUrl();
							if (false) {
								int idx = u.indexOf(':');
								if (idx >= 0) {
									u = u.substring(idx);
								}
							}
							properties.put("zookeeper.url", u);
						}
						config.update(properties);
						System.out.println("Config admin has properties: " + config.getProperties());
					} else {
						System.out.println("================ cant find FabricService or ConfigurationAdmin in OSGi registry! Must not be started yet!");
					}
				}
				System.out.println("Config admin: " + configurationAdmin);


				// now lets make sure we have the Ssh and JClouds services
				// as currently we have no OSGi blueprint support in IDE :(
				if (blueprintFabricServiceDoesNotWork) {
					// lets look up the services
					Map<ContainerProvider,Map<String,Object>> providers = Bundles.lookupServicesMap(context, ContainerProvider.class);
					Set<Entry<ContainerProvider, Map<String,Object>>> entrySet = providers.entrySet();
					for (Entry<ContainerProvider, Map<String,Object>> entry : entrySet) {
						ContainerProvider provider = entry.getKey();
						try {
							impl.registerProvider(provider, entry.getValue());
						} catch (Exception e) {
							FabricPlugin.getLogger().warning("Failed to add FabricAgent provider " + provider + ". " + e, e);
						}
					}

					if (impl.getProviders().size() < 3) {
						// lets add the ssh and Jclouds providers
						SshContainerProvider ssh = new SshContainerProvider();
						impl.registerProvider("ssh", ssh);

						JcloudsContainerProvider jclouds = new JcloudsContainerProvider();

						ZookeeperCredentialStore credentialStore = new ZookeeperCredentialStore();
						credentialStore.setZooKeeper(getZooKeeper());
						credentialStore.init();
						jclouds.setCredentialStore(credentialStore);

						// TODO set firewall factory
						FirewallManagerFactoryImpl firewallManagerFactory = new FirewallManagerFactoryImpl();
						firewallManagerFactory.bind(new Ec2FirewallSupport());
						jclouds.setFirewallManagerFactory(firewallManagerFactory);

						JClouds.bindProviders(jclouds);
						impl.registerProvider("jclouds", jclouds);
					}
				}

				Collection<ContainerProvider> providers = impl.getProviders().values();
				for (ContainerProvider provider : providers) {
					System.out.println("=================== Fabric Agent Provider: " + provider);
				}

				/*
				if (impl.getProvider("ssh") == null) {
					SshContainerProvider provider = new SshContainerProvider();
					impl.registerProvider("ssh", provider);
				}
				if (impl.getProvider("jclouds") == null) {
					JcloudsContainerProvider provider = new JcloudsContainerProvider();
					provider.setMavenProxy(getMavenProxy());

					// TODO now bind in each compute service...
					impl.registerProvider("jclouds", provider);
				}
				 */
			}

			addWatcher();

		} else {
			FabricPlugin.getLogger().warning("Fabric not found on url: " + url);
		}
	}

	public IZKClient getZooKeeper() {
		return zooKeeper;
	}

	public void setZooKeeper(IZKClient zooKeeper) {
		this.zooKeeper = zooKeeper;
	}

	public Container[] getAgents() {
		FabricService service = getFabricService();
		if (service == null) {
			return null;
		}
		return service.getContainers();
	}

	public Version[] getVersions() {
		FabricService service = getFabricService();
		if (service == null) {
			return null;
		}
		return service.getVersions();
	}

	public BundleContext getBundleContext() {
		return FabricPlugin.getDefault().getBundle().getBundleContext();
	}

	public String getName() {
		return details.getName();
	}

	public String getUrl() {
		return url;
	}

	public String getDefaultVersionId() {
		FabricService service = getFabricService();
		if (service != null) {
			Version defaultVersion = service.getDefaultVersion();
			if (defaultVersion != null) {
				return defaultVersion.getName();
			}
		}
		return null;
	}

	public FabricService getFabricService() {
		checkConnected();
		return fabricService;
	}

	public void checkConnected() {
		if (initialised.compareAndSet(false, true)) {
			try {
				initialised();
			} catch (Exception e) {
				FabricNotConnectedException fnce = new FabricNotConnectedException(this, e);
				final String PID = FabricPlugin.PLUGIN_ID;
				MultiStatus status = new MultiStatus(PID, 1, "Unable to connect to Fabric. Please make sure Fabric is running on the specified host and the used ports are not blocked.", fnce);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
				fabric.onDisconnect();
				throw fnce;
			}
		} else {
			try {
				this.fabricService.getFabricStatus();
			} catch (Exception ex) {
				throw new FabricNotConnectedException(this, ex);
			}
		}
	}

	public void setFabricService(FabricService fabricService) {
		this.fabricService = fabricService;
	}

	private IZKClient createZooKeeper() throws Exception {
		IZKClient answer = Bundles.lookupService(getBundleContext(),
				IZKClient.class);
		if (answer == null || blueprintFabricServiceDoesNotWork) {
			// TODO lets remove this local hack when we move to build 73!k t
			factory = new ZKClientFactoryBean() {
				@Override
				public void destroy() throws Exception {
					if (zkClient != null) {
						// Note we cannot use zkClient.close()
						// since you cannot currently close a client which is not connected
						zkClient.close();
						zkClient = null;
					}
				}
			};
			FabricPlugin.getLogger().info("Trying to connect to fabric on: " + url);
			factory.setPassword(details.getZkPassword());
			factory.setConnectString(url);
			try {
				answer = factory.getObject();
				if (answer == null) {
					FabricPlugin.getLogger().warning("No Fabric ZooKeeper found");
				} else {
					answer.registerListener(new LifecycleListener() {

						@Override
						public void onDisconnected() {
							FabricPlugin.getLogger().info("Fabric ZooKeeper disconnected");
							connected.set(false);
						}

						@Override
						public void onConnected() {
							FabricPlugin.getLogger().info("Fabric ZooKeeper connected");
							connected.set(true);
						}
					});
				}
			} catch (Exception e) {
				factory.destroy();
				throw e;
			}
		}
		return answer;
	}

	public void dispose() {
		if (blueprintFabricServiceDoesNotWork && fabricService instanceof FabricServiceImpl) {
			FabricServiceImpl impl = (FabricServiceImpl) fabricService;
			IZKClient zooKeeper = impl.getZooKeeper();
			try {
				factory.destroy();
				factory = null;

				// destroy() works better if its not connected
				if (zooKeeper instanceof ZKClient) {
					ZKClient zkclient = (ZKClient) zooKeeper;
					zooKeeper.close();
				} else {
					zooKeeper.close();
				}
			} catch (Exception e) {
				FabricPlugin.getLogger().warning("Failed to disconnect from ZooKeeper for " + details + ". " + e, e);
			}
		}
	}

	protected void onWatchEvent(WatchedEvent event) {
		System.out.println("=================== ZK watcher: " + event);
		addWatcher();
		fabric.onZooKeeperUpdate();
	}

	protected void addWatcher() {
		if (isConnected() && zooKeeper != null) {
			addWatcher("/fabric/registry/containers/provision");
			addWatcher("/fabric/registry/containers/alive");
			addWatcher("/fabric/registry/containers/config");

		} else {
			FabricPlugin.getLogger().warning("No ZooKeeper connection!");
		}
	}

	private boolean addWatcher(String path) {
		try {
			if (zooKeeper.exists(path) != null) {
				//zooKeeper.getZKChildren(path, watcher);
				zooKeeper.getChildren(path, watcher);
				return true;
			}
		} catch (Exception e) {
			FabricPlugin.getLogger().warning("Failed to add watcher: " + e, e);
		}
		return false;
	}

	public boolean isConnected() {
		try {
			checkConnected();
		} catch (Exception e) {
			FabricPlugin.getLogger().debug("Fabric " + this + " is not connected  " + e);
			dispose();
			fabric.onDisconnect();
			return false;
		}
		return connected.get();
	}
}
