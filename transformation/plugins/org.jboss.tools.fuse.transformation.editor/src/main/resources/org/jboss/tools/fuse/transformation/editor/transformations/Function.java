/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.transformations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Formatter;

/**
 * This annotation can be used to specify methods that will be available as transformations for mappings. Classes containing
 * transformation methods can be specified in extensions to the <code>org.jboss.tools.fuse.transformation.editor.function</code>
 * extension point.
 * <p>
 * During design-time, all transformations applicable to the type of source property being transformed will be shown in the
 * transformation editor's <strong>Transformation</strong> dialog when users select that option from the drop-down menu for a
 * property in the details view. When a transformation name is selected, the parameter names and descriptions specified in this
 * annotation will be shown.
 * </p>
 * <h3>Transformation Requirements</h3>
 * <ul>
 * <li>Any number of transformations can be provided by an implementation, but transformation names must be unique across ALL
 * implementations for the same source property type. This means method overloading is only supported for transformations that
 * transform source properties with different types. For example, given an existing transformation, <code>foo(String source)</code>,
 * the transformation <code>foo(int source)</code> may also exist, but not <code>foo(String source, String arg1)</code></li>
 * <li>Transformation parameters may have default values, making them effectively optional.</li>
 * <li>Transformations must be public</li>
 * <li>Transformations must have at least one parameter, and the first parameter's type must match the type of applicable source
 * properties</li>
 * <li>The source code for a transformation must be available during runtime under the same folder structure as the class.</li>
 * <li>Transformation parameter types must be a primitive wrapper class (e.g., {@link Integer}) or {@link String}</li>
 * <li>Transformation parameter types must not be variable length</li>
 * </ul>
 * See {@link StringFunctions StringFunctions} for an example of transformation implementations and the use of this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Function {

    String description();

    Arg[] args() default {};

    /**
     * Optional display format that determines how transformations are shown in the detail view of the transformation editor. The
     * format should be specified as described by {@link Formatter}. Each format specifier will be replaced by an editable widget in
     * the editor, so nothing about the format specifiers is relevant other than a required index. The indexes should match the
     * number of arguments in the transformation, meaning format specifier 1 will always refer to the source property value.
     */
    String format() default "";

    public @interface Arg {

        String name();

        String description();

        String defaultValue() default "";

        boolean hideDefault() default false;
    }
}
