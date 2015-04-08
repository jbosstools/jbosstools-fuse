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
import java.util.List;
import org.apache.camel.model.WireTapDefinition;
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
 * The Node class from Camel's WireTapDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class WireTap extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "WireTap.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "WireTap.InheritErrorHandler";
    public static final String PROPERTY_URI = "WireTap.Uri";
    public static final String PROPERTY_NEWEXCHANGEPROCESSORREF = "WireTap.NewExchangeProcessorRef";
    public static final String PROPERTY_NEWEXCHANGEEXPRESSION = "WireTap.NewExchangeExpression";
    public static final String PROPERTY_HEADERS = "WireTap.Headers";
    public static final String PROPERTY_EXECUTORSERVICEREF = "WireTap.ExecutorServiceRef";
    public static final String PROPERTY_COPY = "WireTap.Copy";
    public static final String PROPERTY_ONPREPAREREF = "WireTap.OnPrepareRef";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private String uri;
    private String newExchangeProcessorRef;
    private ExpressionDefinition newExchangeExpression;
    private List headers;
    private String executorServiceRef;
    private Boolean copy;
    private String onPrepareRef;

    public WireTap() {
    }

    public WireTap(WireTapDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "wireTap.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "wireTapEIP";
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

    /**
     * @return the newExchangeProcessorRef
     */
    public String getNewExchangeProcessorRef() {
        return this.newExchangeProcessorRef;
    }

    /**
     * @param newExchangeProcessorRef the newExchangeProcessorRef to set
     */
    public void setNewExchangeProcessorRef(String newExchangeProcessorRef) {
        String oldValue = this.newExchangeProcessorRef;
        this.newExchangeProcessorRef = newExchangeProcessorRef;
        if (!isSame(oldValue, newExchangeProcessorRef)) {
            firePropertyChange(PROPERTY_NEWEXCHANGEPROCESSORREF, oldValue, newExchangeProcessorRef);
        }
    }

    /**
     * @return the newExchangeExpression
     */
    public ExpressionDefinition getNewExchangeExpression() {
        return this.newExchangeExpression;
    }

    /**
     * @param newExchangeExpression the newExchangeExpression to set
     */
    public void setNewExchangeExpression(ExpressionDefinition newExchangeExpression) {
        ExpressionDefinition oldValue = this.newExchangeExpression;
        this.newExchangeExpression = newExchangeExpression;
        if (!isSame(oldValue, newExchangeExpression)) {
            firePropertyChange(PROPERTY_NEWEXCHANGEEXPRESSION, oldValue, newExchangeExpression);
        }
    }

    /**
     * @return the headers
     */
    public List getHeaders() {
        return this.headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(List headers) {
        List oldValue = this.headers;
        this.headers = headers;
        if (!isSame(oldValue, headers)) {
            firePropertyChange(PROPERTY_HEADERS, oldValue, headers);
        }
    }

    /**
     * @return the executorServiceRef
     */
    public String getExecutorServiceRef() {
        return this.executorServiceRef;
    }

    /**
     * @param executorServiceRef the executorServiceRef to set
     */
    public void setExecutorServiceRef(String executorServiceRef) {
        String oldValue = this.executorServiceRef;
        this.executorServiceRef = executorServiceRef;
        if (!isSame(oldValue, executorServiceRef)) {
            firePropertyChange(PROPERTY_EXECUTORSERVICEREF, oldValue, executorServiceRef);
        }
    }

    /**
     * @return the copy
     */
    public Boolean getCopy() {
        return this.copy;
    }

    /**
     * @param copy the copy to set
     */
    public void setCopy(Boolean copy) {
        Boolean oldValue = this.copy;
        this.copy = copy;
        if (!isSame(oldValue, copy)) {
            firePropertyChange(PROPERTY_COPY, oldValue, copy);
        }
    }

    /**
     * @return the onPrepareRef
     */
    public String getOnPrepareRef() {
        return this.onPrepareRef;
    }

    /**
     * @param onPrepareRef the onPrepareRef to set
     */
    public void setOnPrepareRef(String onPrepareRef) {
        String oldValue = this.onPrepareRef;
        this.onPrepareRef = onPrepareRef;
        if (!isSame(oldValue, onPrepareRef)) {
            firePropertyChange(PROPERTY_ONPREPAREREF, oldValue, onPrepareRef);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelWireTapCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelWireTapInheritErrorHandler);
        PropertyDescriptor descUri = new TextPropertyDescriptor(PROPERTY_URI, Messages.propertyLabelWireTapUri);
        PropertyDescriptor descNewExchangeProcessorRef = new TextPropertyDescriptor(PROPERTY_NEWEXCHANGEPROCESSORREF, Messages.propertyLabelWireTapNewExchangeProcessorRef);
        PropertyDescriptor descNewExchangeExpression = new ExpressionPropertyDescriptor(PROPERTY_NEWEXCHANGEEXPRESSION, Messages.propertyLabelWireTapNewExchangeExpression);
        PropertyDescriptor descHeaders = new ListPropertyDescriptor(PROPERTY_HEADERS, Messages.propertyLabelWireTapHeaders);
        PropertyDescriptor descExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_EXECUTORSERVICEREF, Messages.propertyLabelWireTapExecutorServiceRef);
        PropertyDescriptor descCopy = new BooleanPropertyDescriptor(PROPERTY_COPY, Messages.propertyLabelWireTapCopy);
        PropertyDescriptor descOnPrepareRef = new TextPropertyDescriptor(PROPERTY_ONPREPAREREF, Messages.propertyLabelWireTapOnPrepareRef);

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_URI, descUri);
        descriptors.put(PROPERTY_NEWEXCHANGEPROCESSORREF, descNewExchangeProcessorRef);
        descriptors.put(PROPERTY_NEWEXCHANGEEXPRESSION, descNewExchangeExpression);
        descriptors.put(PROPERTY_HEADERS, descHeaders);
        descriptors.put(PROPERTY_EXECUTORSERVICEREF, descExecutorServiceRef);
        descriptors.put(PROPERTY_COPY, descCopy);
        descriptors.put(PROPERTY_ONPREPAREREF, descOnPrepareRef);
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
        if (PROPERTY_URI.equals(id)) {
            setUri(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_NEWEXCHANGEPROCESSORREF.equals(id)) {
            setNewExchangeProcessorRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_NEWEXCHANGEEXPRESSION.equals(id)) {
            setNewExchangeExpression(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_HEADERS.equals(id)) {
            setHeaders(Objects.convertTo(value, List.class));
            return;
        }
        if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
            setExecutorServiceRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_COPY.equals(id)) {
            setCopy(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_ONPREPAREREF.equals(id)) {
            setOnPrepareRef(Objects.convertTo(value, String.class));
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
        if (PROPERTY_URI.equals(id)) {
            return this.getUri();
        }
        if (PROPERTY_NEWEXCHANGEPROCESSORREF.equals(id)) {
            return this.getNewExchangeProcessorRef();
        }
        if (PROPERTY_NEWEXCHANGEEXPRESSION.equals(id)) {
            return this.getNewExchangeExpression();
        }
        if (PROPERTY_HEADERS.equals(id)) {
            return this.getHeaders();
        }
        if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
            return this.getExecutorServiceRef();
        }
        if (PROPERTY_COPY.equals(id)) {
            return this.getCopy();
        }
        if (PROPERTY_ONPREPAREREF.equals(id)) {
            return this.getOnPrepareRef();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        WireTapDefinition answer = new WireTapDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setUri(toXmlPropertyValue(PROPERTY_URI, this.getUri()));
        answer.setNewExchangeProcessorRef(toXmlPropertyValue(PROPERTY_NEWEXCHANGEPROCESSORREF, this.getNewExchangeProcessorRef()));
        Objects.setField(answer, "newExchangeExpression", toXmlPropertyValue(PROPERTY_NEWEXCHANGEEXPRESSION, this.getNewExchangeExpression()));
        answer.setHeaders(toXmlPropertyValue(PROPERTY_HEADERS, this.getHeaders()));
        answer.setExecutorServiceRef(toXmlPropertyValue(PROPERTY_EXECUTORSERVICEREF, this.getExecutorServiceRef()));
        answer.setCopy(toXmlPropertyValue(PROPERTY_COPY, this.getCopy()));
        answer.setOnPrepareRef(toXmlPropertyValue(PROPERTY_ONPREPAREREF, this.getOnPrepareRef()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return WireTapDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof WireTapDefinition) {
            WireTapDefinition node = (WireTapDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setUri(node.getUri());
            this.setNewExchangeProcessorRef(node.getNewExchangeProcessorRef());
            Objects.setField(this, "newExchangeExpression", node.getNewExchangeExpression());
            this.setHeaders(node.getHeaders());
            this.setExecutorServiceRef(node.getExecutorServiceRef());
            this.setCopy(node.getCopy());
            this.setOnPrepareRef(node.getOnPrepareRef());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof WireTapDefinition. Was " + processor.getClass().getName());
        }
    }

}
