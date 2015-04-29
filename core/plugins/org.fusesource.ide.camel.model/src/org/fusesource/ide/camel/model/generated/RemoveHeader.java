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

import org.apache.camel.model.RemoveHeaderDefinition;
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
 * The Node class from Camel's RemoveHeaderDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class RemoveHeader extends AbstractNode {

    public static final String PROPERTY_HEADERNAME = "RemoveHeader.HeaderName";

    private String headerName;

    public RemoveHeader() {
    }

    public RemoveHeader(RemoveHeaderDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "transform.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "removeHeaderNode";
    }

    @Override
    public String getCategoryName() {
        return "Transformation";
    }

    /**
     * @return the headerName
     */
    public String getHeaderName() {
        return this.headerName;
    }

    /**
     * @param headerName the headerName to set
     */
    public void setHeaderName(String headerName) {
        String oldValue = this.headerName;
        this.headerName = headerName;
        if (!isSame(oldValue, headerName)) {
            firePropertyChange(PROPERTY_HEADERNAME, oldValue, headerName);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descHeaderName = new TextPropertyDescriptor(PROPERTY_HEADERNAME, Messages.propertyLabelRemoveHeaderHeaderName);

        descriptors.put(PROPERTY_HEADERNAME, descHeaderName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_HEADERNAME.equals(id)) {
            setHeaderName(Objects.convertTo(value, String.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_HEADERNAME.equals(id)) {
            return this.getHeaderName();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        RemoveHeaderDefinition answer = new RemoveHeaderDefinition();

        answer.setHeaderName(toXmlPropertyValue(PROPERTY_HEADERNAME, this.getHeaderName()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return RemoveHeaderDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof RemoveHeaderDefinition) {
            RemoveHeaderDefinition node = (RemoveHeaderDefinition) processor;

            this.setHeaderName(node.getHeaderName());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof RemoveHeaderDefinition. Was " + processor.getClass().getName());
        }
    }

}
