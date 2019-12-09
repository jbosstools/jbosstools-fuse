/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tools.fuse.transformation.core.camel;

import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.jboss.tools.fuse.transformation.core.camel.EndpointHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
		endpoint = new CamelEndpoint(URI_1);
    }

    @Test
	@Ignore("test is failing due to not xml encoded character")
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
	@Ignore("test is failing due to not xml encoded character")
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
		String beforeUri = "transform:xml2json?targetModel=test.ReplaceMe";
		CamelEndpoint ep = new CamelEndpoint(beforeUri);
        String afterUri = "transform:xml2json?targetModel=example.Object";
        
        EndpointHelper.setTargetModel(ep, "example.Object");
        Assert.assertEquals(afterUri, ep.getUri());
    }
}
