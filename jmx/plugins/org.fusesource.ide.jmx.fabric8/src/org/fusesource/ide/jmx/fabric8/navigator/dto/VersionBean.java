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
import io.fabric8.api.Version;
import io.fabric8.api.VersionSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.jmx.fabric8.navigator.VersionNode;

/**
 * @author lhein
 */
public class VersionBean implements Version, HasId {

	private static final String KEY_ID = "id";
	private static final String KEY_ATTRIBUTES = "attributes";
	private static final String KEY_DEFAULT_VERSION = "defaultVersion";
	private static final String KEY_PROFILE_IDS = "profileIds";
	private static final String KEY_PROFILES = "profiles";
	
	private Map<String, Object> data;
	private Map<String, ProfileBean> profiles;
	private VersionNode parent;
	
	/**
	 * creates a version dto 
	 * 
	 * @param data
	 */
	public VersionBean(VersionNode parent, Map<String, Object> data) {
		this.data = data;
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Version o) {
		return new VersionSequence(getId()).compareTo(new VersionSequence(o.getId()));
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.HasId#getId()
	 */
	@Override
	public String getId() {
		return (String)this.data.get(KEY_ID);
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.Version#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		if (this.data.get(KEY_ATTRIBUTES) != null) {
			return (Map<String, String>)this.data.get(KEY_ATTRIBUTES);
		}
		return new HashMap<String, String>();
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.Version#getProfile(java.lang.String)
	 */
	@Override
	public Profile getProfile(String arg0) {
		return this.profiles.get(arg0);
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.Version#getProfileIds()
	 */
	@Override
	public List<String> getProfileIds() {
		List<String> ids = new ArrayList<String>();
		String[] profileIds = this.data.get(KEY_PROFILE_IDS) != null ? (String[])this.data.get(KEY_PROFILE_IDS) : (this.data.get(KEY_PROFILES) != null ? (String[])this.data.get(KEY_PROFILES) : new String[0]);
		for (String id : profileIds) ids.add(id);
		return ids;
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.Version#getProfiles()
	 */
	@Override
	public List<Profile> getProfiles() {
		List<Profile> profiles = new ArrayList<Profile>();
		profiles.addAll(this.profiles.values());
		return profiles;
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.Version#getRequiredProfile(java.lang.String)
	 */
	@Override
	public Profile getRequiredProfile(String arg0) {
		return this.profiles.get(arg0);
	}

	/* (non-Javadoc)
	 * @see io.fabric8.api.Version#hasProfile(java.lang.String)
	 */
	@Override
	public boolean hasProfile(String arg0) {
		return getProfile(arg0) != null;
	}
	
	/**
	 * returns true if this version is the default version
	 * 
	 * @return
	 */
	public boolean isDefaultVersion() {
		if (this.data.get(KEY_DEFAULT_VERSION) != null) {
			return (boolean)this.data.get(KEY_DEFAULT_VERSION);
		}
		return false;
	}

	@Override
	public String revision() {
		// TODO Auto-generated method stub
		return null;
	}
}
