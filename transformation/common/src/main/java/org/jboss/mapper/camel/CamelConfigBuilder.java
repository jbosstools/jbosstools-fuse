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

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.core.xml.AbstractCamelEndpointFactoryBean;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.jboss.mapper.TransformType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * CamelConfigBuilder provides read/write access to Camel configuration used in
 * a data transformation project. This class assumes that all Camel
 * configuration is stored in a Spring application context. Any changes to Camel
 * configuration through direct methods on this class or the underlying
 * CamelContextFactoryBean config model are in-memory only and not persisted
 * until saveConfig() is called.
 */
public abstract class CamelConfigBuilder {

    public static String SPRING_NS = "http://camel.apache.org/schema/spring";
    public static String BLUEPRINT_NS = "http://camel.apache.org/schema/blueprint";

    protected Element camelConfig;
    private JAXBContext jaxbCtx;

    /**
     * Load a Spring application context containing Camel configuration from the
     * specified file.
     * 
     * @param file reference to a file containing Camel configuration
     * @return a config builder loaded with camel configuration
     * @throws Exception failed to read/parse configuration file
     */
    public static CamelConfigBuilder loadConfig(final File file) throws Exception {
        Element parent = loadCamelConfig(file);
        Element camelEle = getChildElement(parent, SPRING_NS, "camelContext");
        if (camelEle != null) {
            return new CamelSpringBuilder(camelEle);
        } else {
            return new CamelBlueprintBuilder(getChildElement(parent, BLUEPRINT_NS, "camelContext"));
        }
    }
    
    /**
     * Due to https://issues.apache.org/jira/browse/CAMEL-8498, we cannot set
     * endpoints on CamelContextFactoryBean directly.  Use reflection for now
     * until this issue is resolved upstream.
     */
    public static void setEndpoints(Object camelContext, List<? extends AbstractCamelEndpointFactoryBean> endpoints) {
        try {
            Field endpointsField = camelContext.getClass().getDeclaredField("endpoints");
            endpointsField.setAccessible(true);
            endpointsField.set(camelContext, endpoints);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to access endpoints field in CamelContextFactoryBean", ex);
        }
    }

    /**
     * Returns the root element in the Spring application context which contains
     * bean definitions as well as the Camel Context configuration.
     * 
     * @return the <beans> element from the application context
     */
    public Element getConfiguration() {
        // It's possible that there were updates to the JAXB config model
        // without a corresponding saveConfig(), so refresh the camelContext
        // in our DOM
        try {
            updateCamelContext();
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to update DOM with JAXB model for camelContext", ex);
        }
        return camelConfig;
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
        DataFormatDefinition unmarshaller = createDataFormat(source, sourceClass);
        DataFormatDefinition marshaller = createDataFormat(target, targetClass);

        // Create a transformation endpoint
        String unmarshallerId = unmarshaller != null ? unmarshaller.getId() : null;
        String marshallerId = marshaller != null ? marshaller.getId() : null;
        String endpointUri = EndpointHelper.createEndpointUri(dozerConfigPath,
                transformId, sourceClass, targetClass, unmarshallerId, marshallerId);
        addEndpoint(transformId, endpointUri);
    }

    /**
     * Persists the in-memory state of Camel configuration to the specified
     * output stream.
     * 
     * @param output stream to write config to
     * @throws Exception failed to save configuration
     */
    public void saveConfig(final OutputStream output) throws Exception {
        updateCamelContext();
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        tf.transform(new DOMSource(camelConfig.getOwnerDocument()), new StreamResult(output));
    }
    
    public CamelEndpoint getEndpoint(String endpointId) {
        AbstractCamelEndpointFactoryBean endpoint = null;
        for (AbstractCamelEndpointFactoryBean ep : getEndpoints()) {
            if (endpointId.equals(ep.getId())) {
                endpoint = ep;
                break;
            }
        }
        return new CamelEndpoint(endpoint);
    }
    
    public List<String> getTransformEndpointIds() {
        List<String> endpointIds = new LinkedList<String>();
        for (AbstractCamelEndpointFactoryBean ep : getEndpoints()) {
            if (ep.getUri().startsWith(EndpointHelper.DOZER_SCHEME)) {
                endpointIds.add(ep.getId());
            }
        }
        return endpointIds;
    }
    
    protected abstract List<? extends AbstractCamelEndpointFactoryBean> getEndpoints();
    
    protected abstract List<DataFormatDefinition> getDataFormats();

    // If the JAXB config model for CamelContext was changed, call this method
    // to marshal those changes into the DOM for the Spring application context
    protected abstract void updateCamelContext() throws Exception;
    
    protected abstract AbstractCamelEndpointFactoryBean addEndpoint(String id, String uri);
    
    protected abstract Class<?> getCamelContextType();
    
    protected String getPackage(String type) {
        int idx = type.lastIndexOf('.');
        return idx > 0 ? type.substring(0, idx) : type;
    }
    
    protected DataFormatDefinition createDataFormat(TransformType type, String className) throws Exception {
        DataFormatDefinition dataFormat;

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
    
    protected DataFormatDefinition createJsonDataFormat() throws Exception {
        final String id = "transform-json";

        DataFormatDefinition dataFormat = getDataFormat(id);
        if (dataFormat == null) {
            // Looks like we need to create a new one
            JsonDataFormat jdf = new JsonDataFormat();
            jdf.setLibrary(JsonLibrary.Jackson);
            jdf.setId(id);
            getDataFormats().add(jdf);
            dataFormat = jdf;
        }
        return dataFormat;
    }

    
    protected DataFormatDefinition createJaxbDataFormat(String contextPath) throws Exception {
        final String id = contextPath.replaceAll("\\.", "");
        DataFormatDefinition dataFormat = getDataFormat(id);

        if (dataFormat == null) {
            JaxbDataFormat df = new JaxbDataFormat();
            df.setContextPath(contextPath);
            df.setId(id);
            getDataFormats().add(df);
            dataFormat = df;
        }
        return dataFormat;
    }
    
    protected DataFormatDefinition getDataFormat(String id) {
        DataFormatDefinition dataFormat = null;
        for (DataFormatDefinition df : getDataFormats()) {
            if (id.equals(df.getId())) {
                dataFormat = df;
                break;
            }
        }
        return dataFormat;
    }
    
    protected synchronized JAXBContext getJAXBContext() {
        if (jaxbCtx == null) {
            try {
                jaxbCtx = JAXBContext.newInstance(getCamelContextType());
            } catch (final JAXBException jaxbEx) {
                throw new RuntimeException(jaxbEx);
            }
        }
        return jaxbCtx;
    }    

    private static Element loadCamelConfig(File configFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);
        Document doc = dbf.newDocumentBuilder().parse(configFile);
        return doc.getDocumentElement();
    }

    // Returns the first instance of a child element that matches the specified name
    private static Element getChildElement(Element parent, String childNS, String childName) {
        Element child = null;
        NodeList children = parent.getElementsByTagNameNS(childNS, childName);
        if (children.getLength() > 0) {
            child = (Element) children.item(0);
        }
        return child;
    }

}
