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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.DataFormatHandler;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.common.I18n;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Handles the interpretation and conversion of <a href="https://www.w3.org/XML/">XML documents</a> to and from {@link Node nodes}.
 */
public class XmlHandler implements DataFormatHandler {

    static final String COMMENT = "<comment>";

    private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

    static {
        FACTORY.setNamespaceAware(true);
    }

    Map<String, String> prefixByNamespace = new HashMap<>();

    private void indent(StringBuilder builder,
                        int indent) {
        for (int ndx = indent; --ndx >= 0;) {
            builder.append(' ');
        }
    }

    /**
     * @param documentNode
     * 		The node representing the XML document
     * @return the SAX handler used to populate the supplied documentNode
     */
    protected SaxHandler newSaxHandler(Node documentNode) {
        return new SaxHandler(documentNode);
    }

    /**
     * Populates the supplied documentNode with the XML document read by the supplied stream.
     *
     * @param documentNode
     * 		The node representing the XML document
     * @param stream
     * 		The stream used to read the XML document
     * @throws Exception if any error occurs
     */
    protected void parse(Node documentNode,
                         InputStream stream) throws Exception {
        SAXParser parser = FACTORY.newSAXParser();
        SaxHandler saxHandler = newSaxHandler(documentNode);
        parser.setProperty("http://xml.org/sax/properties/lexical-handler", saxHandler);
        parser.parse(stream, saxHandler);
    }

    /**
     * {@inheritDoc}
     *
     * @param data
     * 		The file path of the source as a {@link String}
     * @see org.jboss.chrysalix.DataFormatHandler#toSourceNode(java.lang.Object, org.jboss.chrysalix.Node)
     */
    @Override
    public Node toSourceNode(Object data,
                             Node parent) throws Exception {
        // Create file node, using its path as a namespace
        File file = new File(data.toString());
        if (!file.exists()) {
        	throw new IllegalArgumentException(I18n.localize(getClass(), "Source file does not exist: %s", file));
        }
        Node node = parent.addChild(file.getParentFile().getAbsolutePath(), file.getName(), null);
        // Create file node's contents
        try (InputStream stream = new FileInputStream(file)) {
            parse(node, stream);
        }
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * @return the XML document as an array of strings representing each line
     * @see org.jboss.chrysalix.DataFormatHandler#toTargetData(org.jboss.chrysalix.Node)
     */
    @Override
    public Object toTargetData(Node targetNode) throws Exception {
        if (targetNode.children().length == 0) {
        	return null;
        }
        List<String> lines = new ArrayList<>();
        lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    	StringBuilder builder = new StringBuilder();
        for (Node child : targetNode.children()) {
            toTargetData(child, lines, builder, 0, prefixByNamespace);
        }
        return lines.toArray(new String[lines.size()]);
    }

    private void toTargetData(Node node,
                              List<String> lines,
                              StringBuilder builder,
                              int indent,
                              Map<String, String> prefixByNamespace) throws Exception {
        indent(builder, indent);
        if (COMMENT.equals(node.qualifiedName())) {
        	builder.append("<!--");
        	builder.append(node.value());
        	builder.append("-->");
        	lines.add(builder.toString());
        	builder.setLength(0);
        } else {
            String prefixedName = prefixByNamespace.get(node.namespace()) + ':' + node.name();
            builder.append('<');
            builder.append(prefixedName);
            for (Attribute attr : node.attributes()) {
            	builder.append(" ");
            	builder.append(attr.name());
            	builder.append("=\"");
            	builder.append(attr.value());
            	builder.append("\"");
            }
            if (indent == 0) { // schema node
                for (Entry<String, String> entry : prefixByNamespace.entrySet()) {
                	builder.append(" xmlns");
                	if (!entry.getValue().isEmpty()) {
                		builder.append(':');
                		builder.append(entry.getValue());
                	}
                	builder.append("=\"");
            		builder.append(entry.getKey());
                	builder.append("\"");
                }
            }
            Object value = node.value();
            Node[] children = node.children();
            if (children.length == 0 && value != null && !value.toString().isEmpty()) {
            	builder.append('>');
            	builder.append(node.value());
            	builder.append("</");
            	builder.append(prefixedName);
            	builder.append('>');
            	lines.add(builder.toString());
            	builder.setLength(0);
            } else {
                if (children.length > 0) {
                	builder.append('>');
                	lines.add(builder.toString());
                	builder.setLength(0);
                    int childIndent = indent + 2;
                    if (value != null && !value.toString().isEmpty()) {
                        indent(builder, childIndent);
                        builder.append(value.toString());
                    	lines.add(builder.toString());
                    	builder.setLength(0);
                    }
                    for (Node child : children) {
                        toTargetData(child, lines, builder, childIndent, prefixByNamespace);
                    }
                    indent(builder, indent);
                	builder.append("</");
                	builder.append(prefixedName);
                	builder.append('>');
                	lines.add(builder.toString());
                	builder.setLength(0);
                } else {
                    builder.append("/>");
                	lines.add(builder.toString());
                	builder.setLength(0);
                }
            }
        }
    }

    /**
	 * {@inheritDoc}
	 *
     * @param data
     * 		The file path of the target as a {@link String}
	 * @see org.jboss.chrysalix.DataFormatHandler#toTargetNode(java.lang.Object, org.jboss.chrysalix.Node)
	 */
	@Override
	public Node toTargetNode(Object data,
							 Node parent) throws Exception {
        // Create file node, using its path as a namespace
        File file = new File(data.toString());
        Node node = parent.addChild(file.getParentFile().getAbsolutePath(), file.getName(), null);
        if (file.exists()) {
            // Create file node's contents
            try (InputStream stream = new FileInputStream(file)) {
                parse(node, stream);
            }
        }
        return node;
	}

	/**
	 * The DefaultHandler2 used to convert
	 */
	protected class SaxHandler extends DefaultHandler2 {

    	/**
    	 * The current node being populated
    	 */
        protected Node node;

        /**
         * Populates the supplied documentNode
         *
         * @param documentNode
         * 		The node representing the XML document
         */
        protected SaxHandler(Node documentNode) {
            node = documentNode;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
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

        /**
         * {@inheritDoc}
         *
         * Adds comments as child nodes to the current {@link #node}.
         *
         * @see org.xml.sax.ext.DefaultHandler2#comment(char[], int, int)
         */
        @Override
        public void comment(char[] chrs,
                            int start,
                            int length) {
            node.addChild(null, COMMENT, "string").setValue(String.valueOf(chrs, start, length));
        }

        /**
         * {@inheritDoc}
         *
         * Replaces the current {@link #node} with its parent.
         *
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri,
                               String localName,
                               String qName) {
            node = node.parent();
        }

        /**
         * {@inheritDoc}
         *
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @SuppressWarnings("unused") // For SAXException that might be thrown by subclasses
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

        /**
         * {@inheritDoc}
         *
         * @see org.xml.sax.helpers.DefaultHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        @Override
        public void startPrefixMapping(String prefix,
                                       String uri) {
            prefixByNamespace.putIfAbsent(uri, prefix);
        }
    }
}
