/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to specify methods that will be available as functions within transformations. Classes containing
 * function methods can be specified in extensions to the <code>org.jboss.tools.fuse.transformation.editor.function</code> extension
 * point.
 * <p>
 * During design-time, all function names applicable to the type of source field being transformed will be shown in the
 * {@link org.jboss.tools.fuse.transformation.editor.TransformationEditor transformation editor's} <strong>Function</strong> dialog
 * when users select that option from the drop-down menu for a field in the details view. When a function name is selected, the
 * argument names and descriptions specified in this annotation will be shown.
 * </p>
 * <h3>Function Requirements</h3>
 * <ul>
 * <li>Any number of functions can be provided by an implementation, but function names must be unique across ALL implementations
 * for the same source field type. This means method overloading is only supported for functions that transform source fields with
 * different types. For example, given an existing function, <code>foo(String source)</code>, the function
 * <code>foo(int source)</code> may also exist, but not <code>foo(String source, String arg1)</code></li>
 * <li>Function arguments may have default values, making them effectively optional.</li>
 * <li>Functions must be public</li>
 * <li>Functions must have at least one argument, and the first argument's type must match the type of applicable source fields</li>
 * <li>The source code for a function must be available during runtime under the same folder structure as the class. This can
 * generally be accomplished by creating the implementation in a resources folder that is also a compiled folder.</li>
 * <li>The Type of each function arguments must be a primitive wrapper class (e.g., {@link Integer}) or {@link String}</li>
 * <li>Function argument types must not be variable length</li>
 * </ul>
 * See {@link org.jboss.tools.fuse.transformation.editor.function.StringFunctions StringFunctions} for an example of function
 * implementations and the use of this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Function {

    String description();

    Arg[] args() default {};

    /**
     * Optional display format that determines how functions are shown in the detail view of the
     * {@link org.jboss.tools.fuse.transformation.editor.TransformationEditor transformation editor}. The format should be specified
     * as described by {@link java.util.Formatter}. Each format specifier will be replaced by an editable widget in the editor, so
     * nothing about the format specifiers is relevant other than a required index. The indexes should match the number of arguments
     * in the function, meaning format specifier 1 will always refer to the source field value.
     */
    String format() default "";

    public @interface Arg {

        String name();

        String description();

        String defaultValue() default "";

        boolean hideDefault() default false;
    }
}
