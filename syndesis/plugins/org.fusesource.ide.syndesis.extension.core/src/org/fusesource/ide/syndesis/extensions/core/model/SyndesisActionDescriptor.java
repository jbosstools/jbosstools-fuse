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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lheinema
 */
public class SyndesisActionDescriptor {
	@JsonProperty("kind")
	private String kind;							// endpoint|bean|step
	
	@JsonProperty("entrypoint")
	private String entryPoint;						// direct:${groupId}/${artifactId}/${actionId}
	
	@JsonProperty("inputDataShape")
	private ActionDataShape inputDataShape;
	
	@JsonProperty("outputDataShape")
	private ActionDataShape outputDataShape;
	
	@JsonProperty("propertyDefinitionSteps")
	private List<String> propertyDefinitionSteps = new ArrayList<>();
	
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
	 * @return the propertyDefinitionSteps
	 */
	public List<String> getPropertyDefinitionSteps() {
		return this.propertyDefinitionSteps;
	}

	/**
	 * @param tags
	 */
	public void setPropertyDefinitionSteps(List<String> propertyDefinitionSteps) {
		this.propertyDefinitionSteps = propertyDefinitionSteps;
	}

	/**
	 * @return the entryPoint
	 */
	public String getEntryPoint() {
		return this.entryPoint;
	}
	
	/**
	 * @param entryPoint the entryPoint to set
	 */
	public void setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
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
	
	/**
	 * @return the inputDataShape
	 */
	public ActionDataShape getInputDataShape() {
		return this.inputDataShape;
	}
	
	/**
	 * @param inputDataShape the inputDataShape to set
	 */
	public void setInputDataShape(ActionDataShape inputDataShape) {
		this.inputDataShape = inputDataShape;
	}
	
	/**
	 * @return the outputDataShape
	 */
	public ActionDataShape getOutputDataShape() {
		return this.outputDataShape;
	}
	
	/**
	 * @param outputDataShape the outputDataShape to set
	 */
	public void setOutputDataShape(ActionDataShape outputDataShape) {
		this.outputDataShape = outputDataShape;
	}
}
