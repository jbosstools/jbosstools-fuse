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

public class BooleanFunctions {

    @Function(description = "Transforms the source Boolean value to a double value.",
              format = "Transform %1$s to a double value")
    public Double toDouble(Boolean source) {
        return source ? 1.0 : 0.0;
    }

    @Function(description = "Transforms the source Boolean value to a floating-point value.",
              format = "Transform %1$s to a floating-point value")
    public Float toFloat(Boolean source) {
        return source ? 1f : 0f;
    }

    @Function(description = "Transforms the source Boolean value to an integer.",
              format = "Transform %1$s to an integer")
    public Integer toInteger(Boolean source) {
        return source ? 1 : 0;
    }

    @Function(description = "Transforms the source Boolean value to a long value.",
              format = "Transform %1$s to long value")
    public Long toLong(Boolean source) {
        return source ? 1L : 0L;
    }

    @Function(description = "Transforms the source Boolean value to a short value.",
              format = "Transform %1$s to short value")
    public Short toShort(Boolean source) {
        return source ? (short)1 : 0;
    }
}
