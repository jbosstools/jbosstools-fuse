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
package org.jboss.tools.fuse.transformation.core.model.json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JsonModelGeneratorIT {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private static String JSON_INST_PATH = "xyz-order.json";
	private static String JSON_SCHEMA_PATH = "xyz-order-schema.json";

    @Test
    public void generateFromInstance() throws Exception {
		File jsonInst = getFile(JSON_INST_PATH);
        JsonModelGenerator modelGen = new JsonModelGenerator();
		File targetFolder = tmpFolder.newFolder("target");
		modelGen.generateFromInstance("XYZOrder", "jsonmodelgen.generateFromInstance", jsonInst.toURI().toURL(), targetFolder);

        // Check for generated classes
		File genDir = new File(targetFolder, "jsonmodelgen/generateFromInstance");
        Assert.assertTrue(new File(genDir, "LineItem.java").exists());
        Assert.assertTrue(new File(genDir, "XYZOrder.java").exists());
    }

    @Test
    public void generateFromSchema() throws Exception {
		File jsonSchmea = getFile(JSON_SCHEMA_PATH);
        JsonModelGenerator modelGen = new JsonModelGenerator();
		File targetFolder = tmpFolder.newFolder("target");
		modelGen.generateFromSchema("XYZOrder", "jsonmodelgen.generateFromSchema", jsonSchmea.toURI().toURL(), targetFolder);

        // Check for generated classes
		File genDir = new File(targetFolder, "jsonmodelgen/generateFromSchema");
        Assert.assertTrue(new File(genDir, "LineItem.java").exists());
        Assert.assertTrue(new File(genDir, "XYZOrder.java").exists());
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
