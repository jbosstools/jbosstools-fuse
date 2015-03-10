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
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
        } catch (JAXBException jaxbEx) {
            throw new RuntimeException(
                    "Failed to update DOM with JAXB model for camelContext", jaxbEx);
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
    public abstract void addTransformation(String transformId, String dozerConfigPath,
            TransformType source, String sourceClass,
            TransformType target, String targetClass)
            throws Exception;

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

    public abstract List<String> getTransformEndpointIds();

    public abstract CamelEndpoint getEndpoint(String endpointId);

    // If the JAXB config model for CamelContext was changed, call this method
    // to marshal those changes into the DOM for the Spring application context
    protected abstract void updateCamelContext() throws JAXBException;

    protected String getPackage(String type) {
        int idx = type.lastIndexOf('.');
        return idx > 0 ? type.substring(0, idx) : type;
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
