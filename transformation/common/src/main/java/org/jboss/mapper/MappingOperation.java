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
package org.jboss.mapper;

/**
 * Generic representation of a mapping operation.
 * 
 * @param <S> the type of the mapping source
 * @param <T> the type of the mapping target
 */
public interface MappingOperation<S, T> {

    /**
     * Returns the source for this mapping.
     * 
     * @return mapping source
     */
    S getSource();

    /**
     * Returns the target for this mapping.
     * 
     * @return mapping target
     */
    T getTarget();

    /**
     * Indicates the type of the mapping operation.
     * 
     * @return mapping type
     */
    MappingType getType();
}
