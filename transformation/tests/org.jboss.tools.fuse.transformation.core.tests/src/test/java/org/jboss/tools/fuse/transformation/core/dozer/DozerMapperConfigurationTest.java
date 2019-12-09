/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tools.fuse.transformation.core.dozer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.assertj.core.api.Assertions;
import org.jboss.tools.fuse.transformation.core.ExpressionMapping;
import org.jboss.tools.fuse.transformation.core.FieldMapping;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.TransformationMapping;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.VariableMapping;
import org.jboss.tools.fuse.transformation.core.dozer.config.Configuration;
import org.jboss.tools.fuse.transformation.core.dozer.config.Field;
import org.jboss.tools.fuse.transformation.core.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.core.model.ModelBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import abcorder.ABCOrder;
import example.AClass;
import example.BClass;

public class DozerMapperConfigurationTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private static final String EXAMPLE_MAP = "exampleMapping.xml";

    private static final String CUSTOM_CLASS = "org.foo.TestCustomizer";
    private static final String CUSTOM_OPERATION = "customMap";

    private Model modelA;
    private Model modelB;

    @Before
    public void setUp() {
        modelA = new Model("AClass", "example.AClass");
        modelA.setModelClass(AClass.class);
        modelA.addChild("A1", "java.lang.Object");
        modelA.addChild("A2", "java.lang.Object");
        modelA.addChild("A3", "java.lang.Object");

        modelB = new Model("BClass", "example.BClass");
        modelB.setModelClass(BClass.class);
        modelB.addChild("B1", "java.lang.Object");
        modelB.addChild("B2", "java.lang.Object");
        modelB.addChild("B3", "java.lang.Object");
    }

    /*
     * Verifies that mapping a field that is not a direct child of the class
     * mapping is prefixed correctly.
     */
    @Test
    public void mapGrandchild() throws Exception {
		DozerMapperConfiguration config = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
        Model modelA = ModelBuilder.fromJavaClass(A.class);
        Model modelB = ModelBuilder.fromJavaClass(B.class);
        config.addClassMapping(modelA.getType(), modelB.getType());
        Model sourceField = modelA.get("data");
        Model targetField = modelB.get("c").get("d").get("data");
        config.mapField(sourceField, targetField);

       Mapping mapping = config.getClassMapping(sourceField, targetField);
       Field field = (Field)mapping.getFieldOrFieldExclude().get(0);
       Assert.assertEquals("data", field.getA().getContent());
       Assert.assertEquals("c.d.data", field.getB().getContent());
    }

    /*
     * Basic validation of a field mapping.
     */
    @Test
    public void mapFields() throws Exception {
		DozerMapperConfiguration config = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
        Model modelA = ModelBuilder.fromJavaClass(A.class);
        Model modelB = ModelBuilder.fromJavaClass(B.class);
        config.addClassMapping(modelA.getType(), modelB.getType());
        Model sourceField = modelA.get("data");
        Model targetField = modelB.get("data");
        config.mapField(sourceField, targetField);

       Mapping mapping = config.getClassMapping(sourceField, targetField);
       Field field = (Field)mapping.getFieldOrFieldExclude().get(0);
       Assert.assertEquals("data", field.getA().getContent());
       Assert.assertEquals("data", field.getB().getContent());
    }

    @Test
    public void clearMappings() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        config.mapField(modelA.get("A1"), modelB.get("B1"));
        Assert.assertEquals(1, config.getMappings().size());
        config.removeAllMappings();
        Assert.assertEquals(0, config.getMappings().size());
    }

    @Test
    public void getMappings() throws Exception {
        DozerMapperConfiguration config = loadConfig("fieldAndVariableMapping.xml");
        Assert.assertEquals(2, config.getMappings().size());
        int fieldMappings = 0;
        int variableMappings = 0;
        for (MappingOperation<?,?> mapping : config.getMappings()) {
            if (MappingType.VARIABLE.equals(mapping.getType())) {
                variableMappings++;
            } else if (MappingType.FIELD.equals(mapping.getType())) {
                fieldMappings++;
            }
        }
        Assert.assertEquals(1, fieldMappings);
        Assert.assertEquals(1, variableMappings);
    }

    @Test
    public void getMapping() throws Exception {
        DozerMapperConfiguration config = loadConfig("exampleMapping.xml");
        MappingOperation<?,?> custNumMapping = config.getMapping(
                config.getSourceModel().get("header.customerNum"),
                config.getTargetModel().get("custId"));
        Assert.assertEquals(config.getMappings().get(0).getSource(), custNumMapping.getSource());
        Assert.assertEquals(config.getMappings().get(0).getTarget(), custNumMapping.getTarget());

        MappingOperation<?,?> itemIdMapping = config.getMapping(
                config.getSourceModel().get("orderItems.item.id"),
                config.getTargetModel().get("lineItems.itemId"));
        Assert.assertEquals(config.getMappings().get(4).getSource(), itemIdMapping.getSource());
        Assert.assertEquals(config.getMappings().get(4).getTarget(), itemIdMapping.getTarget());
    }

    @Test
    public void getMappingsListsAndNested() throws Exception {
        DozerMapperConfiguration config = loadConfig("parentsAndLists.xml");
        Assert.assertEquals(8, config.getMappings().size());
        Model referenceModel = ModelBuilder.fromJavaClass(example.ListsAndNestedTypes.class);
        Model testField1 = null;
        Model testField2 = null;
        Model testField3 = null;

        for (MappingOperation<?,?> mapping : config.getMappings()) {
            Assert.assertNotNull(mapping.getSource());
            Assert.assertNotNull(mapping.getTarget());
            Model model = (Model)mapping.getSource();
            switch(model.getName()) {
                case "field1" :
                    testField1 = model;
                    break;
                case "B1" :
                    testField2 = model;
                    break;
                case "A1" :
                    testField3 = model;
                    break;
            }
        }

        Assert.assertEquals(referenceModel.get("nested1.field1"), testField1);
        Assert.assertEquals(referenceModel.get("nested1.classB.B1"), testField2);
        Assert.assertEquals(referenceModel.get("listOfAs.A1"), testField3);
    }

    @Test
    public void getVariables() throws Exception {
        DozerMapperConfiguration config = loadConfig("fieldAndVariableMapping.xml");
        Variable var3 = config.addVariable("VAR3", "XYZ");
        List<Variable> variables = config.getVariables();
        Assert.assertTrue(variables.contains(var3));
        Assert.assertEquals(3, variables.size());
    }

    @Test
    public void mapField() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        config.mapField(source, target);
        FieldMapping mapping = (FieldMapping)config.getMappings().get(0);
        Assert.assertEquals(source, mapping.getSource());
        Assert.assertEquals(target, mapping.getTarget());
    }

    @Test
    public void mapVariable() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model target = modelB.get("B1");
        Variable variable = config.addVariable("VAR1", "ABC-VAL");
        config.mapVariable(variable, modelB.get("B1"));
        VariableMapping mapping = (VariableMapping)config.getMappings().get(0);
        Assert.assertEquals(variable, mapping.getSource());
        Assert.assertEquals(target, mapping.getTarget());
    }

    @Test
    public void mapVariableToIndex() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model target = ModelBuilder
                .fromJavaClass(ABCOrder.class).get("orderItems").get("item").get("id");
        Variable variable = config.addVariable("VAR1", "ABC-VAL");
        List<Integer> index = Arrays.asList(new Integer[] {null, 0, null});
        config.mapVariable(variable, target, index);
        VariableMapping mapping = (VariableMapping)config.getMappings().get(0);
        Assert.assertEquals(variable, mapping.getSource());
        Assert.assertEquals(index, mapping.getTargetIndex());
    }

    @Test
    public void mapExpression() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model target = modelB.get("B1");
        config.mapExpression("simple", "\\${property.foo}", modelB.get("B1"));
        ExpressionMapping mapping = (ExpressionMapping)config.getMappings().get(0);
        Assert.assertEquals("simple", mapping.getSource().getLanguage());
        Assert.assertEquals("\\${property.foo}", mapping.getSource().getExpression());
        Assert.assertEquals(target, mapping.getTarget());
    }

    @Test
    public void getExpression() throws Exception {
        DozerMapperConfiguration config = loadConfig("expressionMapping.xml");
        DozerExpressionMapping expMap = (DozerExpressionMapping)config.getMappings().get(1);
        Assert.assertEquals("simple", expMap.getSource().getLanguage());
        Assert.assertEquals("\\${header.customerNumber}", expMap.getSource().getExpression());
    }

    @Test
    public void mapListItem() throws Exception {
		DozerMapperConfiguration config = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
        config.addClassMapping(ListOfC.class.getName(), ListOfD.class.getName());
        Model modelA = ModelBuilder.fromJavaClass(ListOfC.class);
        Model modelB = ModelBuilder.fromJavaClass(ListOfD.class);
        Model source = modelA.get("listOfCs").get("d");
        Model target = modelB.get("listOfDs").get("data");
        config.mapField(source, target);
        // Verify auto mapping of parent collection
        FieldMapping parentMapping = (FieldMapping)config.getMappings().get(0);
        Assert.assertEquals(source.getParent(), parentMapping.getSource());
        Assert.assertEquals(target.getParent(), parentMapping.getTarget());

        // Verify field mapping
        FieldMapping mapping = (FieldMapping)config.getMappings().get(1);
        Assert.assertEquals(source, mapping.getSource());
        Assert.assertEquals(target, mapping.getTarget());
    }
    
    @Test
	@Ignore("test is failing due to a wrong type return")
    public void mapListItemGrandparentCollection() throws Exception {
		DozerMapperConfiguration config = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
        config.addClassMapping(ListOfC.class.getName(), ListOfC.class.getName());
        Model modelE = ModelBuilder.fromJavaClass(ListOfC.class);
        Model field = modelE.get("listOfCs").get("d").get("data");
        config.mapField(field, field);
        // Verify auto mapping of parent collection
        FieldMapping parentMapping = (FieldMapping)config.getMappings().get(0);
        Assert.assertEquals(field.getParent().getParent(), parentMapping.getSource());
        Assert.assertEquals(field.getParent().getParent(), parentMapping.getTarget());

        // Verify field mapping
        FieldMapping mapping = (FieldMapping)config.getMappings().get(1);
        Assert.assertEquals(field, mapping.getSource());
        Assert.assertEquals(field, mapping.getTarget());
    }

    @Test
    public void getSourceMappings() throws Exception {
		DozerMapperConfiguration config = DozerMapperConfiguration.loadConfig(getFile(EXAMPLE_MAP), this.getClass().getClassLoader());

        Model source = config.getSourceModel().get("header").get("customerNum");
        Assert.assertEquals(1, config.getMappingsForSource(source).size());
        FieldMapping mapping = (FieldMapping)config.getMappingsForSource(source).get(0);
        Assert.assertEquals(source, mapping.getSource());
    }

    @Test
    public void getTargetMappings() throws Exception {
		DozerMapperConfiguration config = DozerMapperConfiguration.loadConfig(getFile(EXAMPLE_MAP), this.getClass().getClassLoader());

        Model target = config.getTargetModel().get("custId");
        Assert.assertEquals(1, config.getMappingsForTarget(target).size());
        FieldMapping mapping = (FieldMapping)config.getMappingsForTarget(target).get(0);
        Assert.assertEquals(target, mapping.getTarget());
    }

    @Test
    public void removeMapping() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        FieldMapping mapping = config.mapField(source, target);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(1, config.getMappings().size());

        config.removeMapping(mapping);
        Assert.assertEquals(0, config.getMappings().size());
    }

    @Test
    public void setTransformation() throws Exception {
        final String transformationClass = "org.foo.TestCustomizer";
        final String transformationName = "customMap";
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        FieldMapping mapping = config.mapField(source, target);
        config.setTransformation(mapping, transformationClass, transformationName);
        Assert.assertEquals(1, config.getMappings().size());
        TransformationMapping custom = (TransformationMapping)config.getMappings().get(0);
        Assert.assertEquals(transformationClass, custom.getTransformationClass());
        Assert.assertEquals(transformationName, custom.getTransformationName());
        Assert.assertEquals(0, custom.getTransformationArguments().length);
    }

    @Test
    public void setTransformationWithArguments() throws Exception {
        final String transformationClass = "org.foo.TestCustomizer";
        final String transformationName = "customMap";
        final String arg = Boolean.class.getName() + "=" + "true";
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        FieldMapping mapping = config.mapField(source, target);
        config.setTransformation(mapping, transformationClass, transformationName, arg);
        Assert.assertEquals(1, config.getMappings().size());
        TransformationMapping custom = (TransformationMapping)config.getMappings().get(0);
        Assert.assertEquals(transformationClass, custom.getTransformationClass());
        Assert.assertEquals(transformationName, custom.getTransformationName());
        String[] args = custom.getTransformationArguments();
        Assert.assertEquals(1, args.length);
        Assert.assertEquals(arg, args[0]);
    }

    @Test
    public void testEdits() throws Exception {
        DozerMapperConfiguration config = loadConfig("allFeatures.xml");
        // Edit variable names and values
        Variable var1 = config.getVariable("VAR1");
        var1.setValue(var1.getValue() + "-EDIT");
        Variable var2 = config.getVariable("VAR2");
        var2.setName("VAR3");
        var2.setValue(var2.getValue() + "-EDIT");

        // Edit mappings
        for (MappingOperation<?,?> mapping : config.getMappings()) {
            if (mapping instanceof TransformationMapping) {
                TransformationMapping custom = (TransformationMapping)mapping;
                custom.setTransformationClass(custom.getTransformationClass() + "Edited");
                custom.setTransformationName(custom.getTransformationName() + "Edited");
            } else if (mapping instanceof ExpressionMapping) {
                ExpressionMapping expression = (ExpressionMapping)mapping;
                expression.getSource().setExpression("customerNumber");
                expression.getSource().setLanguage("header");
            } else if (mapping instanceof VariableMapping) {
                VariableMapping variable = (VariableMapping)mapping;
                variable.setVariable(config.getVariable("VAR3"));
            }
        }

        // Serialize the edited config and compare to our reference
        compareConfig(config, "editConfiguration.xml");
    }

    @Test
    public void loadCustomMappingConfig() throws Exception {
        DozerMapperConfiguration config = loadConfig("customMapping.xml");
        TransformationMapping custom = (TransformationMapping)config.getMappings().get(0);
        Assert.assertEquals(CUSTOM_CLASS, custom.getTransformationClass());
        Assert.assertEquals(CUSTOM_OPERATION, custom.getTransformationName());
    }

    @Test
    public void loadIndexedMappings() throws Exception {
        DozerMapperConfiguration config = loadConfig("indexedMapping.xml");
        FieldMapping scalarToVector = (FieldMapping)config.getMappings().get(0);
        FieldMapping vectorToScalar = (FieldMapping)config.getMappings().get(1);
        Assert.assertEquals("fieldL2", scalarToVector.getTarget().getName());
        Assert.assertEquals("fieldL3", vectorToScalar.getSource().getName());
    }

    @Test
    public void setIndexOnInvalidTarget() throws Exception {
        boolean expressionExCaught = false;
        boolean variableExCaught = false;
        List<Integer> indexes = Arrays.asList(new Integer[] {1, 5, 7});
        DozerExpressionMapping exMap = new DozerExpressionMapping(null, null, null, null);
        try {
            exMap.setSourceIndex(indexes);
        } catch (UnsupportedOperationException uoEx) {
            expressionExCaught = true;
        }
        DozerVariableMapping varMap = new DozerVariableMapping(null, null, null, null);
        try {
            varMap.setSourceIndex(indexes);
        } catch (UnsupportedOperationException uoEx) {
            variableExCaught = true;
        }

        Assert.assertTrue(expressionExCaught);
        Assert.assertTrue(variableExCaught);
    }

    @Test
    public void setMappingIndexes() throws Exception {
        DozerMapperConfiguration config = loadConfig("indexedMapping.xml");

        FieldMapping scalarToVector = (FieldMapping)config.getMappings().get(0);
        FieldMapping vectorToScalar = (FieldMapping)config.getMappings().get(1);
        scalarToVector.setTargetIndex(Arrays.asList(new Integer[] {1, null}));
        vectorToScalar.setSourceIndex(Arrays.asList(new Integer[] {2, 3, null}));

        // Serialize the edited config and compare to our reference
        compareConfig(config, "indexedMapping2.xml");
    }

    @Test
    public void createIndexedMappings() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        config.addClassMapping("example.DeepList", "example.DeepList");
        Model deepList = config.getSourceModel();
        config.mapField(
                deepList.get("fieldL1"), deepList.get("listL1.fieldL2"),
                Arrays.asList(new Integer[] {null}), Arrays.asList(new Integer[] {0, null}));

        config.mapField(
                deepList.get("listL1.listL2.fieldL3"), deepList.get("fieldL1"),
                Arrays.asList(new Integer[] {0, 0, null}), Arrays.asList(new Integer[] {null}));

        // Serialize the edited config and compare to our reference
        compareConfig(config, "indexedMapping.xml");
    }

    @Test
    public void wildcardDisabledOnNewConfigurations() throws Exception {
		DozerMapperConfiguration mapConfig = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
        Configuration config = mapConfig.getDozerConfig().getConfiguration();
        Assert.assertNotNull(config);
        Assert.assertFalse(config.isWildcard());
    }

    @Test
    public void getClassMapping() throws Exception {
        DozerMapperConfiguration config = loadConfig("exampleMapping.xml");
        Model source = config.getSourceModel();
        Model target = config.getTargetModel();
        Assert.assertEquals("xyzorderschema.XyzOrderSchema", config.getRootType(target.get("custId")));
        Assert.assertEquals("abcorder.ABCOrder", config.getRootType(source.get("header.customerNum")));
    }

    @Test
    public void mapAbcToAbc() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model abcOrder = ModelBuilder.fromJavaClass(ABCOrder.class);
        config.addClassMapping("abcorder.ABCOrder", "abcorder.ABCOrder");
        config.mapField(abcOrder.get("header.customerNum"), abcOrder.get("header.customerNum"));
        config.mapField(abcOrder.get("orderItems.item"), abcOrder.get("orderItems.item"));
        config.mapField(abcOrder.get("orderItems.item.id"), abcOrder.get("orderItems.item.id"));

        // Serialize the edited config and compare to our reference
        compareConfig(config, "abc2abc.xml");
    }

    private DozerMapperConfiguration loadConfig(String configName) throws Exception {
		return DozerMapperConfiguration.loadConfig(getFile(configName), this.getClass().getClassLoader());
    }

    private void compareConfig(DozerMapperConfiguration config, String referenceConfigPath) throws Exception {
        // Serialize the edited config and compare to our reference
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        config.saveConfig(bos);
		Assertions.assertThat(bos.toString(StandardCharsets.UTF_8.name())).isXmlEqualToContentOf(getFile(referenceConfigPath));
		// XMLUnit.setIgnoreWhitespace(true);
		// XMLAssert.assertXMLEqual(reference, edited);
    }

	private File getFile(String fileName) {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(fileName, ".xml", tmpFolder.getRoot());
			Files.copy(this.getClass().getResourceAsStream(fileName), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpFile;
	}

	public String toString(Document document) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}
}

@SuppressWarnings("unused")
class A {
    private String data;
}

@SuppressWarnings("unused")
class B {
    private C c;
    private String data;
}

@SuppressWarnings("unused")
class C {
    private D d;
}

@SuppressWarnings("unused")
class D {
    private String data;
}

@SuppressWarnings("unused")
class ListOfC {
    private List<C> listOfCs = new ArrayList<C>(1);
}

@SuppressWarnings("unused")
class ListOfD {
    private List<D> listOfDs = new ArrayList<D>(1);
}
