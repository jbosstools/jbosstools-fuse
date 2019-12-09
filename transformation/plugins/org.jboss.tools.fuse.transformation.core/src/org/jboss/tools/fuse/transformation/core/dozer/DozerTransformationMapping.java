/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.dozer;

import static org.jboss.tools.fuse.transformation.core.dozer.TransformationArgumentHelper.EMPTY_STRING_ARRAY;
import static org.jboss.tools.fuse.transformation.core.dozer.TransformationArgumentHelper.emptyForNull;
import static org.jboss.tools.fuse.transformation.core.dozer.TransformationArgumentHelper.getArgumentPart;
import static org.jboss.tools.fuse.transformation.core.dozer.TransformationArgumentHelper.getArgumentParts;

import java.util.Arrays;

import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.TransformationMapping;

/**
 * Dozer implementation of a transformation mapping.
 */
public class DozerTransformationMapping extends DozerFieldMapping implements TransformationMapping {

    private static final String SEP = ","; //$NON-NLS-1$

    /**
     * Create a new DozerTransformationMapping.
     *
     * @param fieldMapping field mapping being customized
     */
    public DozerTransformationMapping(DozerFieldMapping fieldMapping) {

        super(fieldMapping.getSource(),
              fieldMapping.getTarget(),
              fieldMapping.getMapping(),
              fieldMapping.getField());
    }

    @Override
    public void addTransformationArgument(String type,
                                          String value) {
        String[] parts = getArgumentParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : ""); //$NON-NLS-1$
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : ""); //$NON-NLS-1$
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        builder.append(SEP);
        builder.append(emptyForNull(type));
        builder.append("="); //$NON-NLS-1$
        builder.append(emptyForNull(value));
        getField().setCustomConverterArgument(builder.toString());
    }

    @Override
    public void addTransformationArguments(String... arguments) {
        String[] parts = getArgumentParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : ""); //$NON-NLS-1$
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : ""); //$NON-NLS-1$
        for (String arg : arguments) {
            builder.append(SEP);
            builder.append(arg);
        }
        getField().setCustomConverterArgument(builder.toString());
    }

    @Override
    public String[] getTransformationArguments() {
        String[] parts = getArgumentParts(getField(), SEP);
        return parts.length < 2 ? EMPTY_STRING_ARRAY : Arrays.copyOfRange(parts, 2, parts.length);
    }

    @Override
    public String getTransformationClass() {
        return getArgumentPart(getField(), SEP, 0);
    }

    @Override
    public String getTransformationName() {
        return getArgumentPart(getField(), SEP, 1);
    }

    @Override
    public MappingType getType() {
        return MappingType.TRANSFORMATION;
    }

    @Override
    public void setTransformationClass(String name) {
        String[] parts = getArgumentParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(emptyForNull(name));
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : ""); //$NON-NLS-1$
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        getField().setCustomConverterArgument(builder.toString());
    }

    @Override
    public void setTransformationName(String name) {
        String[] parts = getArgumentParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : ""); //$NON-NLS-1$
        builder.append(SEP);
        builder.append(emptyForNull(name));
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        getField().setCustomConverterArgument(builder.toString());
    }
}
