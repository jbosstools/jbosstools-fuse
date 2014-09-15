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
import java.util.List;

import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerMetadataDTO;
import org.fusesource.ide.fabric8.core.dto.CreateContainerOptionsDTO;
import org.fusesource.ide.fabric8.core.dto.FabricStatusDTO;
import org.fusesource.ide.fabric8.core.dto.LogResultsDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.RequirementsDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;

/**
 * interface describing the communication protocol for
 * working with Fabric8 
 * 
 * @author lhein
 */
public interface Fabric8ConnectorType {
	
	/**
	 * connects to the fabric
	 * 
	 * @throws IOException on connection failures
	 */
	public void connect() throws IOException;
	
	/**
	 * disconnects from fabric
	 */
	public void disconnect();
	
	/**
	 * checks if the connection is established or not
	 * 
	 * @return
	 */
	public boolean isConnected();
	
	/**
	 * returns the fabric8 dto
	 * 
	 * @return	the fabric8 facade object
	 */
	public Fabric8Facade getFabricFacade();
	
	/**
	 * returns a list of all containers
	 * 
	 * @return	a list of all containers in the fabric, can be empty but not null
	 */
	public List<ContainerDTO> getContainers();
	
	/**
	 * returns a container for the given id or null if not found
	 * 
	 * @param containerId	the id of the container
	 * @return	the container or null if not found
	 */
	public ContainerDTO getContainer(String containerId);
	
	/**
	 * returns the current container
	 * 
	 * @return	the container or null
	 */
	public ContainerDTO getCurrentContainer();
	
	/**
	 * starts the container with the given id
	 * 
	 * @param containerId
	 */
	public void startContainer(String containerId);
	
	/**
	 * stops the container with the given id
	 * 
	 * @param containerId
	 */
	public void stopContainer(String containerId);
	
	/**
	 * stops and destroys the container with the given id
	 * 
	 * @param containerId
	 */
	public void destroyContainer(String containerId);
	
	/**
	 * returns the url for the hawtio webapp
	 * 
	 * @return
	 */
	public String getWebUrl();
	
	/**
	 * returns the url for the fabric internal git repo
	 * 
	 * @return
	 */
	public String getGitUrl();
	
	/**
	 * returns a list of versions
	 * 
	 * @return
	 */
	public List<VersionDTO> getVersions();
	
	/**
	 * deletes the version with the given id
	 * 
	 * @param versionId
	 */
	public void deleteVersion(String versionId);
	
	/**
	 * returns the url for the maven download proxy
	 * 
	 * @return
	 */
	public String getMavenProxyDownloadUrl();
	
	/**
	 * returns the url for the maven upload proxy
	 * 
	 * @return
	 */
	public String getMavenProxyUploadUrl();
	
	/**
	 * returns the profiles for a specific version
	 * 
	 * @param versionId
	 * @return
	 */
	public List<ProfileDTO> getProfiles(String versionId);

	/**
	 * returns the profile for the given version and profile id
	 * 
	 * @param versionId
	 * @param profileId
	 * @return
	 */
	public ProfileDTO getProfile(String versionId, String profileId);
	
	/**
	 * creates a profile for the given version and profile id
	 * 
	 * @param versionId
	 * @param profileId
	 * @return
	 */
	public ProfileDTO createProfile(String versionId, String profileId);
	
	/**
	 * deletes a profile
	 * 
	 * @param versionId	the version id
	 * @param profileId	the profile id
	 */
	public void deleteProfile(String versionId, String profileId);
	
	/**
	 * creates a version with the given id
	 * 
	 * @param versionId
	 * @return
	 */
	public VersionDTO createVersion(String versionId);
	
	/**
	 * creates a new version as sub version for the given parent version id
	 * 
	 * @param parentVersionId
	 * @param versionId
	 * @return
	 */
	public VersionDTO createVersion(String parentVersionId, String versionId);
	
	/**
	 * returns the default version
	 * 
	 * @return
	 */
	public VersionDTO getDefaultVersion();
	
	/**
	 * sets the default version
	 * 
	 * @param versionId
	 */
	public void setDefaultVersion(String versionId);
	
	/**
	 * sets the version of the container
	 * 
	 * @param containerId
	 * @param versionId
	 */
	public void setVersionForContainer(String containerId, String versionId);
	
	/**
	 * sets the profiles for a container - current profile ids are replaced fully
	 * 
	 * @param containerId
	 * @param versionId
	 * @param profileIds
	 */
    public void setProfilesForContainer(String containerId, String versionId, List<String> profileIds);

    /**
     * adds one or more profiles to a container
     * 
     * @param containerId
     * @param profileIds
     */
    public void addProfilesToContainer(String containerId, String... profileIds);

    /**
     * removes one or more profiles from a container
     * 
     * @param containerId
     * @param profileIds
     */
    public void removeProfilesFromContainer(String containerId, String... profileIds);
    
    /**
     * sets the profile optionals
     * 
     * @param versionId
     * @param profileId
     * @param optionals
     */
    public void setProfileOptionals(String versionId, String profileId, List<String> optionals);
    
    /**
     * sets the profile bundles
     * 
     * @param versionId
     * @param profileId
     * @param bundles
     */
    public void setProfileBundles(String versionId, String profileId, List<String> bundles);

    /**
     * set profile fabs
     * 
     * @param versionId
     * @param profileId
     * @param fabs
     */
    public void setProfileFabs(String versionId, String profileId, List<String> fabs);
    
    /**
     * sets the profile features
     * 
     * @param versionId
     * @param profileId
     * @param features
     */
    public void setProfileFeatures(String versionId, String profileId, List<String> features);
    
    /**
     * sets the profile overrides
     * 
     * @param versionId
     * @param profileId
     * @param overrides
     */
    public void setProfileOverrides(String versionId, String profileId, List<String> overrides);
    
    /**
     * sets the profile parent ids
     * 
     * @param versionId
     * @param profileId
     * @param parentIds
     */
    public void setProfileParentIds(String versionId, String profileId, List<String> parentIds);
    
    /**
     * sets the profile repositories
     * 
     * @param versionId
     * @param profileId
     * @param repositories
     */
    public void setProfileRepositories(String versionId, String profileId, List<String> repositories);
    
	/**
	 * returns the zookeeper url
	 * 
	 * @return
	 */
	public String getZookeeperUrl();
	
	/**
	 * returns the maven repo uri
	 * 
	 * @return
	 */
	public String getMavenRepoURI();
	
	/**
	 * returns the requirements
	 * 
	 * @return
	 */
	public RequirementsDTO getRequirements();
	
	/**
	 * returns the requirements
	 * 
	 * @return
	 */
	public void setRequirements(RequirementsDTO requirements);
	
	/**
	 * returns the fabric status
	 * 
	 * @return
	 */
	public FabricStatusDTO getFabricStatus();
	
	/**
	 * creates a container
	 * 
	 * @param options
	 * @return
	 */
	public CreateContainerMetadataDTO[] createContainers(CreateContainerOptionsDTO options);
	
	/**
	 * creates a container
	 * 
	 * @param options
	 * @return
	 */
	public CreateContainerMetadataDTO[] createContainers(CreateContainerOptions options);
	
	/**
	 * queries the logs using the given filter 
	 * 
	 * @param filter
	 * @return
	 */
	public LogResultsDTO queryLog(LogFilter filter);
}
