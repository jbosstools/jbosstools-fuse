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

import java.util.List;

import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.VariableMapping;
import org.jboss.tools.fuse.transformation.dozer.config.Field;
import org.jboss.tools.fuse.transformation.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.model.Model;

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
                ? "${" + variableName + "}"
                : variableName;
    }
    
    public static boolean isQualified(String variableName) {
        return variableName.startsWith("${") && variableName.endsWith("}");
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
