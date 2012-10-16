
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.SetOutHeaderDefinition;
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
 * The Node class for Camel's SetOutHeaderDefinition
 */
public class SetOutHeader extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "SetOutHeader.Expression";
	public static final String PROPERTY_HEADERNAME = "SetOutHeader.HeaderName";
	
	private ExpressionDefinition expression;
	private String headerName;
	
    public SetOutHeader() {
    }		
	
    public SetOutHeader(SetOutHeaderDefinition definition, RouteContainer parent) {

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
  		return "setOutHeaderNode";
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
		firePropertyChange(PROPERTY_EXPRESSION, oldValue, expression);
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
		firePropertyChange(PROPERTY_HEADERNAME, oldValue, headerName);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelSetOutHeaderExpression);
    		PropertyDescriptor descHeaderName = new TextPropertyDescriptor(PROPERTY_HEADERNAME, Messages.propertyLabelSetOutHeaderHeaderName);
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
		SetOutHeaderDefinition answer = new SetOutHeaderDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setHeaderName(toXmlPropertyValue(PROPERTY_HEADERNAME, this.getHeaderName()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return SetOutHeaderDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof SetOutHeaderDefinition) {
      SetOutHeaderDefinition node = (SetOutHeaderDefinition) processor;
      this.setExpression(node.getExpression());
      this.setHeaderName(node.getHeaderName());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof SetOutHeaderDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
