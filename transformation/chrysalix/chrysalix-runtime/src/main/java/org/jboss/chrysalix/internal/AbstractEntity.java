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
package org.jboss.chrysalix.internal;

import org.jboss.chrysalix.Entity;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.common.Arg;

public abstract class AbstractEntity implements Entity {

    protected Node parent;
    protected String namespace;
    protected String name;
    protected String type;
    protected Object value;

    protected AbstractEntity(Node parent,
                             String namespace,
                             String name,
                             String type) {
        Arg.notEmpty(name, "name");
        this.parent = parent;
        this.namespace = namespace == null ? "" : namespace;
        this.name = name == null ? "" : name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractEntity other = (AbstractEntity)obj;
        if (!name.equals(other.name)) return false;
        if (!namespace.equals(other.namespace)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + namespace.hashCode();
        return result;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public Node parent() {
        return parent;
    }

    @Override
    public String path() {
        return (parent == null ? "" : parent.path()) + '/' + qualifiedName();
    }

    @Override
    public String qualifiedName() {
        return namespace.isEmpty() ? name : '{' + namespace + "}:" + name;
    }

    @Override
    public Node root() {
        return parent == null && this instanceof Node ? (Node)this : parent.root();
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{name=");
        builder.append(name);
        builder.append(", namespace=");
        builder.append(namespace);
        builder.append(", type=");
        builder.append(type);
        builder.append(", value=");
        builder.append(value);
        builder.append(", parent=");
        builder.append(parent == null ? null : parent.path());
        return builder.toString();
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public Object value() {
        return value;
    }
}
