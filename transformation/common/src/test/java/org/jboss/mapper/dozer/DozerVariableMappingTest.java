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
package org.jboss.mapper.dozer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.mapper.Variable;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DozerVariableMappingTest {
    
    private final String unqualified = "myvar";
    private final String qualified = "${myvar}";
    
    private static final File CONFIG_ROOT = 
            new File("target/test-classes/org/jboss/mapper/dozer");
    
    @Test
    public void qualifyVariableName() {
        Assert.assertEquals(qualified, DozerVariableMapping.qualifyName(unqualified));
        // make sure we don't qualify twice
        Assert.assertEquals(qualified, DozerVariableMapping.qualifyName(qualified));
    }
    
    @Test
    public void unqualifyVariableName() {
        Assert.assertEquals(unqualified, DozerVariableMapping.unqualifyName(qualified));
        // unqalify on a non-qualifed name should be a NOP
        Assert.assertEquals(unqualified, DozerVariableMapping.unqualifyName(unqualified));
    }
    
    @Test
    public void removeVariablesWhenEmpty() throws Exception {
        DozerMapperConfiguration dozerConfig = DozerMapperConfiguration.loadConfig(
                new File(CONFIG_ROOT, "fieldAndVariableMapping.xml"));
        // Remove all variables
        for (Variable var : dozerConfig.getVariables()) {
            dozerConfig.removeVariable(var);
        }
        // Save the config and then load it again
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        dozerConfig.saveConfig(bos);
        Document doc = XMLUnit.buildTestDocument(
                new InputSource(new ByteArrayInputStream(bos.toByteArray())));
        // There shouldn't be a variables element since we removed all variables
        Assert.assertEquals(0, doc.getElementsByTagName("variables").getLength());
        
    }
}

