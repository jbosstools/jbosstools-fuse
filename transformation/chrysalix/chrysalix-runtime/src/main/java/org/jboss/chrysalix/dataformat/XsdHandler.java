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
package org.jboss.chrysalix.dataformat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XsdHandler extends XmlHandler {

    private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    Node fileNode;
    LinkedList<String> deferredElements = new LinkedList<>();
    boolean resolving;
    int elementId;
    String elementPath = "";

    @Override
    public void load(Node fileNode) throws Exception {
        this.fileNode = fileNode;
        super.load(fileNode);
        // Parse again until all deferred references have been resolved
        resolving = true;
        while (!deferredElements.isEmpty()) {
            elementId = 0;
            super.load(fileNode);
        }
        resolving = false;
    }

    @Override
    XmlSaxHandler newSaxHandler(Node fileNode) {
        return new XsdSaxHandler(fileNode);
    }

    class XsdSaxHandler extends XmlSaxHandler {

        private String xsdPrefix;
        private String targetNs;
        private boolean deferring;
        private String skipping;
        private LinkedList<Boolean> nodeAdded = new LinkedList<>();
        private Map<String, String> namespaceByPrefix = new HashMap<>();

        XsdSaxHandler(Node fileNode) {
            super(fileNode);
        }

        @Override
        public void characters(char[] chrs,
                               int start,
                               int length) {
            if (skipping == null && !resolving) super.characters(chrs, start, length);
        }

        @Override
        public void comment(char[] chrs,
                            int start,
                            int length) {
        }

        private void copy(Node from,
                          Node to) {
            to.setValue(from.value());
            for (Attribute attr : from.attributes()) {
                to.addAttribute(attr.qualifiedName(), attr.type());
                to.setValue(attr.value());
            }
            for (Node refChild : from.children()) {
                copy(refChild, to.addChild(refChild.namespace(), refChild.name(), refChild.type()));
            }
        }

        private void defer() {
            deferredElements.add(elementPath);
            deferring = true;
        }

        @Override
        public void endElement(String uri,
                               String localName,
                               String qName) {
            if (deferring && deferredElements.peekLast().equals(elementPath)) deferring = false;
            if (nodeAdded.pop()) super.endElement(uri, localName, qName);
            if (elementPath.equals(skipping)) skipping = null;
            elementPath = elementPath.substring(0, elementPath.lastIndexOf('-'));
        }

        private Node rootNode(String name) {
            int ndx = name.indexOf(':');
            String prefix = ndx < 0 ? "" : name.substring(0, ndx);
            return fileNode.child(namespaceByPrefix.get(prefix), name.substring(ndx + 1));
        }

        @Override
        public void startElement(String uri,
                                 String localName,
                                 String qName,
                                 Attributes attributes) throws SAXException {
            elementId++;
            elementPath += "-" + elementId;
            boolean nodeAdded = false;
            try {
                if (deferring) return;
                if (!XSD_NS.equals(uri)) return;
                if ("schema".equals(localName)) {
                    // TODO handle chameleons
                    targetNs = attributes.getValue("targetNamespace");
                    return;
                }
                if ("include".equals(localName) || "import".equals(localName)) {
                    File file = new File(attributes.getValue("schemaLocation"));
                    if (!file.exists()) file = new File(fileNode.parent().path(), file.getPath());
                    try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()))) {
                        parse(fileNode, stream);
                    } catch (IOException | ParserConfigurationException e) {
                        throw new SAXException(e);
                    }
                    return;
                }
                if (resolving) {
                    if (elementPath.equals(deferredElements.peek())) deferredElements.pop();
                    else return;
                }
                if (("complexType".equals(localName) || "simpleType".equals(localName)) && node == fileNode) {
                    node = fileNode.addChild(targetNs, attributes.getValue("name"), null);
                    node.addAttribute("<type>", "boolean").setValue(true);
                    nodeAdded = true;
                } else if ("element".equals(localName)) {
                    String name = attributes.getValue("name");
                    if (name == null) {
                        Node ref = rootNode(attributes.getValue("ref"));
                        node = node.addChild(ref.namespace(), ref.name(), ref.type());
                        copy(ref, node);
                    } else {
                        String type = attributes.getValue("type");
                        if (type != null && !xsdReference(type)) {
                            Node rootNode = rootNode(type);
                            if (rootNode == null) {
                                defer();
                                return;
                            }
                            type = rootNode.type();
                        }
                        node = node.addChild(targetNs, name, type);
                    }
                    nodeAdded = true;
                    String max = attributes.getValue("maxOccurs");
                    if (max != null) {
                        try {
                            if (Integer.valueOf(max) > 1) node.setList(true);
                        } catch (NumberFormatException e) {
                            if ("unbounded".equals(max)) node.setList(true);
                        }
                    }
                } else if ("attribute".equals(localName)) {
                    node.addAttribute(attributes.getValue("name"), attributes.getValue("type"));
                } else if ("extension".equals(localName) || "restriction".equals(localName)) {
                    String type = attributes.getValue("base");
                    if (!xsdReference(type)) {
                        Node rootNode = rootNode(type);
                        if (rootNode == null) {
                            defer();
                            return;
                        }
                        type = rootNode.type();
                    }
                    node.setType(type);
                } else skipping = elementPath;
            } finally {
                this.nodeAdded.push(nodeAdded);
            }
        }

        @Override
        public void startPrefixMapping(String prefix,
                                       String uri) {
            super.startPrefixMapping(prefix, uri);
            if (XSD_NS.equals(uri)) xsdPrefix = prefix;
            namespaceByPrefix.put(prefix, uri);
        }

        private boolean xsdReference(String name) {
            int ndx = name.indexOf(':');
            String prefix;
            if (ndx < 0) prefix = "";
            else prefix = name.substring(0, ndx);
            return xsdPrefix.equals(prefix);
        }
    }
}
