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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.blueprint.CamelContextFactoryBean;
import org.apache.camel.blueprint.CamelEndpointFactoryBean;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.DataFormatsDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * CamelConfigBuilder provides read/write access to Camel configuration used in
 * a data transformation project. This class assumes that all Camel
 * configuration is stored in a Spring application context. Any changes to Camel
 * configuration through direct methods on this class or the underlying
 * CamelContextFactoryBean config model are in-memory only and not persisted
 * until saveConfig() is called.
 */
public class CamelBlueprintBuilder extends CamelConfigBuilder {
    
    private final CamelContextFactoryBean camelContext;
    private final DocumentBuilderFactory docBuilder;

    public CamelBlueprintBuilder(Element camelConfig) throws Exception {
        this.camelConfig = camelConfig;
        JAXBElement<CamelContextFactoryBean> ccfb = getJAXBContext()
                .createUnmarshaller().unmarshal(camelConfig, CamelContextFactoryBean.class);
        camelContext = ccfb.getValue();

        docBuilder = DocumentBuilderFactory.newInstance();
        docBuilder.setNamespaceAware(true);
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
     *  Slightly different implementation than Spring as we need to replace Spring 
     *  namespaces with blueprint namespaces during marshalling.  It's a hack, but it's 
     *  better than maintaining two parallel sets of our own generated model classes.
     */
    protected void updateCamelContext() throws Exception {
        // Marshal our current config as a string and replace namespaces
        StringWriter sw = new StringWriter();
        getJAXBContext().createMarshaller().marshal(camelContext, sw);
        String newXml = sw.toString().replaceAll(SPRING_NS, BLUEPRINT_NS);
        
        // Parse the updated XML and replace the current config element
        Document doc  = docBuilder.newDocumentBuilder().parse(
                new InputSource(new StringReader(newXml)));
        Node parent = camelConfig.getParentNode();
        Node newConfig = camelConfig.getOwnerDocument().importNode(doc.getDocumentElement(), true);
        parent.replaceChild(newConfig, camelConfig);
        camelConfig = (Element)newConfig;
    }

    @Override
    public List<DataFormatDefinition> getDataFormats() {
        DataFormatsDefinition dfd = camelContext.getDataFormats();
        if (dfd == null) {
            dfd = new DataFormatsDefinition();
            camelContext.setDataFormats(dfd);
        }
        
        if (dfd.getDataFormats() == null) {
            dfd.setDataFormats(new ArrayList<DataFormatDefinition>());
        }
        return dfd.getDataFormats();
    }

    @Override
    public List<CamelEndpoint> getEndpoints() {
        return CamelEndpoint.fromList(getBlueprintEndpoints());
    }

    @Override
    protected CamelEndpoint addEndpoint(String id, String uri) {
        CamelEndpointFactoryBean endpoint = new CamelEndpointFactoryBean();
        endpoint.setId(id);
        endpoint.setUri(uri);
        getBlueprintEndpoints().add(endpoint);
        return new CamelEndpoint(endpoint);
    }
    
    protected Class<?> getCamelContextType() {
        return CamelContextFactoryBean.class;
    }
    
    private List<CamelEndpointFactoryBean> getBlueprintEndpoints() {
        if (camelContext.getEndpoints() == null) {
            camelContext.setEndpoints(new ArrayList<CamelEndpointFactoryBean>());
        }
        return camelContext.getEndpoints();
    }
}
