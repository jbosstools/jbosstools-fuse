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
public interface TransformationMapping extends FieldMapping {

    /**
     * Adds an argument to the transformation used for this custom mapping in the form <code>&lt;type>=&lt;value></code>
     */
    void addTransformationArgument(String type,
                                   String value);

    /**
     * Adds arguments to the transformation used for this custom mapping, where each supplied argument must be in the form
     * <code>&lt;type>=&lt;value></code>
     */
    void addTransformationArguments(String... arguments);

    /**
     * Returns the transformation arguments as an array of strings in the form <code>&lt;type>=&lt;value></code>. Note, the first
     * transformation argument, the source field value, is not referenceable, so the first string refers to the first argument
     * <em>after</em> the source field argument.
     */
    String[] getTransformationArguments();

    /**
     * Returns the name of the transformation class used for this custom mapping.
     *
     * @return the transformation class name
     * @see #setTransformationClass(String)
     */
    String getTransformationClass();

    /**
     * Returns the name of the transformation in the {@link #getTransformationClass() transformation class} used for this custom
     * mapping.
     *
     * @return the transformation name
     * @see #setTransformationName(String)
     */
    String getTransformationName();

    /**
     * Set the name of the {@link #getTransformationClass() transformation class} used for this custom mapping.
     *
     * @param name
     *        the transformation class name
     */
    void setTransformationClass(String name);

    /**
     * Set the name of the {@link #getTransformationName() transformation} used for this custom mapping.
     *
     * @param name
     *        the transformation name
     */
    void setTransformationName(String name);
}
