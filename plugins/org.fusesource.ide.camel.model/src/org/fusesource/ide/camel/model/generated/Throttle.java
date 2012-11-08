
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ThrottleDefinition;
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
 * The Node class for Camel's ThrottleDefinition
 */
public class Throttle extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "Throttle.Expression";
	public static final String PROPERTY_EXECUTORSERVICEREF = "Throttle.ExecutorServiceRef";
	public static final String PROPERTY_TIMEPERIODMILLIS = "Throttle.TimePeriodMillis";
	public static final String PROPERTY_ASYNCDELAYED = "Throttle.AsyncDelayed";
	public static final String PROPERTY_CALLERRUNSWHENREJECTED = "Throttle.CallerRunsWhenRejected";
	
	private ExpressionDefinition expression;
	private String executorServiceRef;
	private Long timePeriodMillis;
	private Boolean asyncDelayed;
	private Boolean callerRunsWhenRejected;
	
    public Throttle() {
    }		
	
    public Throttle(ThrottleDefinition definition, RouteContainer parent) {

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
  		return "throttleNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Control Flow";
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
	 * @return the executorServiceRef
	 */
	public String getExecutorServiceRef() {
		return this.executorServiceRef;
	}
	
	/**
	 * @param executorServiceRef the executorServiceRef to set
	 */
	public void setExecutorServiceRef(String executorServiceRef) {
		String oldValue = this.executorServiceRef;
		this.executorServiceRef = executorServiceRef;
		firePropertyChange(PROPERTY_EXECUTORSERVICEREF, oldValue, executorServiceRef);
	}

	/**
	 * @return the timePeriodMillis
	 */
	public Long getTimePeriodMillis() {
		return this.timePeriodMillis;
	}
	
	/**
	 * @param timePeriodMillis the timePeriodMillis to set
	 */
	public void setTimePeriodMillis(Long timePeriodMillis) {
		Long oldValue = this.timePeriodMillis;
		this.timePeriodMillis = timePeriodMillis;
		firePropertyChange(PROPERTY_TIMEPERIODMILLIS, oldValue, timePeriodMillis);
	}

	/**
	 * @return the asyncDelayed
	 */
	public Boolean getAsyncDelayed() {
		return this.asyncDelayed;
	}
	
	/**
	 * @param asyncDelayed the asyncDelayed to set
	 */
	public void setAsyncDelayed(Boolean asyncDelayed) {
		Boolean oldValue = this.asyncDelayed;
		this.asyncDelayed = asyncDelayed;
		firePropertyChange(PROPERTY_ASYNCDELAYED, oldValue, asyncDelayed);
	}

	/**
	 * @return the callerRunsWhenRejected
	 */
	public Boolean getCallerRunsWhenRejected() {
		return this.callerRunsWhenRejected;
	}
	
	/**
	 * @param callerRunsWhenRejected the callerRunsWhenRejected to set
	 */
	public void setCallerRunsWhenRejected(Boolean callerRunsWhenRejected) {
		Boolean oldValue = this.callerRunsWhenRejected;
		this.callerRunsWhenRejected = callerRunsWhenRejected;
		firePropertyChange(PROPERTY_CALLERRUNSWHENREJECTED, oldValue, callerRunsWhenRejected);
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelThrottleExpression);
    		PropertyDescriptor descExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_EXECUTORSERVICEREF, Messages.propertyLabelThrottleExecutorServiceRef);
    		PropertyDescriptor descTimePeriodMillis = new TextPropertyDescriptor(PROPERTY_TIMEPERIODMILLIS, Messages.propertyLabelThrottleTimePeriodMillis);
      	PropertyDescriptor descAsyncDelayed = new BooleanPropertyDescriptor(PROPERTY_ASYNCDELAYED, Messages.propertyLabelThrottleAsyncDelayed);
      	PropertyDescriptor descCallerRunsWhenRejected = new BooleanPropertyDescriptor(PROPERTY_CALLERRUNSWHENREJECTED, Messages.propertyLabelThrottleCallerRunsWhenRejected);
  		descriptors.put(PROPERTY_EXPRESSION, descExpression);
		descriptors.put(PROPERTY_EXECUTORSERVICEREF, descExecutorServiceRef);
		descriptors.put(PROPERTY_TIMEPERIODMILLIS, descTimePeriodMillis);
		descriptors.put(PROPERTY_ASYNCDELAYED, descAsyncDelayed);
		descriptors.put(PROPERTY_CALLERRUNSWHENREJECTED, descCallerRunsWhenRejected);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			setExpression(Objects.convertTo(value, ExpressionDefinition.class));
		}		else if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
			setExecutorServiceRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_TIMEPERIODMILLIS.equals(id)) {
			setTimePeriodMillis(Objects.convertTo(value, Long.class));
		}		else if (PROPERTY_ASYNCDELAYED.equals(id)) {
			setAsyncDelayed(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_CALLERRUNSWHENREJECTED.equals(id)) {
			setCallerRunsWhenRejected(Objects.convertTo(value, Boolean.class));
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
		}		else if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
			return this.getExecutorServiceRef();
		}		else if (PROPERTY_TIMEPERIODMILLIS.equals(id)) {
			return this.getTimePeriodMillis();
		}		else if (PROPERTY_ASYNCDELAYED.equals(id)) {
			return this.getAsyncDelayed();
		}		else if (PROPERTY_CALLERRUNSWHENREJECTED.equals(id)) {
			return this.getCallerRunsWhenRejected();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		ThrottleDefinition answer = new ThrottleDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setExecutorServiceRef(toXmlPropertyValue(PROPERTY_EXECUTORSERVICEREF, this.getExecutorServiceRef()));
    answer.setTimePeriodMillis(toXmlPropertyValue(PROPERTY_TIMEPERIODMILLIS, this.getTimePeriodMillis()));
    answer.setAsyncDelayed(toXmlPropertyValue(PROPERTY_ASYNCDELAYED, this.getAsyncDelayed()));
    answer.setCallerRunsWhenRejected(toXmlPropertyValue(PROPERTY_CALLERRUNSWHENREJECTED, this.getCallerRunsWhenRejected()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return ThrottleDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof ThrottleDefinition) {
      ThrottleDefinition node = (ThrottleDefinition) processor;
      this.setExpression(node.getExpression());
      this.setExecutorServiceRef(node.getExecutorServiceRef());
      this.setTimePeriodMillis(node.getTimePeriodMillis());
      this.setAsyncDelayed(node.getAsyncDelayed());
      this.setCallerRunsWhenRejected(node.getCallerRunsWhenRejected());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof ThrottleDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
