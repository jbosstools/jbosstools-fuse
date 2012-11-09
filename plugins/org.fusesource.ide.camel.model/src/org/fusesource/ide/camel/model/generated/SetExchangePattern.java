
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.ExchangePattern;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.SetExchangePatternDefinition;
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
 * The Node class for Camel's SetExchangePatternDefinition
 */
public class SetExchangePattern extends AbstractNode {

	public static final String PROPERTY_PATTERN = "SetExchangePattern.Pattern";
	
	private ExchangePattern pattern;
	
    public SetExchangePattern() {
    }		
	
    public SetExchangePattern(SetExchangePatternDefinition definition, RouteContainer parent) {

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
  		return "setExchangePatternNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Transformation";
  	}


	

	/**
	 * @return the pattern
	 */
	public ExchangePattern getPattern() {
		return this.pattern;
	}
	
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(ExchangePattern pattern) {
		ExchangePattern oldValue = this.pattern;
		this.pattern = pattern;
		firePropertyChange(PROPERTY_PATTERN, oldValue, pattern);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
    	PropertyDescriptor descPattern = new EnumPropertyDescriptor(PROPERTY_PATTERN, Messages.propertyLabelSetExchangePatternPattern, ExchangePattern.class);
  		descriptors.put(PROPERTY_PATTERN, descPattern);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_PATTERN.equals(id)) {
			setPattern(Objects.convertTo(value, ExchangePattern.class));
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
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		SetExchangePatternDefinition answer = new SetExchangePatternDefinition();
    answer.setPattern(toXmlPropertyValue(PROPERTY_PATTERN, this.getPattern()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return SetExchangePatternDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof SetExchangePatternDefinition) {
      SetExchangePatternDefinition node = (SetExchangePatternDefinition) processor;
      this.setPattern(node.getPattern());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof SetExchangePatternDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
