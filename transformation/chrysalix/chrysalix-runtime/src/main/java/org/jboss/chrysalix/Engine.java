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
import org.jboss.chrysalix.common.I18n;
import org.jboss.chrysalix.transformer.AppendTransformer;
import org.jboss.chrysalix.transformer.MapTransformer;
import org.jboss.chrysalix.transformer.StoreTransformer;
import org.jboss.chrysalix.transformer.ToUppercaseTransformer;

// TODO API for individual mappings, including blocks
// TODO mult. sources/targets
// TODO twitter to salesforce, soap to rest
// TODO relative paths
// TODO if/loop
// TODO JSON/CSV/HL7/EDI/binary
// TODO fill lists
// TODO file repository
// ==== vs. Dozer
// chained transformations
// could run in parallel
// can update existing target
// extensible transformations and data formats
// lists
// same-named nodes
// namespaces
// data preview
// debugging
// if/loop
// no 3rd-party libraries necessary for different data formats
// mappings based on relative paths
/**
 * The Chrysalix runtime engine provides the means to map data from any source to any target.
 * Source data must first be interpreted by a supplied {@link DataFormatHandler Data format handler} and
 * {@link #toSourceNode(Object, DataFormatHandler, Repository) converted} into a generic {@link Node nodes} stored in a supplied
 * {@link Repository repository}.
 * A target node must then be {@link #toTargetNode(Object, DataFormatHandler, Repository) created} using an independent handler and
 * repository, optionally containing its own data (as determined by the handler).
 * The data in the source nodes can then be {@link #map(Node, Node, String) mapped} to the target node using a supplied mappings
 * file.
 * Finally, the target node can be {@link #toTargetData(Node, DataFormatHandler) converted} to a data format using another
 * independent handler.
 * Note, the same or different handlers and/or repositories may be used in any part of this process.
 */
public class Engine {

	private static final String SOURCE = "<source>";
    private static final String TARGET = "<target>";

    public static void debug(Node node) {
    	debug(node, 0);
    }

    private static void debug(Node node,
                              int level) {
		println(level, node.qualifiedName() + ": type=" + node.type() + ", index=" + node.index() + ", list=" + node.isList() + ", value=" + node.value());
		Attribute[] attrs = node.attributes();
		if (attrs.length > 0) {
			println(++level, "attributes:");
			level++;
			for (Attribute attr : node.attributes()) {
				println(level, "@" + attr.qualifiedName() + ": type=" + attr.type() + ", value=" + attr.value());
			}
			level -= 2;
		}
		Node[] children = node.children();
		if (children.length > 0) {
			println(++level, "children:");
			level++;
			for (Node child : children) {
				debug(child, level);
			}
		}
    }

    private static void indent(int level) {
    	for (int ndx = level; --ndx >= 0;) {
			System.out.print("  ");
    	}
    }

    private static void println(int level,
                                String text) {
    	indent(level);
		System.out.println(text);
    }

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

	/**
	 * Splits a slash-delimited (/) path reference into a list of each node/attribute reference within it.
	 * <p>
	 * Node references must be in the form:
	 * </p><p>
	 * <code>{&lt;<b>namespace</b>>}:&lt;<b>name</b>>[<b>[</b>&lt;<b>index</b>><b>]</b>]</code>
	 * </p><p>
	 * Attribute references must be in the form:
	 * </p><p>
	 * <code>&lt;<b>node reference as above</b>><b>/@</b>&lt;<b>name</b>></code>
	 * </p>
	 *
	 * @param path
	 * 		A path to a node or attribute in a repository.
	 * @return a list of each node reference within the supplied path.
	 */
    public static String[] splitPath(String path) {
    	Arg.notNull(path, "path");
        List<String> names = new ArrayList<>();
        String[] rawNames = path.split("/");
        StringBuilder builder = new StringBuilder();
        for (String name : rawNames) {
        	if (builder.length() == 0) {
                int ndx = name.indexOf('{');
                if (ndx >= 0) {
                	if (name.indexOf('}') > 0) { // name w/ ns w/o slashes
                		names.add(name);
                	} else if (builder.length() == 0) { // beginning of ns w/ slash(es)
                        builder.append(name);
                	}
                } else { // name w/o ns
                    names.add(name);
                }
        	} else {
        		builder.append('/');
        		builder.append(name);
            	if (name.indexOf('}') > 0) { // end of ns plus name
            		names.add(builder.toString());
            		builder.setLength(0);
            	}
        	}
        }
        return names.toArray(new String[names.size()]);
    }

    private Map<String, Class<? extends Transformer>> transformerByName = new HashMap<>();

    /**
     * Creates the engine and registers all built-in {@link Transformer transformers}
     */
    public Engine() {
        registerTransformer(MapTransformer.class);
        registerTransformer(StoreTransformer.class);
        registerTransformer(AppendTransformer.class);
        registerTransformer(ToUppercaseTransformer.class);
    }

    /**
     * Maps the supplied source Node to the supplied target Node using the supplied mappings file.
     *
     * @param sourceNode
     * 		The node representing the root of the source data used in the mappings.
     * @param targetNode
     * 		The node representing the root of the target data created or updated by the mappings.
     * @param mappingsFilePath
     * 		The path to the mappings file used to perform the mappings.
     * @throws Exception if any error occurs.
     */
    public void map(Node sourceNode,
                    Node targetNode,
                    String mappingsFilePath) throws Exception {
        Arg.notNull(sourceNode, "sourceNode");
        Arg.notNull(targetNode, "targetNode");
        Arg.notEmpty(mappingsFilePath, "mappingsFilePath");
        File file = new File(mappingsFilePath);
        if (!file.exists()) {
        	throw new IllegalArgumentException(I18n.localize(getClass(), "Mappings file does not exist: %s", file));
        }
        Map<String, Object> context = new HashMap<>();
        context.put(Transformer.DATA, null);
        context.put(Transformer.SOURCE_FILE_NODE, sourceNode);
        context.put(Transformer.TARGET_FILE_NODE, targetNode);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lineNumber = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), lineNumber++) {
                lineNumber = transform(lineNumber, line, mappingsFilePath, context, reader);
            }
        }
    }

    /**
     * Registers the supplied Transformer class as available for use in {@link #map(Node, Node, String) mappings}.
     *
     * @param transformerClass
     * 		A class that transforms data in some way.
     * @return <code>true</code> if the supplied transformerClass was successfully registered; <code>false</code> if it was
     * 		previously registered.
     */
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

    private Node rootNode(String name,
                          DataFormatHandler handler,
                          Repository repository) {
        Arg.notNull(handler, "handler");
        Arg.notNull(repository, "repository");
        String handlerName = handler.getClass().getSimpleName();
        Node rootNode = repository.rootNode(handlerName);
        if (rootNode == null) {
        	rootNode = repository.newRootNode(handlerName);
        }
        Node node = rootNode.child(TARGET);
        return node == null ? rootNode.addChild(TARGET, null): node;
    }

    /**
     * @param data
     * 		The source data to be converted to nodes. The data's type is dependent upon the supplied handler.
     * @param handler
     * 		The data format handler used to interpret and convert the supplied data to nodes.
     * @param repository
     * 		The repository containing the nodes that represent the data.
     * @return the node within the supplied repository representing the root of the supplied data.
     * @throws Exception if any error occurs.
     */
    public Node toSourceNode(Object data,
                             DataFormatHandler handler,
                             Repository repository) throws Exception {
        Arg.notNull(data, "data");
        return handler.toSourceNode(data, rootNode(SOURCE, handler, repository));
    }

    /**
     * @param targetNode
     * 		The target node to be converted to data in the format determined by the supplied handler.
     * @param handler
     * 		The data format handler used to convert the supplied node to data in a handler-determined format.
     * @return the node within the supplied repository representing the root of the target data.
     * @throws Exception if any error occurs.
     */
    public Object toTargetData(Node targetNode,
                               DataFormatHandler handler) throws Exception {
        Arg.notNull(targetNode, "targetNode");
        Arg.notNull(handler, "handler");
        return handler.toTargetData(targetNode);
    }

    /**
     * @param data The data used to create a new, empty target node or to convert existing data into a target node. The data's type
     * 		is dependent upon the supplied handler.
     * @param handler
     * 		The data format handler used to interpret and convert the supplied data to nodes.
     * @param repository
     * 		The repository containing the nodes that represent the data.
     * @return the node within the supplied repository representing the root of the supplied data.
     * @throws Exception if any error occurs.
     */
    public Node toTargetNode(Object data,
                             DataFormatHandler handler,
                             Repository repository) throws Exception {
        Arg.notNull(data, "data");
        return handler.toTargetNode(data, rootNode(TARGET, handler, repository));
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
            }
            transformer.transformAfterBlock(context);
        } catch (Exception e) {
            throw new MappingException(e, "Exception in mappings file \"%s\" at line %d: %s", mappingsFileName, lineNumber, line);
        }
        return lineNumber;
    }
}
