/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Quick and dirty test class generator for a transformation endpoint. Not
 * optimized or very configurable at this point.
 */
public class TestGenerator {

	private static final String TEST_TEMPLATE = "TestTemplate.java.template"; //$NON-NLS-1$
    private static final String ENDPOINT_KEY = "\\$\\[transform-id\\]"; //$NON-NLS-1$
    private static final String PACKAGE_KEY = "\\$\\[package-name\\]"; //$NON-NLS-1$
    private static final String CLASSNAME_KEY = "\\$\\[test-name\\]"; //$NON-NLS-1$

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
            throw new Exception("Unable to create test, target already exists: " //$NON-NLS-1$
                    + testPath.getAbsolutePath());
        }
        // Create any needed parent directories for the package name
        if (!testPath.getParentFile().exists()) {
            testPath.getParentFile().mkdirs();
        }

        writeTemplate(template, testPath);
    }

    private static String readTemplate() throws Exception {
        StringBuilder templateStr = new StringBuilder();
		try (InputStreamReader reader = new InputStreamReader(TestGenerator.class.getResourceAsStream(TEST_TEMPLATE), StandardCharsets.UTF_8)) {
			char[] buf = new char[1024];
			int count = 0;
			while ((count = reader.read(buf)) != -1) {
				templateStr.append(buf, 0, count);
			}
        }
        return templateStr.toString();
    }

    private static void writeTemplate(String content, File target) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target))) {
            writer.write(content);
        }
    }

    private static String createTestPath(String packageName, String className) {
        StringBuilder path = new StringBuilder();
        if (packageName != null && packageName.length() > 0) {
            path.append(packageName.replaceAll("\\.", "/")); //$NON-NLS-1$ //$NON-NLS-2$
            path.append("/"); //$NON-NLS-1$
        }
        path.append(className + ".java"); //$NON-NLS-1$
        return path.toString();
    }
}
