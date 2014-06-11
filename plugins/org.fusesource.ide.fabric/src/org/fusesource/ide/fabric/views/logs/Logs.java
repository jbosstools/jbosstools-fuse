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

package org.fusesource.ide.fabric.views.logs;

import javax.management.ObjectName;

import org.eclipse.jface.viewers.ISelection;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.fabric.FabricPlugin;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.tree.DomainNode;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.core.tree.ObjectNameNode;
import org.jboss.tools.jmx.core.tree.Root;


public class Logs {

	public static LogEventBean toLogEvent(Object value) {
		if (value instanceof LogEventBean) {
			return (LogEventBean) value;
		}
		return null;
	}

	public static ILogBrowser toLogBrowser(Object value) {
		ILogBrowser answer = null;
		if (value instanceof ILogBrowser) {
			return (ILogBrowser) value;
		}
		if (value instanceof HasLogBrowser) {
			HasLogBrowser lb = (HasLogBrowser) value;
			answer = lb.getLogBrowser();
			if (answer != null) {
				return answer;
			}
		}
		if (value instanceof Root) {
			Root root = (Root) value;
			if (root != null) {
				DomainNode domainNode = root.getDomainNode("org.fusesource.insight");
				if (domainNode != null) {
					Node[] children = domainNode.getChildren();
					if (children != null) {
						for (Node node : children) {
							if (node instanceof ObjectNameNode) {
								ObjectNameNode nameNode = (ObjectNameNode) node;
								ObjectName objectName = nameNode.getObjectName();
								if (objectName != null) {
									String typeName = objectName.getKeyProperty("type");
									if (typeName != null && typeName.equals("LogQuery")) {
										FabricPlugin.getLogger().debug("================ found objectName: " + objectName);
										return new JmxFabricLogBrowser(root.getConnection(), objectName);
									}
								}
							}
						}
					}
				}
			}

		}
		if (value instanceof IConnectionWrapper) {
			IConnectionWrapper connection = (IConnectionWrapper) value;
			return toLogBrowser(connection.getRoot());
		}
		if (value instanceof Node) {
			Node node = (Node) value;
			answer = toLogBrowser(node.getParent());
			if (answer != null) {
				return answer;
			}
		}
		return null;
	}

	public static ILogBrowser getSelectionLogBrowser(ISelection selection) {
		Object value = Selections.getFirstSelection(selection);
		return toLogBrowser(value);
	}

	public static boolean hasThrowableInformation(LogEventBean event) {
		String[] exception = event.getException();
		return exception != null && exception.length > 0;
	}

	public static String[] getThrowableRep(LogEventBean event) {
		return event.getException();
	}

}
