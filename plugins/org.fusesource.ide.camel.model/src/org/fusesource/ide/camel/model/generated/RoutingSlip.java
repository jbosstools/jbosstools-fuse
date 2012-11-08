
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RoutingSlipDefinition;
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
 * The Node class for Camel's RoutingSlipDefinition
 */
public class RoutingSlip extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "RoutingSlip.Expression";
	public static final String PROPERTY_URIDELIMITER = "RoutingSlip.UriDelimiter";
	public static final String PROPERTY_IGNOREINVALIDENDPOINTS = "RoutingSlip.IgnoreInvalidEndpoints";
	
	private ExpressionDefinition expression;
	private String uriDelimiter;
	private Boolean ignoreInvalidEndpoints;
	
    public RoutingSlip() {
    }		
	
    public RoutingSlip(RoutingSlipDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "routingSlip.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "routingSlipEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Routing";
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
	 * @return the uriDelimiter
	 */
	public String getUriDelimiter() {
		return this.uriDelimiter;
	}
	
	/**
	 * @param uriDelimiter the uriDelimiter to set
	 */
	public void setUriDelimiter(String uriDelimiter) {
		String oldValue = this.uriDelimiter;
		this.uriDelimiter = uriDelimiter;
		firePropertyChange(PROPERTY_URIDELIMITER, oldValue, uriDelimiter);
	}

	/**
	 * @return the ignoreInvalidEndpoints
	 */
	public Boolean getIgnoreInvalidEndpoints() {
		return this.ignoreInvalidEndpoints;
	}
	
	/**
	 * @param ignoreInvalidEndpoints the ignoreInvalidEndpoints to set
	 */
	public void setIgnoreInvalidEndpoints(Boolean ignoreInvalidEndpoints) {
		Boolean oldValue = this.ignoreInvalidEndpoints;
		this.ignoreInvalidEndpoints = ignoreInvalidEndpoints;
		firePropertyChange(PROPERTY_IGNOREINVALIDENDPOINTS, oldValue, ignoreInvalidEndpoints);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelRoutingSlipExpression);
    		PropertyDescriptor descUriDelimiter = new TextPropertyDescriptor(PROPERTY_URIDELIMITER, Messages.propertyLabelRoutingSlipUriDelimiter);
      	PropertyDescriptor descIgnoreInvalidEndpoints = new BooleanPropertyDescriptor(PROPERTY_IGNOREINVALIDENDPOINTS, Messages.propertyLabelRoutingSlipIgnoreInvalidEndpoints);
  		descriptors.put(PROPERTY_EXPRESSION, descExpression);
		descriptors.put(PROPERTY_URIDELIMITER, descUriDelimiter);
		descriptors.put(PROPERTY_IGNOREINVALIDENDPOINTS, descIgnoreInvalidEndpoints);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			setExpression(Objects.convertTo(value, ExpressionDefinition.class));
		}		else if (PROPERTY_URIDELIMITER.equals(id)) {
			setUriDelimiter(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_IGNOREINVALIDENDPOINTS.equals(id)) {
			setIgnoreInvalidEndpoints(Objects.convertTo(value, Boolean.class));
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
		}		else if (PROPERTY_URIDELIMITER.equals(id)) {
			return this.getUriDelimiter();
		}		else if (PROPERTY_IGNOREINVALIDENDPOINTS.equals(id)) {
			return this.getIgnoreInvalidEndpoints();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		RoutingSlipDefinition answer = new RoutingSlipDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setUriDelimiter(toXmlPropertyValue(PROPERTY_URIDELIMITER, this.getUriDelimiter()));
    answer.setIgnoreInvalidEndpoints(toXmlPropertyValue(PROPERTY_IGNOREINVALIDENDPOINTS, this.getIgnoreInvalidEndpoints()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return RoutingSlipDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof RoutingSlipDefinition) {
      RoutingSlipDefinition node = (RoutingSlipDefinition) processor;
      this.setExpression(node.getExpression());
      this.setUriDelimiter(node.getUriDelimiter());
      this.setIgnoreInvalidEndpoints(node.getIgnoreInvalidEndpoints());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof RoutingSlipDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
