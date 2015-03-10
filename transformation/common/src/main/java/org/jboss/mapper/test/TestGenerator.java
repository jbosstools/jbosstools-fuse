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
package org.jboss.mapper.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Quick and dirty test class generator for a transformation endpoint. Not
 * optimized or very configurable at this point.
 */
public class TestGenerator {

    private static final String TEST_TEMPLATE = "templates/TestTemplate.java";
    private static final String ENDPOINT_KEY = "\\$\\[transform-id\\]";
    private static final String PACKAGE_KEY = "\\$\\[package-name\\]";
    private static final String CLASSNAME_KEY = "\\$\\[test-name\\]";

    /**
     * Creates a test class for a transformation endpoint with the specified
     * transformid.
     * 
     * @param transformId id of a transform endpoint to test
     * @param packageName package name for the generated test class
     * @param className class name for the generated test class
     * @param targetPath the directory where you want the class to go
     * @throws Exception failed to create the test class
     */
    public static void createTransformTest(String transformId,
            String packageName,
            String className,
            File targetPath) throws Exception {

        String template = readTemplate()
                .replaceAll(ENDPOINT_KEY, transformId)
                .replaceAll(PACKAGE_KEY, packageName)
                .replaceAll(CLASSNAME_KEY, className);
        
        File testPath = new File(targetPath, createTestPath(packageName, className));
        // Check for collision on the path given for the test class
        if (testPath.exists()) {
            throw new Exception("Unable to create test, target already exists: "
                    + testPath.getAbsolutePath());
        }
        // Create any needed parent directories for the package name
        if (!testPath.getParentFile().exists()) {
            testPath.getParentFile().mkdirs();
        }

        writeTemplate(template, testPath);
    }

    private static String readTemplate() throws Exception {
        InputStreamReader reader = null;
        StringBuilder templateStr = new StringBuilder();
        try {
            InputStream is =
                    TestGenerator.class.getClassLoader().getResourceAsStream(TEST_TEMPLATE);
            reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            char[] buf = new char[1024];
            int count = 0;
            while ((count = reader.read(buf)) != -1) {
                templateStr.append(buf, 0, count);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return templateStr.toString();
    }

    private static void writeTemplate(String content, File target) throws Exception {
        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(target));
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static String createTestPath(String packageName, String className) {
        StringBuilder path = new StringBuilder();
        if (packageName != null && packageName.length() > 0) {
            path.append(packageName.replaceAll("\\.", "/"));
            path.append("/");
        }
        path.append(className + ".java");
        return path.toString();
    }
}
