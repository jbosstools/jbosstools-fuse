/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.mapper.dozer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.Literal;
import org.jboss.mapper.LiteralMapping;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.dozer.config.Field;
import org.jboss.mapper.dozer.config.Mapping;
import org.jboss.mapper.model.Model;
import org.jboss.mapper.model.ModelBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import example.AClass;
import example.BClass;

public class DozerMapperConfigurationTest {
    
    private static final File EXAMPLE_MAP = 
            new File("src/test/resources/org/jboss/mapper/dozer/exampleMapping.xml");
    private static final File CONFIG_ROOT = 
            new File("target/test-classes/org/jboss/mapper/dozer");
    
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
        DozerMapperConfiguration config = DozerMapperConfiguration.newConfig();
        Model modelA = ModelBuilder.fromJavaClass(A.class);
        Model modelB = ModelBuilder.fromJavaClass(B.class);
        config.addClassMapping(modelA.getType(), modelB.getType());
        config.map(modelA.get("data"), modelB.get("c").get("d").get("data"));
        
       Mapping mapping = config.getClassMapping(modelA);
       Field field = (Field)mapping.getFieldOrFieldExclude().get(0);
       Assert.assertEquals("data", field.getA().getContent());
       Assert.assertEquals("c.d.data", field.getB().getContent());
    }
    
    /*
     * Basic validation of a field mapping.
     */
    @Test
    public void mapFields() throws Exception {
        DozerMapperConfiguration config = DozerMapperConfiguration.newConfig();
        Model modelA = ModelBuilder.fromJavaClass(A.class);
        Model modelB = ModelBuilder.fromJavaClass(B.class);
        config.addClassMapping(modelA.getType(), modelB.getType());
        config.map(modelA.get("data"), modelB.get("data"));
        
       Mapping mapping = config.getClassMapping(modelA);
       Field field = (Field)mapping.getFieldOrFieldExclude().get(0);
       Assert.assertEquals("data", field.getA().getContent());
       Assert.assertEquals("data", field.getB().getContent());
    }
    
    @Test
    public void clearMappings() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        config.map(modelA.get("A1"), modelB.get("B1"));
        Assert.assertEquals(1, config.getMappings().size());
        config.removeAllMappings();
        Assert.assertEquals(0, config.getMappings().size());
    }
    
    @Test
    public void getMappings() throws Exception {
        DozerMapperConfiguration config = loadConfig("fieldAndLiteralMapping.xml");
        Assert.assertEquals(2, config.getMappings().size());
        int fieldMappings = 0;
        int literalMappings = 0;
        for (MappingOperation<?,?> mapping : config.getMappings()) {
            if (MappingType.LITERAL.equals(mapping.getType())) {
                fieldMappings++;
            } else if (MappingType.FIELD.equals(mapping.getType())) {
                literalMappings++;
            }
        }
        Assert.assertEquals(1, fieldMappings);
        Assert.assertEquals(1, literalMappings);
    }
    
    @Test
    public void getLiterals() throws Exception {
        DozerMapperConfiguration config = loadConfig("fieldAndLiteralMapping.xml");
        Literal acme = new Literal("ACME");
        config.map(acme, modelB.get("B3"));
        List<Literal> literals = config.getLiterals();
        Assert.assertTrue(literals.contains(acme));
        Assert.assertEquals(2, literals.size());
    }
    
    @Test
    public void mapField() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        config.map(source, target);
        FieldMapping mapping = (FieldMapping)config.getMappings().get(0);
        Assert.assertEquals(source, mapping.getSource());
        Assert.assertEquals(target, mapping.getTarget());
    }
    
    @Test
    public void mapLiteral() throws Exception {
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model target = modelB.get("B1");
        Literal literal = new Literal("literally?!");
        config.map(literal, modelB.get("B1"));
        LiteralMapping mapping = (LiteralMapping)config.getMappings().get(0);
        Assert.assertEquals(literal, mapping.getSource());
        Assert.assertEquals(target, mapping.getTarget());
    }
    
    @Test
    public void mapListItem() throws Exception {
        DozerMapperConfiguration config = DozerMapperConfiguration.newConfig();
        Model modelA = ModelBuilder.fromJavaClass(ListOfC.class);
        Model modelB = ModelBuilder.fromJavaClass(ListOfD.class);
        Model source = modelA.get("listOfCs").get("d");
        Model target = modelB.get("listOfDs").get("data");
        config.map(source, target);
        FieldMapping mapping = (FieldMapping)config.getMappings().get(0);
        Assert.assertEquals(source, mapping.getSource());
        Assert.assertEquals(target, mapping.getTarget());
    }
    
    @Test
    public void getSourceMappings() throws Exception {
        DozerMapperConfiguration config = DozerMapperConfiguration.loadConfig(EXAMPLE_MAP);
        
        Model source = config.getSourceModel().get("header").get("customerNum");
        Assert.assertEquals(1, config.getMappingsForSource(source).size());
        FieldMapping mapping = (FieldMapping)config.getMappingsForSource(source).get(0);
        Assert.assertEquals(source, mapping.getSource());
    }

    @Test
    public void getTargetMappings() throws Exception {
        DozerMapperConfiguration config = DozerMapperConfiguration.loadConfig(EXAMPLE_MAP);
        
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
        FieldMapping mapping = config.map(source, target);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(1, config.getMappings().size());
        
        config.removeMapping(mapping);
        Assert.assertEquals(0, config.getMappings().size());
    }
    
    @Test
    public void customMappingClassOnly() throws Exception {
        final String customizeClass = "org.foo.TestCustomizer";
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        FieldMapping mapping = config.map(source, target);
        config.customizeMapping(mapping, customizeClass);
        Assert.assertEquals(1, config.getMappings().size());
        CustomMapping custom = (CustomMapping)config.getMappings().get(0);
        Assert.assertEquals(customizeClass, custom.getMappingClass());
        Assert.assertNull(custom.getMappingOperation());
    }
    
    @Test
    public void customMappingClassAndOperation() throws Exception {
        final String customizeClass = "org.foo.TestCustomizer";
        final String customizeOperation = "customMap";
        DozerMapperConfiguration config = loadConfig("emptyDozerMapping.xml");
        Model source = modelA.get("A1");
        Model target = modelB.get("B1");
        FieldMapping mapping = config.map(source, target);
        config.customizeMapping(mapping, customizeClass, customizeOperation);
        Assert.assertEquals(1, config.getMappings().size());
        CustomMapping custom = (CustomMapping)config.getMappings().get(0);
        Assert.assertEquals(customizeClass, custom.getMappingClass());
        Assert.assertEquals(customizeOperation, custom.getMappingOperation());
    }
    
    @Test
    public void loadCustomMappingConfig() throws Exception {
        DozerMapperConfiguration config = loadConfig("customMapping.xml");
        CustomMapping custom = (CustomMapping)config.getMappings().get(0);
        Assert.assertEquals(CUSTOM_CLASS, custom.getMappingClass());
        Assert.assertEquals(CUSTOM_OPERATION, custom.getMappingOperation());
    }
    
    private DozerMapperConfiguration loadConfig(String configName) throws Exception {
        return DozerMapperConfiguration.loadConfig(new File(CONFIG_ROOT, configName));
    }
}

class A {
    private String data;
}

class B {
    private C c;
    private String data;
}

class C {
    private D d;
}

class D {
    private String data;
}

class ListOfC {
    private List<C> listOfCs = new ArrayList<C>(1);
}

class ListOfD {
    private List<D> listOfDs = new ArrayList<D>(1);
}

