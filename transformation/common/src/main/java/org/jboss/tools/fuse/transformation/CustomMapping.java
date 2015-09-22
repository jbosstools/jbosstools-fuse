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
package org.jboss.tools.fuse.transformation;

/**
 * Custom mappings are field mappings with a user-supplied class which is used to customize the mapping from source to target.
 */
public interface CustomMapping extends FieldMapping {

    /**
     * Adds an argument to the function used for this custom mapping in the form <code>&lt;type>=&lt;value></code>
     */
    void addFunctionArgument(String type,
                             String value);

    /**
     * Adds arguments to the function used for this custom mapping, where each supplied argument must be in the form
     * <code>&lt;type>=&lt;value></code>
     */
    void addFunctionArguments(String... arguments);

    /**
     * Returns the function arguments as an array of strings in the form <code>&lt;type>=&lt;value></code>. Note, the first function
     * argument, the source field value, is not referenceable, so the first string refers to the first argument <em>after</em> the
     * source field argument.
     */
    String[] getFunctionArguments();

    /**
     * Returns the name of the function class used for this custom mapping.
     *
     * @return the function class name
     * @see #setFunctionClass(String)
     */
    String getFunctionClass();

    /**
     * Returns the name of the function in the {@link #getFunctionClass() function class} used for this custom mapping.
     *
     * @return the function name
     * @see #setFunctionName(String)
     */
    String getFunctionName();

    /**
     * Set the name of the {@link #getFunctionClass() function class} used for this custom mapping.
     *
     * @param name
     *            the function class name
     */
    void setFunctionClass(String name);

    /**
     * Set the name of the {@link #getFunctionName() function} used for this custom mapping.
     *
     * @param name
     *            the function name
     */
    void setFunctionName(String name);
}
