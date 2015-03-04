/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.wizards;

import org.jboss.mapper.TransformType;

/**
 *
 */
public enum ModelType {

    /**
     *
     */
    CLASS("Java Class", TransformType.JAVA),

    /**
     *
     */
    JAVA("Java Source", TransformType.JAVA),

    /**
     *
     */
    JSON("JSON", TransformType.JSON),

    /**
     *
     */
    JSON_SCHEMA("JSON Schema", TransformType.JSON),

    /**
     *
     */
    XML("XML", TransformType.XML),

    /**
     *
     */
    XSD("XSD", TransformType.XML);

    final String text;

    /**
     *
     */
    public final TransformType transformType;

    private ModelType(final String text,
            final TransformType transformType) {
        this.text = text;
        this.transformType = transformType;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
