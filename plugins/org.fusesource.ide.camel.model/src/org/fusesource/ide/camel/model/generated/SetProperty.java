
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.SetPropertyDefinition;
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
 * The Node class for Camel's SetPropertyDefinition
 */
public class SetProperty extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "SetProperty.Expression";
	public static final String PROPERTY_PROPERTYNAME = "SetProperty.PropertyName";
	
	private ExpressionDefinition expression;
	private String propertyName;
	
    public SetProperty() {
    }		
	
    public SetProperty(SetPropertyDefinition definition, RouteContainer parent) {

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
  		return "setPropertyNode";
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
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}
	
	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		String oldValue = this.propertyName;
		this.propertyName = propertyName;
		if (!isSame(oldValue, propertyName)) {
		    firePropertyChange(PROPERTY_PROPERTYNAME, oldValue, propertyName);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelSetPropertyExpression);
    		PropertyDescriptor descPropertyName = new TextPropertyDescriptor(PROPERTY_PROPERTYNAME, Messages.propertyLabelSetPropertyPropertyName);
  		descriptors.put(PROPERTY_EXPRESSION, descExpression);
		descriptors.put(PROPERTY_PROPERTYNAME, descPropertyName);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			setExpression(Objects.convertTo(value, ExpressionDefinition.class));
		}		else if (PROPERTY_PROPERTYNAME.equals(id)) {
			setPropertyName(Objects.convertTo(value, String.class));
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
		}		else if (PROPERTY_PROPERTYNAME.equals(id)) {
			return this.getPropertyName();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		SetPropertyDefinition answer = new SetPropertyDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setPropertyName(toXmlPropertyValue(PROPERTY_PROPERTYNAME, this.getPropertyName()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return SetPropertyDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof SetPropertyDefinition) {
      SetPropertyDefinition node = (SetPropertyDefinition) processor;
      this.setExpression(node.getExpression());
      this.setPropertyName(node.getPropertyName());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof SetPropertyDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
