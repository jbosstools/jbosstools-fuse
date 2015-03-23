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
package org.jboss.mapper.dozer;

import static org.jboss.mapper.dozer.CustomParameterHelper.emptyForNull;
import static org.jboss.mapper.dozer.CustomParameterHelper.getParameterPart;

import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.MappingType;

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

    @Override
    public String getMappingClass() {
        return getParameterPart(getField(), SEP, 0);
    }

    @Override
    public String getMappingOperation() {
        return getParameterPart(getField(), SEP, 1);
    }

    @Override
    public MappingType getType() {
        return MappingType.CUSTOM;
    }

    @Override
    public void setMappingOperation(String operationName) {
        getField().setCustomConverterParam(
                emptyForNull(getMappingClass()) + SEP + emptyForNull(operationName));
    }

    @Override
    public void setMappingClass(String className) {
        String param = emptyForNull(className);
        if (getMappingOperation() != null) {
            param += SEP + getMappingOperation();
        }
        getField().setCustomConverterParam(param);
    }
}
