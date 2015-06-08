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
package org.jboss.tools.fuse.transformation;

import java.util.List;

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
    
    /**
     * Returns a list of indexes corresponding for the source model in this mapping.
     * The index of a non-collection field will always be null.
     * @return index list
     */
    List<Integer> getSourceIndex();
    
    /**
     * Specifies the source index for this mapping.  An index value must be supplied for
     * every model in the tree.  For example, a source model with two parent models would
     * require a list containing three index values.
     * @param indexes index list
     */
    void setSourceIndex(List<Integer> indexes);
    
    /**
     * Returns a list of indexes corresponding for the target model in this mapping.
     * The index of a non-collection field will always be null.
     * @return index list
     */
    List<Integer> getTargetIndex();
    
    /**
     * Specifies the target index for this mapping.  An index value must be supplied for
     * every model in the tree.  For example, a target model with two parent models would
     * require a list containing three index values.
     * @param indexes index list
     */
    void setTargetIndex(List<Integer> indexes);
}
