/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RemoveHeadersDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.ExpressionPropertyDescriptor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexUnionPropertyDescriptor;
import org.fusesource.ide.commons.properties.EnumPropertyDescriptor;
import org.fusesource.ide.commons.properties.ListPropertyDescriptor;
import org.fusesource.ide.commons.properties.UnionTypeValue;

/**
 * The Node class for Camel's RemoveHeadersDefinition
 */
public class RemoveHeaders extends AbstractNode {

	public static final String PROPERTY_PATTERN = "RemoveHeaders.Pattern";
	public static final String PROPERTY_EXCLUDEPATTERN = "RemoveHeaders.ExcludePattern";
	
	private String pattern;
	private String excludePattern;
	
    public RemoveHeaders() {
    }		
	
    public RemoveHeaders(RemoveHeadersDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "transform.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "removeHeadersNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Transformation";
  	}


	

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		String oldValue = this.pattern;
		this.pattern = pattern;
		if (!isSame(oldValue, pattern)) {
		    firePropertyChange(PROPERTY_PATTERN, oldValue, pattern);
		}
	}

	/**
	 * @return the excludePattern
	 */
	public String getExcludePattern() {
		return this.excludePattern;
	}
	
	/**
	 * @param excludePattern the excludePattern to set
	 */
	public void setExcludePattern(String excludePattern) {
		String oldValue = this.excludePattern;
		this.excludePattern = excludePattern;
		if (!isSame(oldValue, excludePattern)) {
		    firePropertyChange(PROPERTY_EXCLUDEPATTERN, oldValue, excludePattern);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descPattern = new TextPropertyDescriptor(PROPERTY_PATTERN, Messages.propertyLabelRemoveHeadersPattern);
    		PropertyDescriptor descExcludePattern = new TextPropertyDescriptor(PROPERTY_EXCLUDEPATTERN, Messages.propertyLabelRemoveHeadersExcludePattern);
  		descriptors.put(PROPERTY_PATTERN, descPattern);
		descriptors.put(PROPERTY_EXCLUDEPATTERN, descExcludePattern);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_PATTERN.equals(id)) {
			setPattern(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_EXCLUDEPATTERN.equals(id)) {
			setExcludePattern(Objects.convertTo(value, String.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_PATTERN.equals(id)) {
			return this.getPattern();
		}		else if (PROPERTY_EXCLUDEPATTERN.equals(id)) {
			return this.getExcludePattern();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		RemoveHeadersDefinition answer = new RemoveHeadersDefinition();
    answer.setPattern(toXmlPropertyValue(PROPERTY_PATTERN, this.getPattern()));
    answer.setExcludePattern(toXmlPropertyValue(PROPERTY_EXCLUDEPATTERN, this.getExcludePattern()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return RemoveHeadersDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof RemoveHeadersDefinition) {
      RemoveHeadersDefinition node = (RemoveHeadersDefinition) processor;
      this.setPattern(node.getPattern());
      this.setExcludePattern(node.getExcludePattern());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof RemoveHeadersDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
