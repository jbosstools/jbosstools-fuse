/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.tools.fuse.transformation.dozer;

import static org.jboss.tools.fuse.transformation.dozer.TransformationArgumentHelper.EMPTY_STRING_ARRAY;
import static org.jboss.tools.fuse.transformation.dozer.TransformationArgumentHelper.emptyForNull;
import static org.jboss.tools.fuse.transformation.dozer.TransformationArgumentHelper.getArgumentPart;
import static org.jboss.tools.fuse.transformation.dozer.TransformationArgumentHelper.getArgumentParts;
import java.util.Arrays;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.TransformationMapping;

/**
 * Dozer implementation of a transformation mapping.
 */
public class DozerTransformationMapping extends DozerFieldMapping implements TransformationMapping {

    private static final String SEP = ",";

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
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : "");
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : "");
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        builder.append(SEP);
        builder.append(emptyForNull(type));
        builder.append("=");
        builder.append(emptyForNull(value));
        getField().setCustomConverterArgument(builder.toString());
    }

    @Override
    public void addTransformationArguments(String... arguments) {
        String[] parts = getArgumentParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : "");
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : "");
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
        builder.append(parts.length > 1 ? parts[1] : "");
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        getField().setCustomConverterArgument(builder.toString());
    }

    @Override
    public void setTransformationName(String name) {
        String[] parts = getArgumentParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : "");
        builder.append(SEP);
        builder.append(emptyForNull(name));
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        getField().setCustomConverterArgument(builder.toString());
    }
}
