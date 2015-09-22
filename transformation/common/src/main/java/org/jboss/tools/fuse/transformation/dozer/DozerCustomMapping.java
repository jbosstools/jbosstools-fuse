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

import static org.jboss.tools.fuse.transformation.dozer.CustomParameterHelper.EMPTY_STRING_ARRAY;
import static org.jboss.tools.fuse.transformation.dozer.CustomParameterHelper.emptyForNull;
import static org.jboss.tools.fuse.transformation.dozer.CustomParameterHelper.getParameterPart;
import static org.jboss.tools.fuse.transformation.dozer.CustomParameterHelper.getParameterParts;
import java.util.Arrays;
import org.jboss.tools.fuse.transformation.CustomMapping;
import org.jboss.tools.fuse.transformation.MappingType;

/**
 * Dozer implementation of a custom mapping.
 */
public class DozerCustomMapping extends DozerFieldMapping implements CustomMapping {

    private static final String SEP = ",";

    /**
     * Create a new DozerCustomMapping.
     *
     * @param fieldMapping field mapping being customized
     */
    public DozerCustomMapping(DozerFieldMapping fieldMapping) {

        super(fieldMapping.getSource(),
              fieldMapping.getTarget(),
              fieldMapping.getMapping(),
              fieldMapping.getField());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.CustomMapping#addFunctionArgument(java.lang.String, java.lang.String)
     */
    @Override
    public void addFunctionArgument(String type,
                                    String value) {
        String[] parts = getParameterParts(getField(), SEP);
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
        getField().setCustomConverterParam(builder.toString());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.CustomMapping#addFunctionArguments(java.lang.String[])
     */
    @Override
    public void addFunctionArguments(String... arguments) {
        String[] parts = getParameterParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : "");
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : "");
        for (String arg : arguments) {
            builder.append(SEP);
            builder.append(arg);
        }
        getField().setCustomConverterParam(builder.toString());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.CustomMapping#getFunctionArguments()
     */
    @Override
    public String[] getFunctionArguments() {
        String[] parts = getParameterParts(getField(), SEP);
        return parts.length < 2 ? EMPTY_STRING_ARRAY : Arrays.copyOfRange(parts, 2, parts.length);
    }

    @Override
    public String getFunctionClass() {
        return getParameterPart(getField(), SEP, 0);
    }

    @Override
    public String getFunctionName() {
        return getParameterPart(getField(), SEP, 1);
    }

    @Override
    public MappingType getType() {
        return MappingType.CUSTOM;
    }

    @Override
    public void setFunctionClass(String name) {
        String[] parts = getParameterParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(emptyForNull(name));
        builder.append(SEP);
        builder.append(parts.length > 1 ? parts[1] : "");
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        getField().setCustomConverterParam(builder.toString());
    }

    @Override
    public void setFunctionName(String name) {
        String[] parts = getParameterParts(getField(), SEP);
        StringBuilder builder = new StringBuilder(parts.length > 0 ? parts[0] : "");
        builder.append(SEP);
        builder.append(emptyForNull(name));
        for (int ndx = 2; ndx < parts.length; ndx++) {
            builder.append(SEP);
            builder.append(parts[ndx]);
        }
        getField().setCustomConverterParam(builder.toString());
    }
}
