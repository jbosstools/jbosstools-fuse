/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved.  See the COPYRIGHT.txt file distributed with this work
 * for information regarding copyright ownership.  Some portions may be
 * licensed to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * Chrysalix is free software. Unless otherwise indicated, all code in
 * Chrysalix is licensed to you under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * Chrysalix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.chrysalix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.chrysalix.common.Arg;
import org.jboss.chrysalix.internal.transformer.AppendTransformer;
import org.jboss.chrysalix.internal.transformer.MapTransformer;
import org.jboss.chrysalix.internal.transformer.StoreTransformer;
import org.jboss.chrysalix.internal.transformer.ToUppercaseTransformer;
import org.jboss.chrysalix.spi.Transformer;

// TODO API for individual mappings, including blocks
// TODO mult. sources/targets
// TODO twitter to salesforce, soap to rest
// TODO recursive elements
// TODO relative paths
// TODO if/loop
// TODO JSON/Java
// TODO fill lists
// TODO file repository
public class Engine {

	private static String[] splitLine(String line,
                                      String transformerName,
                                      Map<String, Object> context) throws MappingException {
        boolean inString = false;
        boolean inVariable = false;
        boolean escaping = false;
        List<String> args = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (char chr : line.toCharArray()) {
            if (escaping) {
                builder.append(chr);
                escaping = false;
            } else if (chr == '\\') {
                if (inVariable) {
                    throw new MappingException("Variable beginning with \"%s\" cannot contain a backslash", builder);
                }
                escaping = true;
            } else if (chr == '"' || chr == '\'') {
                if (inVariable) {
                    throw new MappingException("Variable beginning with \"%s\" cannot contain a quote", builder);
                }
                inString = !inString;
                builder.append(chr);
            } else if (chr == ' ' || chr == '\t') {
                if (inVariable) {
                    throw new MappingException("Variable beginning with \"%s\" cannot contain whitespace", builder);
                }
                if (inString) {
                    builder.append(chr);
                } else {
                    args.add(builder.toString());
                    builder.setLength(0);
                }
            } else if (chr == '$') {
                builder.append(chr);
                if (inVariable) {
                    inVariable = false;
                    if (!"store".equals(transformerName)) {
                        Object data = context.get(builder.toString());
                        if (data == null) {
                            throw new MappingException("Unknown variable reference: %s", builder);
                        }
                        builder.setLength(0);
                        builder.append(data);
                    }
                } else {
                    inVariable = true;
                }
            } else {
                builder.append(chr);
            }
        }
        if (builder.length() > 0) {
            args.add(builder.toString());
        }
        return args.toArray(new String[args.size()]);
    }

    public static String[] splitPath(String path) {
        List<String> names = new ArrayList<>();
        String[] rawNames = path.split("/");
        StringBuilder builder = new StringBuilder();
        for (String name : rawNames) {
            if (name.startsWith("{")) {
                if (name.endsWith("}")) {
                    builder.append(name.substring(1, name.length() - 1));
                } else {
                    builder.append(name.substring(1));
                }
            } else {
                int ndx = name.indexOf('}');
                if (ndx >= 0) {
                    builder.append('/');
                    builder.append(name.substring(0, ndx));
                    builder.append(name.substring(ndx + 1));
                    names.add(builder.toString());
                    builder.setLength(0);
                } else if (builder.length() > 0) {
                    builder.append('/');
                    builder.append(name);
                } else {
                    names.add(name);
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

    private String registryFolder;

    private Map<String, Class<? extends Transformer>> transformerByName = new HashMap<>();

    public Engine(String registryFolder) {
        Arg.notEmpty(registryFolder, "registryFolder");
        this.registryFolder = registryFolder;
        this.registryFolder.toString();
        registerTransformer(MapTransformer.class);
        registerTransformer(StoreTransformer.class);
        registerTransformer(AppendTransformer.class);
        registerTransformer(ToUppercaseTransformer.class);
    }

    public void map(Node sourceFileNode,
                    Node targetFileNode,
                    String mappingsFileName) throws Exception {
        Arg.notNull(sourceFileNode, "sourceFileNode");
        Arg.notNull(targetFileNode, "targetFileNode");
        Arg.notEmpty(mappingsFileName, "mappingsFileName");
        Map<String, Object> context = new HashMap<>();
        context.put(Transformer.DATA, null);
        context.put(Transformer.SOURCE_FILE_NODE, sourceFileNode);
        context.put(Transformer.TARGET_FILE_NODE, targetFileNode);
        try (BufferedReader reader = new BufferedReader(new FileReader(mappingsFileName))) {
            int lineNumber = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), lineNumber++) {
                lineNumber = transform(lineNumber, line, mappingsFileName, context, reader);
            }
        }
    }

    public boolean registerTransformer(Class<? extends Transformer> transformerClass) {
        Arg.notNull(transformerClass, "transformerClass");
        // TODO figure out how jar is passed to camel dozer from transformation.core
        String name = transformerClass.getSimpleName();
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());
        String suffix = Transformer.class.getSimpleName();
        if (name.endsWith(suffix)) {
        	name = name.substring(0, name.length() - suffix.length());
        }
        return transformerByName.putIfAbsent(name, transformerClass) == null;
    }

    public void toFile(Node fileNode,
                       DataFormatHandler handler) throws Exception {
        Arg.notNull(fileNode, "fileNode");
        Arg.notNull(handler, "handler");
        handler.save(fileNode);
    }

    public Node toNode(String filePath,
                       DataFormatHandler handler,
                       Repository repository) throws Exception {
        Arg.notEmpty(filePath, "filePath");
        Arg.notNull(handler, "handler");
        Arg.notNull(repository, "repository");
        // Create node, including ancestors, for file path
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        Node node = null;
        for (String name : splitPath(filePath)) {
            if (name.isEmpty()) {
            	continue;
            }
            node = node == null ? repository.newRootNode(name) : node.addChild(null, name, null);
        }
        // Create file node's contents using applicable handler
        if (file.exists()) {
        	handler.load(node);
        }
        return node;
    }

    private int transform(int lineNumber,
                          String line,
                          String mappingsFileName,
                          Map<String, Object> context,
                          BufferedReader reader) throws Exception {
        line = line.trim();
        if (line.startsWith("#")) {
            return lineNumber; // comment
        }
        int ndx = line.indexOf(' ');
        String transformerName = line.substring(0, ndx < 0 ? line.length() : ndx);
        Class<? extends Transformer> transformerClass = transformerByName.get(transformerName);
        if (transformerClass == null) {
            throw new MappingException("Unknown transformation in mappings file \"%s\" at line %d: %s",
                                       mappingsFileName, lineNumber, line);
        }
        Transformer transformer = transformerClass.newInstance();
        line = line.substring(ndx + 1);
        boolean hasBlock = line.endsWith(" {");
        if (hasBlock) {
        	line = line.substring(0, line.lastIndexOf(' '));
        }
        try {
            String[] args = splitLine(line, transformerName, context);
            // Process transformations if supplied
            transformer.transform(context, args);
            if (hasBlock) {
                lineNumber++;
                for (line = reader.readLine(); line != null && !"}".equals(line.trim()); line = reader.readLine(), lineNumber++) {
                    lineNumber = transform(lineNumber, line, mappingsFileName, context, reader);
                }
                lineNumber++;
                transformer.transformAfterBlock(context);
            }
        } catch (Exception e) {
            throw new MappingException(e, "Exception in mappings file \"%s\" at line %d: %s", mappingsFileName, lineNumber, line);
        }
        return lineNumber;
    }
}
