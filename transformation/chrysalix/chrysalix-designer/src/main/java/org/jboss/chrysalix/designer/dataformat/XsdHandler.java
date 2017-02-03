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
package org.jboss.chrysalix.designer.dataformat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.common.I18n;
import org.jboss.chrysalix.dataformat.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XsdHandler extends XmlHandler {

    private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
    private static final String GLOBAL_NON_ELEMENT = "<globalNonElement>";
    private static final String UNRESOLVED = "<unresolved>";

    Node fileNode;
    LinkedList<String> deferredReferences = new LinkedList<>();
    boolean resolving;
    int elementId;
    String elementPath = "";

    @Override
    public void load(Node fileNode) throws Exception {
        this.fileNode = fileNode;
        super.load(fileNode);
        // Parse again until all deferred references have been resolved
        resolving = true;
        while (!deferredReferences.isEmpty()) {
            elementId = 0;
            int size = deferredReferences.size();
            super.load(fileNode);
            if (deferredReferences.size() == size) {
            	throw new Exception(I18n.localize(getClass(), "One ore more deferred references are never resolvable: %s", deferredReferences));
            }
        }
        resolving = false;
        // Remove all global entities that are not elements
        for (Node node : fileNode.children()) {
        	if (node.attribute(GLOBAL_NON_ELEMENT) != null) {
        		node.remove();
        	}
        }
    }

    @Override
    protected XmlSaxHandler newSaxHandler(Node fileNode) {
        return new XsdSaxHandler(fileNode);
    }

    // TODO elem w/maxOccurs=0 or attr w/use="prohibited"
    // TODO model and attr groups
    // TODO handle chameleons
    class XsdSaxHandler extends XmlSaxHandler {

        private String xsdPrefix;
        private String targetNs;
        private boolean deferring;
        private LinkedList<Boolean> nodeChanged = new LinkedList<>();
        private Map<String, String> namespaceByPrefix = new HashMap<>();

        XsdSaxHandler(Node fileNode) {
            super(fileNode);
        }

        @Override
        public void characters(char[] chrs,
                               int start,
                               int length) {
        }

        @Override
        public void comment(char[] chrs,
                            int start,
                            int length) {
        }

        private void copy(Node from,
                          Node to) {
            to.setList(from.list());
            to.setValue(from.value());
            for (Attribute attr : from.attributes()) {
            	if (GLOBAL_NON_ELEMENT.equals(attr.qualifiedName())) {
            		continue;
            	}
                Attribute toAttr = to.addAttribute(attr.qualifiedName(), attr.type());
                toAttr.setValue(attr.value());
            }
            for (Node fromChild : from.children()) {
                copy(fromChild, to.addChild(fromChild.namespace(), fromChild.name(), fromChild.type()));
            }
        }

        private void defer() {
            deferredReferences.add(elementPath);
            deferring = true;
            // Mark global node (if exists) as unresolved so references remain deferred
            if (this.node == fileNode) return;
            Node node = this.node;
            while (node.parent() != fileNode) {
            	node = node.parent();
            }
            node.addAttribute(UNRESOLVED, "boolean").setValue(true);
        }

        @Override
        public void endElement(String uri,
                               String localName,
                               String qName) {
            if (deferring && deferredReferences.peekLast().equals(elementPath)) deferring = false;
            if (nodeChanged.pop()) super.endElement(uri, localName, qName);
            elementPath = elementPath.substring(0, elementPath.lastIndexOf('-'));
        }

        private Node referencedNode(String name) {
            int ndx = name.indexOf(':');
            String prefix = ndx < 0 ? "" : name.substring(0, ndx);
            Node node = fileNode.child(namespaceByPrefix.get(prefix), name.substring(ndx + 1));
            return node == null || node.attribute(UNRESOLVED) != null ? null : node;
        }

        private boolean resolvingThisElementPath() {
            if (elementPath.equals(deferredReferences.peek())) {
            	deferredReferences.pop();
                // Remove unresolved marker from global node (if exists)
                if (this.node == fileNode) return true;
                Node node = this.node;
                while (node.parent() != fileNode) {
                	node = node.parent();
                }
                Attribute attr = node.attribute(UNRESOLVED);
                if (attr != null) { // May be null if an earlier child was resolved
                	attr.remove();
                }
            	return true;
            }
            return false;
        }

        private boolean startAttribute(Attributes attributes) {
            if (resolving && !resolvingThisElementPath()) {
            	return false;
            }
        	String name = attributes.getValue("name");
            if (name == null) {
                Node ref = referencedNode(attributes.getValue("ref"));
                if (ref == null) {
                    defer();
                    return false;
                }
                node.addAttribute(ref.namespace(), ref.name(), ref.type());
            } else {
                String type = attributes.getValue("type");
                if (type == null || xsdReference(type)) {
                    node.addAttribute(name, type);
                } else {
                    Node ref = referencedNode(type);
                    if (ref == null) {
                        defer();
                        return false;
                    }
                    node.addAttribute(name, type);
                }
            }
            return false;
        }

        private boolean startElement(Attributes attributes) {
        	String name = attributes.getValue("name");
            if (resolving && !resolvingThisElementPath()) {
            	if (name == null) {
                    Node ref = referencedNode(attributes.getValue("ref"));
            		node = node.child(ref.namespace(), ref.name());
            	} else {
            		node = node.child(targetNs, name);
            	}
            	return true;
            }
            if (name == null) {
                Node ref = referencedNode(attributes.getValue("ref"));
                if (ref == null) {
                    defer();
                    return false;
                }
                node = node.addChild(ref.namespace(), ref.name(), ref.type());
                copy(ref, node);
            } else {
                String type = attributes.getValue("type");
                if (type == null || xsdReference(type)) {
                    node = node.addChild(targetNs, name, type);
                } else {
                    Node ref = referencedNode(type);
                    if (ref == null) {
                        defer();
                        return false;
                    }
                    node = node.addChild(targetNs, name, type);
                    copy(ref, node);
                }
            }
            String max = attributes.getValue("maxOccurs");
            if (max != null) {
                try {
                    if (Integer.valueOf(max) > 1) {
                    	node.setList(true);
                    }
                } catch (NumberFormatException e) {
                    if ("unbounded".equals(max)) {
                    	node.setList(true);
                    }
                }
            }
            return true;
        }

        @Override
        public void startElement(String uri,
                                 String localName,
                                 String qName,
                                 Attributes attributes) throws SAXException {
            elementId++;
            elementPath += "-" + elementId;
            boolean nodeChanged = false;
            if (!deferring && XSD_NS.equals(uri)) {
                if ("schema".equals(localName)) {
                	nodeChanged = startSchema(attributes);
                } else if ("include".equals(localName) || "import".equals(localName)) {
                	nodeChanged = startIncludeImport(attributes);
                } else if ("complexType".equals(localName) || "simpleType".equals(localName)) {
                	nodeChanged = startGlobalType(attributes);
                } else if ("element".equals(localName)) {
                	nodeChanged = startElement(attributes);
                } else if ("attribute".equals(localName)) {
                	nodeChanged = startAttribute(attributes);
                } else if ("extension".equals(localName) || "restriction".equals(localName)) {
                	nodeChanged = startSuperclass(attributes);
                } else if ("list".equals(localName)) {
                	nodeChanged = startList(attributes);
                } else if ("attributeGroup".equals(localName) || "modelGroup".equals(localName)) {
                	nodeChanged = startGroup(attributes);
                }
            }
            this.nodeChanged.push(nodeChanged);
        }

        private boolean startGlobalType(Attributes attributes) {
        	if (node != fileNode) {
        		return false;
        	}
        	String name = attributes.getValue("name");
            if (resolving && !resolvingThisElementPath()) {
                node = fileNode.child(targetNs, name);
            	return true;
            }
            node = fileNode.addChild(targetNs, name, null);
            node.addAttribute(GLOBAL_NON_ELEMENT, "boolean").setValue(true);
        	return true;
        }

        private boolean startGroup(Attributes attributes) {
        	String id = attributes.getValue("id");
            if (resolving && !resolvingThisElementPath()) {
                node = fileNode.child(targetNs, id);
            	return true;
            }
            node = fileNode.addChild(targetNs, id, null);
            node.addAttribute(GLOBAL_NON_ELEMENT, "boolean").setValue(true);
        	return true;
        }

        private boolean startIncludeImport(Attributes attributes) throws SAXException {
            File file = new File(attributes.getValue("schemaLocation"));
            if (!file.exists()) file = new File(fileNode.parent().path(), file.getPath());
            try (InputStream stream = new FileInputStream(file.getAbsolutePath())) {
                parse(fileNode, stream);
            } catch (IOException | ParserConfigurationException e) {
                throw new SAXException(e);
            }
        	return false;
        }

        private boolean startList(Attributes attributes) {
            if (resolving && !resolvingThisElementPath()) {
            	return false;
            }
            String type = attributes.getValue("itemType");
            if (type == null || xsdReference(type)) {
                node.setType(type);
            } else {
                Node ref = referencedNode(type);
                if (ref == null) {
                    defer();
                    return false;
                }
                node.setType(type);
            }
        	return false;
        }

        @Override
        public void startPrefixMapping(String prefix,
                                       String uri) {
            super.startPrefixMapping(prefix, uri);
            if (XSD_NS.equals(uri)) xsdPrefix = prefix;
            namespaceByPrefix.put(prefix, uri);
        }

        private boolean startSchema(Attributes attributes) {
            targetNs = attributes.getValue("targetNamespace");
        	return false;
        }

        private boolean startSuperclass(Attributes attributes) {
            if (resolving && !resolvingThisElementPath()) {
            	return false;
            }
            String type = attributes.getValue("base");
            if (!xsdReference(type)) {
                Node ref = referencedNode(type);
                if (ref == null) {
                    defer();
                    return false;
                }
                copy(ref, node);
            }
            node.setType(type);
        	return false;
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
