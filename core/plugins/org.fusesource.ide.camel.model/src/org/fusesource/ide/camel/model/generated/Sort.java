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

import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.SortDefinition;
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
 * The Node class from Camel's SortDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Sort extends AbstractNode {

    public static final String PROPERTY_EXPRESSION = "Sort.Expression";
    public static final String PROPERTY_COMPARATORREF = "Sort.ComparatorRef";

    private ExpressionDefinition expression;
    private String comparatorRef;

    public Sort() {
    }

    public Sort(SortDefinition definition, RouteContainer parent) {
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
        return "sortEIP";
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
     * @return the comparatorRef
     */
    public String getComparatorRef() {
        return this.comparatorRef;
    }

    /**
     * @param comparatorRef the comparatorRef to set
     */
    public void setComparatorRef(String comparatorRef) {
        String oldValue = this.comparatorRef;
        this.comparatorRef = comparatorRef;
        if (!isSame(oldValue, comparatorRef)) {
            firePropertyChange(PROPERTY_COMPARATORREF, oldValue, comparatorRef);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descExpression = new ExpressionPropertyDescriptor(PROPERTY_EXPRESSION, Messages.propertyLabelSortExpression);
        PropertyDescriptor descComparatorRef = new TextPropertyDescriptor(PROPERTY_COMPARATORREF, Messages.propertyLabelSortComparatorRef);

        descriptors.put(PROPERTY_EXPRESSION, descExpression);
        descriptors.put(PROPERTY_COMPARATORREF, descComparatorRef);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_EXPRESSION.equals(id)) {
            setExpression(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_COMPARATORREF.equals(id)) {
            setComparatorRef(Objects.convertTo(value, String.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_EXPRESSION.equals(id)) {
            return this.getExpression();
        }
        if (PROPERTY_COMPARATORREF.equals(id)) {
            return this.getComparatorRef();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        SortDefinition answer = new SortDefinition();

        answer.setExpression(toXmlPropertyValue(PROPERTY_EXPRESSION, this.getExpression()));
        answer.setComparatorRef(toXmlPropertyValue(PROPERTY_COMPARATORREF, this.getComparatorRef()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return SortDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof SortDefinition) {
            SortDefinition node = (SortDefinition) processor;

            this.setExpression(node.getExpression());
            this.setComparatorRef(node.getComparatorRef());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof SortDefinition. Was " + processor.getClass().getName());
        }
    }

}
