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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.Engine;
import org.jboss.chrysalix.InMemoryRepository;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.Repository;
import org.junit.Test;

public class XsdHandlerTest {

    private static final String RESOURCES_FOLDER = "src/test/resources/" + XsdHandlerTest.class.getPackage().getName().replace('.', '/') + '/';
    private static final String SOURCE_XSD = RESOURCES_FOLDER + "report.xsd";
    private static final String IPO_NS = "http://www.example.com/IPO";
    private static final String REPORT_NS = "http://www.example.com/Report";

    private void debug(Node node) {
    	debug(node, 0);
    }

    private void debug(Node node,
                       int level) {
		println(level, node.qualifiedName() + ": " + node.type() + ", index=" + node.index() + ", list=" + node.list());
		Attribute[] attrs = node.attributes();
		if (attrs.length > 0) {
			println(level, "attributes:");
			level++;
			for (Attribute attr : node.attributes()) {
				println(level, "@" + attr.qualifiedName() + " " + attr.type());
			}
			level--;
		}
		Node[] children = node.children();
		if (children.length > 0) {
			println(level, "children:");
			level++;
			for (Node child : children) {
				debug(child, level);
			}
		}
    }

    private void indent(int level) {
    	for (int ndx = level; --ndx >= 0;) {
			System.out.print("  ");
    	}
    }

    private void println(int level,
                         String text) {
    	indent(level);
		System.out.println(text);
    }

    @Test
    public void loadXsdModel() throws Exception {
        Engine engine = new Engine(RESOURCES_FOLDER);
        XsdHandler handler = new XsdHandler();
        Repository repository = new InMemoryRepository();
        Node sourceFileNode = engine.toNode(SOURCE_XSD, handler, repository);
        debug(sourceFileNode);
        assertThat(sourceFileNode.children().length, is(3));
        assertThat(sourceFileNode.child(REPORT_NS, "purchaseReport"), notNullValue());
        assertThat(sourceFileNode.child(IPO_NS, "comment"), notNullValue());
        Node purchaseOrder = sourceFileNode.child(IPO_NS, "purchaseOrder");
        assertThat(purchaseOrder, notNullValue());
        assertThat(purchaseOrder.type(), is("ipo:PurchaseOrderType"));
        Attribute orderDate = purchaseOrder.attribute("orderDate");
        assertThat(orderDate.type(), is("date"));
        assertThat(orderDate, notNullValue());
        Node shipTo = purchaseOrder.child(IPO_NS, "shipTo");
        assertThat(shipTo, notNullValue());
        assertThat(shipTo.list(), is(false));
        Node name = shipTo.child(IPO_NS, "name");
        assertThat(name, notNullValue());
        assertThat(name.type(), is("string"));
        assertThat(purchaseOrder.child(IPO_NS, "billTo"), notNullValue());
        assertThat(purchaseOrder.child(IPO_NS, "comment"), notNullValue());
        Node items = purchaseOrder.child(IPO_NS, "items");
        assertThat(items, notNullValue());
        assertThat(items.list(), is(false));
        Node item = items.child(IPO_NS, "item");
        assertThat(item, notNullValue());
        assertThat(item.list(), is(true));
    }
}
