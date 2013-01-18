
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.PolicyDefinition;
import org.apache.camel.model.ProcessorDefinition;
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
 * The Node class for Camel's PolicyDefinition
 */
public class Policy extends AbstractNode {

	public static final String PROPERTY_REF = "Policy.Ref";
	
	private String ref;
	
    public Policy() {
    }		
	
    public Policy(PolicyDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "generic.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "policyNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Miscellaneous";
  	}


	

	/**
	 * @return the ref
	 */
	public String getRef() {
		return this.ref;
	}
	
	/**
	 * @param ref the ref to set
	 */
	public void setRef(String ref) {
		String oldValue = this.ref;
		this.ref = ref;
		if (!isSame(oldValue, ref)) {
		    firePropertyChange(PROPERTY_REF, oldValue, ref);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descRef = new TextPropertyDescriptor(PROPERTY_REF, Messages.propertyLabelPolicyRef);
  		descriptors.put(PROPERTY_REF, descRef);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_REF.equals(id)) {
			setRef(Objects.convertTo(value, String.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_REF.equals(id)) {
			return this.getRef();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		PolicyDefinition answer = new PolicyDefinition();
    answer.setRef(toXmlPropertyValue(PROPERTY_REF, this.getRef()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return PolicyDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof PolicyDefinition) {
      PolicyDefinition node = (PolicyDefinition) processor;
      this.setRef(node.getRef());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof PolicyDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
