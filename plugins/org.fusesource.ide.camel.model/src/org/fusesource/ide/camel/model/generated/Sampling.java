
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.SamplingDefinition;
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
 * The Node class for Camel's SamplingDefinition
 */
public class Sampling extends AbstractNode {

	public static final String PROPERTY_SAMPLEPERIOD = "Sampling.SamplePeriod";
	public static final String PROPERTY_MESSAGEFREQUENCY = "Sampling.MessageFrequency";
	public static final String PROPERTY_UNITS = "Sampling.Units";
	
	private Long samplePeriod;
	private Long messageFrequency;
	private TimeUnit units;
	
    public Sampling() {
    }		
	
    public Sampling(SamplingDefinition definition, RouteContainer parent) {

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
  		return "samplingNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Miscellaneous";
  	}


	

	/**
	 * @return the samplePeriod
	 */
	public Long getSamplePeriod() {
		return this.samplePeriod;
	}
	
	/**
	 * @param samplePeriod the samplePeriod to set
	 */
	public void setSamplePeriod(Long samplePeriod) {
		Long oldValue = this.samplePeriod;
		this.samplePeriod = samplePeriod;
		firePropertyChange(PROPERTY_SAMPLEPERIOD, oldValue, samplePeriod);
	}

	/**
	 * @return the messageFrequency
	 */
	public Long getMessageFrequency() {
		return this.messageFrequency;
	}
	
	/**
	 * @param messageFrequency the messageFrequency to set
	 */
	public void setMessageFrequency(Long messageFrequency) {
		Long oldValue = this.messageFrequency;
		this.messageFrequency = messageFrequency;
		firePropertyChange(PROPERTY_MESSAGEFREQUENCY, oldValue, messageFrequency);
	}

	/**
	 * @return the units
	 */
	public TimeUnit getUnits() {
		return this.units;
	}
	
	/**
	 * @param units the units to set
	 */
	public void setUnits(TimeUnit units) {
		TimeUnit oldValue = this.units;
		this.units = units;
		firePropertyChange(PROPERTY_UNITS, oldValue, units);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descSamplePeriod = new TextPropertyDescriptor(PROPERTY_SAMPLEPERIOD, Messages.propertyLabelSamplingSamplePeriod);
    		PropertyDescriptor descMessageFrequency = new TextPropertyDescriptor(PROPERTY_MESSAGEFREQUENCY, Messages.propertyLabelSamplingMessageFrequency);
      	PropertyDescriptor descUnits = new EnumPropertyDescriptor(PROPERTY_UNITS, Messages.propertyLabelSamplingUnits, TimeUnit.class);
  		descriptors.put(PROPERTY_SAMPLEPERIOD, descSamplePeriod);
		descriptors.put(PROPERTY_MESSAGEFREQUENCY, descMessageFrequency);
		descriptors.put(PROPERTY_UNITS, descUnits);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_SAMPLEPERIOD.equals(id)) {
			setSamplePeriod(Objects.convertTo(value, Long.class));
		}		else if (PROPERTY_MESSAGEFREQUENCY.equals(id)) {
			setMessageFrequency(Objects.convertTo(value, Long.class));
		}		else if (PROPERTY_UNITS.equals(id)) {
			setUnits(Objects.convertTo(value, TimeUnit.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_SAMPLEPERIOD.equals(id)) {
			return this.getSamplePeriod();
		}		else if (PROPERTY_MESSAGEFREQUENCY.equals(id)) {
			return this.getMessageFrequency();
		}		else if (PROPERTY_UNITS.equals(id)) {
			return this.getUnits();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		SamplingDefinition answer = new SamplingDefinition();
    answer.setSamplePeriod(toXmlPropertyValue(PROPERTY_SAMPLEPERIOD, this.getSamplePeriod()));
    answer.setMessageFrequency(toXmlPropertyValue(PROPERTY_MESSAGEFREQUENCY, this.getMessageFrequency()));
    answer.setUnits(toXmlPropertyValue(PROPERTY_UNITS, this.getUnits()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return SamplingDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof SamplingDefinition) {
      SamplingDefinition node = (SamplingDefinition) processor;
      this.setSamplePeriod(node.getSamplePeriod());
      this.setMessageFrequency(node.getMessageFrequency());
      this.setUnits(node.getUnits());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof SamplingDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
