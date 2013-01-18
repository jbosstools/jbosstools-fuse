
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ConvertBodyDefinition;
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
 * The Node class for Camel's ConvertBodyDefinition
 */
public class ConvertBody extends AbstractNode {

	public static final String PROPERTY_TYPE = "ConvertBody.Type";
	public static final String PROPERTY_CHARSET = "ConvertBody.Charset";
	
	private String type;
	private String charset;
	
    public ConvertBody() {
    }		
	
    public ConvertBody(ConvertBodyDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "convertBody.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "convertEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Transformation";
  	}


	

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		String oldValue = this.type;
		this.type = type;
		if (!isSame(oldValue, type)) {
		    firePropertyChange(PROPERTY_TYPE, oldValue, type);
		}
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return this.charset;
	}
	
	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		String oldValue = this.charset;
		this.charset = charset;
		if (!isSame(oldValue, charset)) {
		    firePropertyChange(PROPERTY_CHARSET, oldValue, charset);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descType = new TextPropertyDescriptor(PROPERTY_TYPE, Messages.propertyLabelConvertBodyType);
    		PropertyDescriptor descCharset = new TextPropertyDescriptor(PROPERTY_CHARSET, Messages.propertyLabelConvertBodyCharset);
  		descriptors.put(PROPERTY_TYPE, descType);
		descriptors.put(PROPERTY_CHARSET, descCharset);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_TYPE.equals(id)) {
			setType(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_CHARSET.equals(id)) {
			setCharset(Objects.convertTo(value, String.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_TYPE.equals(id)) {
			return this.getType();
		}		else if (PROPERTY_CHARSET.equals(id)) {
			return this.getCharset();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		ConvertBodyDefinition answer = new ConvertBodyDefinition();
    answer.setType(toXmlPropertyValue(PROPERTY_TYPE, this.getType()));
    answer.setCharset(toXmlPropertyValue(PROPERTY_CHARSET, this.getCharset()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return ConvertBodyDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof ConvertBodyDefinition) {
      ConvertBodyDefinition node = (ConvertBodyDefinition) processor;
      this.setType(node.getType());
      this.setCharset(node.getCharset());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof ConvertBodyDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
