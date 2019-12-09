/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.foundation.core.databinding.PojoModelObservable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lheinema
 */
public class SyndesisActionDescriptor extends PojoModelObservable {
	
	private static final String PROPERTY_OTHER = "property.other";
	private static final String PROPERTY_KIND = "property.kind";
	private static final String PROPERTY_ENTRYPOINT = "property.entrypoint";
	private static final String PROPERTY_INPUTDATASHAPE = "property.inputdatashape";
	private static final String PROPERTY_OUTPUTDATASHAPE = "property.outputdatashape";
	private static final String PROPERTY_PROPERTYDEFINITIONSTEPS = "property.propertydefinitionsteps";
	private static final String PROPERTY_COMPONENTSCHEME = "property.componentscheme";
	private static final String PROPERTY_CONFIGUREDPROPERTIES = "property.configuredproperties";
	private static final String PROPERTY_CONNECTORCUSTOMIZER = "property.connectorcustomizer";
	
	@JsonProperty("kind")
	private String kind;					
	
	@JsonProperty("entrypoint")
	private String entryPoint;
	
	@JsonProperty("componentScheme")
	private String componentScheme;
	
	@JsonProperty("inputDataShape")
	private ActionDataShape inputDataShape;
	
	@JsonProperty("outputDataShape")
	private ActionDataShape outputDataShape;
	
	@JsonProperty("connectorCustomizers")
	private List<String> connectorCustomizers = new ArrayList<>();
	
	@JsonProperty("propertyDefinitionSteps")
	private List<PropertyDefinitionStep> propertyDefinitionSteps = new ArrayList<>();
	
	@JsonProperty("configuredProperties")
	private Map<String, String> configuredProperties = new HashMap<>();
	
	/**
	 * otherProperties is catching all not matched json data (see JsonAnyGetter/Setter annotation)
	 */
	private Map<String, Object> otherProperties = new HashMap<>();
	
	@JsonAnyGetter
	public Map<String, Object> any() {
		return otherProperties;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		firePropertyChange(PROPERTY_OTHER, this.otherProperties.get(name), value);
		otherProperties.put(name, value);
	}

	/**
	 * @return the propertyDefinitionSteps
	 */
	public List<PropertyDefinitionStep> getPropertyDefinitionSteps() {
		return this.propertyDefinitionSteps;
	}

	/**
	 * @param tags
	 */
	public void setPropertyDefinitionSteps(List<PropertyDefinitionStep> propertyDefinitionSteps) {
		firePropertyChange(PROPERTY_PROPERTYDEFINITIONSTEPS, this.propertyDefinitionSteps, propertyDefinitionSteps);
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
		firePropertyChange(PROPERTY_ENTRYPOINT, this.entryPoint, entryPoint);
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
		firePropertyChange(PROPERTY_KIND, this.kind, kind);
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
		firePropertyChange(PROPERTY_INPUTDATASHAPE, this.inputDataShape, inputDataShape);
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
		firePropertyChange(PROPERTY_OUTPUTDATASHAPE, this.outputDataShape, outputDataShape);
		this.outputDataShape = outputDataShape;
	}
	
	/**
	 * @return the componentScheme
	 */
	public String getComponentScheme() {
		return this.componentScheme;
	}
	
	/**
	 * @param componentScheme the componentScheme to set
	 */
	public void setComponentScheme(String componentScheme) {
		firePropertyChange(PROPERTY_COMPONENTSCHEME, this.componentScheme, componentScheme);
		this.componentScheme = componentScheme;
	}
	
	/**
	 * @return the configuredProperties
	 */
	public Map<String, String> getConfiguredProperties() {
		return this.configuredProperties;
	}
	
	/**
	 * @param configuredProperties the configuredProperties to set
	 */
	public void setConfiguredProperties(Map<String, String> configuredProperties) {
		firePropertyChange(PROPERTY_CONFIGUREDPROPERTIES, this.configuredProperties, configuredProperties);
		this.configuredProperties = configuredProperties;
	}
	
	/**
	 * @return the connectorCustomizers
	 */
	public List<String> getConnectorCustomizers() {
		return this.connectorCustomizers;
	}
	
	/**
	 * @param connectorCustomizers the connectorCustomizers to set
	 */
	public void setConnectorCustomizers(List<String> connectorCustomizers) {
		firePropertyChange(PROPERTY_CONNECTORCUSTOMIZER, this.connectorCustomizers, connectorCustomizers);
		this.connectorCustomizers = connectorCustomizers;
	}
}
