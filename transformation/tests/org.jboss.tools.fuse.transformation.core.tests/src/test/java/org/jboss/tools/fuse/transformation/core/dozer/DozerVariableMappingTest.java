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
package org.jboss.tools.fuse.transformation.core.dozer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.tools.fuse.transformation.core.Variable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DozerVariableMappingTest {
    
    private final String unqualified = "myvar";
    private final String qualified = "${myvar}";
    
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
    
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
		DozerMapperConfiguration dozerConfig = DozerMapperConfiguration.loadConfig(getFile("fieldAndVariableMapping.xml"));
        // Remove all variables
        for (Variable var : dozerConfig.getVariables()) {
            dozerConfig.removeVariable(var);
        }
        // Save the config and then load it again
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        dozerConfig.saveConfig(bos);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(bos.toByteArray())));
        // There shouldn't be a variables element since we removed all variables
        Assert.assertEquals(0, doc.getElementsByTagName("variables").getLength());
        
    }
    
    @Test
    public void removeOneVariable() throws Exception {
		DozerMapperConfiguration dozerConfig = DozerMapperConfiguration.loadConfig(getFile("fieldAndVariableMapping.xml"));
        Variable var = dozerConfig.getVariable("VAR2");
        dozerConfig.removeVariable(var);
        Assert.assertEquals(1, dozerConfig.getVariables().size());
    }

	private File getFile(String fileName) {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(fileName, ".xml", tmpFolder.getRoot());
			Files.copy(this.getClass().getResourceAsStream(fileName), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpFile;
	}
}

