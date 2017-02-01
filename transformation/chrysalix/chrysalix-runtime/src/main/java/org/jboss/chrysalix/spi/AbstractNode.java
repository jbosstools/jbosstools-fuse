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
package org.jboss.chrysalix.spi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jboss.chrysalix.Attribute;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.common.Arg;
import org.jboss.chrysalix.internal.AbstractEntity;
import org.jboss.chrysalix.internal.AttributeImpl;

public abstract class AbstractNode extends AbstractEntity implements Node {

    protected int index;
    protected Set<Attribute> attributes = new LinkedHashSet<>();
    protected List<Node> children = new ArrayList<>();
    protected boolean list;

    protected AbstractNode(Node parent,
                           String namespace,
                           String name,
                           String type) {
        super(parent, namespace, name, type);
        Arg.notNull(parent, "parent");
    }

    protected AbstractNode(String namespace,
                           String name) {
        super(null, namespace, name, null);
    }

    @Override
    public Attribute addAttribute(String qualifiedName,
                                  String type) {
        int ndx = qualifiedName.lastIndexOf(':');
        String ns = ndx < 0 ? "" : qualifiedName.substring(0, ndx);
        return addAttribute(ns, qualifiedName.substring(ndx + 1), type);
    }

    @Override
    public Attribute addAttribute(String namespace,
                                  String name,
                                  String type) {
        Attribute attribute = new AttributeImpl(this, namespace, name, type);
        attributes.add(attribute);
        return attribute;
    }

    @Override
    public Node addChild(String qualifiedName,
                         String type) {
        int ndx = qualifiedName.lastIndexOf(':');
        String ns = ndx < 0 ? "" : qualifiedName.substring(0, ndx);
        return addChild(ns, qualifiedName.substring(ndx + 1), type);
    }

    @Override
    public Attribute attribute(String qualifiedName) {
        int ndx = qualifiedName.lastIndexOf(':');
        String ns = ndx < 0 ? "" : qualifiedName.substring(0, ndx);
        return attribute(ns, qualifiedName.substring(ndx + 1));
    }

    @Override
    public Attribute attribute(String namespace,
                               String name) {
        for (Attribute attr : attributes) {
            if (attr.name().equals(name) && attr.namespace().equals(namespace)) return attr;
        }
        return null;
    }

    @Override
    public Attribute[] attributes() {
        return attributes.toArray(new Attribute[attributes.size()]);
    }

    @Override
    public Node child(String qualifiedName) {
        int ndx = qualifiedName.lastIndexOf(':');
        String ns = ndx < 0 ? "" : qualifiedName.substring(0, ndx);
        return child(ns, qualifiedName.substring(ndx + 1));
    }

    @Override
    public Node child(String namespace,
                      String name) {
        int indexNdx = name.indexOf('[');
        int index;
        if (indexNdx < 0) index = 0;
        else {
            index = Integer.valueOf(name.substring(indexNdx + 1, name.indexOf(']', indexNdx)));
            name = name.substring(0, indexNdx);
        }
        int ndx = 0;
        for (Node child : children()) {
            if (child.name().equals(name) && child.namespace().equals(namespace)) {
                if (ndx == index) return child;
                ndx++;
            }
        }
        return null;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public boolean list() {
        return list;
    }

    @Override
    public String path() {
        return index == 0 ? super.path() : super.path() + '[' + index + ']';
    }

    @Override
    public void setList(boolean list) {
        this.list = list;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Node");
        builder.append(super.toString());
        builder.append(", index=");
        builder.append(index);
        builder.append(", list=");
        builder.append(list);
        builder.append(", attributes=[");
        int noNamesLength = builder.length();
        for (Attribute attr : attributes()) {
            if (builder.length() > noNamesLength) builder.append(", ");
            builder.append(attr.qualifiedName());
        }
        builder.append("], children=[");
        noNamesLength = builder.length();
        for (Node node : children()) {
            if (builder.length() > noNamesLength) builder.append(", ");
            builder.append(node.qualifiedName());
        }
        builder.append("]}");
        return builder.toString();
    }
}
