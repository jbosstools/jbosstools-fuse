
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.PollEnrichDefinition;
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
 * The Node class for Camel's PollEnrichDefinition
 */
public class PollEnrich extends AbstractNode {

	public static final String PROPERTY_RESOURCEURI = "PollEnrich.ResourceUri";
	public static final String PROPERTY_AGGREGATIONSTRATEGYREF = "PollEnrich.AggregationStrategyRef";
	public static final String PROPERTY_TIMEOUT = "PollEnrich.Timeout";
	public static final String PROPERTY_POLLMULTIPLE = "PollEnrich.PollMultiple";
	
	private String resourceUri;
	private String aggregationStrategyRef;
	private Long timeout;
	private Boolean pollMultiple;
	
    public PollEnrich() {
    }		
	
    public PollEnrich(PollEnrichDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "pollEnrich.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "pollEnrichEIP";
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

	/**
	 * @return the timeout
	 */
	public Long getTimeout() {
		return this.timeout;
	}
	
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(Long timeout) {
		Long oldValue = this.timeout;
		this.timeout = timeout;
		firePropertyChange(PROPERTY_TIMEOUT, oldValue, timeout);
	}

	/**
	 * @return the pollMultiple
	 */
	public Boolean getPollMultiple() {
		return this.pollMultiple;
	}
	
	/**
	 * @param pollMultiple the pollMultiple to set
	 */
	public void setPollMultiple(Boolean pollMultiple) {
		Boolean oldValue = this.pollMultiple;
		this.pollMultiple = pollMultiple;
		firePropertyChange(PROPERTY_POLLMULTIPLE, oldValue, pollMultiple);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descResourceUri = new TextPropertyDescriptor(PROPERTY_RESOURCEURI, Messages.propertyLabelPollEnrichResourceUri);
    		PropertyDescriptor descAggregationStrategyRef = new TextPropertyDescriptor(PROPERTY_AGGREGATIONSTRATEGYREF, Messages.propertyLabelPollEnrichAggregationStrategyRef);
    		PropertyDescriptor descTimeout = new TextPropertyDescriptor(PROPERTY_TIMEOUT, Messages.propertyLabelPollEnrichTimeout);
      	PropertyDescriptor descPollMultiple = new BooleanPropertyDescriptor(PROPERTY_POLLMULTIPLE, Messages.propertyLabelPollEnrichPollMultiple);
  		descriptors.put(PROPERTY_RESOURCEURI, descResourceUri);
		descriptors.put(PROPERTY_AGGREGATIONSTRATEGYREF, descAggregationStrategyRef);
		descriptors.put(PROPERTY_TIMEOUT, descTimeout);
		descriptors.put(PROPERTY_POLLMULTIPLE, descPollMultiple);
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
		}		else if (PROPERTY_TIMEOUT.equals(id)) {
			setTimeout(Objects.convertTo(value, Long.class));
		}		else if (PROPERTY_POLLMULTIPLE.equals(id)) {
			setPollMultiple(Objects.convertTo(value, Boolean.class));
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
		}		else if (PROPERTY_TIMEOUT.equals(id)) {
			return this.getTimeout();
		}		else if (PROPERTY_POLLMULTIPLE.equals(id)) {
			return Objects.<Boolean>getField(this, "pollMultiple");
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		PollEnrichDefinition answer = new PollEnrichDefinition();
    answer.setResourceUri(toXmlPropertyValue(PROPERTY_RESOURCEURI, this.getResourceUri()));
    answer.setAggregationStrategyRef(toXmlPropertyValue(PROPERTY_AGGREGATIONSTRATEGYREF, this.getAggregationStrategyRef()));
    answer.setTimeout(toXmlPropertyValue(PROPERTY_TIMEOUT, this.getTimeout()));
    answer.setPollMultiple(toXmlPropertyValue(PROPERTY_POLLMULTIPLE, Objects.<Boolean>getField(this, "pollMultiple")));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return PollEnrichDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof PollEnrichDefinition) {
      PollEnrichDefinition node = (PollEnrichDefinition) processor;
      this.setResourceUri(node.getResourceUri());
      this.setAggregationStrategyRef(node.getAggregationStrategyRef());
      this.setTimeout(node.getTimeout());
      this.setPollMultiple(Objects.<Boolean>getField(node, "pollMultiple"));
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof PollEnrichDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
