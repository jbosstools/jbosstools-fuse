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
import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.model.LoadBalancerDefinition;
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
 * The Node class for Camel's LoadBalanceDefinition
 */
public class LoadBalance extends AbstractNode {

	public static final String PROPERTY_REF = "LoadBalance.Ref";
	public static final String PROPERTY_LOADBALANCERTYPE = "LoadBalance.LoadBalancerType";
	
	private String ref;
	private LoadBalancerDefinition loadBalancerType;
	
    public LoadBalance() {
    }		
	
    public LoadBalance(LoadBalanceDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "loadBalance.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "loadBalanceEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Routing";
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
	 * @return the loadBalancerType
	 */
	public LoadBalancerDefinition getLoadBalancerType() {
		return this.loadBalancerType;
	}
	
	/**
	 * @param loadBalancerType the loadBalancerType to set
	 */
	public void setLoadBalancerType(LoadBalancerDefinition loadBalancerType) {
		LoadBalancerDefinition oldValue = this.loadBalancerType;
		this.loadBalancerType = loadBalancerType;
		if (!isSame(oldValue, loadBalancerType)) {
		    firePropertyChange(PROPERTY_LOADBALANCERTYPE, oldValue, loadBalancerType);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descRef = new TextPropertyDescriptor(PROPERTY_REF, Messages.propertyLabelLoadBalanceRef);
    
      
		PropertyDescriptor descLoadBalancerType = new ComplexUnionPropertyDescriptor(PROPERTY_LOADBALANCERTYPE, Messages.propertyLabelLoadBalanceLoadBalancerType, LoadBalancerDefinition.class, new UnionTypeValue[]{
		        new UnionTypeValue("failover", org.apache.camel.model.loadbalancer.FailoverLoadBalancerDefinition.class),
		        new UnionTypeValue("random", org.apache.camel.model.loadbalancer.RandomLoadBalancerDefinition.class),
		        new UnionTypeValue("custom", org.apache.camel.model.loadbalancer.CustomLoadBalancerDefinition.class),
		        new UnionTypeValue("roundRobin", org.apache.camel.model.loadbalancer.RoundRobinLoadBalancerDefinition.class),
		        new UnionTypeValue("sticky", org.apache.camel.model.loadbalancer.StickyLoadBalancerDefinition.class),
		        new UnionTypeValue("topic", org.apache.camel.model.loadbalancer.TopicLoadBalancerDefinition.class),
		        new UnionTypeValue("weighted", org.apache.camel.model.loadbalancer.WeightedLoadBalancerDefinition.class),
		  		});
  	  		descriptors.put(PROPERTY_REF, descRef);
		descriptors.put(PROPERTY_LOADBALANCERTYPE, descLoadBalancerType);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_REF.equals(id)) {
			setRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_LOADBALANCERTYPE.equals(id)) {
			setLoadBalancerType(Objects.convertTo(value, LoadBalancerDefinition.class));
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
		}		else if (PROPERTY_LOADBALANCERTYPE.equals(id)) {
			return this.getLoadBalancerType();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		LoadBalanceDefinition answer = new LoadBalanceDefinition();
    answer.setRef(toXmlPropertyValue(PROPERTY_REF, this.getRef()));
    answer.setLoadBalancerType(toXmlPropertyValue(PROPERTY_LOADBALANCERTYPE, this.getLoadBalancerType()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return LoadBalanceDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof LoadBalanceDefinition) {
      LoadBalanceDefinition node = (LoadBalanceDefinition) processor;
      this.setRef(node.getRef());
      this.setLoadBalancerType(node.getLoadBalancerType());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof LoadBalanceDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
