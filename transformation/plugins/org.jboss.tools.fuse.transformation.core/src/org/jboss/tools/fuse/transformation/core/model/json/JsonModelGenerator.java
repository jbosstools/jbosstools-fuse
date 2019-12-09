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
package org.jboss.tools.fuse.transformation.core.model.json;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;

import com.sun.codemodel.JCodeModel;

/**
 * Model generator for JSON type definitions. This generator supports model
 * generation from JSON schema and JSON instance data.
 */
public class JsonModelGenerator {

    private final JsonGenerationConfig config;

    /**
     * Create a new XmlModelGenerator with default configuration.
     */
    public JsonModelGenerator() {
        this(new JsonGenerationConfig());
    }

    /**
     * Configuration used to control model generation behavior.
     * 
     * @param config
     */
    public JsonModelGenerator(final JsonGenerationConfig config) {
        this.config = config;
    }

    /**
     * Generates Java classes in targetPath directory given a JSON schema.
     * 
     * @param className name of the top-level class used for the generated model
     * @param packageName package name for generated model classes
     * @param schemaUrl url for the JSON schema
     * @param targetPath directory where class source will be generated
     * @throws IOException failure during model generation
     */
    public JCodeModel generateFromSchema(final String className, final String packageName,
            final URL schemaUrl, final File targetPath) throws IOException {

        return generate(className, packageName, schemaUrl, targetPath);
    }

    /**
     * Generates Java classes in targetPath directory given a JSON instance
     * document.
     * 
     * @param className name of the top-level class used for the generated model
     * @param packageName package name for generated model classes
     * @param instanceUrl url for the JSON message containing instance data
     * @param targetPath directory where class source will be generated
     * @throws IOException failure during model generation
     */
    public JCodeModel generateFromInstance(final String className, final String packageName,
            final URL instanceUrl, final File targetPath) throws IOException {

        config.setSourceType(SourceType.JSON);
        return generate(className, packageName, instanceUrl, targetPath);
    }

    private JCodeModel generate(final String className, final String packageName,
            final URL inputUrl, final File targetPath) throws IOException {

        final SchemaMapper mapper = createSchemaMapper();
        final JCodeModel codeModel = new JCodeModel();
        mapper.generate(codeModel, className, packageName, inputUrl);
        try (PrintStream status = new PrintStream(new ByteArrayOutputStream())) {
            codeModel.build(targetPath, status);
        }

        return codeModel;
    }

    private SchemaMapper createSchemaMapper() {
        final RuleFactory ruleFactory = new RuleFactory();
        ruleFactory.setAnnotator(new Jackson2Annotator() {

            @Override
            public boolean isAdditionalPropertiesSupported() {
                return false;
            }
        });
        ruleFactory.setGenerationConfig(config);
        return new SchemaMapper(ruleFactory, new SchemaGenerator());
    }
}
