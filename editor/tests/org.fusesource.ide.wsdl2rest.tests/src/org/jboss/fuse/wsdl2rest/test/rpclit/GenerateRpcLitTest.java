package org.jboss.fuse.wsdl2rest.test.rpclit;
/*
 * Copyright (c) 2008 SL_OpenSource Consortium
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.MethodInfo;
import org.jboss.fuse.wsdl2rest.impl.Main;
import org.jboss.fuse.wsdl2rest.impl.Wsdl2Rest;
import org.junit.Assert;
import org.junit.Test;


public class GenerateRpcLitTest {

    static final String WSDL_LOCATION = "resources/rpclit/Address.wsdl";
    static final String OUTPUT_PATH = "target/generated-sources/wsdl2rest";

    @Test
    public void testGenerate() throws Exception {

        File wsdlFile = new File(WSDL_LOCATION);
        Path outpath = new File(OUTPUT_PATH).toPath();
        
        Wsdl2Rest tool = new Wsdl2Rest(wsdlFile.toURI().toURL(), outpath);
        tool.setTargetContext(Paths.get("rpclit-camel-context.xml"));
        tool.setTargetAddress(new URL("http://localhost:8080/rpclit"));
        tool.setTargetBean(AddressBean.class.getName());
        
        List<EndpointInfo> clazzDefs = tool.process();
        Assert.assertEquals(1, clazzDefs.size());
        EndpointInfo clazzDef = clazzDefs.get(0);
        Assert.assertEquals("org.jboss.fuse.wsdl2rest.test.rpclit", clazzDef.getPackageName());
        Assert.assertEquals("Address", clazzDef.getClassName());

        List<MethodInfo> methods = clazzDef.getMethods();
        Assert.assertEquals(5, methods.size());
    }

//    @Test
//    public void testMain() throws Exception {
//        
//        String[] args = new String[] {"--wsdl=file:" + WSDL_LOCATION, "--out=" + OUTPUT_PATH};
//        List<EndpointInfo> clazzDefs = new Main().mainInternal(args);
//
//        Assert.assertEquals(1, clazzDefs.size());
//        EndpointInfo clazzDef = clazzDefs.get(0);
//        Assert.assertEquals("org.jboss.fuse.wsdl2rest.test.rpclit", clazzDef.getPackageName());
//        Assert.assertEquals("Address", clazzDef.getClassName());
//
//        List<MethodInfo> methods = clazzDef.getMethods();
//        Assert.assertEquals(5, methods.size());
//    }
}