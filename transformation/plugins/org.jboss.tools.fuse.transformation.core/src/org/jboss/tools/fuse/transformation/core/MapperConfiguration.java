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

import java.io.OutputStream;
import java.util.List;

import org.jboss.tools.fuse.transformation.core.model.Model;

/**
 * Abstraction over specific mapping framework implementations. Tooling should use this contract vs. interacting directly with the
 * underlying mapping framework.
 */
public interface MapperConfiguration {

    /**
     * Add a class mapping between the fromClass and toClass. Honestly, this method is sort of Dozer-specific and probably shouldn't
     * be in this interface.
     *
     * @param fromClass
     *        source class name
     * @param toClass
     *        target class name
     */
    void addClassMapping(String fromClass,
                         String toClass);

    /**
     * Add a variable definition to the mapping configuration. If an existing mapping exists with the same variable name, the
     * variable value is updated instead of adding a new variable definition.
     *
     * @param name
     *        variable name
     * @param value
     *        variable value
     * @return reference to the new Variable
     */
    Variable addVariable(String name,
                         String value);

    /**
     * Returns the mapping, if defined, between the specified source and target fields.
     *
     * @param source
     *        source field
     * @param target
     *        target field
     * @return mapping instance or null if no mapping exists between the two fields.
     */
    MappingOperation<?, ?> getMapping(Model source,
                                      Model target);

    /**
     * Get a list of all mappings in the mapper configuration.
     *
     * @return list of mappings
     */
    List<MappingOperation<?, ?>> getMappings();

    /**
     * Get all mappings that include the specified model as a source.
     *
     * @param source
     *        source model
     * @return list of mappings
     */
    List<MappingOperation<?, ?>> getMappingsForSource(Model source);

    /**
     * Get all mappings that include the specified model as a target.
     *
     * @param target
     *        target model
     * @return list of mappings
     */
    List<MappingOperation<?, ?>> getMappingsForTarget(Model target);

    /**
     * Returns the source model for the mapping.
     *
     * @return source model
     */
    Model getSourceModel();

    /**
     * Returns the target model for the mapping.
     *
     * @return target model
     */
    Model getTargetModel();

    /**
     * Retrieve a variable by name.
     *
     * @param variableName
     *        name of the variable
     * @return variable reference or null if the variable is not defined
     */
    Variable getVariable(String variableName);

    /**
     * Get the list of variables used as the source for mappings.
     *
     * @return list of variables
     */
    List<Variable> getVariables();

    /**
     * Map an expression to a target field.
     *
     * @param expression
     *        expression language
     * @param expression
     *        expression text
     * @param target
     *        target field
     * @return mapping created
     */
    ExpressionMapping mapExpression(String language,
                                    String expression,
                                    Model target);

    /**
     * Map an expression to a target using an index for the target field.
     *
     * @param expression
     *        expression language
     * @param expression
     *        expression text
     * @param target
     *        target field
     * @param targetIndex
     *        index for target field
     * @return mapping created
     */
    ExpressionMapping mapExpression(String language,
                                    String expression,
                                    Model target,
                                    List<Integer> targetIndex);

    /**
     * Map a source field to a target field.
     *
     * @param source
     *        model for the source field
     * @param target
     *        model for the target field
     * @return mapping created
     */
    FieldMapping mapField(Model source,
                          Model target);

    /**
     * Map a source field to a target field using indexes.
     *
     * @param source
     *        model for the source field
     * @param target
     *        model for the target field
     * @param sourceIndex
     *        index for source field
     * @param targetIndex
     *        index for target field
     * @return mapping created
     */
    FieldMapping mapField(Model source,
                          Model target,
                          List<Integer> sourceIndex,
                          List<Integer> targetIndex);

    /**
     * Map a variable to a target field.
     *
     * @param variable
     *        source variable
     * @param target
     *        target field
     * @return mapping created
     */
    VariableMapping mapVariable(Variable variable,
                                Model target);

    /**
     * Map a variable to a target using an index for the target field.
     *
     * @param variable
     *        source variable
     * @param target
     *        target field
     * @param targetIndex
     *        index for target field
     * @return mapping created
     */
    VariableMapping mapVariable(Variable variable,
                                Model target,
                                List<Integer> targetIndex);

    /**
     * Remove all mappings in the mapper configuration.
     */
    void removeAllMappings();

    /**
     * Remove a specific mapping from the mapper configuration.
     *
     * @param mapping
     *        mapping to remove
     */
    void removeMapping(MappingOperation<?, ?> mapping);

    /**
     * Remove the specified variable from the mapping configuration. If no mapping is defined with the specified variable's name,
     * this method returns false.
     *
     * @param variable
     *        variable to remove
     * @return true if the variable was removed, false otherwise
     */
    boolean removeVariable(Variable variable);

    /**
     * Write the mapping configuration to the specified output stream.
     *
     * @param output
     *        stream to write to
     * @throws Exception
     *         marshaling or writing the config failed
     */
    void saveConfig(OutputStream output) throws Exception;

    /**
     * Set a transformation to be used by an existing FieldMapping.
     *
     * @param mapping
     *        mapping on which to set the transformation
     * @param transformationClass
     *        class containing the transformation with the supplied name
     * @param transformationName
     *        transformation in the transformationClass to use
     * @param transformationArguments
     *        Strings representing each transformation argument in the form <code>&lt;type>=&lt;value></code>
     * @return A new transformation mapping
     */
    TransformationMapping setTransformation(FieldMapping mapping,
                                            String transformationClass,
                                            String transformationName,
                                            String... transformationArguments);
}
