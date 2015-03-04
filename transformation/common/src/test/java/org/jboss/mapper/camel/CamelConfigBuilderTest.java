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
package org.jboss.mapper.camel;

import java.io.File;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.mapper.TransformType;
import org.jboss.mapper.dozer.DozerMapperConfiguration;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class CamelConfigBuilderTest {

    private static final String NEW_CONFIG = "new-camel-config.xml";
    private static final String MULTI_CONFIG = "multiple-camel-config.xml";
    private static final String XML_JSON = "xml-to-json.xml";
    private static final String JAVA_XML = "java-to-xml.xml";
    private static final String JAVA_JAVA = "java-to-java.xml";
    private static final String XML_JAVA = "xml-to-java.xml";
    private static final String BLUEPRINT_CONFIG = "blueprint-config.xml";
    private static final String NEW_BLUEPRINT_CONFIG = "new-blueprint-config.xml";

    @Test
    public void createXmlToJson() throws Exception {
        Document xmlJsonDoc = loadDocument(XML_JSON);
        CamelConfigBuilder config = CamelConfigBuilder.loadConfig(getFile(NEW_CONFIG));
        config.addTransformation("xml2json", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG,
                TransformType.XML, "xml.ABCOrder", 
                TransformType.JSON, "json.XYZOrder");
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(xmlJsonDoc, config.getConfiguration().getOwnerDocument());
    }
    
    @Test
    public void createJavaToXml() throws Exception {
        Document javaXmlDoc = loadDocument(JAVA_XML);
        CamelConfigBuilder config = CamelConfigBuilder.loadConfig(getFile(NEW_CONFIG));
        config.addTransformation("java2xml", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG,
                TransformType.JAVA, "source.Input", 
                TransformType.XML, "target.Output");
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(javaXmlDoc, config.getConfiguration().getOwnerDocument());
    }
    
    @Test
    public void createXmlToJava() throws Exception {
        Document xmlJavaDoc = loadDocument(XML_JAVA);
        CamelConfigBuilder config = CamelConfigBuilder.loadConfig(getFile(NEW_CONFIG));
        config.addTransformation("xml2java", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG,
                TransformType.XML, "source.Input", 
                TransformType.JAVA, "target.Output");
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(xmlJavaDoc, config.getConfiguration().getOwnerDocument());
    }
    
    @Test
    public void createJavaToJava() throws Exception {
        Document javaJavaDoc = loadDocument(JAVA_JAVA);
        CamelConfigBuilder config = CamelConfigBuilder.loadConfig(getFile(NEW_CONFIG));
        config.addTransformation("java2java", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG,
                TransformType.JAVA, "source.Input", 
                TransformType.JAVA, "target.Output");
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(javaJavaDoc, config.getConfiguration().getOwnerDocument());
    }
    
    @Test
    public void readSpringConfig() throws Exception {
        CamelSpringBuilder config = (CamelSpringBuilder)CamelConfigBuilder.loadConfig(getFile(XML_JSON));
        Assert.assertNotNull(config.getCamelContext());
        Assert.assertEquals(1, config.getCamelContext().getEndpoint().size());
        Assert.assertEquals(2, config.getCamelContext().getDataFormats()
                .getAvroOrBarcodeOrBase64().size());
    }
    
    @Test
    public void readBlueprintConfig() throws Exception {
        CamelBlueprintBuilder config = (CamelBlueprintBuilder)CamelConfigBuilder.loadConfig(getFile(BLUEPRINT_CONFIG));
        Assert.assertNotNull(config.getCamelContext());
        Assert.assertEquals(1, config.getCamelContext().getEndpoint().size());
        Assert.assertEquals(2, config.getCamelContext().getDataFormats()
                .getAvroOrBarcodeOrBase64().size());
    }
    
    @Test
    public void createBlueprintConfig() throws Exception {
        Document blueprintDoc = loadDocument(BLUEPRINT_CONFIG);
        CamelConfigBuilder config = CamelConfigBuilder.loadConfig(getFile(NEW_BLUEPRINT_CONFIG));
        config.addTransformation("xml2json", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG,
                TransformType.XML, "abcorder.ABCOrder", 
                TransformType.JSON, "xyzorderschema.XyzOrderSchema");
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(blueprintDoc, config.getConfiguration().getOwnerDocument());
    }
    
    @Test
    public void existingConfig() throws Exception {
        // Add another transform endpoint to a config that already has one
        Document multiDoc = loadDocument(MULTI_CONFIG);
        CamelConfigBuilder config = CamelConfigBuilder.loadConfig(getFile(XML_JSON));
        config.addTransformation("xml2json2", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG,
                TransformType.XML, "org.foo.ABCOrder", 
                TransformType.JSON, "json.XYZOrder");
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(multiDoc, config.getConfiguration().getOwnerDocument());
    }
    
    private Document loadDocument(String path) throws Exception {
        return XMLUnit.buildControlDocument(new InputSource(
                getClass().getResourceAsStream(path)));
    }
    
    private File getFile(String fileName) {
        return new File("src/test/resources/org/jboss/mapper/camel/" + fileName);
    }
}
