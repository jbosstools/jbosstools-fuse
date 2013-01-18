
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.List;
import java.util.Map;
import org.apache.camel.model.CatchDefinition;
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
 * The Node class for Camel's CatchDefinition
 */
public class Catch extends AbstractNode {

	public static final String PROPERTY_EXCEPTIONS = "Catch.Exceptions";
	public static final String PROPERTY_HANDLED = "Catch.Handled";
	
	private List exceptions;
	private ExpressionDefinition handled;
	
    public Catch() {
    }		
	
    public Catch(CatchDefinition definition, RouteContainer parent) {

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
  		return "catchEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Control Flow";
  	}


	

	/**
	 * @return the exceptions
	 */
	public List getExceptions() {
		return this.exceptions;
	}
	
	/**
	 * @param exceptions the exceptions to set
	 */
	public void setExceptions(List exceptions) {
		List oldValue = this.exceptions;
		this.exceptions = exceptions;
		if (!isSame(oldValue, exceptions)) {
		    firePropertyChange(PROPERTY_EXCEPTIONS, oldValue, exceptions);
		}
	}

	/**
	 * @return the handled
	 */
	public ExpressionDefinition getHandled() {
		return this.handled;
	}
	
	/**
	 * @param handled the handled to set
	 */
	public void setHandled(ExpressionDefinition handled) {
		ExpressionDefinition oldValue = this.handled;
		this.handled = handled;
		if (!isSame(oldValue, handled)) {
		    firePropertyChange(PROPERTY_HANDLED, oldValue, handled);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
    	PropertyDescriptor descExceptions = new ListPropertyDescriptor(PROPERTY_EXCEPTIONS, Messages.propertyLabelCatchExceptions);
    
  	PropertyDescriptor descHandled = new ExpressionPropertyDescriptor(PROPERTY_HANDLED, Messages.propertyLabelCatchHandled);
  		descriptors.put(PROPERTY_EXCEPTIONS, descExceptions);
		descriptors.put(PROPERTY_HANDLED, descHandled);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXCEPTIONS.equals(id)) {
			setExceptions(Objects.convertTo(value, List.class));
		}		else if (PROPERTY_HANDLED.equals(id)) {
			setHandled(Objects.convertTo(value, ExpressionDefinition.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_EXCEPTIONS.equals(id)) {
			return this.getExceptions();
		}		else if (PROPERTY_HANDLED.equals(id)) {
			return this.getHandled();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		CatchDefinition answer = new CatchDefinition();
    answer.setExceptions(toXmlPropertyValue(PROPERTY_EXCEPTIONS, this.getExceptions()));
    Objects.setField(answer, "handled", toXmlPropertyValue(PROPERTY_HANDLED, this.getHandled()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return CatchDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof CatchDefinition) {
      CatchDefinition node = (CatchDefinition) processor;
      this.setExceptions(node.getExceptions());
      Objects.setField(this, "handled", node.getHandled());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof CatchDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
