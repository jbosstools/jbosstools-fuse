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
package org.fusesource.ide.jmx.fabric8.navigator.dto;

import io.fabric8.api.HasId;
import io.fabric8.api.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.navigator.ProfileNode;

/**
 * @author lhein
 */
public class ProfileBean implements Profile, HasId {

	private static final String KEY_ATTRIBUTES = "attributes";
	private static final String KEY_ABSTRACT = "abstract";
	private static final String KEY_BUNDLES = "bundles";
	private static final String KEY_CHILD_IDS = "childIds";
	private static final String KEY_CONFIGURATION_FILE_NAMES = "configurationFileNames";
	private static final String KEY_CONFIGURATIONS = "configurations";
	private static final String KEY_CONTAINER_COUNT = "containerCount";
	private static final String KEY_CONTAINERS = "containers";
	private static final String KEY_ENDORSED_LIBRARIES = "endorsedLibraries";
	private static final String KEY_EXTENSION_LIBRARIES = "extensionLibraries";
	private static final String KEY_FABS = "fabs";
	private static final String KEY_FEATURES = "features";
	private static final String KEY_FILE_CONFIGURATIONS = "fileConfigurations";
	private static final String KEY_HIDDEN = "hidden";
	private static final String KEY_ICON_URL = "iconURL";
	private static final String KEY_ID = "id";
	private static final String KEY_LIBRARIES = "libraries";
	private static final String KEY_LOCKED = "locked";
	private static final String KEY_OPTIONALS = "optionals";
	private static final String KEY_OVERLAY = "overlay";
	private static final String KEY_OVERRIDES = "overrides";
	private static final String KEY_PARENT_IDS = "parentIds";
	private static final String KEY_PARENTS = "parents";
	private static final String KEY_PROFILE_HASH = "profileHash";
	private static final String KEY_REPOSITORIES = "repositories";
	private static final String KEY_SUMMARY_MARKDOWN = "summaryMarkdown";
	private static final String KEY_TAGS = "tags";
	private static final String KEY_VERSION = "version";

	private Map<String, Object> data;
	private Map<String, ProfileBean> profiles;
	private String versionId;
	private ProfileNode parent;
	
	/**
	 * creates a profile dto
	 * 
	 * @param parent
	 * @param version
	 * @param data
	 */
	public ProfileBean(ProfileNode parent, String version, Map<String, Object> data) {
		this.data = data;
		this.versionId = version;
		this.parent = parent;
	}
	
	/**
	 * creates a profile dto
	 * 
	 * @param parent
	 * @param version
	 * @param profileId
	 */
	public ProfileBean(ProfileNode parent, String version, String profileId) {
		this.versionId = version;
		this.parent = parent;
		try {
			this.data = this.parent.getFabric().getFacade().queryProfile(profileId, version);
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
			this.data = new HashMap<String, Object>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Profile o) {
		return getId().compareTo(o.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.HasId#getId()
	 */
	@Override
	public String getId() {
		return (String) this.data.get(KEY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		if (this.data.get(KEY_ATTRIBUTES) != null) {
			return (Map<String, String>) this.data.get(KEY_ATTRIBUTES);
		}
		return new HashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getBundles()
	 */
	@Override
	public List<String> getBundles() {
		List<String> bundleList = new ArrayList<String>();
		String[] bundles = this.data.get(KEY_BUNDLES) != null ? (String[]) this.data
				.get(KEY_BUNDLES) : new String[0];
		for (String bundle : bundles)
			bundleList.add(bundle);
		return bundleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getConfiguration(java.lang.String)
	 */
	@Override
	public Map<String, String> getConfiguration(String pid) {
		Map<String, Map<String, String>> configurations = getConfigurations();
		if (configurations != null) {
			return configurations.get(pid);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getConfigurationFileNames()
	 */
	@Override
	public Set<String> getConfigurationFileNames() {
		String[] cfn = this.data.containsKey(KEY_CONFIGURATION_FILE_NAMES) ? (String[])this.data.get(KEY_CONFIGURATION_FILE_NAMES) : new String[0];
		Set<String> cfnset = new HashSet<String>();
		for (String name : cfn) {
			cfnset.add(name);
		}
		return cfnset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getConfigurations()
	 */
	@Override
	public Map<String, Map<String, String>> getConfigurations() {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
		String[] pids = this.data.containsKey(KEY_CONFIGURATIONS) ? (String[])this.data.get(KEY_CONFIGURATIONS) : new String[0];
		for (String pid : pids) {
			result.put(pid, new HashMap<String, String>()); // we use empty hashmap as value for the moment until we can grab the real config
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getEndorsedLibraries()
	 */
	@Override
	public List<String> getEndorsedLibraries() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_ENDORSED_LIBRARIES) != null ? (String[]) this.data
				.get(KEY_ENDORSED_LIBRARIES) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getExtensionLibraries()
	 */
	@Override
	public List<String> getExtensionLibraries() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_EXTENSION_LIBRARIES) != null ? (String[]) this.data
				.get(KEY_EXTENSION_LIBRARIES) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getFabs()
	 */
	@Override
	public List<String> getFabs() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_FABS) != null ? (String[]) this.data
				.get(KEY_FABS) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getFeatures()
	 */
	@Override
	public List<String> getFeatures() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_FEATURES) != null ? (String[]) this.data
				.get(KEY_FEATURES) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getFileConfiguration(java.lang.String)
	 */
	@Override
	public byte[] getFileConfiguration(String arg0) {
		return new byte[0]; // not yet supported
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getFileConfigurations()
	 */
	@Override
	public Map<String, byte[]> getFileConfigurations() {
		return new HashMap<String, byte[]>(); // not yet supported
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getIconURL()
	 */
	@Override
	public String getIconURL() {
		return this.data.containsKey(KEY_ICON_URL) ? (String)this.data.get(KEY_ICON_URL) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getLibraries()
	 */
	@Override
	public List<String> getLibraries() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_LIBRARIES) != null ? (String[]) this.data
				.get(KEY_LIBRARIES) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getOptionals()
	 */
	@Override
	public List<String> getOptionals() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_OPTIONALS) != null ? (String[]) this.data
				.get(KEY_OPTIONALS) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getOverrides()
	 */
	@Override
	public List<String> getOverrides() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_OVERRIDES) != null ? (String[]) this.data
				.get(KEY_OVERRIDES) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getParentIds()
	 */
	@Override
	public List<String> getParentIds() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_PARENT_IDS) != null ? (String[]) this.data
				.get(KEY_PARENT_IDS) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getParents()
	 */
	@Override
	public List<Profile> getParents() {
		List<Profile> list = new ArrayList<Profile>();
		String[] entryset = this.data.get(KEY_PARENTS) != null ? (String[]) this.data
				.get(KEY_PARENTS) : new String[0];
		for (String entry : entryset)
			list.add(new ProfileBean(this.parent, getVersion(), entry));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getProfileHash()
	 */
	@Override
	public String getProfileHash() {
		return this.data.containsKey(KEY_PROFILE_HASH) ? (String)this.data.get(KEY_PROFILE_HASH) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getRepositories()
	 */
	@Override
	public List<String> getRepositories() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_REPOSITORIES) != null ? (String[]) this.data
				.get(KEY_REPOSITORIES) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getSummaryMarkdown()
	 */
	@Override
	public String getSummaryMarkdown() {
		return this.data.containsKey(KEY_SUMMARY_MARKDOWN) ? (String)this.data.get(KEY_SUMMARY_MARKDOWN) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getTags()
	 */
	@Override
	public List<String> getTags() {
		List<String> list = new ArrayList<String>();
		String[] entryset = this.data.get(KEY_TAGS) != null ? (String[]) this.data
				.get(KEY_TAGS) : new String[0];
		for (String entry : entryset)
			list.add(entry);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#getVersion()
	 */
	@Override
	public String getVersion() {
		return this.data.containsKey(KEY_VERSION) ? (String)this.data.get(KEY_VERSION) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#isAbstract()
	 */
	@Override
	public boolean isAbstract() {
		return this.data.containsKey(KEY_ABSTRACT) ? (boolean)this.data.get(KEY_ABSTRACT) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#isHidden()
	 */
	@Override
	public boolean isHidden() {
		return this.data.containsKey(KEY_HIDDEN) ? (boolean)this.data.get(KEY_HIDDEN) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#isLocked()
	 */
	@Override
	public boolean isLocked() {
		return this.data.containsKey(KEY_LOCKED) ? (boolean)this.data.get(KEY_LOCKED) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.fabric8.api.Profile#isOverlay()
	 */
	@Override
	public boolean isOverlay() {
		return this.data.containsKey(KEY_OVERLAY) ? (boolean)this.data.get(KEY_OVERLAY) : null;
	}

}
