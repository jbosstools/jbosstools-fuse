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

import org.apache.camel.model.InOnlyDefinition;
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
 * The Node class from Camel's InOnlyDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class InOnly extends AbstractNode {

    public static final String PROPERTY_URI = "InOnly.Uri";

    private String uri;

    public InOnly() {
    }

    public InOnly(InOnlyDefinition definition, RouteContainer parent) {
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
        return "inOnlyEIP";
    }

    @Override
    public String getCategoryName() {
        return "Transformation";
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        String oldValue = this.uri;
        this.uri = uri;
        if (!isSame(oldValue, uri)) {
            firePropertyChange(PROPERTY_URI, oldValue, uri);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descUri = new TextPropertyDescriptor(PROPERTY_URI, Messages.propertyLabelInOnlyUri);

        descriptors.put(PROPERTY_URI, descUri);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_URI.equals(id)) {
            setUri(Objects.convertTo(value, String.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_URI.equals(id)) {
            return this.getUri();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        InOnlyDefinition answer = new InOnlyDefinition();

        answer.setUri(toXmlPropertyValue(PROPERTY_URI, this.getUri()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return InOnlyDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof InOnlyDefinition) {
            InOnlyDefinition node = (InOnlyDefinition) processor;

            this.setUri(node.getUri());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof InOnlyDefinition. Was " + processor.getClass().getName());
        }
    }

}
