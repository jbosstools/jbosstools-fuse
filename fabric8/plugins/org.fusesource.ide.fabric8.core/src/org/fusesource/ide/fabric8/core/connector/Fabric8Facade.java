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
 * DTO class for handling Fabric8
 * 
 * @author lhein
 */
public class Fabric8Facade {
	
	private Fabric8ConnectorType connection;

	/**
	 * creates a fabric8 facade 
	 * 
	 * @param connection	the fabric8 connector type
	 */
	public Fabric8Facade(Fabric8ConnectorType connection) {
		this.connection = connection;
	}
	
	/**
	 * returns a list of all containers in the fabric
	 * 
	 * @return	a possibly empty list of all containers in the fabric, but never null
	 */
	public List<ContainerDTO> getContainers() {
		return this.connection.getContainers();
	}
	
	/**
	 * returns the container with the given id
	 * 
	 * @param containerId	the container id
	 * @return	the container or null if not found
	 */
	public ContainerDTO getContainer(String containerId) {
		return this.connection.getContainer(containerId);
	}
	
	/**
	 * returns the current container for fabric
	 * 
	 * @return
	 */
	public ContainerDTO getCurrentContainer() {
		return this.connection.getCurrentContainer();
	}
	
	/**
	 * starts the container
	 * 
	 * @param container
	 */
	public void startContainer(ContainerDTO container) {
		startContainer(container.getId());
	}
	
	/**
	 * starts the container
	 * 
	 * @param containerId
	 */
	public void startContainer(String containerId) {
		this.connection.startContainer(containerId);
	}
	
	/**
	 * stops the container
	 * 
	 * @param container
	 */
	public void stopContainer(ContainerDTO container) {
		stopContainer(container.getId());
	}
	
	/**
	 * stops the container
	 * 
	 * @param containerId
	 */
	public void stopContainer(String containerId) {
		this.connection.stopContainer(containerId);
	}
	
	/**
	 * destroys the container
	 * 
	 * @param container
	 */
	public void destroyContainer(ContainerDTO container) {
		destroyContainer(container.getId());
	}
	
	/**
	 * destroys the container
	 * 
	 * @param containerId
	 */
	public void destroyContainer(String containerId) {
		this.connection.destroyContainer(containerId);
	}
	
	/**
	 * sets the version of the container
	 * 
	 * @param containerId
	 * @param versionId
	 */
	public void setVersionForContainer(String containerId, String versionId) {
		this.connection.setVersionForContainer(containerId, versionId);
	}
		
	/**
	 * returns the web url of the hawtio app
	 * 
	 * @return
	 */
	public String getWebUrl() {
		return this.connection.getWebUrl();
	}
	
	/**
	 * returns the git repo url
	 * 
	 * @return
	 */
	public String getGitUrl() {
		return this.connection.getGitUrl();
	}
	
	/**
	 * returns the url of the upload proxy
	 * 
	 * @return
	 */
	public String getMavenUploadProxyUrl() {
		return this.connection.getMavenProxyUploadUrl();
	}

	/**
	 * returns the url of the download proxy
	 * 
	 * @return
	 */
	public String getMavenDownloadProxyUrl() {
		return this.connection.getMavenProxyDownloadUrl();
	}
	
	/**
	 * returns the default version 
	 * 
	 * @return
	 */
	public VersionDTO getDefaultVersion() {
		return this.connection.getDefaultVersion();
	}
	
	/**
	 * sets the default version
	 * 
	 * @param version
	 */
	public void setDefaultVersion(VersionDTO version) {
		setDefaultVersion(version.getId());
	}
	
	/**
	 * sets the default version
	 * 
	 * @param versionId
	 */
	public void setDefaultVersion(String versionId) {
		this.connection.setDefaultVersion(versionId);
	}
	
	/**
	 * returns the versions 
	 * 
	 * @return
	 */
	public List<VersionDTO> getVersions() {
		return this.connection.getVersions();
	}
	
	/**
	 * deletes a version
	 * 
	 * @param version
	 */
	public void deleteVersion(VersionDTO version) {
		deleteVersion(version.getId());
	}
	
	/**
	 * deletes a version
	 * 
	 * @param versionId
	 */
	public void deleteVersion(String versionId) {
		this.connection.deleteVersion(versionId);
	}
	
	/**
	 * creates a version with the given id
	 * 
	 * @param versionId
	 * @return
	 */
	public VersionDTO createVersion(String versionId) {
		return this.connection.createVersion(versionId);
	}
	
	/**
	 * creates a new version as sub version for the given parent version id
	 * 
	 * @param parentVersionId
	 * @param versionId
	 * @return
	 */
	public VersionDTO createVersion(String parentVersionId, String versionId) {
		return this.connection.createVersion(parentVersionId, versionId);
	}
	
	/**
	 * returns the profiles for a specific version
	 * 
	 * @param versionId
	 * @return
	 */
	public List<ProfileDTO> getProfiles(String versionId) {
		return this.connection.getProfiles(versionId);
	}

	/**
	 * returns the profile for the given version and profile id
	 * 
	 * @param versionId
	 * @param profileId
	 * @return
	 */
	public ProfileDTO getProfile(String versionId, String profileId) {
		return this.connection.getProfile(versionId, profileId);
	}
	
	/**
	 * creates a profile for the given version and profile id
	 * 
	 * @param versionId
	 * @param profileId
	 * @return
	 */
	public ProfileDTO createProfile(String versionId, String profileId) {
		return this.connection.createProfile(versionId, profileId);
	}
	
	/**
	 * deletes a profile
	 * 
	 * @param versionId	the version id
	 * @param profileId	the profile id
	 */
	public void deleteProfile(String versionId, String profileId) {
		this.connection.deleteProfile(versionId, profileId);
	}
	
	/**
	 * sets the profiles for the container
	 * 
	 * @param containerId
	 * @param versionId
	 * @param profileIds
	 */
    public void setProfilesForContainer(String containerId, String versionId, List<String> profileIds) {
        this.connection.setProfilesForContainer(containerId, versionId, profileIds);
    }

    /**
     * adds profiles to the container
     * 
     * @param containerId
     * @param profileIds
     */
    public void addProfilesToContainer(String containerId, String... profileIds) {
        this.connection.addProfilesToContainer(containerId, profileIds);    	
    }

    /**
     * removes profiles from a container
     * 
     * @param containerId
     * @param profileIds
     */
    public void removeProfiles(String containerId, String... profileIds) {
    	this.connection.removeProfilesFromContainer(containerId, profileIds);
    }
    
    /**
     * sets the bundles for a profile
     * 
     * @param versionId
     * @param profileId
     * @param bundles
     */
	public void setProfileBundles(String versionId, String profileId,
			List<String> bundles) {
		this.connection.setProfileBundles(versionId, profileId, bundles);
	}
	
	/**
	 * sets the fabs for a profile
	 * 
	 * @param versionId
	 * @param profileId
	 * @param fabs
	 */
	public void setProfileFabs(String versionId, String profileId,
			List<String> fabs) {
		this.connection.setProfileFabs(versionId, profileId, fabs);
	}
	
	/**
	 * sets the features for a profile
	 * 
	 * @param versionId
	 * @param profileId
	 * @param features
	 */
	public void setProfileFeatures(String versionId, String profileId,
			List<String> features) {
		this.connection.setProfileFeatures(versionId, profileId, features);
	}
	
	/**
	 * sets the optionals for a profile
	 * 
	 * @param versionId
	 * @param profileId
	 * @param optionals
	 */
	public void setProfileOptionals(String versionId, String profileId,
			List<String> optionals) {
		this.connection.setProfileOptionals(versionId, profileId, optionals);		
	}
	
	/**
	 * sets the overrides for a profile
	 * 
	 * @param versionId
	 * @param profileId
	 * @param overrides
	 */
	public void setProfileOverrides(String versionId, String profileId,
			List<String> overrides) {
		this.connection.setProfileOverrides(versionId, profileId, overrides);		
	}
	
	/**
	 * sets the parent ids for a profile
	 * 
	 * @param versionId
	 * @param profileId
	 * @param parentIds
	 */
	public void setProfileParentIds(String versionId, String profileId,
			List<String> parentIds) {
		this.connection.setProfileParentIds(versionId, profileId, parentIds);		
	}
	
	/**
	 * sets the repositories for a profile
	 * 
	 * @param versionId
	 * @param profileId
	 * @param repositories
	 */
	public void setProfileRepositories(String versionId, String profileId,
			List<String> repositories) {
		this.connection.setProfileRepositories(versionId, profileId, repositories);
	}
	
	/**
	 * returns the zookeeper url
	 * 
	 * @return
	 */
	public String getZookeeperUrl() {
		return this.connection.getZookeeperUrl();
	}
	
	/**
	 * returns the maven repo uri
	 * 
	 * @return
	 */
	public String getMavenRepoURI() {
		return this.connection.getMavenRepoURI();
	}
	
	/**
	 * returns the requirements
	 * 
	 * @return
	 */
	public RequirementsDTO getRequirements() {
		return this.connection.getRequirements();
	}
	
	/**
	 * sets the requirements
	 * 
	 * @return
	 */
	public void setRequirements(RequirementsDTO requirements) {
		this.connection.setRequirements(requirements);
	}
	
	/**
	 * returns the fabric status
	 * 
	 * @return
	 */
	public FabricStatusDTO getFabricStatus() {
		return this.connection.getFabricStatus();
	}

	/**
	 * creates a container
	 * 
	 * @param options
	 * @return
	 */
	public CreateContainerMetadataDTO[] createContainers(CreateContainerOptionsDTO options) {
		return this.connection.createContainers(options);
	}
	
	/**
	 * creates a container
	 * 
	 * @param options
	 * @return
	 */
	public CreateContainerMetadataDTO[] createContainers(CreateContainerOptions options) {
		return this.connection.createContainers(options);
	}
	
	/**
	 * queries the logs
	 * 
	 * @param filter
	 * @return
	 */
	public LogResultsDTO queryLog(LogFilter filter) {
		return this.connection.queryLog(filter);
	}
}
