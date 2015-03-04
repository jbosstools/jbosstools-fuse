/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.mapper.camel;

import org.jboss.mapper.camel.spring.CamelEndpointFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EndpointHelperTest {
    
    private static final String URI_1 = 
            "transform:xml2json?sourceModel=xml.ABCOrder&amp;"
            + "targetModel=json.XYZOrder&amp;"
            + "marshalId=transform-json&amp;"
            + "unmarshalId=xml&amp;"
            + "dozerConfigPath=dozerBeanMapping.xml";
    private CamelEndpoint endpoint;
    
    @Before
    public void setUp() {
        endpoint = new CamelEndpoint(new CamelEndpointFactoryBean());
        endpoint.setUri(URI_1);
    }

    @Test
    public void setSourceModel() {
        String refUri = "transform:xml2json?sourceModel=new.MyOrder&amp;"
                + "targetModel=json.XYZOrder&amp;"
                + "marshalId=transform-json&amp;"
                + "unmarshalId=xml&amp;"
                + "dozerConfigPath=dozerBeanMapping.xml";
        EndpointHelper.setSourceModel(endpoint, "new.MyOrder");
        Assert.assertEquals(refUri, endpoint.getUri());
    }
    
    @Test
    public void setTargetModel() {
        String refUri = "transform:xml2json?sourceModel=xml.ABCOrder&amp;"
                + "targetModel=new.MyOrder&amp;"
                + "marshalId=transform-json&amp;"
                + "unmarshalId=xml&amp;"
                + "dozerConfigPath=dozerBeanMapping.xml";
        EndpointHelper.setTargetModel(endpoint, "new.MyOrder");
        Assert.assertEquals(refUri, endpoint.getUri());
    }
    
    @Test
    public void replaceEndpointParameter() {
        CamelEndpoint ep = new CamelEndpoint(new CamelEndpointFactoryBean());
        String beforeUri = "transform:xml2json?targetModel=test.ReplaceMe";
        String afterUri = "transform:xml2json?targetModel=example.Object";
        ep.setUri(beforeUri);
        
        EndpointHelper.setTargetModel(ep, "example.Object");
        Assert.assertEquals(afterUri, ep.getUri());
    }
}
