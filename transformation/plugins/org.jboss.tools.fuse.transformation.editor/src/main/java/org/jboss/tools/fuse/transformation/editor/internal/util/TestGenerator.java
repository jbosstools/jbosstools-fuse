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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Quick and dirty test class generator for a transformation endpoint. Not
 * optimized or very configurable at this point.
 */
public class TestGenerator {

    private static final String SPRING_TEST_TEMPLATE = "templates/spring-test.template"; //$NON-NLS-1$
    private static final String BLUEPRINT_TEST_TEMPLATE = "templates/blueprint-test.template"; //$NON-NLS-1$
    private static final String ENDPOINT_KEY = "\\$\\[transform-id\\]"; //$NON-NLS-1$
    private static final String PACKAGE_KEY = "\\$\\[package-name\\]"; //$NON-NLS-1$
    private static final String CLASSNAME_KEY = "\\$\\[test-name\\]"; //$NON-NLS-1$
    private static final String FILENAME_KEY = "\\$\\[camel-name\\]"; //$NON-NLS-1$
    
    private TestGenerator() {
    	// allow only static access
    }

    /**
     * Creates a test class for a transformation endpoint with the specified transform ID.
     *
     * @param transformId id of a transform endpoint to test
     * @param packageName package name for the generated test class
     * @param className class name for the generated test class
     * @param fileName
     * @param isSpring boolean flag indicating this is spring or blueprint
     * @return The contents of the test class
     * @throws IOException failed to create the test class
     */
    public static String createTransformTestText(String transformId,
            String packageName,
            String className,
            String fileName,
            boolean isSpring) throws IOException {

        String template;
        if (isSpring) {
            template = readTemplate(SPRING_TEST_TEMPLATE)
                    .replaceAll(ENDPOINT_KEY, transformId)
                    .replaceAll(PACKAGE_KEY, packageName)
                    .replaceAll(CLASSNAME_KEY, className)
                    .replaceAll(FILENAME_KEY, fileName);
        } else {
            template = readTemplate(BLUEPRINT_TEST_TEMPLATE)
                    .replaceAll(ENDPOINT_KEY, transformId)
                    .replaceAll(PACKAGE_KEY, packageName)
                    .replaceAll(CLASSNAME_KEY, className)
                    .replaceAll(FILENAME_KEY, fileName);
        }
        // in the case of a default package, we don't have the package name
        if (packageName.isEmpty()) {
            template = template.replaceFirst("package ;", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return template;
    }

    private static String readTemplate(String template) throws IOException {
        StringBuilder templateStr = new StringBuilder();
        try (InputStreamReader reader =
                 new InputStreamReader(TestGenerator.class.getClassLoader().getResourceAsStream(template),
                                       StandardCharsets.UTF_8)) {
            char[] buf = new char[1024];
            int count = 0;
            while ((count = reader.read(buf)) != -1) {
                templateStr.append(buf, 0, count);
            }
        }
        return templateStr.toString();
    }
}
