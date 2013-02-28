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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.IJMXRunnable;
import org.fusesource.ide.jmx.core.JMXActivator;
import org.fusesource.ide.jmx.core.JMXException;


public class NodeUtils {

	public static PropertyNode findObjectNameNode(Node node,
			ObjectName objectName) {
		Assert.isNotNull(node);

		if (node instanceof ObjectNameNode) {
			ObjectNameNode onNode = (ObjectNameNode) node;
			if (onNode.getObjectName().equals(objectName)) {
				return onNode;
			}
		}
		Node[] children = node.getChildren();
		for (int i = 0; i < children.length; i++) {
			Node child = children[i];
			Node found = findObjectNameNode(child, objectName);
			if (found != null) {
				return (PropertyNode) found;
			}
		}
		return null;
	}

	public static Root createObjectNameTree(final IConnectionWrapper connectionWrapper)
			throws CoreException {
		final Root[] _root = new Root[1];
		connectionWrapper.run(new IJMXRunnable() {
			@Override
			@SuppressWarnings("rawtypes")
			public void run(MBeanServerConnection connection) throws JMXException {
				try {
					Set beanInfo;
					beanInfo = connection.queryNames(new ObjectName("*:*"), null);
					final Root rootNode = NodeBuilder.createRoot(connectionWrapper);
					_root[0] = rootNode;
					MBeansNode mbeans = rootNode.getMBeansNode();
					rootNode.addChild(mbeans);
					Iterator iter = beanInfo.iterator();
					while (iter.hasNext()) {
						ObjectName on = (ObjectName) iter.next();
						NodeBuilder.addToTree(mbeans, on);
					}
				} catch( IOException ioe ) {
					// TODO throw coreexception
				} catch( MalformedObjectNameException mone) {
					// TODO throw coreexception
				}
			}
		});
		Root root = _root[0];
		if (root != null) {
			enrichRootNode(root);
		}
		return root;
	}

	protected static void enrichRootNode(Root root) {
		List<NodeProvider> providers = JMXActivator.getNodeProviders();
		for (NodeProvider provider : providers) {
			provider.provide(root);
		}
	}
}
