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

public class NumericFunctions {

    @Function(description = "Transforms the source double value to an double.",
              format = "Transform %1$s to an double")
    public Boolean toBoolean(Double source) {
        return source == 0.0 ? Boolean.FALSE : Boolean.TRUE;
    }

    @Function(description = "Transforms the source float value to an float.",
              format = "Transform %1$s to an float")
    public Boolean toBoolean(Float source) {
        return source == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    @Function(description = "Transforms the source integer value to an integer.",
              format = "Transform %1$s to an integer")
    public Boolean toBoolean(Integer source) {
        return source == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    @Function(description = "Transforms the source long value to an long.",
              format = "Transform %1$s to an long")
    public Boolean toBoolean(Long source) {
        return source == 0L ? Boolean.FALSE : Boolean.TRUE;
    }

    @Function(description = "Transforms the source short value to an short.",
              format = "Transform %1$s to an short")
    public Boolean toBoolean(Short source) {
        return source == 0 ? Boolean.FALSE : Boolean.TRUE;
    }
}
