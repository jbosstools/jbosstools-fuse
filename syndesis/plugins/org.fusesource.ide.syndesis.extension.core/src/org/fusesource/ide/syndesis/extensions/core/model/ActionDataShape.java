/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lheinema
 */
public class ActionDataShape {
	@JsonProperty("kind")
	private String kind;

	private Map<String, Object> otherProperties = new HashMap<>();
	
	@JsonAnyGetter
	public Map<String, Object> any() {
		return otherProperties;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		otherProperties.put(name, value);
	}
	
	/**
	 * @return the kind
	 */
	public String getKind() {
		return this.kind;
	}
	
	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
}
