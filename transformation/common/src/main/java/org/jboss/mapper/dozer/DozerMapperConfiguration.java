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
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.Literal;
import org.jboss.mapper.LiteralMapping;
import org.jboss.mapper.MapperConfiguration;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.dozer.config.Field;
import org.jboss.mapper.dozer.config.FieldDefinition;
import org.jboss.mapper.dozer.config.Mapping;
import org.jboss.mapper.dozer.config.Mappings;
import org.jboss.mapper.dozer.config.ObjectFactory;
import org.jboss.mapper.model.Model;
import org.jboss.mapper.model.ModelBuilder;

public class DozerMapperConfiguration implements MapperConfiguration {

    public static final String DEFAULT_DOZER_CONFIG = "dozerBeanMapping.xml";

    private static final String LITERAL_MAPPER_CLASS =
            "org.apache.camel.component.dozer.LiteralMapper";
    private static final String LITERAL_MAPPER_ID = "_literalMapping";
    private static final String CUSTOM_MAPPER_ID = "_customMapping";
    private static final String DOZER_SCHEMA_LOC =
            "http://dozer.sourceforge.net http://dozer.sourceforge.net/schema/beanmapping.xsd";

    // JAXB classes for Dozer config model
    private JAXBContext jaxbCtx;
    private ClassLoader loader;
    private final Mappings mapConfig;
    private final Model literalModel =
            new Model("literals", LITERAL_MAPPER_CLASS)
                    .addChild("literal", java.lang.String.class.getName());

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

                if (LITERAL_MAPPER_ID.equals(field.getCustomConverterId())) {
                    mappings.add(new DozerLiteralMapping(new Literal(field
                            .getCustomConverterParam()), targetModel, mapping, field));
                } else {
                    Model sourceParentModel = loadModel(mapping.getClassA().getContent());
                    Model sourceModel = sourceParentModel.get(field.getA().getContent());
                    DozerFieldMapping fieldMapping =
                            new DozerFieldMapping(sourceModel, targetModel, mapping, field);
                    // check to see if this field mapping is customized
                    if (CUSTOM_MAPPER_ID.equals(field.getCustomConverterId())) {
                        String[] params = field.getCustomConverterParam().split(",");
                        String mapperClass = params[0];
                        String mapperOperation = params.length > 1 ? params[1] : null;
                        fieldMapping =
                                new DozerCustomMapping(fieldMapping, mapperClass, mapperOperation);
                    }
                    mappings.add(fieldMapping);
                }
            }
        }
        return mappings;
    }

    @Override
    public List<Literal> getLiterals() {
        LinkedList<Literal> consts = new LinkedList<Literal>();
        for (MappingOperation<?, ?> mapping : getMappings()) {
            if (MappingType.LITERAL.equals(mapping.getType())) {
                consts.add(((LiteralMapping) mapping).getSource());
            }
        }
        return consts;
    }

    @Override
    public DozerFieldMapping map(final Model source, final Model target) {
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
    public DozerLiteralMapping map(final Literal literal, final Model target) {
        Mapping mapping = getLiteralMapping(target);
        Field field = new Field();
        field.setA(createField(literalModel, LITERAL_MAPPER_CLASS));
        field.setB(createField(target, mapping.getClassB().getContent()));
        field.setCustomConverterId(LITERAL_MAPPER_ID);
        field.setCustomConverterParam(literal.getValue());
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerLiteralMapping(literal, target, mapping, field);
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
        DozerFieldMapping fieldMapping = (DozerFieldMapping) mapping;
        // update the Dozer config to use the custom converter
        fieldMapping.getField().setCustomConverterId(CUSTOM_MAPPER_ID);
        String param = mappingClass;
        if (mappingOperation != null) {
            param += "," + mappingOperation;
        }
        fieldMapping.getField().setCustomConverterParam(param);
        return new DozerCustomMapping(fieldMapping, mappingClass, mappingOperation);
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

    Mapping getLiteralMapping(final Model target) {
        Mapping mapping = null;

        // See if the literal mapping class is already setup for the target
        for (Mapping m : mapConfig.getMapping()) {
            if (m.getClassA().getContent().equals(LITERAL_MAPPER_CLASS)
                    && m.getClassB().getContent().equals(target.getParent().getType())) {
                mapping = m;
                break;
            }
        }
        // If not, we need to create it
        if (mapping == null) {
            mapping = mapClass(LITERAL_MAPPER_CLASS, target.getParent().getType());
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
}
