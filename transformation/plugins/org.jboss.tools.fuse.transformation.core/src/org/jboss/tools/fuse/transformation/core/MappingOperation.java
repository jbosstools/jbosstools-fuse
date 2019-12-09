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
package org.jboss.tools.fuse.transformation.core;

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
    
    /**
     * Specifies the date format for the source mapping. Only used when mapping to a field of a
     * java.util.Date type. 
     * @param format String to use for the formatting - look at Date formats in Java for examples 
     */
    void setSourceDateFormat(String format);
    
    /**
     * Retrieves the current date format for the source mapping or null.
     * @return currently specified date format
     */
    String getSourceDateFormat();

    /**
     * Specifies the date format for the target mapping. Only used when mapping to a field of a
     * java.util.Date type. 
     * @param format String to use for the formatting - look at Date formats in Java for examples 
     */
    void setTargetDateFormat(String format);
    
    /**
     * Retrieves the current date format for the target mapping or null.
     * @return currently specified date format
     */
    String getTargetDateFormat();
}
