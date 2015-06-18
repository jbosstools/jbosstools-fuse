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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.tools.fuse.transformation.CustomMapping;
import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.MapperConfiguration;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.dozer.config.Configuration;
import org.jboss.tools.fuse.transformation.dozer.config.Field;
import org.jboss.tools.fuse.transformation.dozer.config.FieldDefinition;
import org.jboss.tools.fuse.transformation.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.dozer.config.Mappings;
import org.jboss.tools.fuse.transformation.dozer.config.ObjectFactory;
import org.jboss.tools.fuse.transformation.dozer.config.Variables;
import org.jboss.tools.fuse.transformation.model.Model;
import org.jboss.tools.fuse.transformation.model.ModelBuilder;

public class DozerMapperConfiguration implements MapperConfiguration {

    public static final String DEFAULT_DOZER_CONFIG = "dozerBeanMapping.xml";

    private static final String VARIABLE_MAPPER_CLASS =
            "org.apache.camel.component.dozer.VariableMapper";
    private static final String EXPRESSION_MAPPER_CLASS =
            "org.apache.camel.component.dozer.ExpressionMapper";
    private static final String VARIABLE_MAPPER_ID = "_variableMapping";
    private static final String CUSTOM_MAPPER_ID = "_customMapping";
    private static final String EXPRESSION_MAPPER_ID = "_expressionMapping";
    private static final String DOZER_SCHEMA_LOC =
            "http://dozer.sourceforge.net http://dozer.sourceforge.net/schema/beanmapping.xsd";

    // JAXB classes for Dozer config model
    private JAXBContext jaxbCtx;
    private ClassLoader loader;
    private final Mappings mapConfig;
    private Model sourceModel;
    private Model targetModel;
    private final Model variableModel =
            new Model("variables", VARIABLE_MAPPER_CLASS)
                    .addChild("literal", java.lang.String.class.getName());
    private final Model expressionModel =
            new Model("expressions", EXPRESSION_MAPPER_CLASS)
                    .addChild("expression", java.lang.String.class.getName());

    private DozerMapperConfiguration() {
        this(new Mappings());
    }

    private DozerMapperConfiguration(final File file, final ClassLoader loader) throws Exception {
        mapConfig = (Mappings) getJAXBContext().createUnmarshaller().unmarshal(file);
        this.loader = loader;
    }

    private DozerMapperConfiguration(final InputStream stream, final ClassLoader loader) throws Exception {
        mapConfig = (Mappings) getJAXBContext().createUnmarshaller().unmarshal(stream);
        this.loader = loader;
    }

    private DozerMapperConfiguration(final Mappings mapConfig) {
        this.mapConfig = mapConfig;
    }

    public static DozerMapperConfiguration loadConfig(final File file) throws Exception {
        return new DozerMapperConfiguration(file, null);
    }

    public static DozerMapperConfiguration loadConfig(final File file, ClassLoader loader)
            throws Exception {
        return new DozerMapperConfiguration(file, loader);
    }

    public static DozerMapperConfiguration loadConfig(final InputStream stream) throws Exception {
        return new DozerMapperConfiguration(stream, null);
    }

    public static DozerMapperConfiguration loadConfig(final InputStream stream, ClassLoader loader)
            throws Exception {
        return new DozerMapperConfiguration(stream, loader);
    }

    public static DozerMapperConfiguration newConfig() {
        DozerMapperConfiguration config = new DozerMapperConfiguration();
        Configuration dozerConfig = config.getDozerConfig().getConfiguration();
        if (dozerConfig == null) {
            dozerConfig = new Configuration();
            config.getDozerConfig().setConfiguration(dozerConfig);
        }
        dozerConfig.setWildcard(false);
        return config;
    }

    // Adds a <class-a> and <class-b> mapping definition to the dozer config.
    // If multiple fields within a class are being mapped, this should only
    // be called once.
    @Override
    public void addClassMapping(final String fromClass, final String toClass) {
        mapClass(fromClass, toClass);
    }

    @Override
    public void removeAllMappings() {
        mapConfig.getMapping().clear();
    }

    @Override
    public void removeMapping(MappingOperation<?, ?> mapping) {
        ((BaseDozerMapping) mapping).delete();
    }


    @Override
    public List<MappingOperation<?, ?>> getMappings() {
        LinkedList<MappingOperation<?, ?>> mappings = new LinkedList<MappingOperation<?, ?>>();
        for (Mapping mapping : mapConfig.getMapping()) {
            String targetType = mapping.getClassB().getContent();

            for (Object o : mapping.getFieldOrFieldExclude()) {
                if (!(o instanceof Field)) {
                    continue;
                }
                Field field = (Field) o;
                Model targetModel = getModel(getTargetModel(), targetType, field.getB().getContent());

                if (VARIABLE_MAPPER_ID.equals(field.getCustomConverterId())) {
                    Variable variable = getVariable(DozerVariableMapping.unqualifyName(
                            field.getCustomConverterParam()));
                    mappings.add(new DozerVariableMapping(variable, targetModel, mapping, field));
                } else if (EXPRESSION_MAPPER_ID.equals(field.getCustomConverterId())) {
                    Expression expression = new DozerExpression(field);
                    mappings.add(new DozerExpressionMapping(expression, targetModel, mapping, field));
                } else {
                    String sourceType = mapping.getClassA().getContent();
                    Model sourceModel = getModel(getSourceModel(), sourceType, field.getA().getContent());
                    DozerFieldMapping fieldMapping =
                            new DozerFieldMapping(sourceModel, targetModel, mapping, field);
                    // check to see if this field mapping is customized
                    if (CUSTOM_MAPPER_ID.equals(field.getCustomConverterId())) {
                        fieldMapping = new DozerCustomMapping(fieldMapping);
                    }
                    mappings.add(fieldMapping);
                }
            }
        }
        return mappings;
    }

    @Override
    public Variable addVariable(String name, String value) {
        Variable variable = getVariable(name);
        // if the variable already exists, just update it
        if (variable != null) {
            variable.setValue(value);
        } else {
            // looks like we need to create a new one
            variable = createVariable(name, value);
        }
        return variable;
    }

    @Override
    public boolean removeVariable(Variable variable) {
        if (mapConfig.getConfiguration() == null
                || mapConfig.getConfiguration().getVariables() == null) {
            return false;
        }

        Variables dozerVars = mapConfig.getConfiguration().getVariables();
        Iterator<org.jboss.tools.fuse.transformation.dozer.config.Variable> varIt =
                dozerVars.getVariable().iterator();
        boolean removed = false;
        while (varIt.hasNext()) {
            org.jboss.tools.fuse.transformation.dozer.config.Variable dozerVar = varIt.next();
            if (dozerVar.getName().equals(variable.getName())) {
                varIt.remove();
                removed = true;
                break;
            }
        }

        // If there are no variables we need to remove the top-level variables
        // element to make Dozer happy
        if (dozerVars.getVariable().isEmpty()) {
            mapConfig.getConfiguration().setVariables(null);
        }

        return removed;
    }

    @Override
    public List<Variable> getVariables() {
        LinkedList<Variable> variableList = new LinkedList<Variable>();
        if (mapConfig.getConfiguration() == null
                || mapConfig.getConfiguration().getVariables() == null) {
            return variableList;
        }
        Variables dozerVars = mapConfig.getConfiguration().getVariables();
        for (org.jboss.tools.fuse.transformation.dozer.config.Variable dozerVar : dozerVars.getVariable()) {
            variableList.add(new DozerVariable(dozerVar));
        }
        return variableList;
    }

    @Override
    public Variable getVariable(String variableName) {
        Variable variable = null;
        for (Variable var : getVariables()) {
            if (var.getName().equals(variableName)) {
                variable = var;
                break;
            }
        }
        return variable;
    }

    @Override
    public DozerFieldMapping mapField(final Model source, final Model target) {
        return mapField(source, target, DozerUtil.noIndex(source), DozerUtil.noIndex(target));
    }
    
    @Override
    public DozerFieldMapping mapField(Model source, Model target, 
            List<Integer> sourceIndex, List<Integer> targetIndex) {
        
        validateIndex(source, sourceIndex);
        validateIndex(target, targetIndex);
        
        // Only add a class mapping if one has not been created already
        if (getClassMapping(source, target, sourceIndex, targetIndex) == null) {
            addClassMapping(getRootType(source, sourceIndex), getRootType(target, targetIndex));
        }

        // Add field mapping details for the source and target
        return addFieldMapping(source, target, sourceIndex, targetIndex);
    }
    
    @Override
    public DozerVariableMapping mapVariable(final Variable variable, final Model target) {
        return mapVariable(variable, target, DozerUtil.noIndex(target));
    }

    @Override
    public DozerVariableMapping mapVariable(
            final Variable variable, final Model target, List<Integer> targetIndex) {
        // create the mapping
        Mapping mapping = getExtendedMapping(VARIABLE_MAPPER_CLASS, target);
        Field field = new Field();
        field.setA(createField(variableModel, VARIABLE_MAPPER_CLASS));
        field.setB(createField(target, mapping.getClassB().getContent(), targetIndex));
        field.setCustomConverterId(VARIABLE_MAPPER_ID);
        field.setCustomConverterParam(DozerVariableMapping.qualifyName(variable.getName()));
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerVariableMapping(variable, target, mapping, field);
    }
    
    @Override
    public DozerExpressionMapping mapExpression(String language, String expression, Model target) {
        return mapExpression(language, expression, target, DozerUtil.noIndex(target));
    }

    @Override
    public DozerExpressionMapping mapExpression(
            String language, String expression, Model target, List<Integer> targetIndex) {
        Mapping mapping = getExtendedMapping(EXPRESSION_MAPPER_CLASS, target);
        Field field = new Field();
        field.setA(createField(expressionModel, EXPRESSION_MAPPER_CLASS));
        field.setB(createField(target, mapping.getClassB().getContent(), targetIndex));
        DozerExpression dozerExpression = new DozerExpression(field);
        dozerExpression.setExpression(expression);
        dozerExpression.setLanguage(language);
        field.setCustomConverterId(EXPRESSION_MAPPER_ID);
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerExpressionMapping(dozerExpression, target, mapping, field);
    }

    @Override
    public synchronized Model getSourceModel() {
        if (sourceModel == null) {
            Mapping root = getRootMapping();
            if (root != null && root.getClassA() != null) {
                sourceModel = loadModel(root.getClassA().getContent());
            }
        }
        return sourceModel;
    }

    @Override
    public synchronized Model getTargetModel() {
        if (targetModel == null) {
            Mapping root = getRootMapping();
            if (root != null && root.getClassB() != null) {
                targetModel = loadModel(root.getClassB().getContent());
            }
        }
        return targetModel;
    }

    @Override
    public void saveConfig(final OutputStream output) throws Exception {
        final Marshaller m = getJAXBContext().createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, DOZER_SCHEMA_LOC);
        m.marshal(mapConfig, output);
    }

    @Override
    public List<MappingOperation<?, ?>> getMappingsForSource(Model source) {
        List<MappingOperation<?, ?>> sourceMappings = new LinkedList<MappingOperation<?, ?>>();
        for (MappingOperation<?, ?> op : getMappings()) {
            if (op.getSource().equals(source)) {
                sourceMappings.add(op);
            }
        }
        return sourceMappings;
    }

    @Override
    public List<MappingOperation<?, ?>> getMappingsForTarget(Model target) {
        List<MappingOperation<?, ?>> targetMappings = new LinkedList<MappingOperation<?, ?>>();
        for (MappingOperation<?, ?> op : getMappings()) {
            if (op.getTarget().equals(target)) {
                targetMappings.add(op);
            }
        }
        return targetMappings;
    }

    @Override
    public CustomMapping customizeMapping(FieldMapping mapping, String mappingClass) {
        return customizeMapping(mapping, mappingClass, null);
    }

    @Override
    public CustomMapping customizeMapping(FieldMapping mapping, String mappingClass,
            String mappingOperation) {

        DozerFieldMapping fieldMapping = (DozerFieldMapping)mapping;
        fieldMapping.getField().setCustomConverterId(CUSTOM_MAPPER_ID);
        DozerCustomMapping customMapping = new DozerCustomMapping(fieldMapping);
        customMapping.setMappingClass(mappingClass);
        customMapping.setMappingOperation(mappingOperation);

        return customMapping;
    }
    
    String getRootType(Model field) {
        return getRootType(field, DozerUtil.noIndex(field));
    }
    
    String getRootType(Model field, List<Integer> index) {
        Model root = field.getParent();
        for (int i = index.size() - 2; i >= 0; i--) {
            // jump up the parent chain until we hit the model root or a collection with no index
            if (root.getParent() == null || (root.isCollection() && index.get(i) == null)) {
                break;
            }
            root = root.getParent();
        }
        
        return root.isCollection() ? ModelBuilder.getListType(root.getType()) : root.getType();
    }

    Mapping mapClass(final String sourceClass, final String targetClass) {
        final Mapping map = new Mapping();
        final org.jboss.tools.fuse.transformation.dozer.config.Class classA =
                new org.jboss.tools.fuse.transformation.dozer.config.Class();
        final org.jboss.tools.fuse.transformation.dozer.config.Class classB =
                new org.jboss.tools.fuse.transformation.dozer.config.Class();
        classA.setContent(sourceClass);
        classB.setContent(targetClass);
        map.setClassA(classA);
        map.setClassB(classB);
        mapConfig.getMapping().add(map);
        return map;
    }

    Mapping getExtendedMapping(String sourceClass, final Model target) {
        Mapping mapping = null;

        // See if the variable mapping class is already setup for the target
        for (Mapping m : mapConfig.getMapping()) {
            if (m.getClassA().getContent().equals(sourceClass)
                    && m.getClassB().getContent().equals(target.getParent().getType())) {
                mapping = m;
                break;
            }
        }
        // If not, we need to create it
        if (mapping == null) {
            mapping = mapClass(sourceClass, target.getParent().getType());
        }

        return mapping;
    }

    Mappings getDozerConfig() {
        return mapConfig;
    }
    
    FieldDefinition createField(final Model model, final String rootType) {
        return createField(model, rootType, DozerUtil.noIndex(model));
    }

    FieldDefinition createField(final Model model, final String rootType, List<Integer> indexes) {
        final FieldDefinition fd = new FieldDefinition();
        fd.setContent(DozerUtil.getFieldName(model, rootType, indexes));
        return fd;
    }

    // Add a field mapping to the dozer config.
    DozerFieldMapping addFieldMapping(final Model source, final Model target,
            final List<Integer> sourceIndex, final List<Integer> targetIndex) {
        Mapping mapping = getClassMapping(source, target, sourceIndex, targetIndex);
        final Field field = new Field();
        field.setA(createField(source, mapping.getClassA().getContent(), sourceIndex));
        field.setB(createField(target, mapping.getClassB().getContent(), targetIndex));
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerFieldMapping(source, target, mapping, field);
    }
    
    Mapping getClassMapping(final Model source, final Model target) {
        return getClassMapping(getRootType(source), getRootType(target));
    }
    
    Mapping getClassMapping(final Model source, final Model target, 
            final List<Integer> sourceIndex, final List<Integer> targetIndex) {
        return getClassMapping(getRootType(source, sourceIndex), getRootType(target, targetIndex));
    }

    // Return an existing mapping which includes the specified model
    // as a source or target. This basically fetches the mapping definition
    // under which a field mapping can be defined.
    Mapping getClassMapping(final String sourceType, final String targetType) {
        for (final Mapping m : mapConfig.getMapping()) {
            if ((m.getClassA().getContent().equals(sourceType) && m.getClassB().getContent().equals(targetType))) {
                return m;
            }
        }
        // No class mapping found
        return null;
    }

    Mapping getRootMapping() {
        Mapping root = null;
        if (mapConfig.getMapping().size() > 0) {
            root = mapConfig.getMapping().get(0);
        }
        return root;
    }

    private Model loadModel(String className) throws RuntimeException {
        try {
            Class<?> modelClass =
                    loader != null ? loader.loadClass(className) : Class.forName(className);
            return ModelBuilder.fromJavaClass(modelClass);
        } catch (ClassNotFoundException cnfEx) {
            throw new RuntimeException(cnfEx);
        }
    }

    private synchronized JAXBContext getJAXBContext() {
        if (jaxbCtx == null) {
            try {
                jaxbCtx = JAXBContext.newInstance(ObjectFactory.class);
            } catch (final JAXBException jaxbEx) {
                throw new RuntimeException(jaxbEx);
            }
        }
        return jaxbCtx;
    }

    private Variable createVariable(String name, String value) {
        // Add the variable to the dozer config
        Configuration dozerConfig = mapConfig.getConfiguration();
        if (dozerConfig == null) {
            dozerConfig = new Configuration();
            mapConfig.setConfiguration(dozerConfig);
        }
        Variables variables = dozerConfig.getVariables();
        if (variables == null) {
            variables = new Variables();
            dozerConfig.setVariables(variables);
        }
        org.jboss.tools.fuse.transformation.dozer.config.Variable dozerVar =
                new org.jboss.tools.fuse.transformation.dozer.config.Variable();
        dozerVar.setName(name);
        dozerVar.setContent(value);
        variables.getVariable().add(dozerVar);

        return new DozerVariable(dozerVar);
    }

    private Model getModel(Model model, String type, String fieldName) {
        // Indexed fields have a [] which we need to trim off
        String name = DozerUtil.removeIndexes(fieldName);
        // See if the current model is the target model
        if (type.equals(model.getType()) && name.equals(model.getName())) {
            return model;
        }
        // If the target model is not a list, we can get it by name
        Model found = model.get(name);
        // If we still don't have a hit, the target model must be part of a collection
        if (found == null) {
            for (Model childModel : model.getChildren()) {
                if (childModel.isCollection()) {
                    found = getModel(childModel, childModel.getType(), name);
                } else {
                    found = getModel(childModel, type, name);
                }

                if (found != null) {
                    break;
                }
            }
        }

        return found;
    }
    
    private void validateIndex(Model model, List<Integer> index) throws RuntimeException {
        int nodes = DozerUtil.numberOfNodes(model);
        if (nodes != index.size()) {
            throw new RuntimeException("Invalid index size for model, expected " 
                    + nodes + " but index size is " + index.size());
        }
    }
}
