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

import org.apache.camel.model.RollbackDefinition;
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
 * The Node class from Camel's RollbackDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Rollback extends AbstractNode {

    public static final String PROPERTY_MARKROLLBACKONLY = "Rollback.MarkRollbackOnly";
    public static final String PROPERTY_MARKROLLBACKONLYLAST = "Rollback.MarkRollbackOnlyLast";
    public static final String PROPERTY_MESSAGE = "Rollback.Message";

    private Boolean markRollbackOnly;
    private Boolean markRollbackOnlyLast;
    private String message;

    public Rollback() {
    }

    public Rollback(RollbackDefinition definition, RouteContainer parent) {
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
        return "rolbackNode";
    }

    @Override
    public String getCategoryName() {
        return "Control Flow";
    }

    /**
     * @return the markRollbackOnly
     */
    public Boolean getMarkRollbackOnly() {
        return this.markRollbackOnly;
    }

    /**
     * @param markRollbackOnly the markRollbackOnly to set
     */
    public void setMarkRollbackOnly(Boolean markRollbackOnly) {
        Boolean oldValue = this.markRollbackOnly;
        this.markRollbackOnly = markRollbackOnly;
        if (!isSame(oldValue, markRollbackOnly)) {
            firePropertyChange(PROPERTY_MARKROLLBACKONLY, oldValue, markRollbackOnly);
        }
    }

    /**
     * @return the markRollbackOnlyLast
     */
    public Boolean getMarkRollbackOnlyLast() {
        return this.markRollbackOnlyLast;
    }

    /**
     * @param markRollbackOnlyLast the markRollbackOnlyLast to set
     */
    public void setMarkRollbackOnlyLast(Boolean markRollbackOnlyLast) {
        Boolean oldValue = this.markRollbackOnlyLast;
        this.markRollbackOnlyLast = markRollbackOnlyLast;
        if (!isSame(oldValue, markRollbackOnlyLast)) {
            firePropertyChange(PROPERTY_MARKROLLBACKONLYLAST, oldValue, markRollbackOnlyLast);
        }
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        String oldValue = this.message;
        this.message = message;
        if (!isSame(oldValue, message)) {
            firePropertyChange(PROPERTY_MESSAGE, oldValue, message);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descMarkRollbackOnly = new BooleanPropertyDescriptor(PROPERTY_MARKROLLBACKONLY, Messages.propertyLabelRollbackMarkRollbackOnly);
        PropertyDescriptor descMarkRollbackOnlyLast = new BooleanPropertyDescriptor(PROPERTY_MARKROLLBACKONLYLAST, Messages.propertyLabelRollbackMarkRollbackOnlyLast);
        PropertyDescriptor descMessage = new TextPropertyDescriptor(PROPERTY_MESSAGE, Messages.propertyLabelRollbackMessage);

        descriptors.put(PROPERTY_MARKROLLBACKONLY, descMarkRollbackOnly);
        descriptors.put(PROPERTY_MARKROLLBACKONLYLAST, descMarkRollbackOnlyLast);
        descriptors.put(PROPERTY_MESSAGE, descMessage);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_MARKROLLBACKONLY.equals(id)) {
            setMarkRollbackOnly(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_MARKROLLBACKONLYLAST.equals(id)) {
            setMarkRollbackOnlyLast(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_MESSAGE.equals(id)) {
            setMessage(Objects.convertTo(value, String.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_MARKROLLBACKONLY.equals(id)) {
            return this.getMarkRollbackOnly();
        }
        if (PROPERTY_MARKROLLBACKONLYLAST.equals(id)) {
            return this.getMarkRollbackOnlyLast();
        }
        if (PROPERTY_MESSAGE.equals(id)) {
            return this.getMessage();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        RollbackDefinition answer = new RollbackDefinition();

        answer.setMarkRollbackOnly(toXmlPropertyValue(PROPERTY_MARKROLLBACKONLY, this.getMarkRollbackOnly()));
        answer.setMarkRollbackOnlyLast(toXmlPropertyValue(PROPERTY_MARKROLLBACKONLYLAST, this.getMarkRollbackOnlyLast()));
        answer.setMessage(toXmlPropertyValue(PROPERTY_MESSAGE, this.getMessage()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return RollbackDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof RollbackDefinition) {
            RollbackDefinition node = (RollbackDefinition) processor;

            this.setMarkRollbackOnly(node.getMarkRollbackOnly());
            this.setMarkRollbackOnlyLast(node.getMarkRollbackOnlyLast());
            this.setMessage(node.getMessage());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof RollbackDefinition. Was " + processor.getClass().getName());
        }
    }

}
