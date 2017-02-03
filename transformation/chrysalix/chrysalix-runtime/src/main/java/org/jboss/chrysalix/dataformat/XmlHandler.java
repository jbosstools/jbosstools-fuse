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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.DataFormatHandler;
import org.jboss.chrysalix.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class XmlHandler implements DataFormatHandler {

    public static final String COMMENT = "<comment>";
    static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

    static {
        FACTORY.setNamespaceAware(true);
    }

    Map<String, String> prefixByNamespace = new HashMap<>();

    private void indent(PrintWriter writer,
                        int indent) {
        for (int ndx = indent; --ndx >= 0;) {
            writer.print(' ');
        }
    }

    @Override
    public void load(Node fileNode) throws Exception {
        try (InputStream stream = new FileInputStream(fileNode.path())) {
            parse(fileNode, stream);
        }
    }

    protected XmlSaxHandler newSaxHandler(Node fileNode) {
        return new XmlSaxHandler(fileNode);
    }

    protected void parse(Node fileNode,
                         InputStream stream) throws IOException, ParserConfigurationException, SAXException {
        SAXParser parser = FACTORY.newSAXParser();
        XmlSaxHandler xmlSaxHandler = newSaxHandler(fileNode);
        parser.setProperty("http://xml.org/sax/properties/lexical-handler", xmlSaxHandler);
        parser.parse(stream, xmlSaxHandler);
    }

    @Override
    public void save(Node fileNode) throws Exception {
        if (fileNode.children().length == 0) return;
        try (PrintWriter writer = new PrintWriter(fileNode.path())) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            for (Node child : fileNode.children()) {
                save(child, writer, 0, prefixByNamespace);
            }
        }
    }

    private void save(Node node,
                      PrintWriter writer,
                      int indent,
                      Map<String, String> prefixByNamespace) throws Exception {
        indent(writer, indent);
        if (COMMENT.equals(node.qualifiedName())) {
            writer.println("<!--" + node.value() + "-->");
        } else {
            String prefixedName = prefixByNamespace.get(node.namespace()) + ':' + node.name();
            writer.print('<' + prefixedName);
            for (Attribute attr : node.attributes()) {
                writer.print(" " + attr.name() + "=\"" + attr.value() + "\"");
            }
            if (indent == 0) { // schema node
                for (Entry<String, String> entry : prefixByNamespace.entrySet()) {
                    writer.print(" xmlns");
                    writer.print(entry.getValue().isEmpty() ? "" : ':' + entry.getValue());
                    writer.print("=\"" + entry.getKey() + "\"");
                }
            }
            Object value = node.value();
            Node[] children = node.children();
            if (children.length == 0 && value != null && !value.toString().isEmpty()) {
                writer.println(">" + node.value() + "</" + prefixedName + '>');
            } else {
                if (children.length > 0) {
                    writer.println('>');
                    int childIndent = indent + 2;
                    if (value != null && !value.toString().isEmpty()) {
                        indent(writer, childIndent);
                        writer.println(value.toString());
                    }
                    for (Node child : children) {
                        save(child, writer, childIndent, prefixByNamespace);
                    }
                    indent(writer, indent);
                    writer.println("</" + prefixedName + '>');
                } else {
                    writer.println("/>");
                }
            }
        }
    }

    protected class XmlSaxHandler extends DefaultHandler2 {

        protected Node node;

        protected XmlSaxHandler(Node fileNode) {
            node = fileNode;
        }

        @Override
        public void characters(char[] chrs,
                               int start,
                               int length) {
            Object oldVal = node.value();
            String val = String.valueOf(chrs, start, length);
            if (oldVal != null) val = oldVal.toString() + val;
            if (node.children().length > 0) val = val.trim();
            node.setValue(val);
        }

        @Override
        public void comment(char[] chrs,
                            int start,
                            int length) {
            node.addChild(null, COMMENT, "string").setValue(String.valueOf(chrs, start, length));
        }

        @Override
        public void endElement(String uri,
                               String localName,
                               String qName) {
            node = node.parent();
        }

        @SuppressWarnings("unused")
        @Override
        public void startElement(String uri,
                                 String localName,
                                 String qName,
                                 Attributes attributes) throws SAXException {
            // Trim node's value if it has children
            Object val = node.value();
            if (val != null) {
                node.setValue(val.toString().trim());
            }
            node = node.addChild(uri, localName, null);
            for (int ndx = 0; ndx < attributes.getLength(); ++ndx) {
                Attribute attr = node.addAttribute(attributes.getQName(ndx), attributes.getType(ndx));
                attr.setValue(attributes.getValue(ndx));
            }
        }

        @Override
        public void startPrefixMapping(String prefix,
                                       String uri) {
            prefixByNamespace.putIfAbsent(uri, prefix);
        }
    }
}
