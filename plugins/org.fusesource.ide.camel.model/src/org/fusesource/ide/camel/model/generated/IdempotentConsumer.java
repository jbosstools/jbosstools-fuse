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
import org.apache.camel.model.IdempotentConsumerDefinition;
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
 * The Node class for Camel's IdempotentConsumerDefinition
 */
public class IdempotentConsumer extends AbstractNode {

	public static final String PROPERTY_EXPRESSION = "IdempotentConsumer.Expression";
	public static final String PROPERTY_MESSAGEIDREPOSITORYREF = "IdempotentConsumer.MessageIdRepositoryRef";
	public static final String PROPERTY_EAGER = "IdempotentConsumer.Eager";
	public static final String PROPERTY_SKIPDUPLICATE = "IdempotentConsumer.SkipDuplicate";
	public static final String PROPERTY_REMOVEONFAILURE = "IdempotentConsumer.RemoveOnFailure";
	
	private ExpressionDefinition expression;
	private String messageIdRepositoryRef;
	private Boolean eager;
	private Boolean skipDuplicate;
	private Boolean removeOnFailure;
	
    public IdempotentConsumer() {
    }		
	
    public IdempotentConsumer(IdempotentConsumerDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "idempotentConsumer.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "idempotentConsumer";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Routing";
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
		if (!isSame(oldValue, expression)) {
		    firePropertyChange(PROPERTY_EXPRESSION, oldValue, expression);
		}
	}

	/**
	 * @return the messageIdRepositoryRef
	 */
	public String getMessageIdRepositoryRef() {
		return this.messageIdRepositoryRef;
	}
	
	/**
	 * @param messageIdRepositoryRef the messageIdRepositoryRef to set
	 */
	public void setMessageIdRepositoryRef(String messageIdRepositoryRef) {
		String oldValue = this.messageIdRepositoryRef;
		this.messageIdRepositoryRef = messageIdRepositoryRef;
		if (!isSame(oldValue, messageIdRepositoryRef)) {
		    firePropertyChange(PROPERTY_MESSAGEIDREPOSITORYREF, oldValue, messageIdRepositoryRef);
		}
	}

	/**
	 * @return the eager
	 */
	public Boolean getEager() {
		return this.eager;
	}
	
	/**
	 * @param eager the eager to set
	 */
	public void setEager(Boolean eager) {
		Boolean oldValue = this.eager;
		this.eager = eager;
		if (!isSame(oldValue, eager)) {
		    firePropertyChange(PROPERTY_EAGER, oldValue, eager);
		}
	}

	/**
	 * @return the skipDuplicate
	 */
	public Boolean getSkipDuplicate() {
		return this.skipDuplicate;
	}
	
	/**
	 * @param skipDuplicate the skipDuplicate to set
	 */
	public void setSkipDuplicate(Boolean skipDuplicate) {
		Boolean oldValue = this.skipDuplicate;
		this.skipDuplicate = skipDuplicate;
		if (!isSame(oldValue, skipDuplicate)) {
		    firePropertyChange(PROPERTY_SKIPDUPLICATE, oldValue, skipDuplicate);
		}
	}

	/**
	 * @return the removeOnFailure
	 */
	public Boolean getRemoveOnFailure() {
		return this.removeOnFailure;
	}
	
	/**
	 * @param removeOnFailure the removeOnFailure to set
	 */
	public void setRemoveOnFailure(Boolean removeOnFailure) {
		Boolean oldValue = this.removeOnFailure;
		this.removeOnFailure = removeOnFailure;
		if (!isSame(oldValue, removeOnFailure)) {
		    firePropertyChange(PROPERTY_REMOVEONFAILURE, oldValue, removeOnFailure);
		}
	}


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  
  	PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelIdempotentConsumerExpression);
    		PropertyDescriptor descMessageIdRepositoryRef = new TextPropertyDescriptor(PROPERTY_MESSAGEIDREPOSITORYREF, Messages.propertyLabelIdempotentConsumerMessageIdRepositoryRef);
      	PropertyDescriptor descEager = new BooleanPropertyDescriptor(PROPERTY_EAGER, Messages.propertyLabelIdempotentConsumerEager);
      	PropertyDescriptor descSkipDuplicate = new BooleanPropertyDescriptor(PROPERTY_SKIPDUPLICATE, Messages.propertyLabelIdempotentConsumerSkipDuplicate);
      	PropertyDescriptor descRemoveOnFailure = new BooleanPropertyDescriptor(PROPERTY_REMOVEONFAILURE, Messages.propertyLabelIdempotentConsumerRemoveOnFailure);
  		descriptors.put(PROPERTY_EXPRESSION, descExpression);
		descriptors.put(PROPERTY_MESSAGEIDREPOSITORYREF, descMessageIdRepositoryRef);
		descriptors.put(PROPERTY_EAGER, descEager);
		descriptors.put(PROPERTY_SKIPDUPLICATE, descSkipDuplicate);
		descriptors.put(PROPERTY_REMOVEONFAILURE, descRemoveOnFailure);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_EXPRESSION.equals(id)) {
			setExpression(Objects.convertTo(value, ExpressionDefinition.class));
		}		else if (PROPERTY_MESSAGEIDREPOSITORYREF.equals(id)) {
			setMessageIdRepositoryRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_EAGER.equals(id)) {
			setEager(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_SKIPDUPLICATE.equals(id)) {
			setSkipDuplicate(Objects.convertTo(value, Boolean.class));
		}		else if (PROPERTY_REMOVEONFAILURE.equals(id)) {
			setRemoveOnFailure(Objects.convertTo(value, Boolean.class));
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
		}		else if (PROPERTY_MESSAGEIDREPOSITORYREF.equals(id)) {
			return this.getMessageIdRepositoryRef();
		}		else if (PROPERTY_EAGER.equals(id)) {
			return this.getEager();
		}		else if (PROPERTY_SKIPDUPLICATE.equals(id)) {
			return this.getSkipDuplicate();
		}		else if (PROPERTY_REMOVEONFAILURE.equals(id)) {
			return this.getRemoveOnFailure();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		IdempotentConsumerDefinition answer = new IdempotentConsumerDefinition();
    answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
    answer.setMessageIdRepositoryRef(toXmlPropertyValue(PROPERTY_MESSAGEIDREPOSITORYREF, this.getMessageIdRepositoryRef()));
    answer.setEager(toXmlPropertyValue(PROPERTY_EAGER, this.getEager()));
    answer.setSkipDuplicate(toXmlPropertyValue(PROPERTY_SKIPDUPLICATE, this.getSkipDuplicate()));
    answer.setRemoveOnFailure(toXmlPropertyValue(PROPERTY_REMOVEONFAILURE, this.getRemoveOnFailure()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return IdempotentConsumerDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof IdempotentConsumerDefinition) {
      IdempotentConsumerDefinition node = (IdempotentConsumerDefinition) processor;
      this.setExpression(node.getExpression());
      this.setMessageIdRepositoryRef(node.getMessageIdRepositoryRef());
      this.setEager(node.getEager());
      this.setSkipDuplicate(node.getSkipDuplicate());
      this.setRemoveOnFailure(node.getRemoveOnFailure());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof IdempotentConsumerDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
