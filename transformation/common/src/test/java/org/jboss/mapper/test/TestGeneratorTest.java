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
package org.jboss.mapper.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class TestGeneratorTest {
    
    private static final String TARGET_PATH = "target/generated";

    @Test
    public void testCreation() throws Exception {
        File generatedFile = new File(TARGET_PATH + "/org/foo/BarTest.java");
        try {
            TestGenerator.createTransformTest(
                    "abc123", "org.foo", "BarTest", new File(TARGET_PATH));
            Assert.assertTrue(generatedFile.exists());
        } finally {
            generatedFile.delete();
        }
    }
}
