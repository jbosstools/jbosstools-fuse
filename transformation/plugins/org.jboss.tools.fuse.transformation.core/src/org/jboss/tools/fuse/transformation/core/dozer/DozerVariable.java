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

import org.jboss.tools.fuse.transformation.core.dozer.config.Variable;

/**
 * Dozer implementation of Variable which maps directly onto a Dozer Variable.
 */
public class DozerVariable implements org.jboss.tools.fuse.transformation.core.Variable {

    private Variable variable;

    /**
     * Create a new Variable.
     *
     * @param variable dozer variable
     */
    public DozerVariable(Variable variable) {
        this.variable = variable;
    }

    @Override
    public String getValue() {
        return variable.getContent();
    }

    @Override
    public void setValue(String value) {
        variable.setContent(value);
    }

    @Override
    public String getName() {
        return variable.getName();
    }

    @Override
    public void setName(String name) {
        variable.setName(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DozerVariable)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        DozerVariable that = (DozerVariable)obj;
        return that.getVariable().equals(variable);
    }

    @Override
    public int hashCode() {
        return variable.hashCode();
    }

    @Override
    public String toString() {
        return "variable[name:" + variable.getName() + ",value:" + variable.getContent() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    Variable getVariable() {
        return variable;
    }
}
