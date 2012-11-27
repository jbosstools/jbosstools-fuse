package org.fusesource.ide.fabric.actions.jclouds;

import java.lang.String;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.fusesource.fabric.service.jclouds.modules.ZookeeperCredentialStore;
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric.FabricPlugin;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.fusesource.fabric.zookeeper.IZKClient;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;

public class CloudDetails extends ConfigurationDetails {
    private static final String PROPERTY_SETTINGS_PREFIX = "settings_";
    private static final String PROPERTY_PROVIDER_ID = "providerId";
    private static final String PROPERTY_API_ID = "apiId";
    private static final String PROPERTY_ENDPOINT = "endpoint";
    private static final String PROPERTY_OWNER_ID = "ownerId";
    private static final String PROPERTY_CREDENTIAL = "credential";
    private static final String PROPERTY_IDENTITY = "identity";
    private static final String PROPERTY_NAME = "name";

    private static WritableList cloudDetailList = WritableList.withElementType(CloudDetails.class);
    private static AtomicBoolean loadedCloudDetails = new AtomicBoolean(false);
    private static CloudDetails exemplar = new CloudDetails();

    private String name;
    private ProviderMetadata provider;
    private ApiMetadata api;
    private String identity;
    private String credential;
    private String ownerId;
    private boolean storeCredential = true;
    private String providerId;
    private String apiId;
    private String endpoint;
    private CloudDetailsKey key;
    private IZKClient izkClient;
    private HashMap<String, String> settings = new HashMap<String, String>();

    public static void reloadCloudDetailList() {
        cloudDetailList.clear();
        load(cloudDetailList);

    }

    public static WritableList getCloudDetailList() {
        if (loadedCloudDetails.compareAndSet(false, true)) {
            load(cloudDetailList);
        }
        return CloudDetails.cloudDetailList;
    }

    public static CloudDetails asCloudDetails(Object element) {
        if (element instanceof CloudDetails) {
            return (CloudDetails) element;
        }
        return null;
    }


    protected static void load(Collection<CloudDetails> cloudDetailList) {
        Preferences node = exemplar.getConfigurationNode();
        try {
            String[] childrenNames = node.childrenNames();
            for (String name : childrenNames) {
                cloudDetailList.add(new CloudDetails(name, node.node(name)));
            }
        } catch (BackingStoreException e) {
            FabricPlugin.showUserError("Failed to load cloud providers", e.getMessage(), e);
        }
    }

    public static CloudDetails copy(CloudDetails copy) {
        Preferences node = exemplar.getConfigurationNode();
        String id = copy.getId();
        return new CloudDetails(id, node.node(id));
    }


    public CloudDetails() {
    }

    public CloudDetails(String id, Preferences node) {
        super(id);
        this.name = node.get(PROPERTY_NAME, "");
        this.identity = node.get(PROPERTY_IDENTITY, "");
        this.credential = node.get(PROPERTY_CREDENTIAL, "");
        if (Strings.isBlank(credential)) {
            storeCredential = false;
        }
        this.ownerId = node.get(PROPERTY_OWNER_ID, "");
        setProvider(JClouds.getProvider(node.get(PROPERTY_PROVIDER_ID, null)));
        setApi(JClouds.getApi(node.get(PROPERTY_API_ID, null)));
        this.settings.clear();
        try {
            for (String keyName : node.keys()) {
                if (keyName.startsWith(PROPERTY_SETTINGS_PREFIX)) {
                    this.settings.put(keyName, node.get(keyName, ""));
                }
            }
        } catch (BackingStoreException ex) {
            FabricPlugin.showUserError("Settings Error", "Unable to restore settings...", ex);
        }
    }

    /**
     * Returns the object which can be used as a key in a cache using just the identity, credential, owner etc
     */
    public CloudDetailsKey getCacheKey() {
        if (key == null) {
            key = new CloudDetailsKey(identity, credential, ownerId, providerId);
        }
        return key;
    }

    @Override
    protected String getConfigurationNodeId() {
        return "org.fusesource.ide.jclouds.provider";
    }

    @Override
    protected void store(Preferences node) {
        node.put(PROPERTY_NAME, name);
        node.put(PROPERTY_IDENTITY, identity);
        node.put(PROPERTY_PROVIDER_ID, providerId);
        if (storeCredential) {
            node.put(PROPERTY_CREDENTIAL, credential);
        } else {
            node.remove(PROPERTY_CREDENTIAL);
        }
        if (Strings.isBlank(ownerId)) {
            node.remove(PROPERTY_OWNER_ID);
        } else {
            node.put(PROPERTY_OWNER_ID, ownerId);
        }
        Iterator<String> keys = this.settings.keySet().iterator();
        while (keys.hasNext()) {
            String keyName = keys.next();
            String value = this.settings.get(keyName);
            node.put(keyName, value);
        }
    }


    @Override
    public String toString() {
        return "ColumnConfiguration(" + name + ", " + provider + ", " + identity + ")";
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public ProviderMetadata getProvider() {
        return provider;
    }


    public void setProvider(ProviderMetadata provider) {
        this.providerId = JClouds.getId(provider);
        if (provider != this.provider) {
            Object oldValue = this.provider;
            this.provider = provider;
            firePropertyChange("provider", oldValue, provider);
        }
    }

    public ApiMetadata getApi() {
        return api;
    }

    public void setApi(ApiMetadata api) {
        this.apiId = JClouds.getId(api);
        if (api != this.api) {
            Object oldValue = this.api;
            this.api = api;
            firePropertyChange("api", oldValue, api);
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void addSetting(String key, String value) {
        this.settings.put(PROPERTY_SETTINGS_PREFIX + key, value);
        firePropertyChange("settings", value, value);
    }

    public String getSetting(String key, String defaultValue) {
        String retVal = defaultValue;
        if (this.settings.containsKey(PROPERTY_SETTINGS_PREFIX + key)) {
            retVal = this.settings.get(PROPERTY_SETTINGS_PREFIX + key);
        }
        return retVal;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        if (!identity.equals(this.identity)) {
            Object oldValue = this.identity;
            this.identity = identity;
            firePropertyChange(PROPERTY_IDENTITY, oldValue, identity);
        }
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        if (!credential.equals(this.credential)) {
            Object oldValue = this.credential;
            this.credential = credential;
            firePropertyChange(PROPERTY_CREDENTIAL, oldValue, credential);
        }
    }



    public boolean isStoreCredential() {
        return storeCredential;
    }


    public void setStoreCredential(boolean storeCredential) {
        this.storeCredential = storeCredential;
    }


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        if (!ownerId.equals(this.ownerId)) {
            Object oldValue = this.ownerId;
            this.ownerId = ownerId;
            firePropertyChange(PROPERTY_OWNER_ID, oldValue, ownerId);
        }
    }

    public String getProviderName() {
        return JClouds.text(getProvider());
    }

    public String getProviderId() {
        return providerId;
    }

    public static ComputeService createComputeService(CloudDetails details) {
        ProviderMetadata selectedProvider = details.getProvider();
        ApiMetadata selectedApi = details.getApi();

        String identity = details.getIdentity();
        String credential = details.getCredential();
        String endpoint = details.getEndpoint();

        if (selectedProvider != null && !Strings.isBlank(identity) && !Strings.isBlank(credential)) {
            String providerId = selectedProvider.getId();
            String owner = details.getOwnerId();
            Properties props = new Properties();

            props.put("provider", providerId);
            props.put(PROPERTY_IDENTITY, identity);
            props.put(PROPERTY_CREDENTIAL, credential);
            if (!Strings.isBlank(owner)) {
                props.put("jclouds.ec2.ami-query=owner-id", owner + ";state=available;image-type=machine");
            }

            ImmutableSet<AbstractModule> modules;
            IZKClient izkClient = details.getIzkClient();
            if (izkClient != null) {
                ZookeeperCredentialStore credentialStore = new ZookeeperCredentialStore();
                credentialStore.setZooKeeper(izkClient);
                credentialStore.init();
                modules = ImmutableSet.of(new Log4JLoggingModule(), new SshjSshClientModule(), credentialStore);
            } else {
                modules = ImmutableSet.of(new Log4JLoggingModule(), new SshjSshClientModule());
            }


            ContextBuilder builder = null;
            if (selectedProvider != null) {
                builder = ContextBuilder.newBuilder(selectedProvider);
            } else if (selectedApi != null) {
                builder = ContextBuilder.newBuilder(selectedApi);
            }

            if (endpoint != null && !endpoint.isEmpty()) {
                builder = builder.endpoint(endpoint);
            }

            builder = builder.name(details.getName()).modules(modules);
            builder = builder.name(details.getName()).credentials(identity, credential).overrides(props);

            ComputeServiceContext context = builder.build(ComputeServiceContext.class);
            return context.getComputeService();
        }
        return null;
    }


    protected IZKClient getIzkClient() {
        return izkClient;
    }

    protected void setIzkClient(IZKClient izkClient) {
        this.izkClient = izkClient;
    }

    public void setZooKeeper(IZKClient izkClient) {
        this.izkClient = izkClient;

    }


}
