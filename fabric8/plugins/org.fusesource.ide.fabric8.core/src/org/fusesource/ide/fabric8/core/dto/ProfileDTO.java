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
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;

/**
 * DTO class for handling Fabric8 profiles
 * 
 * @author lhein
 */
public class ProfileDTO extends BaseDTO {
	
	private static final String JSON_FIELD_ABSTRACT 			= "abstract"; 					// boolean
	private static final String JSON_FIELD_HIDDEN 				= "hidden"; 					// boolean
	private static final String JSON_FIELD_LOCKED 				= "locked"; 					// boolean
	private static final String JSON_FIELD_OVERLAY 				= "overlay"; 					// boolean
	private static final String JSON_FIELD_CONTAINER_COUNT 		= "containerCount"; 			// int
	private static final String JSON_FIELD_ICON_URL 			= "iconURL"; 					// string
	private static final String JSON_FIELD_PROFILE_HASH 		= "profileHash"; 				// string
	private static final String JSON_FIELD_SUMMARY_MARKDOWN 	= "summaryMarkdown"; 			// string
	private static final String JSON_FIELD_VERSION 				= "version"; 					// string
	private static final String JSON_FIELD_CHILD_IDS 			= "childIds"; 					// list
	private static final String JSON_FIELD_CONTAINERS 			= "containers"; 				// list
	private static final String JSON_FIELD_FABS 				= "fabs"; 						// list
	private static final String JSON_FIELD_FEATURES 			= "features"; 					// list
	private static final String JSON_FIELD_PARENT_IDS 			= "parentIds"; 					// list
	private static final String JSON_FIELD_TAGS 				= "tags"; 						// list
	private static final String JSON_FIELD_BUNDLES 				= "bundles"; 					// list
	private static final String JSON_FIELD_LIBS 				= "libraries"; 					// list
	private static final String JSON_FIELD_REPOSITORIES 		= "repositories"; 				// list
	private static final String JSON_FIELD_OPTIONALS 			= "optionals"; 					// list
	private static final String JSON_FIELD_OVERRIDES 			= "overrides"; 					// list
	private static final String JSON_FIELD_ENDORSED_LIBS 		= "endorsedLibraries"; 			// list
	private static final String JSON_FIELD_EXTENSION_LIBS 		= "extensionLibraries"; 		// list
	private static final String JSON_FIELD_CONFIGS 				= "configurations"; 			// list
	
	private static final String JSON_FIELD_FILE_CONFIGS 		= "fileConfigurations"; 		// list
	private static final String JSON_FIELD_CONFIG_FILE_NAMES 	= "configurationFileNames"; 	// list
	private static final String JSON_FIELD_PARENTS 				= "parents"; 					// list
	
	/**
	 * creates a container facade
	 * 
	 * @param fabric8		ref to fabric8facade
	 * @param attributes	the attributes
	 */
	public ProfileDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
	}
	
	/**
	 * returns the configurations
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getConfigurations() {
		return getFieldValue(JSON_FIELD_CONFIGS);
	}
	
	/**
	 * returns the extension libs
	 * 
	 * @return
	 */
	public List<String> getExtensionLibraries() {
		return getFieldValue(JSON_FIELD_EXTENSION_LIBS);
	}
	
	/**
	 * returns the endorsed libs
	 * 
	 * @return
	 */
	public List<String> getEndorsedLibraries() {
		return getFieldValue(JSON_FIELD_ENDORSED_LIBS);
	}
	
	/**
	 * returns the overrides
	 * 
	 * @return
	 */
	public List<String> getOverrides() {
		return getFieldValue(JSON_FIELD_OVERRIDES);
	}
	
	/**
	 * returns the optionals
	 * 
	 * @return
	 */
	public List<String> getOptionals() {
		return getFieldValue(JSON_FIELD_OPTIONALS);
	}
	
	/**
	 * returns the repositories
	 * 
	 * @return
	 */
	public List<String> getRepositories() {
		return getFieldValue(JSON_FIELD_REPOSITORIES);
	}
	
	/**
	 * returns the libraries
	 * 
	 * @return
	 */
	public List<String> getLibraries() {
		return getFieldValue(JSON_FIELD_LIBS);
	}
	
	/**
	 * returns the bundles
	 * 
	 * @return
	 */
	public List<String> getBundles() {
		return getFieldValue(JSON_FIELD_BUNDLES);
	}
	
	/**
	 * returns true if the profile is abstract
	 * 
	 * @return
	 */
	public Boolean isAbstract() {
		return getFieldValue(JSON_FIELD_ABSTRACT);
	}
	
	/**
	 * returns true if the profile is hidden
	 * 
	 * @return
	 */
	public Boolean isHidden() {
		return getFieldValue(JSON_FIELD_HIDDEN);
	}
	
	/**
	 * returns true if the profile is locked
	 * 
	 * @return
	 */
	public Boolean isLocked() {
		return getFieldValue(JSON_FIELD_LOCKED);
	}
	
	/**
	 * returns true if the profile is overlay
	 * 
	 * @return
	 */
	public Boolean isOverlay() {
		return getFieldValue(JSON_FIELD_OVERLAY);
	}
	
	/**
	 * returns the amount of containers running this profile
	 * 
	 * @return
	 */
	public Integer getContainerCount() {
		return getFieldValue(JSON_FIELD_CONTAINER_COUNT);
	}
	
	/**
	 * returns the icon url
	 * 
	 * @return
	 */
	public String getIconUrl() {
		return getFieldValue(JSON_FIELD_ICON_URL);
	}
	
	/**
	 * returns the hashcode for this profile
	 * 
	 * @return
	 */
	public String getProfileHash() {
		return getFieldValue(JSON_FIELD_PROFILE_HASH);
	}
	
	/**
	 * returns the summary markdown for this profile
	 * 
	 * @return
	 */
	public String getSummaryMarkdown() {
		return getFieldValue(JSON_FIELD_SUMMARY_MARKDOWN);
	}
	
	/**
	 * returns the version id for that profile
	 * 
	 * @return
	 */
	public String getVersionId() {
		return getFieldValue(JSON_FIELD_VERSION);
	}
	
	/**
	 * returns the child ids for this profile
	 * 
	 * @return
	 */
	public List<String> getChildProfileIDs() {
		return getFieldValue(JSON_FIELD_CHILD_IDS);
	}
	
	/**
	 * returns the container ids running this profile
	 * 
	 * @return
	 */
	public List<String> getContainerIDs() {
		return getFieldValue(JSON_FIELD_CONTAINERS);
	}
	
	/**
	 * returns a list of installed FABs
	 * 
	 * @return
	 */
	public List<String> getFabs() {
		return getFieldValue(JSON_FIELD_FABS);
	}
	
	/**
	 * returns a list of installed features
	 * 
	 * @return
	 */
	public List<String> getFeatures() {
		return getFieldValue(JSON_FIELD_FEATURES);
	}
	
	/**
	 * returns the list of parent profiles ids
	 * 
	 * @return
	 */
	public List<String> getParentIDs() {
		return getFieldValue(JSON_FIELD_PARENT_IDS);
	}
	
	/**
	 * returns the parents
	 * 
	 * @return
	 */
	public List<ProfileDTO> getParents() {
		List<ProfileDTO> parents = new ArrayList<ProfileDTO>();
		
		for (String pId : getParentIDs()) {
			ProfileDTO p = fabric8.getProfile(getVersionId(), pId);
			if (p != null) parents.add(p);
		}
		
		return parents;
	}
	
	/**
	 * returns a list of tags
	 * 
	 * @return
	 */
	public List<String> getTags() {
		return getFieldValue(JSON_FIELD_TAGS);
	}

	/**
	 * sets the optionals for a profile
	 * 
	 * @param optionals
	 */
	public void setOptionals(List<String> optionals) {
        setFieldValue(JSON_FIELD_OPTIONALS, optionals);
		fabric8.setProfileOptionals(getVersionId(), getId(), optionals);
    }

	/**
	 * sets the parent ids for a profile
	 * 
	 * @param profileIds
	 */
    public void setParentIds(List<String> profileIds) {
        setFieldValue(JSON_FIELD_PARENT_IDS, profileIds);
        fabric8.setProfileParentIds(getVersionId(), getId(), profileIds);
    }
    
    /**
	 * sets the parents for a profile
	 * 
	 * @param profiles
	 */
    public void setParents(List<ProfileDTO> profiles) {
    	List<String> ids = new ArrayList<String>();
    	for (ProfileDTO p : profiles) {
    		if (p != null && p.getId() != null) {
    			ids.add(p.getId());
    		}
    	}
    	setParentIds(ids);
    }
	
    /**
     * sets the bundles for a profile
     * 
     * @param bundles
     */
    public void setBundles(List<String> bundles) {
        setFieldValue(JSON_FIELD_BUNDLES, bundles);
        fabric8.setProfileBundles(getVersionId(), getId(), bundles);
    }

    /**
     * sets the fabs for a profile
     * 
     * @param fabs
     */
    public void setFabs(List<String> fabs) {
        setFieldValue(JSON_FIELD_FABS, fabs);
        fabric8.setProfileFabs(getVersionId(), getId(), fabs);
    }

    /**
     * sets the features for a profile
     * 
     * @param features
     */
    public void setFeatures(List<String> features) {
        setFieldValue(JSON_FIELD_FEATURES, features);
        fabric8.setProfileFeatures(getVersionId(), getId(), features);
    }

    /**
     * sets the repositories for a profile
     * 
     * @param repositories
     */
    public void setRepositories(List<String> repositories) {
    	setFieldValue(JSON_FIELD_REPOSITORIES, repositories);
        fabric8.setProfileRepositories(getVersionId(), getId(), repositories);
    }

    /**
     * sets the overrides for a profile
     * 
     * @param overrides
     */
    public void setOverrides(List<String> overrides) {
    	setFieldValue(JSON_FIELD_OVERRIDES, overrides);
        fabric8.setProfileOverrides(getVersionId(), getId(), overrides);
    }
		
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#create()
	 */
	@Override
	public void create() {
		ProfileDTO newProfile = this.fabric8.createProfile(getVersionId(), getId());
		newProfile.jsonAttribs.putAll(jsonAttribs);
		newProfile.update();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#delete()
	 */
	@Override
	public void delete() {
		fabric8.deleteProfile(getVersionId(), getId());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	
	
//abstract=false
//bundles=[]
//childIds=[karaf, docker, mq-client, mq-client-base, openshift, insight-metrics.rhq, insight-core, system-dns, autoscale]
//configurationFileNames=[io.fabric8.zookeeper.properties, org.ops4j.pax.web.properties#openshift, org.ops4j.pax.web.properties#docker, jetty.xml#openshift, jetty.xml, io.fabric8.jolokia.properties, io.fabric8.version.properties, jmx.acl.whitelist.properties, io.fabric8.agent.properties, icon.svg, Summary.md, jmx.acl.properties, org.ops4j.pax.web.properties, org.ops4j.pax.url.mvn.properties, io.fabric8.jaas.properties, io.fabric8.insight.metrics.json]
//configurations=[io.fabric8.zookeeper.properties, org.ops4j.pax.web.properties#openshift, org.ops4j.pax.web.properties#docker, jetty.xml#openshift, jetty.xml, io.fabric8.jolokia.properties, io.fabric8.version.properties, jmx.acl.whitelist.properties, io.fabric8.agent.properties, icon.svg, Summary.md, jmx.acl.properties, org.ops4j.pax.web.properties, org.ops4j.pax.url.mvn.properties, io.fabric8.jaas.properties, io.fabric8.insight.metrics.json]
//containerCount=0
//containers=[]
//endorsedLibraries=[]
//extensionLibraries=[]
//fabs=[]
//features=[fabric-agent, fabric-git, fabric-jaas, insight-log, jolokia, fabric-git-server, karaf, fabric-core, fabric-web]
//fileConfigurations=[io.fabric8.zookeeper.properties, org.ops4j.pax.web.properties#openshift, org.ops4j.pax.web.properties#docker, jetty.xml#openshift, jetty.xml, io.fabric8.jolokia.properties, io.fabric8.version.properties, jmx.acl.whitelist.properties, io.fabric8.agent.properties, icon.svg, Summary.md, jmx.acl.properties, org.ops4j.pax.web.properties, org.ops4j.pax.url.mvn.properties, io.fabric8.jaas.properties, io.fabric8.insight.metrics.json]
//hidden=false
//iconURL=/version/1.0/profile/default/file/icon.svg
//id=default
//libraries=[]
//locked=false
//optionals=[mvn:org.ops4j.base/ops4j-base-lang/1.4.0]
//overlay=false
//overrides=[]
//parentIds=[]
//parents=[]
//profileHash=b2f2052
//repositories=[mvn:org.apache.karaf.assemblies.features/standard/${version:karaf}/xml/features, mvn:io.fabric8/fabric8-karaf/${version:fabric}/xml/features]
//summaryMarkdown=This is a base profile which is useable to extend by custom profiles
//tags=[]
//version=1.0
}
