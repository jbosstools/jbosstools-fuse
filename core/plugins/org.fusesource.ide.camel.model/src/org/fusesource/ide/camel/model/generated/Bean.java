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

import org.apache.camel.model.BeanDefinition;
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
 * The Node class from Camel's BeanDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Bean extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "Bean.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "Bean.InheritErrorHandler";
    public static final String PROPERTY_REF = "Bean.Ref";
    public static final String PROPERTY_METHOD = "Bean.Method";
    public static final String PROPERTY_BEANTYPE = "Bean.BeanType";
    public static final String PROPERTY_CACHE = "Bean.Cache";
    public static final String PROPERTY_MULTIPARAMETERARRAY = "Bean.MultiParameterArray";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private String ref;
    private String method;
    private String beanType;
    private Boolean cache;
    private Boolean multiParameterArray;

    public Bean() {
    }

    public Bean(BeanDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "bean.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "beanComp";
    }

    @Override
    public String getCategoryName() {
        return "Components";
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
     * @return the ref
     */
    public String getRef() {
        return this.ref;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(String ref) {
        String oldValue = this.ref;
        this.ref = ref;
        if (!isSame(oldValue, ref)) {
            firePropertyChange(PROPERTY_REF, oldValue, ref);
        }
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        String oldValue = this.method;
        this.method = method;
        if (!isSame(oldValue, method)) {
            firePropertyChange(PROPERTY_METHOD, oldValue, method);
        }
    }

    /**
     * @return the beanType
     */
    public String getBeanType() {
        return this.beanType;
    }

    /**
     * @param beanType the beanType to set
     */
    public void setBeanType(String beanType) {
        String oldValue = this.beanType;
        this.beanType = beanType;
        if (!isSame(oldValue, beanType)) {
            firePropertyChange(PROPERTY_BEANTYPE, oldValue, beanType);
        }
    }

    /**
     * @return the cache
     */
    public Boolean getCache() {
        return this.cache;
    }

    /**
     * @param cache the cache to set
     */
    public void setCache(Boolean cache) {
        Boolean oldValue = this.cache;
        this.cache = cache;
        if (!isSame(oldValue, cache)) {
            firePropertyChange(PROPERTY_CACHE, oldValue, cache);
        }
    }

    /**
     * @return the multiParameterArray
     */
    public Boolean getMultiParameterArray() {
        return this.multiParameterArray;
    }

    /**
     * @param multiParameterArray the multiParameterArray to set
     */
    public void setMultiParameterArray(Boolean multiParameterArray) {
        Boolean oldValue = this.multiParameterArray;
        this.multiParameterArray = multiParameterArray;
        if (!isSame(oldValue, multiParameterArray)) {
            firePropertyChange(PROPERTY_MULTIPARAMETERARRAY, oldValue, multiParameterArray);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelBeanCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelBeanInheritErrorHandler);
        PropertyDescriptor descRef = new TextPropertyDescriptor(PROPERTY_REF, Messages.propertyLabelBeanRef);
        PropertyDescriptor descMethod = new TextPropertyDescriptor(PROPERTY_METHOD, Messages.propertyLabelBeanMethod);
        PropertyDescriptor descBeanType = new TextPropertyDescriptor(PROPERTY_BEANTYPE, Messages.propertyLabelBeanBeanType);
        PropertyDescriptor descCache = new BooleanPropertyDescriptor(PROPERTY_CACHE, Messages.propertyLabelBeanCache);
        PropertyDescriptor descMultiParameterArray = new BooleanPropertyDescriptor(PROPERTY_MULTIPARAMETERARRAY, Messages.propertyLabelBeanMultiParameterArray);

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_REF, descRef);
        descriptors.put(PROPERTY_METHOD, descMethod);
        descriptors.put(PROPERTY_BEANTYPE, descBeanType);
        descriptors.put(PROPERTY_CACHE, descCache);
        descriptors.put(PROPERTY_MULTIPARAMETERARRAY, descMultiParameterArray);
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
        if (PROPERTY_REF.equals(id)) {
            setRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_METHOD.equals(id)) {
            setMethod(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_BEANTYPE.equals(id)) {
            setBeanType(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_CACHE.equals(id)) {
            setCache(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_MULTIPARAMETERARRAY.equals(id)) {
            setMultiParameterArray(Objects.convertTo(value, Boolean.class));
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
        if (PROPERTY_REF.equals(id)) {
            return this.getRef();
        }
        if (PROPERTY_METHOD.equals(id)) {
            return this.getMethod();
        }
        if (PROPERTY_BEANTYPE.equals(id)) {
            return this.getBeanType();
        }
        if (PROPERTY_CACHE.equals(id)) {
            return this.getCache();
        }
        if (PROPERTY_MULTIPARAMETERARRAY.equals(id)) {
            return this.getMultiParameterArray();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        BeanDefinition answer = new BeanDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setRef(toXmlPropertyValue(PROPERTY_REF, this.getRef()));
        answer.setMethod(toXmlPropertyValue(PROPERTY_METHOD, this.getMethod()));
        answer.setBeanType(toXmlPropertyValue(PROPERTY_BEANTYPE, this.getBeanType()));
        answer.setCache(toXmlPropertyValue(PROPERTY_CACHE, this.getCache()));
        answer.setMultiParameterArray(toXmlPropertyValue(PROPERTY_MULTIPARAMETERARRAY, this.getMultiParameterArray()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return BeanDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof BeanDefinition) {
            BeanDefinition node = (BeanDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setRef(node.getRef());
            this.setMethod(node.getMethod());
            this.setBeanType(node.getBeanType());
            this.setCache(node.getCache());
            this.setMultiParameterArray(node.getMultiParameterArray());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof BeanDefinition. Was " + processor.getClass().getName());
        }
    }

}
