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
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.fuse.transformation.core.Expression;
import org.jboss.tools.fuse.transformation.core.FieldMapping;
import org.jboss.tools.fuse.transformation.core.MapperConfiguration;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.TransformationMapping;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.VariableMapping;
import org.jboss.tools.fuse.transformation.core.dozer.DozerMapperConfiguration;
import org.jboss.tools.fuse.transformation.core.model.Model;

public class TransformationManager {

    private final IFile file;
    private final MapperConfiguration delegate;
    private final List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final List<MappingPlaceholder> mappingPlaceholders = new ArrayList<>();

    public TransformationManager(IFile file, URLClassLoader loader) throws JAXBException {
        this.file = file;
        delegate = DozerMapperConfiguration.loadConfig(new File(file.getLocationURI()), loader);
    }

    public void addListener(final PropertyChangeListener listener) {
        listeners.add(listener);
    }

    public Variable addVariable(final String name,
                                final String value) {
        final Variable variable = delegate.addVariable(name, value);
        fireEvent(Event.VARIABLE, null, variable);
        return variable;
    }

    private void fireEvent(final Event eventType,
                           final Object oldValue,
                           final Object newValue) {
        final PropertyChangeEvent event = new PropertyChangeEvent(this, eventType.name(), oldValue, newValue);
        for (final PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

    /**
     * @return <code>true</code> if this manager still has mapping placeholders
     */
    public boolean hasMappingPlaceholders() {
        return !mappingPlaceholders.isEmpty();
    }

    private boolean indexed(Object object,
                            Model target) {
        if (object instanceof Model) return Util.inCollection((Model)object) != Util.inCollection(target);
        return Util.inCollection(target);
    }

    public FieldMapping map(Model source,
                            Model target) {
        boolean indexed = indexed(source, target);
        FieldMapping mapping = delegate.mapField(source, target, updateIndexes(source, indexed), updateIndexes(target, indexed));
        fireEvent(Event.MAPPING, null, mapping);
        return mapping;
    }

    public VariableMapping map(Variable source,
                               Model target) {
        VariableMapping mapping = delegate.mapVariable(source, target, updateIndexes(target, Util.inCollection(target)));
        fireEvent(Event.MAPPING, null, mapping);
        return mapping;
    }

    /**
     * @param model
     * @return <code>true</code> if the supplied model has been mapped at least once
     */
    public boolean mapped(Model model) {
        List<MappingOperation<?, ?>> mappings =
            source(model) ? delegate.getMappingsForSource(model) : delegate.getMappingsForTarget(model);
        return !mappings.isEmpty();
    }

    /**
     * @param source
     * @param target
     * @return <code>true</code> if the supplied source has been mapped to the supplied target
     */
    public boolean mapped(Model source,
                          Model target) {
        return delegate.getMapping(source, target) != null;
    }

    /**
     * @param variable
     * @return <code>true</code> if the supplied variable has been mapped at least once
     */
    public boolean mapped(Variable variable) {
        for (MappingOperation<?, ?> mapping : mappings()) {
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
    public boolean mappingContainsVariable(MappingOperation<?, ?> mapping,
                                           Variable variable) {
        return mapping != null && mapping.getType() == MappingType.VARIABLE && ((VariableMapping)mapping).getSource().getName().equals(variable.getName());
    }

    public List<MappingOperation<?, ?>> mappings() {
        final List<MappingOperation<?, ?>> mappings = delegate.getMappings();
        mappings.addAll(mappingPlaceholders);
        return mappings;
    }

    /**
     * @return a new mapping
     */
    public MappingPlaceholder newMapping() {
        final MappingPlaceholder mapping = new MappingPlaceholder();
        mappingPlaceholders.add(mapping);
        fireEvent(Event.MAPPING, null, mapping);
        return mapping;
    }

    /**
     * @return the project containing the file for this transformation manager
     */
    public IProject project() {
        return file.getProject();
    }

    /**
     * @param listener
     */
    public void removeListener(final PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
    * @param transformationMapping
    * @return a new (non-transformation) field mapping
    */
    public FieldMapping removeTransformation(TransformationMapping transformationMapping) {
        delegate.removeMapping(transformationMapping);
        FieldMapping fieldMapping =
            delegate.mapField(transformationMapping.getSource(),
                              transformationMapping.getTarget(),
                              Util.sourceUpdateIndexes(transformationMapping),
                              Util.targetUpdateIndexes(transformationMapping));
       fireEvent(Event.MAPPING_TRANSFORMATION, transformationMapping, fieldMapping);
        return fieldMapping;
    }

    public boolean removeVariable(Variable variable) {
        for (final MappingOperation<?, ?> mapping : mappings()) {
            if (mappingContainsVariable(mapping, variable)) unMap(mapping);
        }
        boolean removed = delegate.removeVariable(variable);
        fireEvent(Event.VARIABLE, variable, null);
        return removed;
    }

    public Model rootSourceModel() {
        return delegate.getSourceModel();
    }

    public Model rootTargetModel() {
        return delegate.getTargetModel();
    }

    /**
     * @throws Exception
     */
    public void save() throws Exception {
        try (FileOutputStream stream = new FileOutputStream(new File(file.getLocationURI()))) {
            delegate.saveConfig(stream);
            project().refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }

    public MappingOperation<?, ?> setExpression(MappingOperation<?, ?> mapping,
                                                String language,
                                                String expression) {
        MappingOperation<?, ?> resultMapping;
        if (mapping.getTarget() == null) {
            ((MappingPlaceholder)mapping).setSource(new ExpressionPlaceholder(language, expression));
            resultMapping = mapping;
        } else {
            if (mapping.getType() == null) mappingPlaceholders.remove(mapping);
            else delegate.removeMapping(mapping);
            resultMapping =
                delegate.mapExpression(language,
                                       expression,
                                       (Model)mapping.getTarget(),
                                       updateIndexes(mapping.getTarget(),
                                                     mapping.getTargetIndex(),
                                                     Util.inCollection((Model)mapping.getTarget())));
        }
        fireEvent(Event.MAPPING_SOURCE, mapping, resultMapping);
        return resultMapping;
    }

    public MappingOperation<?, ?> setSource(MappingOperation<?, ?> mapping,
                                            Object source) {
        Model target = (Model)mapping.getTarget();
        return update(mapping,
                      source,
                      updateIndexes(source, indexed(source, target)),
                      target,
                      updateIndexes(target, mapping.getTargetIndex(), indexed(source, target)),
                      Event.MAPPING_SOURCE);
    }

    public MappingOperation<?, ?> setTarget(MappingOperation<?, ?> mapping,
                                            Model target) {
        return update(mapping,
                      mapping.getSource(),
                      updateIndexes(mapping.getSource(), mapping.getSourceIndex(), indexed(mapping.getSource(), target)),
                      target,
                      updateIndexes(target, indexed(mapping.getSource(), target)),
                      Event.MAPPING_TARGET);
    }

    public TransformationMapping setTransformation(FieldMapping fieldMapping,
                                                   String transformationClass,
                                                   String transformationName,
                                                   String... transformationArguments) {
        FieldMapping origFieldMapping = fieldMapping;
        if (fieldMapping.getType() == MappingType.TRANSFORMATION) {
            // Remove existing customization
            delegate.removeMapping(fieldMapping);
            fieldMapping =
                delegate.mapField(fieldMapping.getSource(),
                                  fieldMapping.getTarget(),
                                  Util.sourceUpdateIndexes(fieldMapping),
                                  Util.targetUpdateIndexes(fieldMapping));
        }
        TransformationMapping xformMapping =
            delegate.setTransformation(fieldMapping, transformationClass, transformationName, transformationArguments);
        fireEvent(Event.MAPPING_TRANSFORMATION, origFieldMapping, xformMapping);
        return xformMapping;
    }

    /**
     * @param variable
     * @param value
     */
    public void setValue(Variable variable,
                         String value) {
        variable.setValue(value);
        fireEvent(Event.VARIABLE_VALUE, null, variable);
    }

    public boolean source(Model model) {
        return Util.root(model).equals(rootSourceModel());
    }

    public void unMap(MappingOperation<?, ?> mapping) {
        if (mapping.getType() == null) mappingPlaceholders.remove(mapping);
        else delegate.removeMapping(mapping);
        fireEvent(Event.MAPPING, mapping, null);
    }

    private MappingOperation<?, ?> update(MappingOperation<?, ?> mapping,
                                          Object source,
                                          List<Integer> sourceIndexes,
                                          Model target,
                                          List<Integer> targetIndexes,
                                          Event eventType) {
        MappingOperation<?, ?> resultMapping;
        if (source == null || target == null) {
            MappingPlaceholder mappingPlaceholder = (MappingPlaceholder)mapping;
            mappingPlaceholder.setSource(source);
            mappingPlaceholder.setSourceIndex(sourceIndexes);
            mappingPlaceholder.setTarget(target);
            mappingPlaceholder.setTargetIndex(targetIndexes);
            resultMapping = mapping;
        } else {
            if (mapping.getType() == null) mappingPlaceholders.remove(mapping); // No longer a mapping placeholder
            else delegate.removeMapping(mapping);
            if (source instanceof Model) {
                resultMapping = delegate.mapField((Model)source, target, sourceIndexes, targetIndexes);
                resultMapping.setSourceDateFormat(mapping.getSourceDateFormat());
                resultMapping.setTargetDateFormat(mapping.getTargetDateFormat());
                if (mapping.getType() == MappingType.TRANSFORMATION) {
                    TransformationMapping xformMapping = (TransformationMapping)mapping;
                    resultMapping = delegate.setTransformation((FieldMapping)resultMapping,
                                                               xformMapping.getTransformationClass(),
                                                               xformMapping.getTransformationName(),
                                                               xformMapping.getTransformationArguments());
                }
            } else if (source instanceof Variable) resultMapping = delegate.mapVariable((Variable)source, target, targetIndexes);
            else { // Must be an expression
                Expression expression = (Expression)source;
                resultMapping = delegate.mapExpression(expression.getLanguage(), expression.getExpression(), target, targetIndexes);
            }
        }
        fireEvent(eventType, mapping, resultMapping);
        return resultMapping;
    }

    private void updateIndexes(Model model,
                               List<Integer> indexes,
                               int indexesIndex,
                               List<Integer> updateIndexes,
                               boolean indexed) {
        if (model == null) return;
        updateIndexes(model.getParent(), indexes, indexesIndex - 1, updateIndexes, indexed);
        if (model.isCollection() && indexed) {
            Integer index = indexesIndex < 0 ? null : indexes.get(indexesIndex);
            updateIndexes.add(index == null ? 0 : index);
        } else updateIndexes.add(null);
    }

    private List<Integer> updateIndexes(Object object,
                                        boolean indexed) {
        return updateIndexes(object, Collections.<Integer>emptyList(), indexed);
    }

    private List<Integer> updateIndexes(Object object,
                                        List<Integer> indexes,
                                        boolean indexed) {
        if (!(object instanceof Model)) return null;
        List<Integer> updateIndexes = new ArrayList<>();
        updateIndexes(((Model)object).getParent(), indexes, indexes == null ? -1 : indexes.size() - 1, updateIndexes, indexed);
        return updateIndexes;
    }

    public Variable variable(String variableName) {
        return delegate.getVariable(variableName);
    }

    public List<Variable> variables() {
        return delegate.getVariables();
    }

    public enum Event {

        /**
         * Property change event type for adding and removing mappings
         */
        MAPPING,

        /**
         * Property change event type for changing a mapping's source
         */
        MAPPING_SOURCE,

        /**
         * Property change event type for changing a mapping's target
         */
        MAPPING_TARGET,

        /**
         * Property change event type for changing a mapping's transformation
         */
        MAPPING_TRANSFORMATION,

        /**
         * Property change event type for adding and removing variables
         */
        VARIABLE,

        /**
         * Property change event type for changing a variable's name
         */
        VARIABLE_NAME,

        /**
         * Property change event type for changing a variable's value
         */
        VARIABLE_VALUE
    }
}
