
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.BeanDefinition;
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
 * The Node class for Camel's BeanDefinition
 */
public class Bean extends AbstractNode {

	public static final String PROPERTY_REF = "Bean.Ref";
	public static final String PROPERTY_METHOD = "Bean.Method";
	public static final String PROPERTY_BEANTYPE = "Bean.BeanType";
	
	private String ref;
	private String method;
	private String beanType;
	
    public Bean() {
    }		
	
    public Bean(BeanDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "bean.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "beanComp";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Endpoints";
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

	/**
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}
	
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		String oldValue = this.method;
		this.method = method;
		if (!isSame(oldValue, method)) {
		    firePropertyChange(PROPERTY_METHOD, oldValue, method);
		}
	}

	/**
	 * @return the beanType
	 */
	public String getBeanType() {
		return this.beanType;
	}
	
	/**
	 * @param beanType the beanType to set
	 */
	public void setBeanType(String beanType) {
		String oldValue = this.beanType;
		this.beanType = beanType;
		if (!isSame(oldValue, beanType)) {
		    firePropertyChange(PROPERTY_BEANTYPE, oldValue, beanType);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descRef = new TextPropertyDescriptor(PROPERTY_REF, Messages.propertyLabelBeanRef);
    		PropertyDescriptor descMethod = new TextPropertyDescriptor(PROPERTY_METHOD, Messages.propertyLabelBeanMethod);
    		PropertyDescriptor descBeanType = new TextPropertyDescriptor(PROPERTY_BEANTYPE, Messages.propertyLabelBeanBeanType);
  		descriptors.put(PROPERTY_REF, descRef);
		descriptors.put(PROPERTY_METHOD, descMethod);
		descriptors.put(PROPERTY_BEANTYPE, descBeanType);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_REF.equals(id)) {
			setRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_METHOD.equals(id)) {
			setMethod(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_BEANTYPE.equals(id)) {
			setBeanType(Objects.convertTo(value, String.class));
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
		}		else if (PROPERTY_METHOD.equals(id)) {
			return this.getMethod();
		}		else if (PROPERTY_BEANTYPE.equals(id)) {
			return this.getBeanType();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		BeanDefinition answer = new BeanDefinition();
    answer.setRef(toXmlPropertyValue(PROPERTY_REF, this.getRef()));
    answer.setMethod(toXmlPropertyValue(PROPERTY_METHOD, this.getMethod()));
    answer.setBeanType(toXmlPropertyValue(PROPERTY_BEANTYPE, this.getBeanType()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return BeanDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof BeanDefinition) {
      BeanDefinition node = (BeanDefinition) processor;
      this.setRef(node.getRef());
      this.setMethod(node.getMethod());
      this.setBeanType(node.getBeanType());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof BeanDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
