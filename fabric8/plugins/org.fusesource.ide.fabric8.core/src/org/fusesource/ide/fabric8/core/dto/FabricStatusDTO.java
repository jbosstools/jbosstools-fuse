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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 *
 */
public class FabricStatusDTO extends BaseDTO {

	public static final String JSON_PROFILE_STATUS_MAP = "profileStatusMap";
	public static final String JSON_REQUIREMENTS 		= "requirements";
	
	private Map<String, ProfileStatusDTO> profileStatusMap;
	private RequirementsDTO requirements;
	
	/**
	 * 
	 * @param fabric8
	 * @param jsonAttribs
	 */
	public FabricStatusDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
		this.profileStatusMap = new HashMap<String, ProfileStatusDTO>();
	}
	
	/**
	 * @return the requirements
	 */
	public RequirementsDTO getRequirements() {
		return this.requirements;
	}
	
	/**
	 * @param requirements the requirements to set
	 */
	public void setRequirements(RequirementsDTO requirements) {
		this.requirements = requirements;
	}
	
	public void addProfileStatus(ProfileStatusDTO req) {
		if (this.profileStatusMap.containsKey(req.getProfile()) == false) {
			this.profileStatusMap.put(req.getProfile(), req);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#create()
	 */
	@Override
	public void create() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#delete()
	 */
	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	public Map<String, ProfileStatusDTO> getProfileStatusMap() {
		return this.profileStatusMap;
	}
	
	/**
	 * generates the object out of json string
	 * 
	 * @param fabricFacade
	 * @param json
	 * @return
	 */
	public static FabricStatusDTO fromJson(Fabric8Facade fabricFacade, ModelNode rootNode) {
		FabricStatusDTO status = new FabricStatusDTO(fabricFacade, new HashMap<String, Object>());

		List<ModelNode> nodes = JsonHelper.getAsList(rootNode, FabricStatusDTO.JSON_PROFILE_STATUS_MAP);
		for (ModelNode mn : nodes) {
			status.addProfileStatus(ProfileStatusDTO.fromJson(fabricFacade, mn));
		}

		nodes = JsonHelper.getAsList(rootNode, FabricStatusDTO.JSON_REQUIREMENTS);
		RequirementsDTO ro = RequirementsDTO.fromJson(fabricFacade, nodes);
		status.setRequirements(ro);
		
		return status;
	}
}
