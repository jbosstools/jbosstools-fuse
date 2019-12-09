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

import java.util.List;

import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.VariableMapping;
import org.jboss.tools.fuse.transformation.core.dozer.config.Field;
import org.jboss.tools.fuse.transformation.core.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 * Dozer-specific implementation of VariableMapping.
 */
public class DozerVariableMapping extends BaseDozerMapping implements VariableMapping {

    private Variable source;
    private Model target;

    /**
     * Create a new VariableMapping.
     * 
     * @param source source variable
     * @param target target field
     */
    public DozerVariableMapping(Variable source, Model target, Mapping mapping, Field field) {
        super(mapping, field);
        this.source = source;
        this.target = target;
    }

    @Override
    public Variable getSource() {
        return source;
    }

    @Override
    public Model getTarget() {
        return target;
    }

    @Override
    public MappingType getType() {
        return MappingType.VARIABLE;
    }
    
    public static String unqualifyName(String variableName) {
        return isQualified(variableName)
                ? variableName.substring(2, variableName.length() - 1)
                : variableName;
    }
    
    public static String qualifyName(String variableName) {
        return !isQualified(variableName)
                ? "${" + variableName + "}" //$NON-NLS-1$ //$NON-NLS-2$
                : variableName;
    }
    
    public static boolean isQualified(String variableName) {
        return variableName.startsWith("${") && variableName.endsWith("}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void setVariable(Variable variable) {
        source = variable;
        // update the dozer config to reference the new variable name
        getField().setCustomConverterArgument(
                DozerVariableMapping.qualifyName(variable.getName()));
    }
    
    @Override
    public void setTargetIndex(List<Integer> indexes) {
        setFieldIndex(getField().getB(), target, getMapping().getClassB().getContent(), indexes);
    }
}
