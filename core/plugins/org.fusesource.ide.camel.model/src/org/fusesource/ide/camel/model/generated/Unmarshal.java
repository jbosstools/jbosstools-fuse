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

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.UnmarshalDefinition;
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
 * The Node class from Camel's UnmarshalDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Unmarshal extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "Unmarshal.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "Unmarshal.InheritErrorHandler";
    public static final String PROPERTY_REF = "Unmarshal.Ref";
    public static final String PROPERTY_DATAFORMATTYPE = "Unmarshal.DataFormatType";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private String ref;
    private DataFormatDefinition dataFormatType;

    public Unmarshal() {
    }

    public Unmarshal(UnmarshalDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "unmarshal.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "unmarshalNode";
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
     * @return the dataFormatType
     */
    public DataFormatDefinition getDataFormatType() {
        return this.dataFormatType;
    }

    /**
     * @param dataFormatType the dataFormatType to set
     */
    public void setDataFormatType(DataFormatDefinition dataFormatType) {
        DataFormatDefinition oldValue = this.dataFormatType;
        this.dataFormatType = dataFormatType;
        if (!isSame(oldValue, dataFormatType)) {
            firePropertyChange(PROPERTY_DATAFORMATTYPE, oldValue, dataFormatType);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelUnmarshalCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelUnmarshalInheritErrorHandler);
        PropertyDescriptor descRef = new TextPropertyDescriptor(PROPERTY_REF, Messages.propertyLabelUnmarshalRef);
        PropertyDescriptor descDataFormatType = new ComplexUnionPropertyDescriptor(PROPERTY_DATAFORMATTYPE, Messages.propertyLabelUnmarshalDataFormatType, DataFormatDefinition.class, new UnionTypeValue[] {
                new UnionTypeValue("avro", org.apache.camel.model.dataformat.AvroDataFormat.class),
                new UnionTypeValue("base64", org.apache.camel.model.dataformat.Base64DataFormat.class),
                new UnionTypeValue("beanio", org.apache.camel.model.dataformat.BeanioDataFormat.class),
                new UnionTypeValue("bindy", org.apache.camel.model.dataformat.BindyDataFormat.class),
                new UnionTypeValue("castor", org.apache.camel.model.dataformat.CastorDataFormat.class),
                new UnionTypeValue("crypto", org.apache.camel.model.dataformat.CryptoDataFormat.class),
                new UnionTypeValue("csv", org.apache.camel.model.dataformat.CsvDataFormat.class),
                new UnionTypeValue("custom", org.apache.camel.model.dataformat.CustomDataFormat.class),
                new UnionTypeValue("flatpack", org.apache.camel.model.dataformat.FlatpackDataFormat.class),
                new UnionTypeValue("gzip", org.apache.camel.model.dataformat.GzipDataFormat.class),
                new UnionTypeValue("hl7", org.apache.camel.model.dataformat.HL7DataFormat.class),
                new UnionTypeValue("ical", org.apache.camel.model.dataformat.IcalDataFormat.class),
                new UnionTypeValue("jaxb", org.apache.camel.model.dataformat.JaxbDataFormat.class),
                new UnionTypeValue("jibx", org.apache.camel.model.dataformat.JibxDataFormat.class),
                new UnionTypeValue("json", org.apache.camel.model.dataformat.JsonDataFormat.class),
                new UnionTypeValue("protobuf", org.apache.camel.model.dataformat.ProtobufDataFormat.class),
                new UnionTypeValue("rss", org.apache.camel.model.dataformat.RssDataFormat.class),
                new UnionTypeValue("secureXML", org.apache.camel.model.dataformat.XMLSecurityDataFormat.class),
                new UnionTypeValue("serialization", org.apache.camel.model.dataformat.SerializationDataFormat.class),
                new UnionTypeValue("soapjaxb", org.apache.camel.model.dataformat.SoapJaxbDataFormat.class),
                new UnionTypeValue("string", org.apache.camel.model.dataformat.StringDataFormat.class),
                new UnionTypeValue("syslog", org.apache.camel.model.dataformat.SyslogDataFormat.class),
                new UnionTypeValue("tidyMarkup", org.apache.camel.model.dataformat.TidyMarkupDataFormat.class),
                new UnionTypeValue("univocity-csv", org.apache.camel.model.dataformat.UniVocityCsvDataFormat.class),
                new UnionTypeValue("univocity-fixed", org.apache.camel.model.dataformat.UniVocityFixedWidthDataFormat.class),
                new UnionTypeValue("univocity-tsv", org.apache.camel.model.dataformat.UniVocityTsvDataFormat.class),
                new UnionTypeValue("xmlBeans", org.apache.camel.model.dataformat.XMLBeansDataFormat.class),
                new UnionTypeValue("xmljson", org.apache.camel.model.dataformat.XmlJsonDataFormat.class),
                new UnionTypeValue("xmlrpc", org.apache.camel.model.dataformat.XmlRpcDataFormat.class),
                new UnionTypeValue("xstream", org.apache.camel.model.dataformat.XStreamDataFormat.class),
                new UnionTypeValue("pgp", org.apache.camel.model.dataformat.PGPDataFormat.class),
                new UnionTypeValue("zip", org.apache.camel.model.dataformat.ZipDataFormat.class),
                new UnionTypeValue("zipFile", org.apache.camel.model.dataformat.ZipFileDataFormat.class),
        });

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_REF, descRef);
        descriptors.put(PROPERTY_DATAFORMATTYPE, descDataFormatType);
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
        if (PROPERTY_DATAFORMATTYPE.equals(id)) {
            setDataFormatType(Objects.convertTo(value, DataFormatDefinition.class));
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
        if (PROPERTY_DATAFORMATTYPE.equals(id)) {
            return this.getDataFormatType();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        UnmarshalDefinition answer = new UnmarshalDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        answer.setRef(toXmlPropertyValue(PROPERTY_REF, this.getRef()));
        answer.setDataFormatType(toXmlPropertyValue(PROPERTY_DATAFORMATTYPE, this.getDataFormatType()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return UnmarshalDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof UnmarshalDefinition) {
            UnmarshalDefinition node = (UnmarshalDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            this.setRef(node.getRef());
            this.setDataFormatType(node.getDataFormatType());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof UnmarshalDefinition. Was " + processor.getClass().getName());
        }
    }

}
