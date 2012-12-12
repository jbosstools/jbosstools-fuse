
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.EnrichDefinition;
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
 * The Node class for Camel's EnrichDefinition
 */
public class Enrich extends AbstractNode {

	public static final String PROPERTY_RESOURCEURI = "Enrich.ResourceUri";
	public static final String PROPERTY_AGGREGATIONSTRATEGYREF = "Enrich.AggregationStrategyRef";
	
	private String resourceUri;
	private String aggregationStrategyRef;
	
    public Enrich() {
    }		
	
    public Enrich(EnrichDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "enrich.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "enrichEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Transformation";
  	}


	

	/**
	 * @return the resourceUri
	 */
	public String getResourceUri() {
		return this.resourceUri;
	}
	
	/**
	 * @param resourceUri the resourceUri to set
	 */
	public void setResourceUri(String resourceUri) {
		String oldValue = this.resourceUri;
		this.resourceUri = resourceUri;
		firePropertyChange(PROPERTY_RESOURCEURI, oldValue, resourceUri);
	}

	/**
	 * @return the aggregationStrategyRef
	 */
	public String getAggregationStrategyRef() {
		return this.aggregationStrategyRef;
	}
	
	/**
	 * @param aggregationStrategyRef the aggregationStrategyRef to set
	 */
	public void setAggregationStrategyRef(String aggregationStrategyRef) {
		String oldValue = this.aggregationStrategyRef;
		this.aggregationStrategyRef = aggregationStrategyRef;
		firePropertyChange(PROPERTY_AGGREGATIONSTRATEGYREF, oldValue, aggregationStrategyRef);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descResourceUri = new TextPropertyDescriptor(PROPERTY_RESOURCEURI, Messages.propertyLabelEnrichResourceUri);
    		PropertyDescriptor descAggregationStrategyRef = new TextPropertyDescriptor(PROPERTY_AGGREGATIONSTRATEGYREF, Messages.propertyLabelEnrichAggregationStrategyRef);
  		descriptors.put(PROPERTY_RESOURCEURI, descResourceUri);
		descriptors.put(PROPERTY_AGGREGATIONSTRATEGYREF, descAggregationStrategyRef);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_RESOURCEURI.equals(id)) {
			setResourceUri(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_AGGREGATIONSTRATEGYREF.equals(id)) {
			setAggregationStrategyRef(Objects.convertTo(value, String.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_RESOURCEURI.equals(id)) {
			return this.getResourceUri();
		}		else if (PROPERTY_AGGREGATIONSTRATEGYREF.equals(id)) {
			return this.getAggregationStrategyRef();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		EnrichDefinition answer = new EnrichDefinition();
    answer.setResourceUri(toXmlPropertyValue(PROPERTY_RESOURCEURI, this.getResourceUri()));
    answer.setAggregationStrategyRef(toXmlPropertyValue(PROPERTY_AGGREGATIONSTRATEGYREF, this.getAggregationStrategyRef()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return EnrichDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof EnrichDefinition) {
      EnrichDefinition node = (EnrichDefinition) processor;
      this.setResourceUri(node.getResourceUri());
      this.setAggregationStrategyRef(node.getAggregationStrategyRef());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof EnrichDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
