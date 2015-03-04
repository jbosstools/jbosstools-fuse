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

/**
 * This class is a dirty hack to deal with two JAXB models (blueprint and
 * spring) underneath our config layer. The idea is to not expose the different
 * package names for the blueprint and spring models to the UI. Best solution is
 * probably to ditch JAXB in a future milestone.
 */
public class CamelEndpoint {

    private org.jboss.mapper.camel.spring.CamelEndpointFactoryBean springEndpoint;
    private org.jboss.mapper.camel.blueprint.CamelEndpointFactoryBean blueprintEndpoint;

    public CamelEndpoint(Object delegate) {
        if (delegate instanceof org.jboss.mapper.camel.spring.CamelEndpointFactoryBean) {
            springEndpoint = (org.jboss.mapper.camel.spring.CamelEndpointFactoryBean) delegate;
        } else {
            blueprintEndpoint =
                    (org.jboss.mapper.camel.blueprint.CamelEndpointFactoryBean) delegate;
        }
    }

    public String getUri() {
        return isSpring() ? springEndpoint.getUri() : blueprintEndpoint.getUri();
    }

    public void setUri(String uri) {
        if (isSpring()) {
            springEndpoint.setUri(uri);
        } else {
            blueprintEndpoint.setUri(uri);
        }
    }

    public String getId() {
        return isSpring() ? springEndpoint.getId() : blueprintEndpoint.getId();
    }

    public void setId(String id) {
        if (isSpring()) {
            springEndpoint.setId(id);
        } else {
            blueprintEndpoint.setId(id);
        }
    }

    private boolean isSpring() {
        return springEndpoint != null;
    }

}
