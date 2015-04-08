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
import org.apache.camel.model.CatchDefinition;
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
 * The Node class from Camel's CatchDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Catch extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "Catch.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "Catch.InheritErrorHandler";
    public static final String PROPERTY_EXCEPTIONS = "Catch.Exceptions";
    public static final String PROPERTY_HANDLED = "Catch.Handled";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private List exceptions;
    private ExpressionDefinition handled;

    public Catch() {
    }

    public Catch(CatchDefinition definition, RouteContainer parent) {
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
        return "catchEIP";
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

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelCatchCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelCatchInheritErrorHandler);
        PropertyDescriptor descExceptions = new ListPropertyDescriptor(PROPERTY_EXCEPTIONS, Messages.propertyLabelCatchExceptions);
        PropertyDescriptor descHandled = new ExpressionPropertyDescriptor(PROPERTY_HANDLED, Messages.propertyLabelCatchHandled);

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_EXCEPTIONS, descExceptions);
        descriptors.put(PROPERTY_HANDLED, descHandled);
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
        if (PROPERTY_EXCEPTIONS.equals(id)) {
            setExceptions(Objects.convertTo(value, List.class));
            return;
        }
        if (PROPERTY_HANDLED.equals(id)) {
            setHandled(Objects.convertTo(value, ExpressionDefinition.class));
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
        if (PROPERTY_EXCEPTIONS.equals(id)) {
            return this.getExceptions();
        }
        if (PROPERTY_HANDLED.equals(id)) {
            return this.getHandled();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        CatchDefinition answer = new CatchDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setExceptions(toXmlPropertyValue(PROPERTY_EXCEPTIONS, this.getExceptions()));
        Objects.setField(answer, "handled", toXmlPropertyValue(PROPERTY_HANDLED, this.getHandled()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return CatchDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof CatchDefinition) {
            CatchDefinition node = (CatchDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setExceptions(node.getExceptions());
            Objects.setField(this, "handled", node.getHandled());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof CatchDefinition. Was " + processor.getClass().getName());
        }
    }

}
