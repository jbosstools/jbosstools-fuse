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

import org.apache.camel.model.RemovePropertiesDefinition;
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
 * The Node class from Camel's RemovePropertiesDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class RemoveProperties extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "RemoveProperties.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "RemoveProperties.InheritErrorHandler";
    public static final String PROPERTY_PATTERN = "RemoveProperties.Pattern";
    public static final String PROPERTY_EXCLUDEPATTERN = "RemoveProperties.ExcludePattern";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private String pattern;
    private String excludePattern;

    public RemoveProperties() {
    }

    public RemoveProperties(RemovePropertiesDefinition definition, RouteContainer parent) {
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
        return "allEIPs";
    }

    @Override
    public String getCategoryName() {
        return "Transformation";
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
     * @return the pattern
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        String oldValue = this.pattern;
        this.pattern = pattern;
        if (!isSame(oldValue, pattern)) {
            firePropertyChange(PROPERTY_PATTERN, oldValue, pattern);
        }
    }

    /**
     * @return the excludePattern
     */
    public String getExcludePattern() {
        return this.excludePattern;
    }

    /**
     * @param excludePattern the excludePattern to set
     */
    public void setExcludePattern(String excludePattern) {
        String oldValue = this.excludePattern;
        this.excludePattern = excludePattern;
        if (!isSame(oldValue, excludePattern)) {
            firePropertyChange(PROPERTY_EXCLUDEPATTERN, oldValue, excludePattern);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelRemovePropertiesCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelRemovePropertiesInheritErrorHandler);
        PropertyDescriptor descPattern = new TextPropertyDescriptor(PROPERTY_PATTERN, Messages.propertyLabelRemovePropertiesPattern);
        PropertyDescriptor descExcludePattern = new TextPropertyDescriptor(PROPERTY_EXCLUDEPATTERN, Messages.propertyLabelRemovePropertiesExcludePattern);

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_PATTERN, descPattern);
        descriptors.put(PROPERTY_EXCLUDEPATTERN, descExcludePattern);
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
        if (PROPERTY_PATTERN.equals(id)) {
            setPattern(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_EXCLUDEPATTERN.equals(id)) {
            setExcludePattern(Objects.convertTo(value, String.class));
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
        if (PROPERTY_PATTERN.equals(id)) {
            return this.getPattern();
        }
        if (PROPERTY_EXCLUDEPATTERN.equals(id)) {
            return this.getExcludePattern();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        RemovePropertiesDefinition answer = new RemovePropertiesDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setPattern(toXmlPropertyValue(PROPERTY_PATTERN, this.getPattern()));
        answer.setExcludePattern(toXmlPropertyValue(PROPERTY_EXCLUDEPATTERN, this.getExcludePattern()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return RemovePropertiesDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof RemovePropertiesDefinition) {
            RemovePropertiesDefinition node = (RemovePropertiesDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setPattern(node.getPattern());
            this.setExcludePattern(node.getExcludePattern());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof RemovePropertiesDefinition. Was " + processor.getClass().getName());
        }
    }

}
