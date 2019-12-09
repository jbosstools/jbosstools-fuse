/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import org.jboss.tools.fuse.transformation.core.TransformType;

/**
 *
 */
public enum ModelType {

    /**
     *
     */
    CLASS("Java Class", TransformType.JAVA), //$NON-NLS-1$

    /**
     *
     */
    JAVA("Java Source", TransformType.JAVA), //$NON-NLS-1$

    /**
     *
     */
    JSON("JSON", TransformType.JSON), //$NON-NLS-1$

    /**
     *
     */
    JSON_SCHEMA("JSON Schema", TransformType.JSON), //$NON-NLS-1$

    /**
     *
     */
    XML("XML", TransformType.XML), //$NON-NLS-1$

    /**
     *
     */
    XSD("XSD", TransformType.XML), //$NON-NLS-1$
    
    /**
     * Type where a dataformat and model classes already exist.
     */
    OTHER("OTHER", TransformType.OTHER); //$NON-NLS-1$
    

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
