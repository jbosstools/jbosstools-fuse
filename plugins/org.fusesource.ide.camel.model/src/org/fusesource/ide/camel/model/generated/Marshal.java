
/**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
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
import org.apache.camel.model.MarshalDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
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
 * The Node class for Camel's MarshalDefinition
 */
public class Marshal extends AbstractNode {

	public static final String PROPERTY_REF = "Marshal.Ref";
	public static final String PROPERTY_DATAFORMATTYPE = "Marshal.DataFormatType";
	
	private String ref;
	private DataFormatDefinition dataFormatType;
	
    public Marshal() {
    }		
	
    public Marshal(MarshalDefinition definition, RouteContainer parent) {

      super(parent);
    	loadPropertiesFromCamelDefinition(definition);
    	loadChildrenFromCamelDefinition(definition);
    }


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
     */
    @Override
    public String getIconName() {
    	return "marshal.png";
    }
    
  	@Override
  	public String getDocumentationFileName() {
  		return "marshalEIP";
  	}
  	
  	@Override
  	public String getCategoryName() {
  		return "Transformation";
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


	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);
		
  		PropertyDescriptor descRef = new TextPropertyDescriptor(PROPERTY_REF, Messages.propertyLabelMarshalRef);
    
      
		PropertyDescriptor descDataFormatType = new ComplexUnionPropertyDescriptor(PROPERTY_DATAFORMATTYPE, Messages.propertyLabelMarshalDataFormatType, DataFormatDefinition.class, new UnionTypeValue[]{
		        new UnionTypeValue("avro", org.apache.camel.model.dataformat.AvroDataFormat.class),
		        new UnionTypeValue("beanio", org.apache.camel.model.dataformat.BeanioDataFormat.class),
		        new UnionTypeValue("bindy", org.apache.camel.model.dataformat.BindyDataFormat.class),
		        new UnionTypeValue("c24io", org.apache.camel.model.dataformat.C24IODataFormat.class),
		        new UnionTypeValue("castor", org.apache.camel.model.dataformat.CastorDataFormat.class),
		        new UnionTypeValue("crypto", org.apache.camel.model.dataformat.CryptoDataFormat.class),
		        new UnionTypeValue("csv", org.apache.camel.model.dataformat.CsvDataFormat.class),
		        new UnionTypeValue("custom", org.apache.camel.model.dataformat.CustomDataFormat.class),
		        new UnionTypeValue("flatpack", org.apache.camel.model.dataformat.FlatpackDataFormat.class),
		        new UnionTypeValue("gzip", org.apache.camel.model.dataformat.GzipDataFormat.class),
		        new UnionTypeValue("hl7", org.apache.camel.model.dataformat.HL7DataFormat.class),
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
		        new UnionTypeValue("xmlBeans", org.apache.camel.model.dataformat.XMLBeansDataFormat.class),
		        new UnionTypeValue("xmljson", org.apache.camel.model.dataformat.XmlJsonDataFormat.class),
		        new UnionTypeValue("xstream", org.apache.camel.model.dataformat.XStreamDataFormat.class),
		        new UnionTypeValue("pgp", org.apache.camel.model.dataformat.PGPDataFormat.class),
		        new UnionTypeValue("zip", org.apache.camel.model.dataformat.ZipDataFormat.class),
		  		});
  	  		descriptors.put(PROPERTY_REF, descRef);
		descriptors.put(PROPERTY_DATAFORMATTYPE, descDataFormatType);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_REF.equals(id)) {
			setRef(Objects.convertTo(value, String.class));
		}		else if (PROPERTY_DATAFORMATTYPE.equals(id)) {
			setDataFormatType(Objects.convertTo(value, DataFormatDefinition.class));
		}    else {
			super.setPropertyValue(id, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_REF.equals(id)) {
			return this.getRef();
		}		else if (PROPERTY_DATAFORMATTYPE.equals(id)) {
			return this.getDataFormatType();
		}    else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		MarshalDefinition answer = new MarshalDefinition();
    answer.setRef(toXmlPropertyValue(PROPERTY_REF, this.getRef()));
    answer.setDataFormatType(toXmlPropertyValue(PROPERTY_DATAFORMATTYPE, this.getDataFormatType()));
        super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> getCamelDefinitionClass() {
	  return MarshalDefinition.class;
  }

	@SuppressWarnings("rawtypes")
	@Override
	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
    super.loadPropertiesFromCamelDefinition(processor);
    
    if (processor instanceof MarshalDefinition) {
      MarshalDefinition node = (MarshalDefinition) processor;
      this.setRef(node.getRef());
      this.setDataFormatType(node.getDataFormatType());
    } else {
      throw new IllegalArgumentException("ProcessorDefinition not an instanceof MarshalDefinition. Was " + processor.getClass().getName());
    }
	}
}
 
      
