/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
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

import org.apache.camel.model.OnCompletionMode;
import org.apache.camel.model.OnCompletionDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.ProcessorDefinition;
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
 * The Node class from Camel's OnCompletionDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class OnCompletion extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "OnCompletion.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "OnCompletion.InheritErrorHandler";
    public static final String PROPERTY_MODE = "OnCompletion.Mode";
    public static final String PROPERTY_ONCOMPLETEONLY = "OnCompletion.OnCompleteOnly";
    public static final String PROPERTY_ONFAILUREONLY = "OnCompletion.OnFailureOnly";
    public static final String PROPERTY_PARALLELPROCESSING = "OnCompletion.ParallelProcessing";
    public static final String PROPERTY_EXECUTORSERVICEREF = "OnCompletion.ExecutorServiceRef";
    public static final String PROPERTY_USEORIGINALMESSAGEPOLICY = "OnCompletion.UseOriginalMessagePolicy";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private OnCompletionMode mode;
    private Boolean onCompleteOnly;
    private Boolean onFailureOnly;
    private Boolean parallelProcessing;
    private String executorServiceRef;
    private Boolean useOriginalMessagePolicy;

    public OnCompletion() {
    }

    public OnCompletion(OnCompletionDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "generic.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "onCompleteEIP";
    }

    @Override
    public String getCategoryName() {
        return "Control Flow";
    }

    /**
     * @return the customId
     */
    public Boolean getCustomId() {
        return this.customId;
    }

    /**
     * @param customId the customId to set
     */
    public void setCustomId(Boolean customId) {
        Boolean oldValue = this.customId;
        this.customId = customId;
        if (!isSame(oldValue, customId)) {
            firePropertyChange(PROPERTY_CUSTOMID, oldValue, customId);
        }
    }

    /**
     * @return the inheritErrorHandler
     */
    public Boolean getInheritErrorHandler() {
        return this.inheritErrorHandler;
    }

    /**
     * @param inheritErrorHandler the inheritErrorHandler to set
     */
    public void setInheritErrorHandler(Boolean inheritErrorHandler) {
        Boolean oldValue = this.inheritErrorHandler;
        this.inheritErrorHandler = inheritErrorHandler;
        if (!isSame(oldValue, inheritErrorHandler)) {
            firePropertyChange(PROPERTY_INHERITERRORHANDLER, oldValue, inheritErrorHandler);
        }
    }

    /**
     * @return the mode
     */
    public OnCompletionMode getMode() {
        return this.mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(OnCompletionMode mode) {
        OnCompletionMode oldValue = this.mode;
        this.mode = mode;
        if (!isSame(oldValue, mode)) {
            firePropertyChange(PROPERTY_MODE, oldValue, mode);
        }
    }

    /**
     * @return the onCompleteOnly
     */
    public Boolean getOnCompleteOnly() {
        return this.onCompleteOnly;
    }

    /**
     * @param onCompleteOnly the onCompleteOnly to set
     */
    public void setOnCompleteOnly(Boolean onCompleteOnly) {
        Boolean oldValue = this.onCompleteOnly;
        this.onCompleteOnly = onCompleteOnly;
        if (!isSame(oldValue, onCompleteOnly)) {
            firePropertyChange(PROPERTY_ONCOMPLETEONLY, oldValue, onCompleteOnly);
        }
    }

    /**
     * @return the onFailureOnly
     */
    public Boolean getOnFailureOnly() {
        return this.onFailureOnly;
    }

    /**
     * @param onFailureOnly the onFailureOnly to set
     */
    public void setOnFailureOnly(Boolean onFailureOnly) {
        Boolean oldValue = this.onFailureOnly;
        this.onFailureOnly = onFailureOnly;
        if (!isSame(oldValue, onFailureOnly)) {
            firePropertyChange(PROPERTY_ONFAILUREONLY, oldValue, onFailureOnly);
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
     * @return the useOriginalMessagePolicy
     */
    public Boolean getUseOriginalMessagePolicy() {
        return this.useOriginalMessagePolicy;
    }

    /**
     * @param useOriginalMessagePolicy the useOriginalMessagePolicy to set
     */
    public void setUseOriginalMessagePolicy(Boolean useOriginalMessagePolicy) {
        Boolean oldValue = this.useOriginalMessagePolicy;
        this.useOriginalMessagePolicy = useOriginalMessagePolicy;
        if (!isSame(oldValue, useOriginalMessagePolicy)) {
            firePropertyChange(PROPERTY_USEORIGINALMESSAGEPOLICY, oldValue, useOriginalMessagePolicy);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelOnCompletionCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelOnCompletionInheritErrorHandler);
        PropertyDescriptor descMode = new EnumPropertyDescriptor(PROPERTY_MODE, Messages.propertyLabelOnCompletionMode, OnCompletionMode.class);
        PropertyDescriptor descOnCompleteOnly = new BooleanPropertyDescriptor(PROPERTY_ONCOMPLETEONLY, Messages.propertyLabelOnCompletionOnCompleteOnly);
        PropertyDescriptor descOnFailureOnly = new BooleanPropertyDescriptor(PROPERTY_ONFAILUREONLY, Messages.propertyLabelOnCompletionOnFailureOnly);
        PropertyDescriptor descParallelProcessing = new BooleanPropertyDescriptor(PROPERTY_PARALLELPROCESSING, Messages.propertyLabelOnCompletionParallelProcessing);
        PropertyDescriptor descExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_EXECUTORSERVICEREF, Messages.propertyLabelOnCompletionExecutorServiceRef);
        PropertyDescriptor descUseOriginalMessagePolicy = new BooleanPropertyDescriptor(PROPERTY_USEORIGINALMESSAGEPOLICY, Messages.propertyLabelOnCompletionUseOriginalMessagePolicy);

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_MODE, descMode);
        descriptors.put(PROPERTY_ONCOMPLETEONLY, descOnCompleteOnly);
        descriptors.put(PROPERTY_ONFAILUREONLY, descOnFailureOnly);
        descriptors.put(PROPERTY_PARALLELPROCESSING, descParallelProcessing);
        descriptors.put(PROPERTY_EXECUTORSERVICEREF, descExecutorServiceRef);
        descriptors.put(PROPERTY_USEORIGINALMESSAGEPOLICY, descUseOriginalMessagePolicy);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_CUSTOMID.equals(id)) {
            setCustomId(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
            setInheritErrorHandler(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_MODE.equals(id)) {
            setMode(Objects.convertTo(value, OnCompletionMode.class));
            return;
        }
        if (PROPERTY_ONCOMPLETEONLY.equals(id)) {
            setOnCompleteOnly(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_ONFAILUREONLY.equals(id)) {
            setOnFailureOnly(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_PARALLELPROCESSING.equals(id)) {
            setParallelProcessing(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
            setExecutorServiceRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_USEORIGINALMESSAGEPOLICY.equals(id)) {
            setUseOriginalMessagePolicy(Objects.convertTo(value, Boolean.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_CUSTOMID.equals(id)) {
            return this.getCustomId();
        }
        if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
            return Objects.<Boolean>getField(this, "inheritErrorHandler");
        }
        if (PROPERTY_MODE.equals(id)) {
            return this.getMode();
        }
        if (PROPERTY_ONCOMPLETEONLY.equals(id)) {
            return this.getOnCompleteOnly();
        }
        if (PROPERTY_ONFAILUREONLY.equals(id)) {
            return this.getOnFailureOnly();
        }
        if (PROPERTY_PARALLELPROCESSING.equals(id)) {
            return this.getParallelProcessing();
        }
        if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
            return this.getExecutorServiceRef();
        }
        if (PROPERTY_USEORIGINALMESSAGEPOLICY.equals(id)) {
            return this.getUseOriginalMessagePolicy();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        OnCompletionDefinition answer = new OnCompletionDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setMode(toXmlPropertyValue(PROPERTY_MODE, this.getMode()));
        answer.setOnCompleteOnly(toXmlPropertyValue(PROPERTY_ONCOMPLETEONLY, this.getOnCompleteOnly()));
        answer.setOnFailureOnly(toXmlPropertyValue(PROPERTY_ONFAILUREONLY, this.getOnFailureOnly()));
        answer.setParallelProcessing(toXmlPropertyValue(PROPERTY_PARALLELPROCESSING, this.getParallelProcessing()));
        answer.setExecutorServiceRef(toXmlPropertyValue(PROPERTY_EXECUTORSERVICEREF, this.getExecutorServiceRef()));
        answer.setUseOriginalMessagePolicy(toXmlPropertyValue(PROPERTY_USEORIGINALMESSAGEPOLICY, this.getUseOriginalMessagePolicy()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return OnCompletionDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof OnCompletionDefinition) {
            OnCompletionDefinition node = (OnCompletionDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setMode(node.getMode());
            this.setOnCompleteOnly(node.getOnCompleteOnly());
            this.setOnFailureOnly(node.getOnFailureOnly());
            this.setParallelProcessing(node.getParallelProcessing());
            this.setExecutorServiceRef(node.getExecutorServiceRef());
            this.setUseOriginalMessagePolicy(node.getUseOriginalMessagePolicy());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof OnCompletionDefinition. Was " + processor.getClass().getName());
        }
    }

}
