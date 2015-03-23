/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.model.Model;

/**
 *
 */
public class MappingPlaceholder implements MappingOperation<Object, Model> {

    private Object source;
    private Model target;

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.MappingOperation#getSource()
     */
    @Override
    public Object getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.MappingOperation#getTarget()
     */
    @Override
    public Model getTarget() {
        return target;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.mapper.MappingOperation#getType()
     */
    @Override
    public MappingType getType() {
        return null;
    }

    void setSource(final Object source) {
        this.source = source;
    }

    void setTarget(final Model target) {
        this.target = target;
    }
}
