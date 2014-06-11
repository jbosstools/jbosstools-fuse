/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.core.tree;

import javax.management.ObjectName;

import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.jmx.core.IConnectionWrapper;



public class NodeBuilder {

    public static void addToList(Node root, ObjectName on) {
        Node node = buildDomainNode(root, on.getDomain());
        node = buildObjectNameNode(node, "on", on.getKeyPropertyListString(), on); //$NON-NLS-1$
    }

    public static void addToTree(Node root, ObjectName on) {
        Node node = buildDomainNode(root, on.getDomain());
        String keyPropertyListString = on.getKeyPropertyListString();
        String[] properties = keyPropertyListString.split(","); //$NON-NLS-1$
        for (int i = 0; i < properties.length; i++) {
            String property = properties[i];
            String key = property.substring(0, property.indexOf('='));
            String value = property.substring(property.indexOf('=') + 1,
                    property.length());
            if (i == properties.length - 1) {
                node = buildObjectNameNode(node, key, value, on);
            } else {
                node = buildPropertyNode(node, key, value);
            }
        }
    }

    public static Root createRoot(IConnectionWrapper connection) {
        return new Root(connection);
    }

    static Node buildDomainNode(Node parent, String domain) {
        Node n = new DomainNode(parent, domain);
        if (parent != null) {
            return parent.addChild(n);
        }
        return n;
    }

    static Node buildPropertyNode(Node parent, String key, String value) {
        Node n = new PropertyNode(parent, key, value);
        if (parent != null) {
            return parent.addChild(n);
        }
        return n;
    }

    static Node buildObjectNameNode(Node parent, String key, String value,
            ObjectName on) {
        Node n = new ObjectNameNode(parent, key, value, on);
        if (parent != null) {
            return parent.addChild(n);
        }
        return n;
    }

}
