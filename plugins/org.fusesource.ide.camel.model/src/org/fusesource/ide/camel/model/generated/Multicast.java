
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
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

package org.fusesource.ide.camel.model.generated;

import java.util.Map;
import org.apache.camel.model.MulticastDefinition;
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
 * The Node class for Camel's MulticastDefinition
 */
public class Multicast extends AbstractNode {

	public static final String PROPERTY_STRATEGYREF = "Multicast.StrategyRef";
	public static final String PROPERTY_EXECUTORSERVICEREF = "Multicast.ExecutorServiceRef";
	public static final String PROPERTY_ONPREPAREREF = "Multicast.OnPrepareRef";
	public static final String PROPERTY_PARALLELPROCESSING = "Multicast.ParallelProcessing";
	public static final String PROPERTY_STREAMING = "Multicast.Streaming";
	public static final String PROPERTY_STOPONEXCEPTION = "Multicast.StopOnException";
	public static final String PROPERTY_TIMEOUT = "Multicast.Timeout";
	public static final String PROPERTY_SHAREUNITOFWORK = "Multicast.ShareUnitOfWork";
	
	private String strategyRef;
	private String executorServiceRef;
	private String onPrepareRef;
	private Boolean parallelProcessing;
	private Boolean streaming;
	private Boolean stopOnException;
	private Long timeout;
	private Boolean shareUnitOfWork;
	
    public Multicast() {
    }		
	
    public Multicast(MulticastDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "multicast.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "multicastEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Routing";
  	}


	

	/**
	 * @return the strategyRef
	 */
	public String getStrategyRef() {
		return this.strategyRef;
	}
	
	/**
	 * @param strategyRef the strategyRef to set
	 */
	public void setStrategyRef(String strategyRef) {
		String oldValue = this.strategyRef;
		this.strategyRef = strategyRef;
		if (!isSame(oldValue, strategyRef)) {
		    firePropertyChange(PROPERTY_STRATEGYREF, oldValue, strategyRef);
		}
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
	 * @return the onPrepareRef
	 */
	public String getOnPrepareRef() {
		return this.onPrepareRef;
	}
	
	/**
	 * @param onPrepareRef the onPrepareRef to set
	 */
	public void setOnPrepareRef(String onPrepareRef) {
		String oldValue = this.onPrepareRef;
		this.onPrepareRef = onPrepareRef;
		if (!isSame(oldValue, onPrepareRef)) {
		    firePropertyChange(PROPERTY_ONPREPAREREF, oldValue, onPrepareRef);
		}
	}

	/**
	 * @return the parallelProcessing
	 */
	public Boolean getParallelProcessing() {
		return this.parallelProcessing;
	}
	
	/**
	 * @param parallelProcessing the parallelProcessing to set
	 */
	public void setParallelProcessing(Boolean parallelProcessing) {
		Boolean oldValue = this.parallelProcessing;
		this.parallelProcessing = parallelProcessing;
		if (!isSame(oldValue, parallelProcessing)) {
		    firePropertyChange(PROPERTY_PARALLELPROCESSING, oldValue, parallelProcessing);
		}
	}

	/**
	 * @return the streaming
	 */
	public Boolean getStreaming() {
		return this.streaming;
	}
	
	/**
	 * @param streaming the streaming to set
	 */
	public void setStreaming(Boolean streaming) {
		Boolean oldValue = this.streaming;
		this.streaming = streaming;
		if (!isSame(oldValue, streaming)) {
		    firePropertyChange(PROPERTY_STREAMING, oldValue, streaming);
		}
	}

	/**
	 * @return the stopOnException
	 */
	public Boolean getStopOnException() {
		return this.stopOnException;
	}
	
	/**
	 * @param stopOnException the stopOnException to set
	 */
	public void setStopOnException(Boolean stopOnException) {
		Boolean oldValue = this.stopOnException;
		this.stopOnException = stopOnException;
		if (!isSame(oldValue, stopOnException)) {
		    firePropertyChange(PROPERTY_STOPONEXCEPTION, oldValue, stopOnException);
		}
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
		if (!isSame(oldValue, timeout)) {
		    firePropertyChange(PROPERTY_TIMEOUT, oldValue, timeout);
		}
	}

	/**
	 * @return the shareUnitOfWork
	 */
	public Boolean getShareUnitOfWork() {
		return this.shareUnitOfWork;
	}
	
	/**
	 * @param shareUnitOfWork the shareUnitOfWork to set
	 */
	public void setShareUnitOfWork(Boolean shareUnitOfWork) {
		Boolean oldValue = this.shareUnitOfWork;
		this.shareUnitOfWork = shareUnitOfWork;
		if (!isSame(oldValue, shareUnitOfWork)) {
		    firePropertyChange(PROPERTY_SHAREUNITOFWORK, oldValue, shareUnitOfWork);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descStrategyRef = new TextPropertyDescriptor(PROPERTY_STRATEGYREF, Messages.propertyLabelMulticastStrategyRef);
    		PropertyDescriptor descExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_EXECUTORSERVICEREF, Messages.propertyLabelMulticastExecutorServiceRef);
    		PropertyDescriptor descOnPrepareRef = new TextPropertyDescriptor(PROPERTY_ONPREPAREREF, Messages.propertyLabelMulticastOnPrepareRef);
      	PropertyDescriptor descParallelProcessing = new BooleanPropertyDescriptor(PROPERTY_PARALLELPROCESSING, Messages.propertyLabelMulticastParallelProcessing);
      	PropertyDescriptor descStreaming = new BooleanPropertyDescriptor(PROPERTY_STREAMING, Messages.propertyLabelMulticastStreaming);
      	PropertyDescriptor descStopOnException = new BooleanPropertyDescriptor(PROPERTY_STOPONEXCEPTION, Messages.propertyLabelMulticastStopOnException);
    		PropertyDescriptor descTimeout = new TextPropertyDescriptor(PROPERTY_TIMEOUT, Messages.propertyLabelMulticastTimeout);
      	PropertyDescriptor descShareUnitOfWork = new BooleanPropertyDescriptor(PROPERTY_SHAREUNITOFWORK, Messages.propertyLabelMulticastShareUnitOfWork);
  		descriptors.put(PROPERTY_STRATEGYREF, descStrategyRef);
		descriptors.put(PROPERTY_EXECUTORSERVICEREF, descExecutorServiceRef);
		descriptors.put(PROPERTY_ONPREPAREREF, descOnPrepareRef);
		descriptors.put(PROPERTY_PARALLELPROCESSING, descParallelProcessing);
		descriptors.put(PROPERTY_STREAMING, descStreaming);
		descriptors.put(PROPERTY_STOPONEXCEPTION, descStopOnException);
		descriptors.put(PROPERTY_TIMEOUT, descTimeout);
		descriptors.put(PROPERTY_SHAREUNITOFWORK, descShareUnitOfWork);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_STRATEGYREF.equals(id)) {
			setStrategyRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
			setExecutorServiceRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_ONPREPAREREF.equals(id)) {
			setOnPrepareRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_PARALLELPROCESSING.equals(id)) {
			setParallelProcessing(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_STREAMING.equals(id)) {
			setStreaming(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_STOPONEXCEPTION.equals(id)) {
			setStopOnException(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_TIMEOUT.equals(id)) {
			setTimeout(Objects.convertTo(value, Long.class));
		}		else if (PROPERTY_SHAREUNITOFWORK.equals(id)) {
			setShareUnitOfWork(Objects.convertTo(value, Boolean.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_STRATEGYREF.equals(id)) {
			return this.getStrategyRef();
		}		else if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
			return this.getExecutorServiceRef();
		}		else if (PROPERTY_ONPREPAREREF.equals(id)) {
			return this.getOnPrepareRef();
		}		else if (PROPERTY_PARALLELPROCESSING.equals(id)) {
			return this.getParallelProcessing();
		}		else if (PROPERTY_STREAMING.equals(id)) {
			return this.getStreaming();
		}		else if (PROPERTY_STOPONEXCEPTION.equals(id)) {
			return this.getStopOnException();
		}		else if (PROPERTY_TIMEOUT.equals(id)) {
			return this.getTimeout();
		}		else if (PROPERTY_SHAREUNITOFWORK.equals(id)) {
			return this.getShareUnitOfWork();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		MulticastDefinition answer = new MulticastDefinition();
    answer.setStrategyRef(toXmlPropertyValue(PROPERTY_STRATEGYREF, this.getStrategyRef()));
    answer.setExecutorServiceRef(toXmlPropertyValue(PROPERTY_EXECUTORSERVICEREF, this.getExecutorServiceRef()));
    answer.setOnPrepareRef(toXmlPropertyValue(PROPERTY_ONPREPAREREF, this.getOnPrepareRef()));
    answer.setParallelProcessing(toXmlPropertyValue(PROPERTY_PARALLELPROCESSING, this.getParallelProcessing()));
    answer.setStreaming(toXmlPropertyValue(PROPERTY_STREAMING, this.getStreaming()));
    answer.setStopOnException(toXmlPropertyValue(PROPERTY_STOPONEXCEPTION, this.getStopOnException()));
    answer.setTimeout(toXmlPropertyValue(PROPERTY_TIMEOUT, this.getTimeout()));
    answer.setShareUnitOfWork(toXmlPropertyValue(PROPERTY_SHAREUNITOFWORK, this.getShareUnitOfWork()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return MulticastDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof MulticastDefinition) {
      MulticastDefinition node = (MulticastDefinition) processor;
      this.setStrategyRef(node.getStrategyRef());
      this.setExecutorServiceRef(node.getExecutorServiceRef());
      this.setOnPrepareRef(node.getOnPrepareRef());
      this.setParallelProcessing(node.getParallelProcessing());
      this.setStreaming(node.getStreaming());
      this.setStopOnException(node.getStopOnException());
      this.setTimeout(node.getTimeout());
      this.setShareUnitOfWork(node.getShareUnitOfWork());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof MulticastDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
