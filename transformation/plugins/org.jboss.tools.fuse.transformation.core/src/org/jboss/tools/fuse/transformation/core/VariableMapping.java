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

import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 * A VariableMapping represents a mapping where the source is a variable and 
 * the target is a model field.
 */
public interface VariableMapping extends MappingOperation<Variable, Model> {

    /**
     * Updates the variable used by this mapping.
     * 
     * @param variable new variable to use
     */
    void setVariable(Variable variable);
}
