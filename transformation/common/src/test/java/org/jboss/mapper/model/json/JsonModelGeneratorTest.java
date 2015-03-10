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
package org.jboss.mapper.model.json;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class JsonModelGeneratorTest {
    
    private static String JSON_INST_PATH =
            "src/test/resources/org/jboss/mapper/model/json/xyz-order.json";
    
    private static String JSON_SCHEMA_PATH =
            "src/test/resources/org/jboss/mapper/model/json/xyz-order-schema.json";

    @Test
    public void generateFromInstance() throws Exception {
        File jsonInst = new File(JSON_INST_PATH);
        JsonModelGenerator modelGen = new JsonModelGenerator();
        modelGen.generateFromInstance("XYZOrder", 
                "jsonmodelgen.generateFromInstance", 
                jsonInst.toURI().toURL(), new File("target"));
        
        // Check for generated classes
        File genDir = new File("target/jsonmodelgen/generateFromInstance");
        Assert.assertTrue(new File(genDir, "LineItem.java").exists());
        Assert.assertTrue(new File(genDir, "XYZOrder.java").exists());
    }
    
    @Test
    public void generateFromSchema() throws Exception {
        File jsonSchmea = new File(JSON_SCHEMA_PATH);
        JsonModelGenerator modelGen = new JsonModelGenerator();
        modelGen.generateFromSchema("XYZOrder", 
                "jsonmodelgen.generateFromSchema", 
                jsonSchmea.toURI().toURL(), new File("target"));
        
        // Check for generated classes
        File genDir = new File("target/jsonmodelgen/generateFromSchema");
        Assert.assertTrue(new File(genDir, "LineItem.java").exists());
        Assert.assertTrue(new File(genDir, "XYZOrder.java").exists());
    }
}
