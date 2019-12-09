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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.tools.fuse.transformation.core.Expression;
import org.jboss.tools.fuse.transformation.core.FieldMapping;
import org.jboss.tools.fuse.transformation.core.MapperConfiguration;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.TransformationMapping;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.dozer.config.Configuration;
import org.jboss.tools.fuse.transformation.core.dozer.config.Field;
import org.jboss.tools.fuse.transformation.core.dozer.config.FieldDefinition;
import org.jboss.tools.fuse.transformation.core.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.core.dozer.config.Mappings;
import org.jboss.tools.fuse.transformation.core.dozer.config.ObjectFactory;
import org.jboss.tools.fuse.transformation.core.dozer.config.Variables;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.core.model.ModelBuilder;

public class DozerMapperConfiguration implements MapperConfiguration {

    public static final String DEFAULT_DOZER_CONFIG = "dozerBeanMapping.xml"; //$NON-NLS-1$

    private static final String VARIABLE_MAPPER_CLASS =
            "org.apache.camel.component.dozer.VariableMapper"; //$NON-NLS-1$
    private static final String EXPRESSION_MAPPER_CLASS =
            "org.apache.camel.component.dozer.ExpressionMapper"; //$NON-NLS-1$
    private static final String VARIABLE_MAPPER_ID = "_variableMapping"; //$NON-NLS-1$
    private static final String CUSTOM_MAPPER_ID = "_customMapping"; //$NON-NLS-1$
    private static final String EXPRESSION_MAPPER_ID = "_expressionMapping"; //$NON-NLS-1$
    public static final String DOZER_6_1_XMLNS = "http://dozermapper.github.io/schema/bean-mapping"; //$NON-NLS-1$
    public static final String DOZER_6_1_SCHEMA_LOC = "https://dozermapper.github.io/schema/bean-mapping.xsd"; //$NON-NLS-1$
    public static final String PRE_DOZER_6_1_XMLNS = "http://dozer.sourceforge.net"; //$NON-NLS-1$
    public static final String PRE_DOZER_6_1_SCHEMA_LOC = "http://dozer.sourceforge.net/schema/beanmapping.xsd"; //$NON-NLS-1$

    public static DozerMapperConfiguration loadConfig(final File file) throws JAXBException {
        return new DozerMapperConfiguration(file, null);
    }
    public static DozerMapperConfiguration loadConfig(final File file, ClassLoader loader)
            throws JAXBException {
        return new DozerMapperConfiguration(file, loader);
    }
    public static DozerMapperConfiguration loadConfig(final InputStream stream) throws JAXBException {
        return new DozerMapperConfiguration(stream, null);
    }
    public static DozerMapperConfiguration loadConfig(final InputStream stream, ClassLoader loader)
            throws JAXBException {
        return new DozerMapperConfiguration(stream, loader);
    }

	public static DozerMapperConfiguration newConfig(ClassLoader loader) {
		DozerMapperConfiguration config = new DozerMapperConfiguration(loader);
        Configuration dozerConfig = config.getDozerConfig().getConfiguration();
        if (dozerConfig == null) {
            dozerConfig = new Configuration();
            config.getDozerConfig().setConfiguration(dozerConfig);
        }
        dozerConfig.setWildcard(false);
        return config;
    }
    // JAXB classes for Dozer config model
    private JAXBContext jaxbCtx;
    private ClassLoader loader;

    private final Mappings mapConfig;

    private Model sourceModel;

    private Model targetModel;

    private final Model variableModel =
            new Model("variables", VARIABLE_MAPPER_CLASS) //$NON-NLS-1$
                    .addChild("literal", java.lang.String.class.getName()); //$NON-NLS-1$

    private final Model expressionModel =
            new Model("expressions", EXPRESSION_MAPPER_CLASS) //$NON-NLS-1$
                    .addChild("expression", java.lang.String.class.getName()); //$NON-NLS-1$

    private DozerMapperConfiguration(final File file, final ClassLoader loader) throws JAXBException {
        mapConfig = (Mappings) getJAXBContext().createUnmarshaller().unmarshal(file);
        this.loader = loader;
    }

    private DozerMapperConfiguration(final InputStream stream, final ClassLoader loader) throws JAXBException {
        mapConfig = (Mappings) getJAXBContext().createUnmarshaller().unmarshal(stream);
        this.loader = loader;
    }

	private DozerMapperConfiguration(ClassLoader loader) {
		this.mapConfig = new Mappings();
		this.loader = loader;
	}

	// Adds a <class-a> and <class-b> mapping definition to the dozer config.
    // If multiple fields within a class are being mapped, this should only
    // be called once.
    @Override
    public void addClassMapping(final String fromClass, final String toClass) {
        mapClass(fromClass, toClass);
    }

    // Add a field mapping to the dozer config.
    DozerFieldMapping addFieldMapping(final Model source, final Model target,
            final List<Integer> sourceIndex, final List<Integer> targetIndex) {
        // If the source and target fields are part of a collection, make sure there's
        // a mapping for the parent field
        if (hasCollectionAncestor(source) && hasCollectionAncestor(target)) {
            mapParentCollection(source, target);
        }
        Mapping mapping = getClassMapping(source, target, sourceIndex, targetIndex);
        final Field field = new Field();
        field.setA(createField(source, mapping.getClassA().getContent(), sourceIndex));
        field.setB(createField(target, mapping.getClassB().getContent(), targetIndex));
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerFieldMapping(source, target, mapping, field);
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


    FieldDefinition createField(final Model model, final String rootType) {
        return createField(model, rootType, DozerUtil.noIndex(model));
    }

    FieldDefinition createField(final Model model, final String rootType, List<Integer> indexes) {
        final FieldDefinition fd = new FieldDefinition();
        fd.setContent(DozerUtil.getFieldName(model, rootType, indexes));
        return fd;
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
        org.jboss.tools.fuse.transformation.core.dozer.config.Variable dozerVar =
                new org.jboss.tools.fuse.transformation.core.dozer.config.Variable();
        dozerVar.setName(name);
        dozerVar.setContent(value);
        variables.getVariable().add(dozerVar);

        return new DozerVariable(dozerVar);
    }

	/**
	 * /!\ Public for Test purpose
	 */
	public Mapping getClassMapping(final Model source, final Model target) {
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

	/**
	 * /!\ Public for test purpose
	 */
	public Mappings getDozerConfig() {
        return mapConfig;
    }

    Mapping getExtendedMapping(String sourceClass, final Model target, List<Integer> index) {
        Mapping mapping = null;

        // See if the variable mapping class is already setup for the target
        for (Mapping m : mapConfig.getMapping()) {
            if (m.getClassA().getContent().equals(sourceClass)
                    && m.getClassB().getContent().equals(getRootType(target, index))) {
                mapping = m;
                break;
            }
        }
        // If not, we need to create it
        if (mapping == null) {
            mapping = mapClass(sourceClass, getRootType(target, index));
        }

        return mapping;
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

    @Override
    public MappingOperation<?, ?> getMapping(Model source, Model target) {
        MappingOperation<?, ?> mapping = null;
        for (MappingOperation<?, ?> op : getMappings()) {
            if (op.getSource().equals(source) && op.getTarget().equals(target)) {
                mapping = op;
                break;
            }
        }
        return mapping;
    }

    @Override
    public List<MappingOperation<?, ?>> getMappings() {
        LinkedList<MappingOperation<?, ?>> mappings = new LinkedList<>();
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
                            field.getCustomConverterArgument()));
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
                        fieldMapping = new DozerTransformationMapping(fieldMapping);
                    }
                    mappings.add(fieldMapping);
                }
            }
        }
        return mappings;
    }

    @Override
    public List<MappingOperation<?, ?>> getMappingsForSource(Model source) {
        List<MappingOperation<?, ?>> sourceMappings = new LinkedList<>();
        for (MappingOperation<?, ?> op : getMappings()) {
            if (op.getSource().equals(source)) {
                sourceMappings.add(op);
            }
        }
        return sourceMappings;
    }

    @Override
    public List<MappingOperation<?, ?>> getMappingsForTarget(Model target) {
        List<MappingOperation<?, ?>> targetMappings = new LinkedList<>();
        for (MappingOperation<?, ?> op : getMappings()) {
            if (op.getTarget().equals(target)) {
                targetMappings.add(op);
            }
        }
        return targetMappings;
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

    private Model getParentCollection(Model model) {
        if (model.getParent() == null) {
        	return null;
        }
        return model.getParent().isCollection() ? model.getParent() : getParentCollection(model.getParent());
    }

    Mapping getRootMapping() {
        Mapping root = null;
        if (!mapConfig.getMapping().isEmpty()) {
            root = mapConfig.getMapping().get(0);
        }
        return root;
    }

	/**
	 * /!\ Public for test purpose
	 */
	public String getRootType(Model field) {
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
    public List<Variable> getVariables() {
        LinkedList<Variable> variableList = new LinkedList<>();
        if (mapConfig.getConfiguration() == null
                || mapConfig.getConfiguration().getVariables() == null) {
            return variableList;
        }
        Variables dozerVars = mapConfig.getConfiguration().getVariables();
        for (org.jboss.tools.fuse.transformation.core.dozer.config.Variable dozerVar : dozerVars.getVariable()) {
            variableList.add(new DozerVariable(dozerVar));
        }
        return variableList;
    }

    private boolean hasCollectionAncestor(Model model) {
        return getParentCollection(model) != null;
    }

    private Model loadModel(String className) {
        try {
            Class<?> modelClass =
                    loader != null ? loader.loadClass(className) : Class.forName(className);
            return ModelBuilder.fromJavaClass(modelClass);
        } catch (ClassNotFoundException cnfEx) {
            throw new RuntimeException(cnfEx);
        }
    }

    Mapping mapClass(final String sourceClass, final String targetClass) {
        final Mapping map = new Mapping();
        final org.jboss.tools.fuse.transformation.core.dozer.config.Class classA =
                new org.jboss.tools.fuse.transformation.core.dozer.config.Class();
        final org.jboss.tools.fuse.transformation.core.dozer.config.Class classB =
                new org.jboss.tools.fuse.transformation.core.dozer.config.Class();
        classA.setContent(sourceClass);
        classB.setContent(targetClass);
        map.setClassA(classA);
        map.setClassB(classB);
        mapConfig.getMapping().add(map);
        return map;
    }

    @Override
    public DozerExpressionMapping mapExpression(String language, String expression, Model target) {
        return mapExpression(language, expression, target, DozerUtil.noIndex(target));
    }

    @Override
    public DozerExpressionMapping mapExpression(
            String language, String expression, Model target, List<Integer> targetIndex) {
        Mapping mapping = getExtendedMapping(EXPRESSION_MAPPER_CLASS, target, targetIndex);
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

    private void mapParentCollection(Model source, Model target) {
        Model sourceParent = getParentCollection(source);
        Model targetParent = getParentCollection(target);
        if (getMapping(sourceParent, targetParent) == null) {
            addFieldMapping(source.getParent(), target.getParent(),
                DozerUtil.noIndex(source.getParent()), DozerUtil.noIndex(target.getParent()));
        }
    }

    @Override
    public DozerVariableMapping mapVariable(final Variable variable, final Model target) {
        return mapVariable(variable, target, DozerUtil.noIndex(target));
    }

    @Override
    public DozerVariableMapping mapVariable(
            final Variable variable, final Model target, List<Integer> targetIndex) {
        // create the mapping
        Mapping mapping = getExtendedMapping(VARIABLE_MAPPER_CLASS, target, targetIndex);
        Field field = new Field();
        field.setA(createField(variableModel, VARIABLE_MAPPER_CLASS));
        field.setB(createField(target, mapping.getClassB().getContent(), targetIndex));
        field.setCustomConverterId(VARIABLE_MAPPER_ID);
        field.setCustomConverterArgument(DozerVariableMapping.qualifyName(variable.getName()));
        mapping.getFieldOrFieldExclude().add(field);

        return new DozerVariableMapping(variable, target, mapping, field);
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
    public boolean removeVariable(Variable variable) {
        if (mapConfig.getConfiguration() == null
                || mapConfig.getConfiguration().getVariables() == null) {
            return false;
        }

        Variables dozerVars = mapConfig.getConfiguration().getVariables();
        Iterator<org.jboss.tools.fuse.transformation.core.dozer.config.Variable> varIt =
                dozerVars.getVariable().iterator();
        boolean removed = false;
        while (varIt.hasNext()) {
            org.jboss.tools.fuse.transformation.core.dozer.config.Variable dozerVar = varIt.next();
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
    public void saveConfig(final OutputStream output) throws Exception {
        final Marshaller m = getJAXBContext().createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, DOZER_6_1_XMLNS + " " + DOZER_6_1_SCHEMA_LOC);
        m.marshal(mapConfig, output);
    }

    @Override
    public TransformationMapping setTransformation(FieldMapping mapping,
                                                   String transformationClass,
                                                   String transformationName,
                                                   String... transformationArguments) {

        DozerFieldMapping fieldMapping = (DozerFieldMapping)mapping;
        fieldMapping.getField().setCustomConverterId(CUSTOM_MAPPER_ID);
        DozerTransformationMapping xfromMapping = new DozerTransformationMapping(fieldMapping);
        xfromMapping.setTransformationClass(transformationClass);
        xfromMapping.setTransformationName(transformationName);
        xfromMapping.addTransformationArguments(transformationArguments);
        return xfromMapping;
    }

    private void validateIndex(Model model, List<Integer> index) {
        int nodes = DozerUtil.numberOfNodes(model);
        if (nodes != index.size()) {
            throw new RuntimeException("Invalid index size for model, expected " //$NON-NLS-1$
                    + nodes + " but index size is " + index.size()); //$NON-NLS-1$
        }
    }
}
