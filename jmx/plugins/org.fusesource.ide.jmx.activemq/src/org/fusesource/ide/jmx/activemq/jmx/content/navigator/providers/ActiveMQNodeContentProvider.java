/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.activemq.jmx.content.navigator.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.activemq.internal.BrokerFacade;
import org.fusesource.ide.jmx.activemq.internal.JmxTemplateBrokerFacade;
import org.fusesource.ide.jmx.activemq.navigator.BrokerNode;
import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.core.tree.Root;

public class ActiveMQNodeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if( parentElement instanceof IConnectionWrapper ) {
			IConnectionWrapper w = (IConnectionWrapper)parentElement;
			Root r = w.getRoot();
			if( r != null ) {
				if (r.containsDomain("org.apache.activemq")) {
					BrokerFacade facade = new JmxTemplateBrokerFacade(new JmxPluginJmxTemplate(r.getConnection()));
					String brokerName = null;
					try {
						brokerName = facade.getBrokerName();
					} catch (Exception e) {
						ActiveMQJMXPlugin.getLogger().warning("Could not find Broker name: " + e, e);
					}
					if (brokerName == null) {
						brokerName = "Broker";
					}
					BrokerNode broker = new BrokerNode(r, facade, brokerName);
					return new Object[]{broker};
				}
			}
		} else if (parentElement instanceof NodeSupport) {
			NodeSupport contexts = (NodeSupport)parentElement;
			return contexts.getChildren();
		} 
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if( element instanceof Node ) {
			Node[] children = ((Node)element).getChildren();
			return children != null && children.length > 0;
		}
		return false;
	}
}

