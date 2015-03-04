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

import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.dozer.config.Field;
import org.jboss.mapper.dozer.config.Mapping;
import org.jboss.mapper.model.Model;

/**
 * Dozer-specific implementation of FieldMapping.
 */
public class DozerFieldMapping extends BaseDozerMapping implements FieldMapping {

    private Model source;
    private Model target;

    /**
     * Create a new FieldMapping.
     * 
     * @param source source model field
     * @param target target model field
     */
    public DozerFieldMapping(Model source, Model target, Mapping mapping, Field field) {
        super(mapping, field);
        this.source = source;
        this.target = target;
    }

    @Override
    public Model getSource() {
        return source;
    }

    @Override
    public Model getTarget() {
        return target;
    }

    @Override
    public MappingType getType() {
        return MappingType.FIELD;
    }
}
