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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.dmr.Property;

/**
 * @author lhein
 */
public class RequirementsDTO extends BaseDTO {

	public static final String JSON_PROFILE_REQS = "profileRequirements";
	public static final String JSON_VERSION = "version";

	private Map<String, ProfileRequirementsDTO> profileRequirements;
	private SshConfigurationDTO sshConfiguration;

	/**
	 * 
	 * @param fabric8
	 * @param jsonAttribs
	 */
	public RequirementsDTO(Fabric8Facade fabric8,
			Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
		this.profileRequirements = new HashMap<String, ProfileRequirementsDTO>();
	}

	public void addProfileRequirement(ProfileRequirementsDTO profileRequirement) {
		if (this.profileRequirements.containsKey(profileRequirement
				.getProfile()) == false) {
			this.profileRequirements.put(profileRequirement.getProfile(),
					profileRequirement);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#create()
	 */
	@Override
	public void create() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#delete()
	 */
	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	public SshConfigurationDTO getSshConfiguration() {
		return sshConfiguration;
	}

	public void setSshConfiguration(SshConfigurationDTO sshConfiguration) {
		this.sshConfiguration = sshConfiguration;
	}

	public List<SshHostConfigurationDTO> getSshHosts() {
		if (sshConfiguration != null) {
			return sshConfiguration.getHosts();
		} else {
			return new ArrayList<>();
		}
	}

	// Fluid API to make constructing requirements easier
	// -------------------------------------------------------------------------
	/**
	 * Looks up and lazily creates if required a SSH host configuration for the
	 * given host alias. The host name will be defaulted to the same hostName
	 * value for cases when the alias is the same as the actual host name
	 */
	public SshHostConfigurationDTO sshHost(String hostName) {
		SshConfigurationDTO config = getSshConfiguration();
		if (config == null) {
			config = new SshConfigurationDTO();
			setSshConfiguration(config);
		}
		List<SshHostConfigurationDTO> hosts = config.getHosts();
		if (hosts == null) {
			hosts = new ArrayList<>();
			config.setHosts(hosts);
		}
		return config.host(hostName);
	}

	/**
	 * Returns the requirements for the given profile; lazily creating
	 * requirements if none exist yet.
	 */
	public ProfileRequirementsDTO profile(String profileId) {
		return getOrCreateProfileRequirement(profileId);
	}

	/**
	 * Returns the ssh configuration; lazily creating one if it does not exist
	 * yet
	 */
	public SshConfigurationDTO sshConfiguration() {
		SshConfigurationDTO answer = getSshConfiguration();
		if (answer == null) {
			answer = new SshConfigurationDTO();
			setSshConfiguration(answer);
		}
		return answer;
	}

	public ProfileRequirementsDTO findProfileRequirements(String profileId) {
		return profileRequirements.get(profileId);
	}

	public void removeProfileRequirements(String profileId) {
		if (this.profileRequirements.containsKey(profileId)) {
			this.profileRequirements.remove(profileId);
		}
	}

	public ProfileRequirementsDTO getOrCreateProfileRequirement(String profileId) {
		if (!this.profileRequirements.containsKey(profileId)) {
			this.profileRequirements.put(profileId,
					ProfileRequirementsDTO.createEmpty(fabric8, profileId));
		}
		return this.profileRequirements.get(profileId);
	}

	public void addOrUpdateProfileRequirements(
			ProfileRequirementsDTO profileReqs) {
		if (this.profileRequirements.containsKey(profileReqs.getProfile())) {
			// update
			ProfileRequirementsDTO toBeUpdated = this
					.findProfileRequirements(profileReqs.getProfile());
			toBeUpdated
					.setDependentProfiles(profileReqs.getDependentProfiles());
			toBeUpdated.setMinimumInstances(profileReqs.getMinimumInstances());
			toBeUpdated.setMaximumInstances(profileReqs.getMaximumInstances());
		} else {
			// add
			this.profileRequirements.put(profileReqs.getProfile(), profileReqs);
		}
	}

	public ModelNode toJSon() {
		ModelNode root = new ModelNode();
		root.get(JSON_VERSION).set((String) getFieldValue(JSON_VERSION));
		ModelNode listNode = root.get(JSON_PROFILE_REQS).setEmptyList();
		for (ProfileRequirementsDTO pr : this.profileRequirements.values()) {
			listNode.add(pr.toJson());
		}
		return root;
	}

	/**
	 * generates a requirements object out of a json string
	 * 
	 * @param fabricFacade
	 * @param nodes
	 * @return
	 */
	public static RequirementsDTO fromJson(Fabric8Facade fabricFacade,
			ModelNode rootNode) {
		Map<String, Object> data = new HashMap<String, Object>();
		String version = JsonHelper.getAsString(rootNode,
				RequirementsDTO.JSON_VERSION);
		if (version != null)
			data.put(RequirementsDTO.JSON_VERSION, version);
		List<ModelNode> depProfsNodes = JsonHelper.getAsList(rootNode,
				RequirementsDTO.JSON_PROFILE_REQS);
		RequirementsDTO requirements = new RequirementsDTO(fabricFacade, data);
		for (ModelNode n : depProfsNodes) {
			Map<String, Object> map = JsonHelper.getAsMap(n);
			ProfileRequirementsDTO profReq = new ProfileRequirementsDTO(
					fabricFacade, map);
			requirements.addProfileRequirement(profReq);
		}
		return requirements;
	}

	/**
	 * generates a requirements object out of a json string
	 * 
	 * @param fabricFacade
	 * @param nodes
	 * @return
	 */
	public static RequirementsDTO fromJson(Fabric8Facade fabricFacade,
			List<ModelNode> nodes) {
		Map<String, Object> data = new HashMap<String, Object>();
		RequirementsDTO ro = new RequirementsDTO(fabricFacade, data);

		for (ModelNode mn : nodes) {
			if (mn.getType().equals(ModelType.PROPERTY)) {
				Property p = mn.asProperty();
				if (p.getName().equalsIgnoreCase(RequirementsDTO.JSON_VERSION)) {
					ro.getJsonAttributes().put(RequirementsDTO.JSON_VERSION,
							p.getValue().asString());
				} else {
					List<ModelNode> nl = JsonHelper.getAsList(mn, p.getName());
					for (ModelNode n : nl) {
						ProfileRequirementsDTO profReq = ProfileRequirementsDTO
								.fromJson(fabricFacade, n);
						ro.addProfileRequirement(profReq);
					}
				}
			}
		}

		return ro;
	}
}
