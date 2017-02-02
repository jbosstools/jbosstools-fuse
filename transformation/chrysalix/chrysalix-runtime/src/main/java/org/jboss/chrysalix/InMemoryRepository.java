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

import java.util.ArrayList;
import java.util.List;
import org.jboss.chrysalix.spi.AbstractNode;

public class InMemoryRepository implements Repository {

    private List<Node> rootNodes = new ArrayList<>();

    @Override
    public Node newRootNode(String name) {
        Node rootNode = new RepositoryNode(null, name);
        rootNodes.add(rootNode);
        return rootNode;
    }

    private class RepositoryNode extends AbstractNode {

        RepositoryNode(Node parent,
                       String namespace,
                       String name,
                       String type) {
            super(parent, namespace, name, type);
        }

        RepositoryNode(String namespace,
                       String name) {
            super(namespace, name);
        }

        @Override
        public Node addChild(String namespace,
                             String name,
                             String type) {
            int ndx = 0;
            for (Node child : children) {
                if (child.name().equals(name) && child.namespace().equals(namespace)) ndx++;
            }
            RepositoryNode child = new RepositoryNode(this, namespace, name, type);
            child.index = ndx;
            children.add(child);
            return child;
        }

        @Override
        public Node[] children() {
            return children.toArray(new Node[children.size()]);
        }

        @Override
        public void remove() {
            RepositoryNode parent = (RepositoryNode)this.parent;
            parent.children.remove(this);
            for (Node child : parent.children) {
                if (child.index() > index) ((RepositoryNode)child).index--;
            }
        }
    }
}
