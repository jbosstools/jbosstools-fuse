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
package org.jboss.mapper.model.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.xml.sax.InputSource;

import com.sun.codemodel.JCodeModel;
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

        final SchemaCompiler sc = XJC.createSchemaCompiler();
        final FileInputStream schemaStream = new FileInputStream(schemaFile);
        final InputSource is = new InputSource(schemaStream);
        is.setSystemId(schemaFile.getAbsolutePath());

        sc.parseSchema(is);
        sc.forcePackageName(packageName);

        final S2JJAXBModel s2 = sc.bind();
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
}
