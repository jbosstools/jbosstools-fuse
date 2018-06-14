/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 *
 */
public class MappingPlaceholder implements MappingOperation<Object, Model> {

    private Object source;
    private List<Integer> sourceIndexes;
    private Model target;
    private List<Integer> targetIndexes;
    private String sourceDateFormat;
    private String targetDateFormat;

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public String getSourceDateFormat() {
        return sourceDateFormat;
    }

    @Override
    public List<Integer> getSourceIndex() {
        if (sourceIndexes != null)
            return new ArrayList<>(sourceIndexes);
        return null;
    }

    @Override
    public Model getTarget() {
        return target;
    }

    @Override
    public String getTargetDateFormat() {
        return targetDateFormat;
    }

    @Override
    public List<Integer> getTargetIndex() {
        if (targetIndexes != null)
            return new ArrayList<>(targetIndexes);
        return null;
    }

    @Override
    public MappingType getType() {
        return null;
    }

    void setSource(final Object source) {
        this.source = source;
    }

    @Override
    public void setSourceDateFormat(String format) {
        sourceDateFormat = format;
    }

    @Override
    public void setSourceIndex(List<Integer> indexes) {
        sourceIndexes = indexes;
    }

    void setTarget(final Model target) {
        this.target = target;
    }

    @Override
    public void setTargetDateFormat(String format) {
        targetDateFormat = format;
    }

    @Override
    public void setTargetIndex(List<Integer> indexes) {
        targetIndexes = indexes;
    }
}
