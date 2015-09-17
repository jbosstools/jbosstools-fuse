/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor;

import java.lang.reflect.Method;

/**
 * Implementations of this interface can be specified in extensions to the
 * <code>org.jboss.tools.fuse.transformation.editor.function</code> extension point to provide built-in functions that can be used
 * in transformations at runtime. Any number of functions can be provided by each implementation, but function
 * {@link Info#name(int) names} must be unique across <strong>ALL</strong> implementations (e.g., multiple functions with the same
 * name but different arguments are not allowed). These functions will be shown in the transformation editor dialog when users
 * select "Add function" from the drop-down menu for a field in the details view. <strong>Implementations must be source-compatible
 * with Java 6</strong>. Each function provided by the implementation must be public and have at least one argument, and the first
 * parameter's type must match the type of the fields to which it is applicable. In addition, implementations of
 * {@link #info(Method)} must return a unique instance of {@link Info} for each function provided. The name, description, and
 * argument descriptions returned by these instances will be shown to the user in the aforementioned "Add function" dialog. Finally,
 * the implementation's source code must be available in the distribution under the same folder structure as the class. This can
 * generally be accomplished by creating the implementation in a resources folder that is also an Eclipse source folder.
 */
public interface Function {

    /**
     * @param function one of the functions in an implementation of this interface
     * @return A unique instance of {@link Info} describing the name, description, and argument descriptions of the supplied
     *         function.
     */
    Info info(Method function);

    interface Info {

        /**
         * @return the function's description
         */
        String description();

        /**
         * @param index the index of one of the function's argument
         * @return the description of the function's argument at the supplied index
         */
        String description(int index);

        /**
         * @param index the index of one of the function's argument
         * @return the name of the function's argument at the supplied index. All names must be unique across all implementations.
         */
        String name(int index);
    }
}
