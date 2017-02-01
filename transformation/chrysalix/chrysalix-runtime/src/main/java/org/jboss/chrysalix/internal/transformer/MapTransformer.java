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
package org.jboss.chrysalix.internal.transformer;

import java.util.Map;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.Engine;
import org.jboss.chrysalix.MappingException;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.spi.Entity;
import org.jboss.chrysalix.spi.Transformer;

public class MapTransformer implements Transformer {

    public MapTransformer() {}

    @Override
    public void transform(Map<String, Object> context,
                          String[] arguments) throws Exception {
        if (arguments.length < 2) {
            throw new MappingException("Syntax is \"map [from] <source path> [to] <target path> [{]\"");
        }
        int argNdx = 0;
        // Find source node for mapping
        String arg = arguments[argNdx++];
        if ("from".equals(arg)) {
            arg = arguments[argNdx++];
        }
        Object source;
        if (arg.startsWith("\"") || arg.startsWith("'")) {
            source = arg;
        } else {
            String sourcePath = arg;
            source = context.get(SOURCE_FILE_NODE);
            for (String name : Engine.splitPath(sourcePath)) {
                if (name.isEmpty()) {
                    continue;
                }
                Node sourceNode = (Node)source;
                if (name.startsWith("@")) {
                    if (!(source instanceof Node)) {
                        throw new MappingException("Invalid attribute reference \"%s\" in source path", name);
                    }
                    String attrName = name.substring(1);
                    source = sourceNode.attribute(attrName);
                    if (source == null) {
                        throw new MappingException("Unknown attribute \"%s\" in source path", attrName);
                    }
                    break;
                }
                source = sourceNode.child(name);
                if (source == null) {
                    throw new MappingException("Unknown node \"%s\" in source path", name);
                }
            }
        }
        context.put(SOURCE, source);
        // Determine source data
        context.put(DATA, source instanceof Entity ? ((Entity)source).value() : source);
        // Find target node for mapping
        arg = arguments[argNdx++];
        if ("to".equals(arg)) {
            arg = arguments[argNdx++];
        }
        String targetPath = arg;
        Entity targetEntity = (Entity)context.get(TARGET_FILE_NODE);
        for (String name : Engine.splitPath(targetPath)) {
            if (name.isEmpty()) {
                continue;
            }
            Node targetNode = (Node)targetEntity;
            if (name.startsWith("@")) {
                String attrName = name.substring(1);
                Attribute attr = targetNode.attribute(attrName);
                targetEntity = attr == null ? targetNode.addAttribute(attrName, null) : attr;
                break;
            }
            Node node = targetNode.child(name);
            targetEntity = node == null ? targetNode.addChild(name, null) : node;
        }
        context.put(TARGET_ENTITY, targetEntity);
    }

    @Override
    public void transformAfterBlock(Map<String, Object> context) {
        // Map value
        Object data = context.get(DATA);
        ((Entity)context.get(TARGET_ENTITY)).setValue(data instanceof String ? Transformer.removeQuotes(data.toString()) : data);
    }
}
