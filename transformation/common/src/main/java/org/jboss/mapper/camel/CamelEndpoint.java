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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.core.xml.AbstractCamelEndpointFactoryBean;
import org.apache.camel.spring.CamelEndpointFactoryBean;

/**
 * This class is a dirty hack to deal with two JAXB models (blueprint and
 * spring) underneath our config layer. The idea is to not expose the different
 * package names for the blueprint and spring models to the UI. Best solution is
 * probably to ditch JAXB in a future milestone.
 */
public class CamelEndpoint {

    private AbstractCamelEndpointFactoryBean delegate;

    public CamelEndpoint(AbstractCamelEndpointFactoryBean delegate) {
        this.delegate = delegate;
    }
    
    public static List<CamelEndpoint> fromList(List<? extends AbstractCamelEndpointFactoryBean> endpoints) {
        List<CamelEndpoint> endpointList = new LinkedList<CamelEndpoint>();
        for (AbstractCamelEndpointFactoryBean endpoint : endpoints) {
            endpointList.add(new CamelEndpoint(endpoint));
        }
        // Prevent direct updates to this list because they won't be reflected in the
        // the underlying model
        return Collections.unmodifiableList(endpointList);
    }

    public String getUri() {
        return delegate.getUri();
    }

    public void setUri(String uri) {
        delegate.setUri(uri);
    }

    public String getId() {
        return delegate.getId();
    }

    public void setId(String id) {
        delegate.setId(id);
    }

    public AbstractCamelEndpointFactoryBean getDelegate() {
        return delegate;
    }
    
    public CamelEndpointFactoryBean asSpringEndpoint() {
        CamelEndpointFactoryBean endpoint;
        if (delegate instanceof CamelEndpointFactoryBean) {
            endpoint = (CamelEndpointFactoryBean)delegate;
        } else {
            endpoint = new CamelEndpointFactoryBean();
            endpoint.setId(delegate.getId());
            endpoint.setUri(delegate.getUri());
        }
        return endpoint;
    }
}
