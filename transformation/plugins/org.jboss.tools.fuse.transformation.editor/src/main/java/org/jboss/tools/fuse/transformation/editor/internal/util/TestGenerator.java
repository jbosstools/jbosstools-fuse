/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Quick and dirty test class generator for a transformation endpoint. Not
 * optimized or very configurable at this point.
 */
public class TestGenerator {

    private static final String SPRING_TEST_TEMPLATE = "templates/spring-test.template";
    private static final String BLUEPRINT_TEST_TEMPLATE = "templates/blueprint-test.template";
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
     * @param isSpring boolean flag indicating this is spring or blueprint
     * @throws Exception failed to create the test class
     */
    public static String createTransformTestText(String transformId,
            String packageName,
            String className,
            boolean isSpring) throws Exception {

        String template = null;
        if (isSpring) {
            template = readTemplate(SPRING_TEST_TEMPLATE)
                    .replaceAll(ENDPOINT_KEY, transformId)
                    .replaceAll(PACKAGE_KEY, packageName)
                    .replaceAll(CLASSNAME_KEY, className);
        } else {
            template = readTemplate(BLUEPRINT_TEST_TEMPLATE)
                    .replaceAll(ENDPOINT_KEY, transformId)
                    .replaceAll(PACKAGE_KEY, packageName)
                    .replaceAll(CLASSNAME_KEY, className);
        }
        // in the case of a default package, we don't have the package name
        if (packageName.isEmpty()) {
            template = template.replaceFirst("package ;", "");
        }
        
        return template;
    }

    private static String readTemplate(String template) throws Exception {
        InputStreamReader reader = null;
        StringBuilder templateStr = new StringBuilder();
        try {
            InputStream is =
                    TestGenerator.class.getClassLoader().getResourceAsStream(template);
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
}
