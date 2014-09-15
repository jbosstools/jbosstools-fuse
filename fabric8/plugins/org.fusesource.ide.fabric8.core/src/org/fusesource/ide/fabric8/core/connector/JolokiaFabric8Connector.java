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
package org.fusesource.ide.fabric8.core.connector;

import io.fabric8.api.CreateContainerOptions;
import io.fabric8.insight.log.LogFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.Fabric8CorePlugin;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerMetadataDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerOptionsDTO;
import org.fusesource.ide.fabric8.core.dto.FabricStatusDTO;
import org.fusesource.ide.fabric8.core.dto.LogResultsDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.RequirementsDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pRemoteException;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pExecResponse;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.jolokia.client.request.J4pWriteRequest;

/**
 * a connection to Fabric8 using Jolokia
 * 
 * @author lhein
 */
public class JolokiaFabric8Connector implements Fabric8ConnectorType {
	
	private static final String FABRIC_MBEAN_URL = "io.fabric8:type=Fabric";
	private static final String PROFILEMGMT_MBEAN_URL = "io.fabric8:type=ProfileManagement";
	private static final String INSIGHT_MBEAN_URL = "io.fabric8.insight:type=LogQuery";
	
	private J4pClient j4p;
    private String userName;
    private String password;
    private String url;
    private Fabric8Facade fabricFacade;
    
    /**
     * creates the fabric connector and returns it
     *
     * @param user     the user
     * @param password the password
     * @param url      the url
     * @return the initialized and ready to use connector
     */
    public static JolokiaFabric8Connector getFabric8Connector(String user, String password, String url) {
    	JolokiaFabric8Connector rc = new JolokiaFabric8Connector();
        rc.setUserName(user);
        rc.setPassword(password);
        rc.setUrl(url);
        return rc;
    }

    /**
     * connects to a fabric
     */
    @Override
    public void connect() throws IOException {
        if (this.j4p != null || this.fabricFacade != null) {
            disconnect();
        }
        this.j4p = J4pClient.url(this.url).user(this.userName).password(this.password).build();
        this.fabricFacade = new Fabric8Facade(this);
    }

    /**
     * disconnects from a fabric
     */
    @Override
    public void disconnect() {
        if (this.j4p != null) {
            this.j4p = null;
        }
        if (this.fabricFacade != null) {
            this.fabricFacade = null;
        }
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#isConnected()
     */
    @Override
    public boolean isConnected() {
    	if (this.fabricFacade != null) {
    		List<ContainerDTO> containers = this.fabricFacade.getContainers();
    		return containers != null && containers.isEmpty() == false;
    	}
    	return false;
    }
    
    /**
     * returns the fabric service implementation
     *
     * @return the fabric service implementation
     */
    @Override
    public Fabric8Facade getFabricFacade() {
        return this.fabricFacade;
    }

    /**
     * returns the user name
     *
     * @return the user name
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * sets the user name
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * returns the password
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * sets the password
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * returns the url
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * sets the url
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * returns the jolokia client
     * 
     * @return
     */
    public J4pClient getJolokiaClient() {
        return j4p;
    }
    
    public <T extends Object> T read(String attribute) {
	    try {
	        J4pReadRequest request = JolokiaHelpers.createReadRequest(FABRIC_MBEAN_URL, attribute);
	        J4pReadResponse response = j4p.execute(request);
	        return response.getValue();
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to read " + attribute, e);
	    }
    }
    
    // **********************************************************************
    // OPERATIONS BELOW
    // **********************************************************************
    
    /*
     * (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getContainers()
     */
    @Override
    public List<ContainerDTO> getContainers() {
    	List<ContainerDTO> containers = new ArrayList<ContainerDTO>();
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "containers()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            List<Map<String, Object>> values = response.getValue();
            for (Map<String, Object> value : values) {
                containers.add(new ContainerDTO(this.fabricFacade, value));
            }
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch containers from fabric8.", e);
        }
    	
    	return containers;
    }
    
    /*
     * (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getContainer(java.lang.String)
     */
    @Override
    public ContainerDTO getContainer(String containerId) {
    	ContainerDTO container = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "getContainer(java.lang.String)", containerId);
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            container = new ContainerDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch container with ID '" + containerId + "' from fabric8.", e);
        }
    	return container;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getCurrentContainer()
     */
    @Override
    public ContainerDTO getCurrentContainer() {
    	ContainerDTO container = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "currentContainer()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            container = new ContainerDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch current container from fabric8.", e);
        }
    	return container;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#startContainer(java.lang.String)
     */
    @Override
    public void startContainer(String containerId) {
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "startContainer(java.lang.String)", containerId);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to start container with id '" + containerId + "'.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#stopContainer(java.lang.String)
     */
    @Override
    public void stopContainer(String containerId) {
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "stopContainer(java.lang.String)", containerId);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to stop container with id '" + containerId + "'.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#destroyContainer(java.lang.String)
     */
    @Override
    public void destroyContainer(String containerId) {
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "destroyContainer(java.lang.String)", containerId);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to destroy container with id '" + containerId + "'.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#createVersion(java.lang.String)
     */
    @Override
    public VersionDTO createVersion(String versionId) {
    	VersionDTO version = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "createVersion()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            version = new VersionDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to create version with id '" + versionId + "'.", e);
        }
    	return version;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#deleteVersion(java.lang.String)
     */
    @Override
    public void deleteVersion(String versionId) {
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "deleteVersion(java.lang.String)", versionId);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to delete version with id '" + versionId + "'.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getVersions()
     */
    @Override
    public List<VersionDTO> getVersions() {
    	List<VersionDTO> versions = new ArrayList<VersionDTO>();
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "versions()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            List<Map<String, Object>> values = response.getValue();
            for (Map<String, Object> value : values) {
            	VersionDTO v = new VersionDTO(this.fabricFacade, value);
            	if (v.getId().equalsIgnoreCase("master")) continue; // don't show "MASTER" version
            	versions.add(new VersionDTO(this.fabricFacade, value));
            }
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch containers from fabric8.", e);
        }
    	
    	return versions;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getGitUrl()
     */
    @Override
    public String getGitUrl() {
    	String gitUrl = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "gitUrl()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            gitUrl = response.getValue();
    	} catch (Exception ex) {
    		if (ex instanceof J4pRemoteException && ((J4pRemoteException)ex).getErrorType().equalsIgnoreCase("javax.management.InstanceNotFoundException")) {
    			Fabric8CorePlugin.getLogger().error("The Fabric8 runtime is not compatible with this tooling version.", ex);
    		} else {
            	Fabric8CorePlugin.getLogger().error("Failed to fetch git url from fabric8.", ex);
    		}
        }
    	return gitUrl;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getMavenProxyDownloadUrl()
     */
    @Override
    public String getMavenProxyDownloadUrl() {
    	String url = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "mavenProxyDownloadUrl()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            url = response.getValue();
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch maven proxy download url from fabric8.", e);
        }
    	return url;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getMavenProxyUploadUrl()
     */
    @Override
    public String getMavenProxyUploadUrl() {
    	String url = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "mavenProxyUploadUrl()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            url = response.getValue();
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch maven proxy upload url from fabric8.", e);
        }
    	return url;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getWebUrl()
     */
    @Override
    public String getWebUrl() {
    	String url = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "webConsoleUrl()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            url = response.getValue();
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch web console url from fabric8.", e);
        }
    	return url;
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getProfiles(java.lang.String)
     */
    @Override
    public List<ProfileDTO> getProfiles(String versionId) {
    	List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "getProfiles(java.lang.String)", versionId);
            J4pExecResponse response = getJolokiaClient().execute(request);
            List<Map<String, Object>> values = response.getValue();
            for (Map<String, Object> value : values) {
            	ProfileDTO p = new ProfileDTO(fabricFacade, value);
            	if (p.isHidden()) {
            		continue; // don't return hidden profiles
            	}
            	profiles.add(p);
            }
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch containers from fabric8.", e);
        }
    	
    	return profiles;
    }

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getProfile(java.lang.String, java.lang.String)
	 */
	@Override
	public ProfileDTO getProfile(String versionId, String profileId) {
		ProfileDTO profile = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "getProfile(java.lang.String, java.lang.String)", versionId, profileId);
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            profile = new ProfileDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch profile with id '" + profileId + "' and version id '" + versionId + "'.", e);
        }
    	return profile;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#createProfile(java.lang.String, java.lang.String)
	 */
	@Override
	public ProfileDTO createProfile(String versionId, String profileId) {
		ProfileDTO profile = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "createProfile(java.lang.String, java.lang.String)", versionId, profileId);
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            profile = new ProfileDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to create profile with id '" + profileId + "' and version id '" + versionId + "'.", e);
        }
    	return profile;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#deleteProfile(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteProfile(String versionId, String profileId) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "deleteProfile(java.lang.String, java.lang.String)", versionId, profileId);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to delete profile with id '" + profileId + "' and version id '" + versionId + "'.", e);
        }
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#createVersion(java.lang.String, java.lang.String)
	 */
	@Override
	public VersionDTO createVersion(String parentVersionId, String versionId) {
		VersionDTO version = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "createVersion(java.lang.String, java.lang.String)", parentVersionId, versionId);
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            version = new VersionDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to create subversion with id '" + versionId + "' under version '" + parentVersionId + "'.", e);
        }
    	return version;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getDefaultVersion()
	 */
	@Override
	public VersionDTO getDefaultVersion() {
		VersionDTO version = null;
    	try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "defaultVersion()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            Map<String, Object> values = response.getValue();
            version = new VersionDTO(this.fabricFacade, values);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch current container from fabric8.", e);
        }
    	return version;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setDefaultVersion(java.lang.String)
	 */
	@Override
	public void setDefaultVersion(String versionId) {
		try {
            J4pWriteRequest request = JolokiaHelpers.createWriteRequest(FABRIC_MBEAN_URL, "DefaultVersion", versionId);
            j4p.execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set default version for container to '" + versionId + "'.", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setVersionForContainer(java.lang.String, java.lang.String)
	 */
	@Override
	public void setVersionForContainer(String containerId, String versionId) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "applyVersionToContainers(java.lang.String, java.util.List)", versionId, JolokiaHelpers.toList(containerId));
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set version of container '" + containerId + "' to '" + versionId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#addProfilesToContainer(java.lang.String, java.lang.String[])
	 */
	@Override
	public void addProfilesToContainer(String containerId, String... profileIds) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "addProfilesToContainer(java.lang.String, java.util.List)", containerId, profileIds);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to add profiles to container '" + containerId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#removeProfilesFromContainer(java.lang.String, java.lang.String[])
	 */
	@Override
	public void removeProfilesFromContainer(String containerId,
			String... profileIds) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "removeProfilesFromContainer(java.lang.String, java.util.List)", containerId, profileIds);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to remove profiles from container '" + containerId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfilesForContainer(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfilesForContainer(String containerId, String versionId,
			List<String> profileIds) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "applyProfilesToContainers(java.lang.String, java.util.List, java.util.List)", versionId, profileIds, JolokiaHelpers.toList(containerId));
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set profiles for container '" + containerId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileBundles(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileBundles(String versionId, String profileId,
			List<String> bundles) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "setProfileBundles(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, bundles);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set bundles for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileFabs(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileFabs(String versionId, String profileId,
			List<String> fabs) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "setProfileFabs(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, fabs);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set fabs for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileFeatures(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileFeatures(String versionId, String profileId,
			List<String> features) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "setProfileFeatures(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, features);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set features for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileOptionals(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileOptionals(String versionId, String profileId,
			List<String> optionals) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "setProfileOptionals(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, optionals);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set optionals for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileOverrides(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileOverrides(String versionId, String profileId,
			List<String> overrides) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "setProfileOverrides(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, overrides);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set overrides for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileParentIds(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileParentIds(String versionId, String profileId,
			List<String> parentIds) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "changeProfileParents(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, parentIds);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to change parent ids for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#setProfileRepositories(java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void setProfileRepositories(String versionId, String profileId,
			List<String> repositories) {
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "setProfileRepositories(java.lang.String, java.lang.String, java.util.List)", versionId, profileId, repositories);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set repositories for profile '" + profileId + "'", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getMavenRepoURI()
	 */
	@Override
	public String getMavenRepoURI() {
		return read("MavenRepoURI");
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getZookeeperUrl()
	 */
	@Override
	public String getZookeeperUrl() {
		return read("ZookeeperUrl");
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getRequirements()
	 */
	@Override
	public RequirementsDTO getRequirements() {
		RequirementsDTO requirements = null;
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "requirementsAsJson()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            String json = response.getValue();
			final ModelNode rootNode = JsonHelper.getModelNode(json);
			requirements = RequirementsDTO.fromJson(fabricFacade, rootNode);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch requirements", e);
        }
		return requirements;
	}
	
	/**
	 * returns the requirements
	 * 
	 * @return
	 */
	public void setRequirements(RequirementsDTO requirements) {
		try {
			String json = requirements.toJSon().toJSONString(true);
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "requirementsJson(java.lang.String)", json);
            getJolokiaClient().execute(request);
		} catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to set requirements", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#getFabricStatus()
	 */
	@Override
	public FabricStatusDTO getFabricStatus() {
		FabricStatusDTO status = null;
		try {
            J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "fabricStatusAsJson()");
            J4pExecResponse response = getJolokiaClient().execute(request);
            String json = response.getValue();
            final ModelNode rootNode = JsonHelper.getModelNode(json);
			status = FabricStatusDTO.fromJson(fabricFacade, rootNode);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to fetch fabric status", e);
        }
		return status;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#createContainers(org.fusesource.ide.fabric8.core.dto.CreateContainerOptionsDTO)
	 */
	@Override
	public CreateContainerMetadataDTO[] createContainers(CreateContainerOptionsDTO options) {
		try {
			Map<String, Object> optionsMap = options.asMap();
			doCreateContainer(optionsMap);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to create container", e);
        }
		return new CreateContainerMetadataDTO[0];
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#createContainers(io.fabric8.api.CreateContainerOptions)
	 */
	@Override
	public CreateContainerMetadataDTO[] createContainers(CreateContainerOptions options) {
		try {
			Map<String, Object> optionsMap = JsonHelper.asMap(options);
			doCreateContainer(optionsMap);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to create container", e);
        }
		return new CreateContainerMetadataDTO[0];
	}
	
	/**
	 * does the real call to create the container
	 * 
	 * @param optionsMap
	 * @return
	 */
	protected CreateContainerMetadataDTO[] doCreateContainer(Map<String, Object> optionsMap) {
		try {
			J4pExecRequest request = JolokiaHelpers.createExecRequest(FABRIC_MBEAN_URL, "createContainers(java.util.Map)", optionsMap);
            getJolokiaClient().execute(request);
        } catch (Exception e) {
        	Fabric8CorePlugin.getLogger().error("Failed to create container", e);
        }
		return new CreateContainerMetadataDTO[0];
	}

	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType#queryLog(io.fabric8.insight.log.LogFilter)
	 */
	@Override
	public LogResultsDTO queryLog(LogFilter filter) {
		try {
			String filterJson = JsonHelper.convertToJson(filter);
			J4pExecRequest request = JolokiaHelpers.createExecRequest(INSIGHT_MBEAN_URL, "filterLogEvents(java.lang.String)", filterJson);
	        J4pExecResponse response = getJolokiaClient().execute(request);
	        String json = response.getValue();
	        final ModelNode rootNode = JsonHelper.getModelNode(json);
			LogResultsDTO result = LogResultsDTO.fromJson(rootNode);
			return result;
		} catch (Exception e) {
			Fabric8CorePlugin.getLogger().error("Failed to query the logs", e);
		}
		return null;
	}
}
