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
package org.jboss.mapper.dozer;

import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.Expression;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.MapperConfiguration;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.Variable;
import org.jboss.mapper.dozer.config.Configuration;
import org.jboss.mapper.dozer.config.Field;
import org.jboss.mapper.dozer.config.FieldDefinition;
import org.jboss.mapper.dozer.config.Mapping;
import org.jboss.mapper.dozer.config.Mappings;
import org.jboss.mapper.dozer.config.ObjectFactory;
import org.jboss.mapper.dozer.config.Variables;
import org.jboss.mapper.model.Model;
import org.jboss.mapper.model.ModelBuilder;

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

    public static DozerMapperConfiguration newConfig() {
        return new DozerMapperConfiguration();
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
            Model targetParentModel = loadModel(mapping.getClassB().getContent());

            for (Object o : mapping.getFieldOrFieldExclude()) {
                if (!(o instanceof Field)) {
                    continue;
                }
                Field field = (Field) o;
                Model targetModel = targetParentModel.get(field.getB().getContent());

                if (VARIABLE_MAPPER_ID.equals(field.getCustomConverterId())) {
                    Variable variable = getVariable(DozerVariableMapping.unqualifyName(
                            field.getCustomConverterParam()));
                    mappings.add(new DozerVariableMapping(variable, targetModel, mapping, field));
                } else if (EXPRESSION_MAPPER_ID.equals(field.getCustomConverterId())) {
                    Expression expression = new DozerExpression(field);
                    mappings.add(new DozerExpressionMapping(expression, targetModel, mapping, field));
                } else {
                    Model sourceParentModel = loadModel(mapping.getClassA().getContent());
                    Model sourceModel = sourceParentModel.get(field.getA().getContent());
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
        Iterator<org.jboss.mapper.dozer.config.Variable> varIt = 
                dozerVars.getVariable().iterator();
        while (varIt.hasNext()) {
            org.jboss.mapper.dozer.config.Variable dozerVar = varIt.next();
            if (dozerVar.getName().equals(variable.getName())) {
                varIt.remove();
                return true;
            }
        }
        
        return false;
    }

    @Override
    public List<Variable> getVariables() {
        LinkedList<Variable> variableList = new LinkedList<Variable>();
        if (mapConfig.getConfiguration() == null 
                || mapConfig.getConfiguration().getVariables() == null) {
            return variableList;
        }
        Variables dozerVars = mapConfig.getConfiguration().getVariables();
        for (org.jboss.mapper.dozer.config.Variable dozerVar : dozerVars.getVariable()) {
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
        // Only add a class mapping if one has not been created already
        if (requiresClassMapping(source.getParent(), target.getParent())) {
            final String sourceType = source.getParent().isCollection()
                    ? ModelBuilder.getListType(source.getParent().getType())
                    : source.getParent().getType();
            final String targetType = target.getParent().isCollection()
                    ? ModelBuilder.getListType(target.getParent().getType())
                    : target.getParent().getType();
            addClassMapping(sourceType, targetType);
        }

        // Add field mapping details for the source and target
        return addFieldMapping(source, target);
    }

    @Override
    public DozerVariableMapping mapVariable(final Variable variable, final Model target) {
        // create the mapping
        Mapping mapping = getExtendedMapping(VARIABLE_MAPPER_CLASS, target);
        Field field = new Field();
        field.setA(createField(variableModel, VARIABLE_MAPPER_CLASS));
        field.setB(createField(target, mapping.getClassB().getContent()));
        field.setCustomConverterId(VARIABLE_MAPPER_ID);
        field.setCustomConverterParam(DozerVariableMapping.qualifyName(variable.getName()));
        mapping.getFieldOrFieldExclude().add(field);
        
        return new DozerVariableMapping(variable, target, mapping, field);
    }

    @Override
    public DozerExpressionMapping mapExpression(String language, String expression, Model target) {
        Mapping mapping = getExtendedMapping(EXPRESSION_MAPPER_CLASS, target);
        Field field = new Field();
        field.setA(createField(expressionModel, EXPRESSION_MAPPER_CLASS));
        field.setB(createField(target, mapping.getClassB().getContent()));
        DozerExpression dozerExpression = new DozerExpression(field);
        dozerExpression.setExpression(expression);
        dozerExpression.setLanguage(language);
        field.setCustomConverterId(EXPRESSION_MAPPER_ID);
        mapping.getFieldOrFieldExclude().add(field);
        
        return new DozerExpressionMapping(dozerExpression, target, mapping, field);
    }

    @Override
    public Model getSourceModel() {
        Model source = null;
        Mapping root = getRootMapping();
        if (root != null && root.getClassA() != null) {
            source = loadModel(root.getClassA().getContent());
        }
        return source;
    }

    @Override
    public Model getTargetModel() {
        Model target = null;
        Mapping root = getRootMapping();
        if (root != null && root.getClassB() != null) {
            target = loadModel(root.getClassB().getContent());
        }
        return target;
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

    boolean requiresClassMapping(final Model source, final Model target) {
        // If a class mapping already exists, then no need to add a new one
        if (getClassMapping(source) != null || getClassMapping(target) != null) {
            return false;
        }
        return true;
    }

    Mapping mapClass(final String sourceClass, final String targetClass) {
        final Mapping map = new Mapping();
        final org.jboss.mapper.dozer.config.Class classA =
                new org.jboss.mapper.dozer.config.Class();
        final org.jboss.mapper.dozer.config.Class classB =
                new org.jboss.mapper.dozer.config.Class();
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
        final FieldDefinition fd = new FieldDefinition();
        fd.setContent(getModelName(model, rootType));
        return fd;
    }

    String getModelName(final Model model, final String rootType) {
        StringBuilder name = new StringBuilder(model.getName());
        for (Model parent = model.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getType().equals(rootType) || parent.isCollection()) {
                break;
            }
            name.insert(0, parent.getName() + ".");
        }
        return name.toString();
    }

    // Add a field mapping to the dozer config.
    DozerFieldMapping addFieldMapping(final Model source, final Model target) {
        Mapping mapping;
        if (getClassMapping(source.getParent()) != null) {
            mapping = getClassMapping(source.getParent());
        } else {
            mapping = getClassMapping(target.getParent());
        }

        final Field field = new Field();
        field.setA(createField(source, mapping.getClassA().getContent()));
        field.setB(createField(target, mapping.getClassB().getContent()));
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerFieldMapping(source, target, mapping, field);
    }

    // Return an existing mapping which includes the specified node's parent
    // as a source or target. This basically fetches the mapping definition
    // under which a field mapping can be defined.
    Mapping getClassMapping(final Model model) {
        Mapping mapping = null;
        final String type =
                model.isCollection() ? ModelBuilder.getListType(model.getType()) : model.getType();

        for (final Mapping m : mapConfig.getMapping()) {
            if ((m.getClassA().getContent().equals(type)
            || m.getClassB().getContent().equals(type))) {
                mapping = m;
                break;
            }
        }
        return mapping;
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
        org.jboss.mapper.dozer.config.Variable dozerVar = 
                new org.jboss.mapper.dozer.config.Variable();
        dozerVar.setName(name);
        dozerVar.setContent(value);
        variables.getVariable().add(dozerVar);
        
        return new DozerVariable(dozerVar);
    }
}
