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
import org.apache.camel.model.SetHeaderDefinition;
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
 * The Node class for Camel's SetHeaderDefinition
 */
public class SetHeader extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "SetHeader.Expression";
	public static final String PROPERTY_HEADERNAME = "SetHeader.HeaderName";
	
	private ExpressionDefinition expression;
	private String headerName;
	
    public SetHeader() {
    }		
	
    public SetHeader(SetHeaderDefinition definition, RouteContainer parent) {

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
  		return "setHeaderNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Transformation";
  	}


	

	/**
	 * @return the expression
	 */
	public ExpressionDefinition getExpression() {
		return this.expression;
	}
	
	/**
	 * @param expression the expression to set
	 */
	public void setExpression(ExpressionDefinition expression) {
		ExpressionDefinition oldValue = this.expression;
		this.expression = expression;
		if (!isSame(oldValue, expression)) {
		    firePropertyChange(PROPERTY_EXPRESSION, oldValue, expression);
		}
	}

	/**
	 * @return the headerName
	 */
	public String getHeaderName() {
		return this.headerName;
	}
	
	/**
	 * @param headerName the headerName to set
	 */
	public void setHeaderName(String headerName) {
		String oldValue = this.headerName;
		this.headerName = headerName;
		if (!isSame(oldValue, headerName)) {
		    firePropertyChange(PROPERTY_HEADERNAME, oldValue, headerName);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelSetHeaderExpression);
    		PropertyDescriptor descHeaderName = new TextPropertyDescriptor(PROPERTY_HEADERNAME, Messages.propertyLabelSetHeaderHeaderName);
  		descriptors.put(PROPERTY_EXPRESSION, descExpression);
		descriptors.put(PROPERTY_HEADERNAME, descHeaderName);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			setExpression(Objects.convertTo(value, ExpressionDefinition.class));
		}		else if (PROPERTY_HEADERNAME.equals(id)) {
			setHeaderName(Objects.convertTo(value, String.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			return this.getExpression();
		}		else if (PROPERTY_HEADERNAME.equals(id)) {
			return this.getHeaderName();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		SetHeaderDefinition answer = new SetHeaderDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setHeaderName(toXmlPropertyValue(PROPERTY_HEADERNAME, this.getHeaderName()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return SetHeaderDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof SetHeaderDefinition) {
      SetHeaderDefinition node = (SetHeaderDefinition) processor;
      this.setExpression(node.getExpression());
      this.setHeaderName(node.getHeaderName());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof SetHeaderDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
