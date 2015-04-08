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

import org.apache.camel.model.ChoiceDefinition;
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
 * The Node class from Camel's ChoiceDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Choice extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "Choice.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "Choice.InheritErrorHandler";
    public static final String PROPERTY_ONLYWHENOROTHERWISE = "Choice.OnlyWhenOrOtherwise";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private boolean onlyWhenOrOtherwise;

    public Choice() {
    }

    public Choice(ChoiceDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "choice.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "choiceEIP";
    }

    @Override
    public String getCategoryName() {
        return "Routing";
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
     * @return the onlyWhenOrOtherwise
     */
    public boolean isOnlyWhenOrOtherwise() {
        return this.onlyWhenOrOtherwise;
    }

    /**
     * @param onlyWhenOrOtherwise the onlyWhenOrOtherwise to set
     */
    public void setOnlyWhenOrOtherwise(boolean onlyWhenOrOtherwise) {
        boolean oldValue = this.onlyWhenOrOtherwise;
        this.onlyWhenOrOtherwise = onlyWhenOrOtherwise;
        if (!isSame(oldValue, onlyWhenOrOtherwise)) {
            firePropertyChange(PROPERTY_ONLYWHENOROTHERWISE, oldValue, onlyWhenOrOtherwise);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelChoiceCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelChoiceInheritErrorHandler);
        PropertyDescriptor descOnlyWhenOrOtherwise = new BooleanPropertyDescriptor(PROPERTY_ONLYWHENOROTHERWISE, Messages.propertyLabelChoiceOnlyWhenOrOtherwise);

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_ONLYWHENOROTHERWISE, descOnlyWhenOrOtherwise);
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
        if (PROPERTY_ONLYWHENOROTHERWISE.equals(id)) {
            setOnlyWhenOrOtherwise(Objects.convertTo(value, boolean.class));
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
        if (PROPERTY_ONLYWHENOROTHERWISE.equals(id)) {
            return Objects.<Boolean>getField(this, "onlyWhenOrOtherwise");
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        ChoiceDefinition answer = new ChoiceDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        Objects.setField(answer, "onlyWhenOrOtherwise", toXmlPropertyValue(PROPERTY_ONLYWHENOROTHERWISE, Objects.<Boolean>getField(this, "onlyWhenOrOtherwise")));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return ChoiceDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof ChoiceDefinition) {
            ChoiceDefinition node = (ChoiceDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            Objects.setField(this, "onlyWhenOrOtherwise", Objects.<Boolean>getField(node, "onlyWhenOrOtherwise"));
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof ChoiceDefinition. Was " + processor.getClass().getName());
        }
    }

}
