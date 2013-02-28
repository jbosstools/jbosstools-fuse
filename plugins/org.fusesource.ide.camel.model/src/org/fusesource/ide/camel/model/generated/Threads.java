/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ThreadsDefinition;
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
 * The Node class for Camel's ThreadsDefinition
 */
public class Threads extends AbstractNode {

	public static final String PROPERTY_EXECUTORSERVICEREF = "Threads.ExecutorServiceRef";
	public static final String PROPERTY_THREADNAME = "Threads.ThreadName";
	public static final String PROPERTY_POOLSIZE = "Threads.PoolSize";
	public static final String PROPERTY_MAXPOOLSIZE = "Threads.MaxPoolSize";
	public static final String PROPERTY_KEEPALIVETIME = "Threads.KeepAliveTime";
	public static final String PROPERTY_TIMEUNIT = "Threads.TimeUnit";
	public static final String PROPERTY_MAXQUEUESIZE = "Threads.MaxQueueSize";
	public static final String PROPERTY_REJECTEDPOLICY = "Threads.RejectedPolicy";
	public static final String PROPERTY_CALLERRUNSWHENREJECTED = "Threads.CallerRunsWhenRejected";
	
	private String executorServiceRef;
	private String threadName;
	private Integer poolSize;
	private Integer maxPoolSize;
	private Long keepAliveTime;
	private TimeUnit timeUnit;
	private Integer maxQueueSize;
	private ThreadPoolRejectedPolicy rejectedPolicy;
	private Boolean callerRunsWhenRejected;
	
    public Threads() {
    }		
	
    public Threads(ThreadsDefinition definition, RouteContainer parent) {

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
  		return "threadNode";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Miscellaneous";
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
		if (!isSame(oldValue, executorServiceRef)) {
		    firePropertyChange(PROPERTY_EXECUTORSERVICEREF, oldValue, executorServiceRef);
		}
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return this.threadName;
	}
	
	/**
	 * @param threadName the threadName to set
	 */
	public void setThreadName(String threadName) {
		String oldValue = this.threadName;
		this.threadName = threadName;
		if (!isSame(oldValue, threadName)) {
		    firePropertyChange(PROPERTY_THREADNAME, oldValue, threadName);
		}
	}

	/**
	 * @return the poolSize
	 */
	public Integer getPoolSize() {
		return this.poolSize;
	}
	
	/**
	 * @param poolSize the poolSize to set
	 */
	public void setPoolSize(Integer poolSize) {
		Integer oldValue = this.poolSize;
		this.poolSize = poolSize;
		if (!isSame(oldValue, poolSize)) {
		    firePropertyChange(PROPERTY_POOLSIZE, oldValue, poolSize);
		}
	}

	/**
	 * @return the maxPoolSize
	 */
	public Integer getMaxPoolSize() {
		return this.maxPoolSize;
	}
	
	/**
	 * @param maxPoolSize the maxPoolSize to set
	 */
	public void setMaxPoolSize(Integer maxPoolSize) {
		Integer oldValue = this.maxPoolSize;
		this.maxPoolSize = maxPoolSize;
		if (!isSame(oldValue, maxPoolSize)) {
		    firePropertyChange(PROPERTY_MAXPOOLSIZE, oldValue, maxPoolSize);
		}
	}

	/**
	 * @return the keepAliveTime
	 */
	public Long getKeepAliveTime() {
		return this.keepAliveTime;
	}
	
	/**
	 * @param keepAliveTime the keepAliveTime to set
	 */
	public void setKeepAliveTime(Long keepAliveTime) {
		Long oldValue = this.keepAliveTime;
		this.keepAliveTime = keepAliveTime;
		if (!isSame(oldValue, keepAliveTime)) {
		    firePropertyChange(PROPERTY_KEEPALIVETIME, oldValue, keepAliveTime);
		}
	}

	/**
	 * @return the timeUnit
	 */
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}
	
	/**
	 * @param timeUnit the timeUnit to set
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		TimeUnit oldValue = this.timeUnit;
		this.timeUnit = timeUnit;
		if (!isSame(oldValue, timeUnit)) {
		    firePropertyChange(PROPERTY_TIMEUNIT, oldValue, timeUnit);
		}
	}

	/**
	 * @return the maxQueueSize
	 */
	public Integer getMaxQueueSize() {
		return this.maxQueueSize;
	}
	
	/**
	 * @param maxQueueSize the maxQueueSize to set
	 */
	public void setMaxQueueSize(Integer maxQueueSize) {
		Integer oldValue = this.maxQueueSize;
		this.maxQueueSize = maxQueueSize;
		if (!isSame(oldValue, maxQueueSize)) {
		    firePropertyChange(PROPERTY_MAXQUEUESIZE, oldValue, maxQueueSize);
		}
	}

	/**
	 * @return the rejectedPolicy
	 */
	public ThreadPoolRejectedPolicy getRejectedPolicy() {
		return this.rejectedPolicy;
	}
	
	/**
	 * @param rejectedPolicy the rejectedPolicy to set
	 */
	public void setRejectedPolicy(ThreadPoolRejectedPolicy rejectedPolicy) {
		ThreadPoolRejectedPolicy oldValue = this.rejectedPolicy;
		this.rejectedPolicy = rejectedPolicy;
		if (!isSame(oldValue, rejectedPolicy)) {
		    firePropertyChange(PROPERTY_REJECTEDPOLICY, oldValue, rejectedPolicy);
		}
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
		if (!isSame(oldValue, callerRunsWhenRejected)) {
		    firePropertyChange(PROPERTY_CALLERRUNSWHENREJECTED, oldValue, callerRunsWhenRejected);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_EXECUTORSERVICEREF, Messages.propertyLabelThreadsExecutorServiceRef);
    		PropertyDescriptor descThreadName = new TextPropertyDescriptor(PROPERTY_THREADNAME, Messages.propertyLabelThreadsThreadName);
    		PropertyDescriptor descPoolSize = new TextPropertyDescriptor(PROPERTY_POOLSIZE, Messages.propertyLabelThreadsPoolSize);
    		PropertyDescriptor descMaxPoolSize = new TextPropertyDescriptor(PROPERTY_MAXPOOLSIZE, Messages.propertyLabelThreadsMaxPoolSize);
    		PropertyDescriptor descKeepAliveTime = new TextPropertyDescriptor(PROPERTY_KEEPALIVETIME, Messages.propertyLabelThreadsKeepAliveTime);
      	PropertyDescriptor descTimeUnit = new EnumPropertyDescriptor(PROPERTY_TIMEUNIT, Messages.propertyLabelThreadsTimeUnit, TimeUnit.class);
    		PropertyDescriptor descMaxQueueSize = new TextPropertyDescriptor(PROPERTY_MAXQUEUESIZE, Messages.propertyLabelThreadsMaxQueueSize);
      	PropertyDescriptor descRejectedPolicy = new EnumPropertyDescriptor(PROPERTY_REJECTEDPOLICY, Messages.propertyLabelThreadsRejectedPolicy, ThreadPoolRejectedPolicy.class);
      	PropertyDescriptor descCallerRunsWhenRejected = new BooleanPropertyDescriptor(PROPERTY_CALLERRUNSWHENREJECTED, Messages.propertyLabelThreadsCallerRunsWhenRejected);
  		descriptors.put(PROPERTY_EXECUTORSERVICEREF, descExecutorServiceRef);
		descriptors.put(PROPERTY_THREADNAME, descThreadName);
		descriptors.put(PROPERTY_POOLSIZE, descPoolSize);
		descriptors.put(PROPERTY_MAXPOOLSIZE, descMaxPoolSize);
		descriptors.put(PROPERTY_KEEPALIVETIME, descKeepAliveTime);
		descriptors.put(PROPERTY_TIMEUNIT, descTimeUnit);
		descriptors.put(PROPERTY_MAXQUEUESIZE, descMaxQueueSize);
		descriptors.put(PROPERTY_REJECTEDPOLICY, descRejectedPolicy);
		descriptors.put(PROPERTY_CALLERRUNSWHENREJECTED, descCallerRunsWhenRejected);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
			setExecutorServiceRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_THREADNAME.equals(id)) {
			setThreadName(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_POOLSIZE.equals(id)) {
			setPoolSize(Objects.convertTo(value, Integer.class));
		}		else if (PROPERTY_MAXPOOLSIZE.equals(id)) {
			setMaxPoolSize(Objects.convertTo(value, Integer.class));
		}		else if (PROPERTY_KEEPALIVETIME.equals(id)) {
			setKeepAliveTime(Objects.convertTo(value, Long.class));
		}		else if (PROPERTY_TIMEUNIT.equals(id)) {
			setTimeUnit(Objects.convertTo(value, TimeUnit.class));
		}		else if (PROPERTY_MAXQUEUESIZE.equals(id)) {
			setMaxQueueSize(Objects.convertTo(value, Integer.class));
		}		else if (PROPERTY_REJECTEDPOLICY.equals(id)) {
			setRejectedPolicy(Objects.convertTo(value, ThreadPoolRejectedPolicy.class));
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
		if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
			return this.getExecutorServiceRef();
		}		else if (PROPERTY_THREADNAME.equals(id)) {
			return this.getThreadName();
		}		else if (PROPERTY_POOLSIZE.equals(id)) {
			return this.getPoolSize();
		}		else if (PROPERTY_MAXPOOLSIZE.equals(id)) {
			return this.getMaxPoolSize();
		}		else if (PROPERTY_KEEPALIVETIME.equals(id)) {
			return this.getKeepAliveTime();
		}		else if (PROPERTY_TIMEUNIT.equals(id)) {
			return this.getTimeUnit();
		}		else if (PROPERTY_MAXQUEUESIZE.equals(id)) {
			return this.getMaxQueueSize();
		}		else if (PROPERTY_REJECTEDPOLICY.equals(id)) {
			return this.getRejectedPolicy();
		}		else if (PROPERTY_CALLERRUNSWHENREJECTED.equals(id)) {
			return this.getCallerRunsWhenRejected();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		ThreadsDefinition answer = new ThreadsDefinition();
    answer.setExecutorServiceRef(toXmlPropertyValue(PROPERTY_EXECUTORSERVICEREF, this.getExecutorServiceRef()));
    answer.setThreadName(toXmlPropertyValue(PROPERTY_THREADNAME, this.getThreadName()));
    answer.setPoolSize(toXmlPropertyValue(PROPERTY_POOLSIZE, this.getPoolSize()));
    answer.setMaxPoolSize(toXmlPropertyValue(PROPERTY_MAXPOOLSIZE, this.getMaxPoolSize()));
    answer.setKeepAliveTime(toXmlPropertyValue(PROPERTY_KEEPALIVETIME, this.getKeepAliveTime()));
    answer.setTimeUnit(toXmlPropertyValue(PROPERTY_TIMEUNIT, this.getTimeUnit()));
    answer.setMaxQueueSize(toXmlPropertyValue(PROPERTY_MAXQUEUESIZE, this.getMaxQueueSize()));
    answer.setRejectedPolicy(toXmlPropertyValue(PROPERTY_REJECTEDPOLICY, this.getRejectedPolicy()));
    answer.setCallerRunsWhenRejected(toXmlPropertyValue(PROPERTY_CALLERRUNSWHENREJECTED, this.getCallerRunsWhenRejected()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return ThreadsDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof ThreadsDefinition) {
      ThreadsDefinition node = (ThreadsDefinition) processor;
      this.setExecutorServiceRef(node.getExecutorServiceRef());
      this.setThreadName(node.getThreadName());
      this.setPoolSize(node.getPoolSize());
      this.setMaxPoolSize(node.getMaxPoolSize());
      this.setKeepAliveTime(node.getKeepAliveTime());
      this.setTimeUnit(node.getTimeUnit());
      this.setMaxQueueSize(node.getMaxQueueSize());
      this.setRejectedPolicy(node.getRejectedPolicy());
      this.setCallerRunsWhenRejected(node.getCallerRunsWhenRejected());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof ThreadsDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
