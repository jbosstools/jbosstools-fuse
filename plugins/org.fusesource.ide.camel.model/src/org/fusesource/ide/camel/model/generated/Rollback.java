
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RollbackDefinition;
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
 * The Node class for Camel's RollbackDefinition
 */
public class Rollback extends AbstractNode {

	public static final String PROPERTY_MESSAGE = "Rollback.Message";
	public static final String PROPERTY_MARKROLLBACKONLY = "Rollback.MarkRollbackOnly";
	public static final String PROPERTY_MARKROLLBACKONLYLAST = "Rollback.MarkRollbackOnlyLast";
	
	private String message;
	private Boolean markRollbackOnly;
	private Boolean markRollbackOnlyLast;
	
    public Rollback() {
    }		
	
    public Rollback(RollbackDefinition definition, RouteContainer parent) {

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
  		return "rolbackNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Control Flow";
  	}


	

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		String oldValue = this.message;
		this.message = message;
		if (!isSame(oldValue, message)) {
		    firePropertyChange(PROPERTY_MESSAGE, oldValue, message);
		}
	}

	/**
	 * @return the markRollbackOnly
	 */
	public Boolean getMarkRollbackOnly() {
		return this.markRollbackOnly;
	}
	
	/**
	 * @param markRollbackOnly the markRollbackOnly to set
	 */
	public void setMarkRollbackOnly(Boolean markRollbackOnly) {
		Boolean oldValue = this.markRollbackOnly;
		this.markRollbackOnly = markRollbackOnly;
		if (!isSame(oldValue, markRollbackOnly)) {
		    firePropertyChange(PROPERTY_MARKROLLBACKONLY, oldValue, markRollbackOnly);
		}
	}

	/**
	 * @return the markRollbackOnlyLast
	 */
	public Boolean getMarkRollbackOnlyLast() {
		return this.markRollbackOnlyLast;
	}
	
	/**
	 * @param markRollbackOnlyLast the markRollbackOnlyLast to set
	 */
	public void setMarkRollbackOnlyLast(Boolean markRollbackOnlyLast) {
		Boolean oldValue = this.markRollbackOnlyLast;
		this.markRollbackOnlyLast = markRollbackOnlyLast;
		if (!isSame(oldValue, markRollbackOnlyLast)) {
		    firePropertyChange(PROPERTY_MARKROLLBACKONLYLAST, oldValue, markRollbackOnlyLast);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descMessage = new TextPropertyDescriptor(PROPERTY_MESSAGE, Messages.propertyLabelRollbackMessage);
      	PropertyDescriptor descMarkRollbackOnly = new BooleanPropertyDescriptor(PROPERTY_MARKROLLBACKONLY, Messages.propertyLabelRollbackMarkRollbackOnly);
      	PropertyDescriptor descMarkRollbackOnlyLast = new BooleanPropertyDescriptor(PROPERTY_MARKROLLBACKONLYLAST, Messages.propertyLabelRollbackMarkRollbackOnlyLast);
  		descriptors.put(PROPERTY_MESSAGE, descMessage);
		descriptors.put(PROPERTY_MARKROLLBACKONLY, descMarkRollbackOnly);
		descriptors.put(PROPERTY_MARKROLLBACKONLYLAST, descMarkRollbackOnlyLast);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_MESSAGE.equals(id)) {
			setMessage(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_MARKROLLBACKONLY.equals(id)) {
			setMarkRollbackOnly(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_MARKROLLBACKONLYLAST.equals(id)) {
			setMarkRollbackOnlyLast(Objects.convertTo(value, Boolean.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_MESSAGE.equals(id)) {
			return this.getMessage();
		}		else if (PROPERTY_MARKROLLBACKONLY.equals(id)) {
			return this.getMarkRollbackOnly();
		}		else if (PROPERTY_MARKROLLBACKONLYLAST.equals(id)) {
			return this.getMarkRollbackOnlyLast();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		RollbackDefinition answer = new RollbackDefinition();
    answer.setMessage(toXmlPropertyValue(PROPERTY_MESSAGE, this.getMessage()));
    answer.setMarkRollbackOnly(toXmlPropertyValue(PROPERTY_MARKROLLBACKONLY, this.getMarkRollbackOnly()));
    answer.setMarkRollbackOnlyLast(toXmlPropertyValue(PROPERTY_MARKROLLBACKONLYLAST, this.getMarkRollbackOnlyLast()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return RollbackDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof RollbackDefinition) {
      RollbackDefinition node = (RollbackDefinition) processor;
      this.setMessage(node.getMessage());
      this.setMarkRollbackOnly(node.getMarkRollbackOnly());
      this.setMarkRollbackOnlyLast(node.getMarkRollbackOnlyLast());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof RollbackDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
