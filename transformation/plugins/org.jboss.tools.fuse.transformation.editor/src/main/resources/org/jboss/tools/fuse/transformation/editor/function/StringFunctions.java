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

import java.lang.reflect.Method;

import org.jboss.tools.fuse.transformation.editor.Function;

public class StringFunctions implements Function {

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.Function#info(java.lang.reflect.Method)
     */
    @Override
    public Info info(Method method) {
        if ("isEmpty".equals(method.getName())) return new Info() {

            @Override
            public String description() {
                return "Returns true if the field's stringified value contains no characters (i.e., its length is zero)";
            }

            @Override
            public String description(int index) {
                return "The string to be converted";
            }

            @Override
            public String name(int index) {
                return "string";
            }
        };
        if ("length".equals(method.getName())) return new Info() {

            @Override
            public String description() {
                return "Returns the number of characters in the field's stringified value";
            }

            @Override
            public String description(int index) {
                return "The string to be converted";
            }

            @Override
            public String name(int index) {
                return "string";
            }
        };
        if ("lowerCase".equals(method.getName())) return new Info() {

            @Override
            public String description() {
                return "Returns the field's stringified value with all of its alphabetic characters converted to lower case";
            }

            @Override
            public String description(int index) {
                return "The string to be converted";
            }

            @Override
            public String name(int index) {
                return "string";
            }
        };
        if ("properCase".equals(method.getName())) return new Info() {

            @Override
            public String description() {
                return "Returns the field's stringified value with its first letter upper case (if alphabetic) and all remaining alphabetic characters converted to lower case";
            }

            @Override
            public String description(int index) {
                return "The string to be converted";
            }

            @Override
            public String name(int index) {
                return "string";
            }
        };
        if ("trim".equals(method.getName())) return new Info() {

            @Override
            public String description() {
                return "Returns the field's stringified value with leading and trailing whitespace omitted";
            }

            @Override
            public String description(int index) {
                return "The string to be converted";
            }

            @Override
            public String name(int index) {
                return "string";
            }
        };
        if ("upperCase".equals(method.getName())) return new Info() {

            @Override
            public String description() {
                return "Returns the field's stringified value with all of its alphabetic characters converted to upper case";
            }

            @Override
            public String description(int index) {
                return "The string to be converted";
            }

            @Override
            public String name(int index) {
                return "string";
            }
        };
        return null;
    }

    public boolean isEmpty(String string) {
        return string.isEmpty();
    }

    public int length(String string) {
        return string.length();
    }

    public String lowerCase(String string) {
        return string.toLowerCase();
    }

    public String properCase(String string) {
        if (string.isEmpty()) return string;
        return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();
    }

    public String trim(String string) {
        return string.trim();
    }

    public String upperCase(String string) {
        return string.toUpperCase();
    }
}
