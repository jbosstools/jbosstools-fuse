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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.DataFormatsDefinition;
import org.apache.camel.spring.CamelContextFactoryBean;
import org.apache.camel.spring.CamelEndpointFactoryBean;
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

    private static final QName SPRING_CONTEXT = new QName(SPRING_NS, "camelContext");

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

    // If the JAXB config model for CamelContext was changed, call this method
    // to marshal those changes into the DOM for the Spring application context
    @Override
    protected void updateCamelContext() throws JAXBException {
        // Replace Camel Context in config DOM
        JAXBElement<CamelContextFactoryBean> contextElement = new JAXBElement<CamelContextFactoryBean>(
                SPRING_CONTEXT, CamelContextFactoryBean.class, null, camelContext);
        DocumentFragment frag = camelConfig.getOwnerDocument().createDocumentFragment();
        getJAXBContext().createMarshaller().marshal(contextElement, frag);
        camelConfig.getParentNode().replaceChild(frag.getFirstChild(), camelConfig);
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
        return CamelEndpoint.fromList(getSpringEndpoints());
    }

    @Override
    protected CamelEndpoint addEndpoint(String id, String uri) {
        CamelEndpointFactoryBean endpoint = new CamelEndpointFactoryBean();
        endpoint.setId(id);
        endpoint.setUri(uri);
        getSpringEndpoints().add(endpoint);
        return new CamelEndpoint(endpoint);
    }
    
    protected Class<?> getCamelContextType() {
        return CamelContextFactoryBean.class;
    }
    
    private List<CamelEndpointFactoryBean> getSpringEndpoints() {
        if (camelContext.getEndpoints() == null) {
            setEndpoints(camelContext, new ArrayList<CamelEndpointFactoryBean>());
        }
        return camelContext.getEndpoints();
    }
}
