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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 *
 */
public class ProfileStatusDTO extends BaseDTO {
	
	private static final String JSON_PROFILE_ID 	= "profile";
	private static final String JSON_MIN_INSTANCES 	= "minimumInstances";
	private static final String JSON_MAX_INSTANCES 	= "maximumInstances";
	private static final String JSON_COUNT 			= "count";
	private static final String JSON_DEP_PROFILES 	= "dependentProfiles";
	private static final String JSON_HEALTH			= "health";
	
	private ProfileRequirementsDTO reqs;
	
	/**
	 * 
	 * @param fabric8
	 * @param jsonAttribs
	 */
	public ProfileStatusDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.fabric8.core.dto.BaseDTO#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

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
	
	public String getProfile() {
		return getFieldValue(JSON_PROFILE_ID);
	}

	public BigInteger getMinimumInstances() {
		return getFieldValue(JSON_MIN_INSTANCES);
	}

	public BigInteger getMaximumInstances() {
		return getFieldValue(JSON_MAX_INSTANCES);
	}
	
	public BigInteger getCount() {
		return getFieldValue(JSON_COUNT);
	}

	public List<String> getDependentProfiles() {
		return getFieldValue(JSON_DEP_PROFILES);
	}
	
	public BigDecimal getHealth(BigInteger count) {
		return getFieldValue(JSON_HEALTH);
	}

	public synchronized ProfileRequirementsDTO getRequirements() {
		if (reqs == null) {
			// load requirements
			RequirementsDTO r = fabric8.getRequirements();
			reqs = r.findProfileRequirements(getProfile());
		}
		return reqs;
	}
	
	/**
	 * generates a profile status out of a json string
	 * 
	 * @param fabricFacade
	 * @param root
	 * @return
	 */
	public static ProfileStatusDTO fromJson(Fabric8Facade fabricFacade, ModelNode root) {
		String profileId = root.keys().iterator().next();
		Map<String, Object> vals = JsonHelper.getAsMap(root.get(profileId));
		ProfileStatusDTO ps = new ProfileStatusDTO(fabricFacade, vals);
		return ps;
	}
}
