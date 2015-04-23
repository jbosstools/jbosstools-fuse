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
package org.jboss.tools.fuse.transformation.model.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

/**
 * Model generator for XML type definitions. This generator supports model
 * generation from XML schema and XML instance data.
 */
public class XmlModelGenerator {

    /**
     * Generates Java classes in targetPath directory given an XML schema.
     * 
     * @param schemaFile file reference to the XML schema
     * @param packageName package name for generated model classes
     * @param targetPath directory where class source will be generated
     * @return the generated code model
     * @throws Exception failure during model generation
     */
    public JCodeModel generateFromSchema(final File schemaFile, final String packageName,
            final File targetPath) throws Exception {

        final SchemaCompiler sc = createSchemaCompiler(schemaFile);
        sc.forcePackageName(packageName);

        final S2JJAXBModel s2 = sc.bind();
        if (s2 == null) {
            throw new Exception("Failed to parse schema into JAXB Model");
        }
        final JCodeModel jcm = s2.generateCode(null, null);
        try (PrintStream status = new PrintStream(new ByteArrayOutputStream())) {
            jcm.build(targetPath, status);
        }

        return jcm;
    }

    /**
     * Generates Java classes in targetPath directory given an XML instance
     * document. This method generates a schema at the path specified by
     * schemaFile and then calls generateFromSchema to generate Java classes.
     * 
     * @param instanceFile file containing xml instance document
     * @param schemaFile a file reference where the schema should be generated
     * @param packageName package name for generated model classes
     * @param targetPath directory where class source will be generated
     * @return the generated code model
     * @throws Exception failure during model generation
     */
    public JCodeModel generateFromInstance(final File instanceFile, final File schemaFile,
            final String packageName, final File targetPath) throws Exception {
        // Step 1 - generate schema from instance doc
        final Inst2XsdOptions options = new Inst2XsdOptions();
        options.setDesign(Inst2XsdOptions.DESIGN_RUSSIAN_DOLL);
        final XmlObject[] xml = new XmlObject[] {XmlObject.Factory.parse(instanceFile)};
        final SchemaDocument[] schemaDocs = Inst2Xsd.inst2xsd(xml, options);
        schemaDocs[0].save(schemaFile, new XmlOptions().setSavePrettyPrint());

        // Step 2 - call generateFromSchema with generated schema
        return generateFromSchema(schemaFile, packageName, targetPath);
    }
    
    /**
     * Returns a list of top-level element definitions for a given schema. 
     * @param schemaFile the schema to check for top-level elements
     * @return list of elements 
     * @throws Exception
     */
    public List<QName> getElementsFromSchema(final File schemaFile) throws Exception {
        List<QName> elements = new LinkedList<QName>();
        SchemaCompiler sc = createSchemaCompiler(schemaFile);
        final S2JJAXBModel s2 = sc.bind();
        if (s2 == null) {
            throw new Exception("Failed to parse schema into JAXB Model");
        }
        for (Mapping mapping : s2.getMappings()) {
            elements.add(mapping.getElement());
        }
        
        return elements;
    }
    
    /**
     * Returns the qualified name of the root element in the specified XML instance
     * document.
     * @param instanceFile XML instance doc
     * @return root element name
     * @throws Exception failed to parse instance document
     */
    public QName getRootElementName(final File instanceFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(instanceFile);
        Element root = doc.getDocumentElement();
        if (root == null) {
            throw new Exception("Invalid instance document : no root element");
        }
        return new QName(root.getNamespaceURI(), root.getNodeName());
    }
    
    /**
     * Returns a mapping of element names to generated Java classes for a given 
     * JCodeModel.  Note that the element names are not qualfied, they are all the
     * local name of the element.
     * @param model generated model from a schema or instance document
     * @return map with element names as keys and class names as values
     */
    public Map<String, String> elementToClassMapping(JCodeModel model) {
        Map<String, String> mappings = new HashMap<String, String>();
        Iterator<JPackage> packageIt = model.packages();
        // We need to search through all generated classes of generated packages
        while (packageIt.hasNext()) {
            Iterator<JDefinedClass> classIt = packageIt.next().classes();
            while (classIt.hasNext()) {
                JDefinedClass jClass = classIt.next();
                // Top-level elements will result in classes with the @XmlRootElement annotation
                // which is all we care about.
                for (JAnnotationUse ann : jClass.annotations()) {
                    if (ann.getAnnotationClass().fullName().equals(XmlRootElement.class.getName())) {
                        JAnnotationValue jVal = ann.getAnnotationMembers().get("name");
                        StringWriter sw = new StringWriter();
                        jVal.generate(new JFormatter(sw));
                        mappings.put(sw.toString().replaceAll("\"", ""), jClass.fullName());
                    }
                }
            }
        }
        return mappings;
    }
    
    private SchemaCompiler createSchemaCompiler(final File schemaFile) throws Exception {
        final SchemaCompiler sc = XJC.createSchemaCompiler();
        final FileInputStream schemaStream = new FileInputStream(schemaFile);
        final InputSource is = new InputSource(schemaStream);

        // to work around windows platform issue
        String id = schemaFile.getAbsolutePath().replace('\\', '/');
        is.setSystemId(id);
        
        sc.parseSchema(is);
        return sc;
    }
}
