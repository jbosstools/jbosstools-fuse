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

import java.util.List;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.apache.camel.model.OnExceptionDefinition;
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
 * The Node class from Camel's OnExceptionDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class OnException extends AbstractNode {

    public static final String PROPERTY_INHERITERRORHANDLER = "OnException.InheritErrorHandler";
    public static final String PROPERTY_EXCEPTIONS = "OnException.Exceptions";
    public static final String PROPERTY_RETRYWHILE = "OnException.RetryWhile";
    public static final String PROPERTY_REDELIVERYPOLICYREF = "OnException.RedeliveryPolicyRef";
    public static final String PROPERTY_HANDLED = "OnException.Handled";
    public static final String PROPERTY_CONTINUED = "OnException.Continued";
    public static final String PROPERTY_ONREDELIVERYREF = "OnException.OnRedeliveryRef";
    public static final String PROPERTY_USEORIGINALMESSAGEPOLICY = "OnException.UseOriginalMessagePolicy";
    public static final String PROPERTY_REDELIVERYPOLICY = "OnException.RedeliveryPolicy";

    private Boolean inheritErrorHandler;
    private List exceptions;
    private ExpressionDefinition retryWhile;
    private String redeliveryPolicyRef;
    private ExpressionDefinition handled;
    private ExpressionDefinition continued;
    private String onRedeliveryRef;
    private Boolean useOriginalMessagePolicy;
    private RedeliveryPolicyDefinition redeliveryPolicy;

    public OnException() {
    }

    public OnException(OnExceptionDefinition definition, RouteContainer parent) {
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
        return "onExceptionEIP";
    }

    @Override
    public String getCategoryName() {
        return "Control Flow";
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
     * @return the exceptions
     */
    public List getExceptions() {
        return this.exceptions;
    }

    /**
     * @param exceptions the exceptions to set
     */
    public void setExceptions(List exceptions) {
        List oldValue = this.exceptions;
        this.exceptions = exceptions;
        if (!isSame(oldValue, exceptions)) {
            firePropertyChange(PROPERTY_EXCEPTIONS, oldValue, exceptions);
        }
    }

    /**
     * @return the retryWhile
     */
    public ExpressionDefinition getRetryWhile() {
        return this.retryWhile;
    }

    /**
     * @param retryWhile the retryWhile to set
     */
    public void setRetryWhile(ExpressionDefinition retryWhile) {
        ExpressionDefinition oldValue = this.retryWhile;
        this.retryWhile = retryWhile;
        if (!isSame(oldValue, retryWhile)) {
            firePropertyChange(PROPERTY_RETRYWHILE, oldValue, retryWhile);
        }
    }

    /**
     * @return the redeliveryPolicyRef
     */
    public String getRedeliveryPolicyRef() {
        return this.redeliveryPolicyRef;
    }

    /**
     * @param redeliveryPolicyRef the redeliveryPolicyRef to set
     */
    public void setRedeliveryPolicyRef(String redeliveryPolicyRef) {
        String oldValue = this.redeliveryPolicyRef;
        this.redeliveryPolicyRef = redeliveryPolicyRef;
        if (!isSame(oldValue, redeliveryPolicyRef)) {
            firePropertyChange(PROPERTY_REDELIVERYPOLICYREF, oldValue, redeliveryPolicyRef);
        }
    }

    /**
     * @return the handled
     */
    public ExpressionDefinition getHandled() {
        return this.handled;
    }

    /**
     * @param handled the handled to set
     */
    public void setHandled(ExpressionDefinition handled) {
        ExpressionDefinition oldValue = this.handled;
        this.handled = handled;
        if (!isSame(oldValue, handled)) {
            firePropertyChange(PROPERTY_HANDLED, oldValue, handled);
        }
    }

    /**
     * @return the continued
     */
    public ExpressionDefinition getContinued() {
        return this.continued;
    }

    /**
     * @param continued the continued to set
     */
    public void setContinued(ExpressionDefinition continued) {
        ExpressionDefinition oldValue = this.continued;
        this.continued = continued;
        if (!isSame(oldValue, continued)) {
            firePropertyChange(PROPERTY_CONTINUED, oldValue, continued);
        }
    }

    /**
     * @return the onRedeliveryRef
     */
    public String getOnRedeliveryRef() {
        return this.onRedeliveryRef;
    }

    /**
     * @param onRedeliveryRef the onRedeliveryRef to set
     */
    public void setOnRedeliveryRef(String onRedeliveryRef) {
        String oldValue = this.onRedeliveryRef;
        this.onRedeliveryRef = onRedeliveryRef;
        if (!isSame(oldValue, onRedeliveryRef)) {
            firePropertyChange(PROPERTY_ONREDELIVERYREF, oldValue, onRedeliveryRef);
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

    /**
     * @return the redeliveryPolicy
     */
    public RedeliveryPolicyDefinition getRedeliveryPolicy() {
        return this.redeliveryPolicy;
    }

    /**
     * @param redeliveryPolicy the redeliveryPolicy to set
     */
    public void setRedeliveryPolicy(RedeliveryPolicyDefinition redeliveryPolicy) {
        RedeliveryPolicyDefinition oldValue = this.redeliveryPolicy;
        this.redeliveryPolicy = redeliveryPolicy;
        if (!isSame(oldValue, redeliveryPolicy)) {
            firePropertyChange(PROPERTY_REDELIVERYPOLICY, oldValue, redeliveryPolicy);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelOnExceptionInheritErrorHandler);
        PropertyDescriptor descExceptions = new ListPropertyDescriptor(PROPERTY_EXCEPTIONS, Messages.propertyLabelOnExceptionExceptions);
        PropertyDescriptor descRetryWhile = new ExpressionPropertyDescriptor(PROPERTY_RETRYWHILE, Messages.propertyLabelOnExceptionRetryWhile);
        PropertyDescriptor descRedeliveryPolicyRef = new TextPropertyDescriptor(PROPERTY_REDELIVERYPOLICYREF, Messages.propertyLabelOnExceptionRedeliveryPolicyRef);
        PropertyDescriptor descHandled = new ExpressionPropertyDescriptor(PROPERTY_HANDLED, Messages.propertyLabelOnExceptionHandled);
        PropertyDescriptor descContinued = new ExpressionPropertyDescriptor(PROPERTY_CONTINUED, Messages.propertyLabelOnExceptionContinued);
        PropertyDescriptor descOnRedeliveryRef = new TextPropertyDescriptor(PROPERTY_ONREDELIVERYREF, Messages.propertyLabelOnExceptionOnRedeliveryRef);
        PropertyDescriptor descUseOriginalMessagePolicy = new BooleanPropertyDescriptor(PROPERTY_USEORIGINALMESSAGEPOLICY, Messages.propertyLabelOnExceptionUseOriginalMessagePolicy);
        PropertyDescriptor descRedeliveryPolicy = new ComplexUnionPropertyDescriptor(PROPERTY_REDELIVERYPOLICY, Messages.propertyLabelOnExceptionRedeliveryPolicy, RedeliveryPolicyDefinition.class, new UnionTypeValue[] {
        });

        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_EXCEPTIONS, descExceptions);
        descriptors.put(PROPERTY_RETRYWHILE, descRetryWhile);
        descriptors.put(PROPERTY_REDELIVERYPOLICYREF, descRedeliveryPolicyRef);
        descriptors.put(PROPERTY_HANDLED, descHandled);
        descriptors.put(PROPERTY_CONTINUED, descContinued);
        descriptors.put(PROPERTY_ONREDELIVERYREF, descOnRedeliveryRef);
        descriptors.put(PROPERTY_USEORIGINALMESSAGEPOLICY, descUseOriginalMessagePolicy);
        descriptors.put(PROPERTY_REDELIVERYPOLICY, descRedeliveryPolicy);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
            setInheritErrorHandler(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_EXCEPTIONS.equals(id)) {
            setExceptions(Objects.convertTo(value, List.class));
            return;
        }
        if (PROPERTY_RETRYWHILE.equals(id)) {
            setRetryWhile(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_REDELIVERYPOLICYREF.equals(id)) {
            setRedeliveryPolicyRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_HANDLED.equals(id)) {
            setHandled(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_CONTINUED.equals(id)) {
            setContinued(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_ONREDELIVERYREF.equals(id)) {
            setOnRedeliveryRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_USEORIGINALMESSAGEPOLICY.equals(id)) {
            setUseOriginalMessagePolicy(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_REDELIVERYPOLICY.equals(id)) {
            setRedeliveryPolicy(Objects.convertTo(value, RedeliveryPolicyDefinition.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
            return Objects.<Boolean>getField(this, "inheritErrorHandler");
        }
        if (PROPERTY_EXCEPTIONS.equals(id)) {
            return this.getExceptions();
        }
        if (PROPERTY_RETRYWHILE.equals(id)) {
            return this.getRetryWhile();
        }
        if (PROPERTY_REDELIVERYPOLICYREF.equals(id)) {
            return this.getRedeliveryPolicyRef();
        }
        if (PROPERTY_HANDLED.equals(id)) {
            return this.getHandled();
        }
        if (PROPERTY_CONTINUED.equals(id)) {
            return this.getContinued();
        }
        if (PROPERTY_ONREDELIVERYREF.equals(id)) {
            return this.getOnRedeliveryRef();
        }
        if (PROPERTY_USEORIGINALMESSAGEPOLICY.equals(id)) {
            return this.getUseOriginalMessagePolicy();
        }
        if (PROPERTY_REDELIVERYPOLICY.equals(id)) {
            return this.getRedeliveryPolicy();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        OnExceptionDefinition answer = new OnExceptionDefinition();

        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setExceptions(toXmlPropertyValue(PROPERTY_EXCEPTIONS, this.getExceptions()));
        Objects.setField(answer, "retryWhile", toXmlPropertyValue(PROPERTY_RETRYWHILE, this.getRetryWhile()));
        answer.setRedeliveryPolicyRef(toXmlPropertyValue(PROPERTY_REDELIVERYPOLICYREF, this.getRedeliveryPolicyRef()));
        Objects.setField(answer, "handled", toXmlPropertyValue(PROPERTY_HANDLED, this.getHandled()));
        Objects.setField(answer, "continued", toXmlPropertyValue(PROPERTY_CONTINUED, this.getContinued()));
        answer.setOnRedeliveryRef(toXmlPropertyValue(PROPERTY_ONREDELIVERYREF, this.getOnRedeliveryRef()));
        answer.setUseOriginalMessagePolicy(toXmlPropertyValue(PROPERTY_USEORIGINALMESSAGEPOLICY, this.getUseOriginalMessagePolicy()));
        answer.setRedeliveryPolicy(toXmlPropertyValue(PROPERTY_REDELIVERYPOLICY, this.getRedeliveryPolicy()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return OnExceptionDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof OnExceptionDefinition) {
            OnExceptionDefinition node = (OnExceptionDefinition) processor;

            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setExceptions(node.getExceptions());
            Objects.setField(this, "retryWhile", node.getRetryWhile());
            this.setRedeliveryPolicyRef(node.getRedeliveryPolicyRef());
            Objects.setField(this, "handled", node.getHandled());
            Objects.setField(this, "continued", node.getContinued());
            this.setOnRedeliveryRef(node.getOnRedeliveryRef());
            this.setUseOriginalMessagePolicy(node.getUseOriginalMessagePolicy());
            this.setRedeliveryPolicy(node.getRedeliveryPolicy());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof OnExceptionDefinition. Was " + processor.getClass().getName());
        }
    }

}
