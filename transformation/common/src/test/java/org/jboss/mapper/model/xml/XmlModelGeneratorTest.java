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
package org.jboss.mapper.model.xml;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class XmlModelGeneratorTest {
    
    private static String XML_INST_PATH =
            "src/test/resources/org/jboss/mapper/model/xml/abc-order.xml";
    private static String XML_SCHEMA_GEN_PATH = "target/abc-order.xsd";

    @Test
    public void generateFromInstance() throws Exception {
        File xmlInst = new File(XML_INST_PATH);
        File genSchema = new File(XML_SCHEMA_GEN_PATH);
        XmlModelGenerator modelGen = new XmlModelGenerator();
        modelGen.generateFromInstance(
                xmlInst, genSchema, "test.generateFromInstance",
                new File("target"));
        
        // Check for generated schema
        Assert.assertTrue(genSchema.exists());
        // Check for generated classes
        File genDir = new File("target/test/generateFromInstance");
        Assert.assertTrue(new File(genDir, "ObjectFactory.java").exists());
        Assert.assertTrue(new File(genDir, "ABCOrder.java").exists());
    }
}
