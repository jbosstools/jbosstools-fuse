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
package org.jboss.tools.fuse.transformation.model.xml;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.junit.Test;
import com.sun.codemodel.JCodeModel;

public class XmlModelGeneratorTest {

    private static String XML_INST_PATH =
        "src/test/resources/org/jboss/tools/fuse/transformation/model/xml/abc-order.xml";
    private static String XML_INST_PATH_2 =
        "src/test/resources/org/jboss/tools/fuse/transformation/model/xml/doc-with-namespace.xml";
    private static String XML_INST_PATH_3 =
        "src/test/resources/org/jboss/tools/fuse/transformation/model/xml/doc-with-multiple-namespaces.xml";
    private static String XML_SCHEMA_PATH =
        "src/test/resources/org/jboss/tools/fuse/transformation/model/xml/multi-element.xsd";
    private static String XML_SCHEMA_PATH_2 =
        "src/test/resources/org/jboss/tools/fuse/transformation/model/xml/abc-order.xsd";
    private static String XML_SCHEMA_PATH_3 =
        "src/test/resources/org/jboss/tools/fuse/transformation/model/xml/Application.xsd";
    private static String XML_SCHEMA_GEN_PATH = "target/abc-order.xsd";

    @Test
    public void generateFromInstance() throws Exception {
        File xmlInst = new File(XML_INST_PATH);
        File genSchema = new File(XML_SCHEMA_GEN_PATH);
        XmlModelGenerator modelGen = new XmlModelGenerator();
        modelGen.generateFromInstance(xmlInst, genSchema, "test.generateFromInstance", new File("target"));
        // Check for generated schema
        Assert.assertTrue(genSchema.exists());
        // Check for generated classes
        File genDir = new File("target/test/generateFromInstance");
        Assert.assertTrue(new File(genDir, "ObjectFactory.java").exists());
        Assert.assertTrue(new File(genDir, "ABCOrder.java").exists());
    }

    @Test
    public void generateFromInstanceWithMultipleNamespaces() throws Exception {
        File xmlInstance = new File(XML_INST_PATH_3);
        File generatedSchema = new File(XML_INST_PATH_3 + ".xsd");
        File generatedSchemaA = new File("bogus.com.a.xsd");
        File generatedSchemaB = new File("bogus.com.b.xsd");
        generatedSchema.deleteOnExit();
        generatedSchemaA.deleteOnExit();
        generatedSchemaB.deleteOnExit();
        File generatedObjectFactoryA = new File("target/com/bogus/a/ObjectFactory.java");
        File generatedObjectFactoryB = new File("target/com/bogus/b/ObjectFactory.java");
        File generatedObjectFactoryC = new File("target/com/bogus/c/ObjectFactory.java");
        File generatedRoot = new File("target/com/bogus/a/ObjectFactory.java");
        XmlModelGenerator modelGen = new XmlModelGenerator();
        JCodeModel codeModel = modelGen.generateFromInstance(xmlInstance, generatedSchema, null, new File("target"));
        Assert.assertTrue(generatedSchema.exists());
        Assert.assertTrue(generatedSchemaA.exists());
        Assert.assertTrue(generatedSchemaB.exists());
        Assert.assertTrue(generatedObjectFactoryA.exists());
        Assert.assertTrue(generatedObjectFactoryB.exists());
        Assert.assertTrue(generatedObjectFactoryC.exists());
        Assert.assertTrue(generatedRoot.exists());
        Map<String, String> mappings = modelGen.elementToClassMapping(codeModel);
        Assert.assertEquals(3, mappings.size());
        Assert.assertEquals("com.bogus.c.Root", mappings.get("root"));
        Assert.assertEquals("com.bogus.a.Element1", mappings.get("element1"));
        Assert.assertEquals("com.bogus.b.Element2", mappings.get("element2"));
    }

    @Test
    public void getGeneratedElementsMultipleElements() throws Exception {
        File xmlSchema = new File(XML_SCHEMA_PATH);
        XmlModelGenerator modelGen = new XmlModelGenerator();
        JCodeModel codeModel = modelGen.generateFromSchema(
                                                           xmlSchema, "test.getGeneratedElementsMultipleElements", new File("target"));
        Map<String, String> mappings = modelGen.elementToClassMapping(codeModel);
        Assert.assertEquals(3, mappings.size());
        Assert.assertEquals("test.getGeneratedElementsMultipleElements.Header", mappings.get("header"));
        Assert.assertEquals("test.getGeneratedElementsMultipleElements.OrderItems", mappings.get("order-items"));
        Assert.assertEquals("test.getGeneratedElementsMultipleElements.ZABCOrder", mappings.get("ZABCOrder"));

    }

    @Test
    public void getGeneratedElementsSingleElement() throws Exception {
        File xmlSchema = new File(XML_SCHEMA_PATH_2);
        XmlModelGenerator modelGen = new XmlModelGenerator();
        JCodeModel codeModel = modelGen.generateFromSchema(
                                                           xmlSchema, "test.getGeneratedElementsSingleElement", new File("target"));
        Map<String, String> mappings = modelGen.elementToClassMapping(codeModel);
        Assert.assertEquals(1, mappings.size());
        Assert.assertEquals("test.getGeneratedElementsSingleElement.ABCOrder", mappings.get("ABCOrder"));

    }

    @Test
    public void getGeneratedElementsXmlElementDecl() throws Exception {
        File xmlSchema = new File(XML_SCHEMA_PATH_3);
        XmlModelGenerator modelGen = new XmlModelGenerator();
        JCodeModel codeModel = modelGen.generateFromSchema(
                                                           xmlSchema, "test.getGeneratedElementsXmlElementDecl", new File("target"));
        Map<String, String> mappings = modelGen.elementToClassMapping(codeModel);
        Assert.assertEquals(2, mappings.size());
        Assert.assertEquals("test.getGeneratedElementsXmlElementDecl.ApplicationType", mappings.get("Application"));
        // Assert.assertEquals("test.getGeneratedElementsXmlElementDecl.ApplicationType", mappings.get("Application"));

    }

    @Test
    public void getRootElement() throws Exception {
        XmlModelGenerator modelGen = new XmlModelGenerator();
        QName rootName = modelGen.getRootElementName(new File(XML_INST_PATH));
        Assert.assertEquals(new QName("ABCOrder"), rootName);
    }

    @Test
    public void getRootElementNamespace() throws Exception {
        XmlModelGenerator modelGen = new XmlModelGenerator();
        QName rootName = modelGen.getRootElementName(new File(XML_INST_PATH_2));
        Assert.assertEquals(new QName("http://example.org", "ABCOrder"), rootName);
    }

    @Test
    public void listElementsFromSchema() throws Exception {
        XmlModelGenerator modelGen = new XmlModelGenerator();
        List<QName> elements = modelGen.getElementsFromSchema(new File(XML_SCHEMA_PATH));
        Assert.assertEquals(3, elements.size());
        Assert.assertTrue(elements.contains(new QName("urn:abc", "order-items")));
        Assert.assertTrue(elements.contains(new QName("urn:abc", "ZABCOrder")));
        Assert.assertTrue(elements.contains(new QName("urn:abc", "header")));
    }
}
