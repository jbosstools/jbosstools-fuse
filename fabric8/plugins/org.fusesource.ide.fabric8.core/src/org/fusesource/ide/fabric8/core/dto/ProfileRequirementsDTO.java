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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;
import org.fusesource.ide.fabric8.core.utils.JsonHelper;
import org.jboss.dmr.ModelNode;

/**
 * @author lhein
 */
public class ProfileRequirementsDTO extends BaseDTO {

	private static final String JSON_PROFILE	   = "profile";
	private static final String JSON_MIN_INSTANCES = "minimumInstances";
	private static final String JSON_MAX_INSTANCES = "maximumInstances";
	private static final String JSON_DEP_PROFILES  = "dependentProfiles";
	
	/**
	 * 
	 * @param fabric8
	 * @param jsonAttribs
	 */
	public ProfileRequirementsDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		super(fabric8, jsonAttribs);
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
	
	public String getProfile() {
		return getFieldValue(JSON_PROFILE);
	}
	
	public BigInteger getMinimumInstances() {
		return getFieldValue(JSON_MIN_INSTANCES);
	}
	
	public void setMinimumInstances(BigInteger min) {
		setFieldValue(JSON_MIN_INSTANCES, min);
	}
	
	public BigInteger getMaximumInstances() {
		return getFieldValue(JSON_MAX_INSTANCES);
	}
	
	public void setMaximumInstances(BigInteger max) {
		setFieldValue(JSON_MAX_INSTANCES, max);
	}

	public List<String> getDependentProfiles() {
		return getFieldValue(JSON_DEP_PROFILES) != null ? (List<String>)getFieldValue(JSON_DEP_PROFILES) : new ArrayList<String>();
	}
	
	public void setDependentProfiles(List<String> depList) {
		setFieldValue(JSON_DEP_PROFILES, depList);
	}
	
	/**
	 * checks if the requirements are empty
	 * 
	 * @return
	 */
	public boolean checkIsEmpty() {
		// we allow 0 maximum instances as being non-empty so we can keep the
		// requirements around to
		// stop things
		return isEmpty(getMinimumInstances()) && 
			   isEmpty(getDependentProfiles()) && 
			   getMaximumInstances() == null;
	}

	protected static boolean isEmpty(BigInteger number) {
		return number == null || number.intValue() == 0;
	}

	protected static boolean isEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}
		
	public static ProfileRequirementsDTO createEmpty(Fabric8Facade fabric8, String profileId) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(JSON_DEP_PROFILES, new ArrayList<String>());
		map.put(JSON_PROFILE, profileId);
		map.put(JSON_MAX_INSTANCES, null);
		map.put(JSON_MIN_INSTANCES, BigInteger.ZERO);
		return new ProfileRequirementsDTO(fabric8, map);
	}
	
	public ModelNode toJson() {
		ModelNode root = new ModelNode();
		
		root.get(JSON_PROFILE).set(getProfile());
		if (getMaximumInstances() != null) root.get(JSON_MAX_INSTANCES).set(getMaximumInstances());
		if (getMinimumInstances() != null) root.get(JSON_MIN_INSTANCES).set(getMinimumInstances());
		
		ModelNode listNode = root.get(JSON_DEP_PROFILES).setEmptyList();
		for (String entry : getDependentProfiles()) {
			listNode.add(entry);
		}
		return root;
	}
	
	/**
	 * generates a profile requirement out of json string
	 * 
	 * @param fabricFacade
	 * @param node
	 * @return
	 */
	public static ProfileRequirementsDTO fromJson(Fabric8Facade fabricFacade, ModelNode node) {
		Map<String, Object> map = JsonHelper.getAsMap(node);
		ProfileRequirementsDTO profReq = new ProfileRequirementsDTO(fabricFacade, map);
		return profReq;
	}
}
