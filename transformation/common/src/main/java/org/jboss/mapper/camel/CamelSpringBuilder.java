/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.mapper.camel;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.jboss.mapper.TransformType;
import org.jboss.mapper.camel.spring.CamelContextFactoryBean;
import org.jboss.mapper.camel.spring.CamelEndpointFactoryBean;
import org.jboss.mapper.camel.spring.DataFormat;
import org.jboss.mapper.camel.spring.DataFormatsDefinition;
import org.jboss.mapper.camel.spring.JaxbDataFormat;
import org.jboss.mapper.camel.spring.JsonDataFormat;
import org.jboss.mapper.camel.spring.JsonLibrary;
import org.jboss.mapper.camel.spring.ObjectFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * CamelConfigBuilder provides read/write access to Camel configuration used in
 * a data transformation project. This class assumes that all Camel
 * configuration is stored in a Spring application context. Any changes to Camel
 * configuration through direct methods on this class or the underlying
 * CamelContextFactoryBean config model are in-memory only and not persisted
 * until saveConfig() is called.
 */
public class CamelSpringBuilder extends CamelConfigBuilder {

    // JAXB classes for Camel config model
    private JAXBContext jaxbCtx;

    private final CamelContextFactoryBean camelContext;

    public CamelSpringBuilder(Element camelConfig) throws Exception {
        this.camelConfig = camelConfig;
        JAXBElement<CamelContextFactoryBean> ccfb = getJAXBContext()
                .createUnmarshaller().unmarshal(camelConfig, CamelContextFactoryBean.class);
        camelContext = ccfb.getValue();
    }

    /**
     * Returns the top-level object model for Camel configuration.
     * 
     * @return Camel Context configuration
     */
    public CamelContextFactoryBean getCamelContext() {
        return camelContext;
    }

    /**
     * Add a transformation to the Camel configuration. This method adds all
     * required data formats, Dozer configuration, and the camel-transform
     * endpoint definition to the Camel config.
     * 
     * @param transformId id for the transformation
     * @param dozerConfigPath path to Dozer config for transformation
     * @param source type of the source data
     * @param sourceClass name of the source model class
     * @param target type of the target data
     * @param targetClass name of the target model class
     * @throws Exception failed to create transformation
     */
    public void addTransformation(String transformId, String dozerConfigPath,
            TransformType source, String sourceClass,
            TransformType target, String targetClass) throws Exception {

        // Add data formats
        DataFormat unmarshaller = createDataFormat(source, sourceClass);
        DataFormat marshaller = createDataFormat(target, targetClass);

        // Create a transformation endpoint
        String unmarshallerId = unmarshaller != null ? unmarshaller.getId() : null;
        String marshallerId = marshaller != null ? marshaller.getId() : null;
        String endpointUri = EndpointHelper.createEndpointUri(dozerConfigPath,
                transformId, sourceClass, targetClass, unmarshallerId, marshallerId);
        CamelEndpointFactoryBean endpoint = new CamelEndpointFactoryBean();
        endpoint.setUri(endpointUri);
        endpoint.setId(transformId);
        camelContext.getEndpoint().add(endpoint);
    }

    public List<String> getTransformEndpointIds() {
        List<String> endpointIds = new LinkedList<String>();
        for (CamelEndpointFactoryBean ep : camelContext.getEndpoint()) {
            if (ep.getUri().startsWith(EndpointHelper.DOZER_SCHEME)) {
                endpointIds.add(ep.getId());
            }
        }
        return endpointIds;
    }

    public CamelEndpoint getEndpoint(String endpointId) {
        CamelEndpointFactoryBean endpoint = null;
        for (CamelEndpointFactoryBean ep : camelContext.getEndpoint()) {
            if (endpointId.equals(ep.getId())) {
                endpoint = ep;
                break;
            }
        }
        return new CamelEndpoint(endpoint);
    }

    // If the JAXB config model for CamelContext was changed, call this method
    // to marshal those changes into the DOM for the Spring application context
    protected void updateCamelContext() throws JAXBException {
        // Replace Camel Context in config DOM
        ObjectFactory of = new ObjectFactory();
        DocumentFragment frag = camelConfig.getOwnerDocument().createDocumentFragment();
        getJAXBContext().createMarshaller().marshal(of.createCamelContext(camelContext), frag);
        camelConfig.getParentNode().replaceChild(frag.getFirstChild(), camelConfig);
    }

    private DataFormat createDataFormat(TransformType type, String className) throws Exception {
        DataFormat dataFormat;

        switch (type) {
            case JSON:
                dataFormat = createJsonDataFormat();
                break;
            case XML:
                dataFormat = createJaxbDataFormat(getPackage(className));
                break;
            case JAVA:
                dataFormat = null;
                break;
            default:
                throw new Exception("Unsupported data format type: " + type);
        }

        return dataFormat;
    }

    private DataFormat createJsonDataFormat() throws Exception {
        final String id = "transform-json";

        DataFormat dataFormat = getDataFormat(id);
        if (dataFormat == null) {
            // Looks like we need to create a new one
            JsonDataFormat jdf = new JsonDataFormat();
            jdf.setLibrary(JsonLibrary.JACKSON);
            jdf.setId(id);
            getDataFormats().add(jdf);
            dataFormat = jdf;
        }
        return dataFormat;
    }

    private List<DataFormat> getDataFormats() {
        DataFormatsDefinition dfd = camelContext.getDataFormats();
        if (dfd == null) {
            dfd = new DataFormatsDefinition();
            camelContext.setDataFormats(dfd);
        }
        return dfd.getAvroOrBarcodeOrBase64();
    }

    private DataFormat getDataFormat(String id) {
        DataFormat dataFormat = null;
        for (DataFormat df : getDataFormats()) {
            if (id.equals(df.getId())) {
                dataFormat = df;
                break;
            }
        }
        return dataFormat;
    }

    private DataFormat createJaxbDataFormat(String contextPath) throws Exception {
        final String id = contextPath.replaceAll("\\.", "");
        DataFormat dataFormat = getDataFormat(id);

        if (dataFormat == null) {
            JaxbDataFormat df = new JaxbDataFormat();
            df.setContextPath(contextPath);
            df.setId(id);
            getDataFormats().add(df);
            dataFormat = df;
        }
        return dataFormat;
    }

    private synchronized JAXBContext getJAXBContext() {
        if (jaxbCtx == null) {
            try {
                jaxbCtx = JAXBContext.newInstance(ObjectFactory.class);
            } catch (final JAXBException jaxbEx) {
                throw new RuntimeException(jaxbEx);
            }
        }
        return jaxbCtx;
    }

}
