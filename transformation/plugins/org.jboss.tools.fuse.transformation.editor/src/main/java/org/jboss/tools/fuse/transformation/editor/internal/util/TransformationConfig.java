/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.fuse.transformation.CustomMapping;
import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.ExpressionMapping;
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.MapperConfiguration;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.VariableMapping;
import org.jboss.tools.fuse.transformation.dozer.DozerMapperConfiguration;
import org.jboss.tools.fuse.transformation.model.Model;

/**
 *
 */
public class TransformationConfig implements MapperConfiguration {

    /**
     * Property change event type for adding and removing mappings
     */
    public static final String MAPPING = "mapping";

    /**
     * Property change event type for changing a mapping's customization
     */
    public static final String MAPPING_CUSTOMIZE = "mappingCustomize";

    /**
     * Property change event type for changing a mapping's source
     */
    public static final String MAPPING_SOURCE = "mappingSource";

    /**
     * Property change event type for changing a mapping's target
     */
    public static final String MAPPING_TARGET = "mappingTarget";

    /**
     * Property change event type for adding and removing variables
     */
    public static final String VARIABLE = "variable";

    /**
     * Property change event type for changing a variable's name
     */
    public static final String VARIABLE_NAME = "variableName";

    /**
     * Property change event type for changing a variable's value
     */
    public static final String VARIABLE_VALUE = "variableValue";

    private final IFile file;
    private final MapperConfiguration delegate;
    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final List<MappingPlaceholder> mappingPlaceholders = new ArrayList<>();

    /**
     * @param file
     * @param loader
     * @throws Exception
     */
    public TransformationConfig(final IFile file,
                                final URLClassLoader loader) throws Exception {
        this.file = file;
        delegate = DozerMapperConfiguration.loadConfig(new File(file.getLocationURI()), loader);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration #addClassMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void addClassMapping(final String fromClass,
                                final String toClass) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param listener
     */
    public void addListener(final PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration #addVariable(java.lang.String, java.lang.String)
     */
    @Override
    public Variable addVariable(final String name,
                                final String value) {
        final Variable variable = delegate.addVariable(name, value);
        fireEvent(VARIABLE, null, variable);
        return variable;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#customizeMapping(org.jboss.tools.fuse.transformation.FieldMapping,
     *      java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public CustomMapping customizeMapping(FieldMapping fieldMapping,
                                          String functionClass,
                                          String functionName,
                                          String... functionArguments) {
        FieldMapping origFieldMapping = fieldMapping;
        if (fieldMapping.getType() == MappingType.CUSTOM) {
            delegate.removeMapping(fieldMapping);
            fieldMapping = delegate.mapField(fieldMapping.getSource(),
                                             fieldMapping.getTarget(),
                                             indexes(fieldMapping.getSource(), fieldMapping.getSourceIndex()),
                                             indexes(fieldMapping.getTarget(), fieldMapping.getTargetIndex()));
        }
        final CustomMapping customMapping = delegate.customizeMapping(fieldMapping, functionClass, functionName, functionArguments);
        fireEvent(MAPPING_CUSTOMIZE, origFieldMapping, customMapping);
        return customMapping;
    }

    Model find(final Object object,
               final Model model) {
        if (model.equals(object)) {
            return model;
        }
        for (final Model child : model.getChildren()) {
            final Model match = find(object, child);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    private void fireEvent(final String eventType,
                           final Object oldValue,
                           final Object newValue) {
        final PropertyChangeEvent event = new PropertyChangeEvent(this, eventType, oldValue, newValue);
        for (final PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingOperation<?, ?> getMapping(Model source,
                                             Model target) {
        return delegate.getMapping(source, target);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#getMappings()
     */
    @Override
    public List<MappingOperation<?, ?>> getMappings() {
        final List<MappingOperation<?, ?>> mappings = delegate.getMappings();
        mappings.addAll(mappingPlaceholders);
        return mappings;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration
     *      #getMappingsForSource(org.jboss.tools.fuse.transformation.model.Model)
     */
    @Override
    public List<MappingOperation<?, ?>> getMappingsForSource(final Model source) {
        final List<MappingOperation<?, ?>> mappings = delegate.getMappingsForSource(source);
        for (final MappingOperation<?, ?> mapping : mappingPlaceholders) {
            if (source.equals(mapping.getSource())) {
                mappings.add(mapping);
            }
        }
        return mappings;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration
     *      #getMappingsForTarget(org.jboss.tools.fuse.transformation.model.Model)
     */
    @Override
    public List<MappingOperation<?, ?>> getMappingsForTarget(final Model target) {
        final List<MappingOperation<?, ?>> mappings = delegate.getMappingsForTarget(target);
        for (final MappingOperation<?, ?> mapping : mappingPlaceholders) {
            if (target.equals(mapping.getTarget())) {
                mappings.add(mapping);
            }
        }
        return mappings;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#getSourceModel()
     */
    @Override
    public Model getSourceModel() {
        return delegate.getSourceModel();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#getTargetModel()
     */
    @Override
    public Model getTargetModel() {
        return delegate.getTargetModel();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#getVariable(java.lang.String)
     */
    @Override
    public Variable getVariable(final String variableName) {
        return delegate.getVariable(variableName);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#getVariables()
     */
    @Override
    public List<Variable> getVariables() {
        return delegate.getVariables();
    }

    /**
     * @return <code>true</code> if this config still has mapping placeholders
     */
    public boolean hasMappingPlaceholders() {
        return !mappingPlaceholders.isEmpty();
    }

    private List<Integer> indexes(Model model,
                                  List<Integer> indexes) {
        if (indexes == null) return noIndexes(model);
        for (Integer index : indexes) {
            if (index != null) return indexes;
        }
        return noIndexes(model);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public ExpressionMapping mapExpression(final String language,
                                           final String expression,
                                           final Model target) {
        final ExpressionMapping mapping = delegate.mapExpression(language, expression, target);
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public ExpressionMapping mapExpression(final String language,
                                           final String expression,
                                           final Model target,
                                           final List<Integer> targetIndexes) {
        final ExpressionMapping mapping = delegate.mapExpression(language, expression, target, indexes(target, targetIndexes));
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration #mapField(org.jboss.tools.fuse.transformation.model.Model,
     *      org.jboss.tools.fuse.transformation.model.Model)
     */
    @Override
    public FieldMapping mapField(final Model source,
                                 final Model target) {
        final FieldMapping mapping = delegate.mapField(source, target);
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public FieldMapping mapField(final Model source,
                                 final Model target,
                                 final List<Integer> sourceIndexes,
                                 final List<Integer> targetIndexes) {
        final FieldMapping mapping =
            delegate.mapField(source, target, indexes(source, sourceIndexes), indexes(target, targetIndexes));
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    /**
     * @param variable
     * @return <code>true</code> if the supplied variable has been mapped at least once
     */
    public boolean mapped(final Variable variable) {
        for (final MappingOperation<?, ?> mapping : getMappings()) {
            if (mappingContainsVariable(mapping, variable)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param mapping
     * @param variable
     * @return <code>true</code> if the supplied mapping contains the supplied variable
     */
    public boolean mappingContainsVariable(final MappingOperation<?, ?> mapping,
                                           final Variable variable) {
        return mapping != null && mapping.getType() == MappingType.VARIABLE
               && ((VariableMapping)mapping).getSource().getName().equals(variable.getName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration #mapVariable(org.jboss.tools.fuse.transformation.Variable,
     *      org.jboss.tools.fuse.transformation.model.Model)
     */
    @Override
    public VariableMapping mapVariable(final Variable variable,
                                       final Model target) {
        final VariableMapping mapping = delegate.mapVariable(variable, target);
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public VariableMapping mapVariable(final Variable variable,
                                       final Model target,
                                       final List<Integer> targetIndexes) {
        final VariableMapping mapping = delegate.mapVariable(variable, target, indexes(target, targetIndexes));
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    /**
     * @return a new mapping
     */
    public MappingPlaceholder newMapping() {
        final MappingPlaceholder mapping = new MappingPlaceholder();
        mappingPlaceholders.add(mapping);
        fireEvent(MAPPING, null, mapping);
        return mapping;
    }

    private List<Integer> noIndexes(final Model model) {
        if (model == null) return null;
        List<Integer> indexes = noIndexes(model.getParent());
        if (indexes == null) return new ArrayList<>();
        indexes.add(null);
        return indexes;
    }

    /**
     * @return the project containing the file for this transformation config
     */
    public IProject project() {
        return file.getProject();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#removeAllMappings()
     */
    @Override
    public void removeAllMappings() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param listener
     */
    public void removeListener(final PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration
     *      #removeMapping(org.jboss.tools.fuse.transformation.MappingOperation)
     */
    @Override
    public void removeMapping(final MappingOperation<?, ?> mapping) {
        if (mapping.getType() == null) {
            mappingPlaceholders.remove(mapping);
        } else {
            delegate.removeMapping(mapping);
        }
        fireEvent(MAPPING, mapping, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration #removeVariable(org.jboss.tools.fuse.transformation.Variable)
     */
    @Override
    public boolean removeVariable(final Variable variable) {
        for (final MappingOperation<?, ?> mapping : getMappings()) {
            if (mappingContainsVariable(mapping, variable)) {
                removeMapping(mapping);
            }
        }
        final boolean removed = delegate.removeVariable(variable);
        fireEvent(VARIABLE, variable, null);
        return removed;
    }

    /**
     * @throws Exception
     */
    public void save() throws Exception {
        try (FileOutputStream stream = new FileOutputStream(new File(file.getLocationURI()))) {
            saveConfig(stream);
            project().refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.MapperConfiguration#saveConfig(java.io.OutputStream)
     */
    @Override
    public void saveConfig(final OutputStream output) throws Exception {
        delegate.saveConfig(output);
    }

    /**
     * @param mapping
     * @param source
     * @param sourceIndexes
     * @param targetIndexes
     * @return The supplied mapping if it remains incomplete, else a new mapping mapped via the delegate
     */
    public MappingOperation<?, ?> setSource(final MappingOperation<?, ?> mapping,
                                            final Object source,
                                            final List<Integer> sourceIndexes,
                                            final List<Integer> targetIndexes) {
        if (mapping.getType() == null) {
            ((MappingPlaceholder)mapping).setSource(source);
        }
        return update(mapping, source, sourceIndexes, (Model)mapping.getTarget(), targetIndexes,
                      MAPPING_SOURCE);
    }

    /**
     * @param mapping
     * @param language
     * @param expression
     * @param targetIndexes
     * @return The supplied mapping if it remains incomplete, else a new mapping mapped via the delegate
     */
    public MappingOperation<?, ?> setSourceExpression(final MappingOperation<?, ?> mapping,
                                                      final String language,
                                                      final String expression,
                                                      final List<Integer> targetIndexes) {
        if (mapping.getType() == null) {
            ((MappingPlaceholder)mapping).setSource(new ExpressionPlaceholder(language,
                                                                              expression));
        } else {
            delegate.removeMapping(mapping);
        }
        final Model targetModel = (Model)mapping.getTarget();
        return update(mapping, language, expression, targetModel, targetIndexes, MAPPING_SOURCE);
    }

    /**
     * @param mapping
     * @param target
     * @param sourceIndexes
     * @param targetIndexes
     * @return The supplied mapping if it remains incomplete, else a new mapping mapped via the delegate
     */
    public MappingOperation<?, ?> setTarget(final MappingOperation<?, ?> mapping,
                                            final Model target,
                                            final List<Integer> sourceIndexes,
                                            final List<Integer> targetIndexes) {
        if (mapping.getType() == null) {
            ((MappingPlaceholder)mapping).setTarget(target);
        }
        return update(mapping, mapping.getSource(), sourceIndexes, target, targetIndexes,
                      MAPPING_TARGET);
    }

    /**
     * @param variable
     * @param value
     */
    public void setValue(final Variable variable,
                         final String value) {
        variable.setValue(value);
        fireEvent(VARIABLE_VALUE, null, variable);
    }

    /**
     * @param customMapping
     * @return a new (non-custom) field mapping
     */
    public FieldMapping uncustomizeMapping(final CustomMapping customMapping) {
        delegate.removeMapping(customMapping);
        final FieldMapping fieldMapping = delegate.mapField(customMapping.getSource(),
                                                            customMapping.getTarget(),
                                                            indexes(customMapping.getSource(), customMapping.getSourceIndex()),
                                                            indexes(customMapping.getTarget(), customMapping.getTargetIndex()));
        fireEvent(MAPPING_CUSTOMIZE, customMapping, fieldMapping);
        return fieldMapping;
    }

    private MappingOperation<?, ?> update(final MappingOperation<?, ?> mapping,
                                          final Object source,
                                          List<Integer> sourceIndexes,
                                          final Model target,
                                          List<Integer> targetIndexes,
                                          final String eventType) {
        MappingOperation<?, ?> resultMapping;
        if (source == null || target == null) {
            resultMapping = mapping;
        } else {
            // Not or no longer a mapping placeholder
            if (mapping.getType() == null) mappingPlaceholders.remove(mapping);
            if (source instanceof Model) {
                if (mapping.getType() != null) delegate.removeMapping(mapping);
                final Model sourceModel = (Model)source;
                resultMapping =
                    delegate.mapField(sourceModel, target, indexes(sourceModel, sourceIndexes), indexes(target, targetIndexes));
                if (mapping.getType() == MappingType.CUSTOM) {
                    final CustomMapping customMapping = (CustomMapping)mapping;
                    resultMapping = delegate.customizeMapping((FieldMapping)resultMapping,
                                                              customMapping.getFunctionClass(),
                                                              customMapping.getFunctionName(),
                                                              customMapping.getFunctionArguments());
                }
            } else if (source instanceof Variable) {
                if (mapping.getType() == MappingType.VARIABLE
                    && target.equals(mapping.getTarget())) {
                    resultMapping = mapping;
                    if (targetIndexes == null) ((VariableMapping)mapping).setVariable((Variable)source);
                    else resultMapping = delegate.mapVariable((Variable)source, target, targetIndexes);
                } else {
                    if (mapping.getType() != null) delegate.removeMapping(mapping);
                    if (targetIndexes == null) resultMapping = delegate.mapVariable((Variable)source, target);
                    else resultMapping = delegate.mapVariable((Variable)source, target, targetIndexes);
                }
            } else {
                if (mapping.getType() != null) delegate.removeMapping(mapping);
                final Expression expression = (Expression)source;
                if (targetIndexes == null) resultMapping = delegate.mapExpression(expression.getLanguage(),
                                                                                  expression.getExpression(),
                                                                                  target);
                else resultMapping = delegate.mapExpression(expression.getLanguage(),
                                                            expression.getExpression(),
                                                            target,
                                                            targetIndexes);
            }
        }
        fireEvent(eventType, mapping, resultMapping);
        return resultMapping;
    }

    private MappingOperation<?, ?> update(final MappingOperation<?, ?> mapping,
                                          final String language,
                                          final String expression,
                                          final Model target,
                                          List<Integer> targetIndexes,
                                          final String eventType) {
        MappingOperation<?, ?> resultMapping;
        if (target == null) {
            resultMapping = mapping;
        } else {
            if (mapping.getType() == null) {
                mappingPlaceholders.remove(mapping);
            }
            if (targetIndexes == null) {
                resultMapping = delegate.mapExpression(language, expression, target);
            } else {
                resultMapping = delegate.mapExpression(language, expression, target, targetIndexes);
            }
        }
        fireEvent(eventType, mapping, resultMapping);
        return resultMapping;
    }
}
