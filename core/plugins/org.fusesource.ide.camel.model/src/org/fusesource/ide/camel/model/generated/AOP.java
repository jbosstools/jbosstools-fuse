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

import org.apache.camel.model.AOPDefinition;
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
 * The Node class from Camel's AOPDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class AOP extends AbstractNode {

    public static final String PROPERTY_INHERITERRORHANDLER = "AOP.InheritErrorHandler";
    public static final String PROPERTY_BEFOREURI = "AOP.BeforeUri";
    public static final String PROPERTY_AFTERURI = "AOP.AfterUri";
    public static final String PROPERTY_AFTERFINALLYURI = "AOP.AfterFinallyUri";

    private Boolean inheritErrorHandler;
    private String beforeUri;
    private String afterUri;
    private String afterFinallyUri;

    public AOP() {
    }

    public AOP(AOPDefinition definition, RouteContainer parent) {
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
        return "AOPEIP";
    }

    @Override
    public String getCategoryName() {
        return "Miscellaneous";
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
     * @return the beforeUri
     */
    public String getBeforeUri() {
        return this.beforeUri;
    }

    /**
     * @param beforeUri the beforeUri to set
     */
    public void setBeforeUri(String beforeUri) {
        String oldValue = this.beforeUri;
        this.beforeUri = beforeUri;
        if (!isSame(oldValue, beforeUri)) {
            firePropertyChange(PROPERTY_BEFOREURI, oldValue, beforeUri);
        }
    }

    /**
     * @return the afterUri
     */
    public String getAfterUri() {
        return this.afterUri;
    }

    /**
     * @param afterUri the afterUri to set
     */
    public void setAfterUri(String afterUri) {
        String oldValue = this.afterUri;
        this.afterUri = afterUri;
        if (!isSame(oldValue, afterUri)) {
            firePropertyChange(PROPERTY_AFTERURI, oldValue, afterUri);
        }
    }

    /**
     * @return the afterFinallyUri
     */
    public String getAfterFinallyUri() {
        return this.afterFinallyUri;
    }

    /**
     * @param afterFinallyUri the afterFinallyUri to set
     */
    public void setAfterFinallyUri(String afterFinallyUri) {
        String oldValue = this.afterFinallyUri;
        this.afterFinallyUri = afterFinallyUri;
        if (!isSame(oldValue, afterFinallyUri)) {
            firePropertyChange(PROPERTY_AFTERFINALLYURI, oldValue, afterFinallyUri);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelAOPInheritErrorHandler);
        PropertyDescriptor descBeforeUri = new TextPropertyDescriptor(PROPERTY_BEFOREURI, Messages.propertyLabelAOPBeforeUri);
        PropertyDescriptor descAfterUri = new TextPropertyDescriptor(PROPERTY_AFTERURI, Messages.propertyLabelAOPAfterUri);
        PropertyDescriptor descAfterFinallyUri = new TextPropertyDescriptor(PROPERTY_AFTERFINALLYURI, Messages.propertyLabelAOPAfterFinallyUri);

        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_BEFOREURI, descBeforeUri);
        descriptors.put(PROPERTY_AFTERURI, descAfterUri);
        descriptors.put(PROPERTY_AFTERFINALLYURI, descAfterFinallyUri);
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
        if (PROPERTY_BEFOREURI.equals(id)) {
            setBeforeUri(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_AFTERURI.equals(id)) {
            setAfterUri(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_AFTERFINALLYURI.equals(id)) {
            setAfterFinallyUri(Objects.convertTo(value, String.class));
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
        if (PROPERTY_BEFOREURI.equals(id)) {
            return this.getBeforeUri();
        }
        if (PROPERTY_AFTERURI.equals(id)) {
            return this.getAfterUri();
        }
        if (PROPERTY_AFTERFINALLYURI.equals(id)) {
            return this.getAfterFinallyUri();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        AOPDefinition answer = new AOPDefinition();

        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setBeforeUri(toXmlPropertyValue(PROPERTY_BEFOREURI, this.getBeforeUri()));
        answer.setAfterUri(toXmlPropertyValue(PROPERTY_AFTERURI, this.getAfterUri()));
        answer.setAfterFinallyUri(toXmlPropertyValue(PROPERTY_AFTERFINALLYURI, this.getAfterFinallyUri()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return AOPDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof AOPDefinition) {
            AOPDefinition node = (AOPDefinition) processor;

            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setBeforeUri(node.getBeforeUri());
            this.setAfterUri(node.getAfterUri());
            this.setAfterFinallyUri(node.getAfterFinallyUri());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof AOPDefinition. Was " + processor.getClass().getName());
        }
    }

}
