/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.karaf.jmx.content.navigator.providers;

import javax.management.MalformedObjectNameException;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.fusesource.ide.jmx.karaf.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.karaf.navigator.fabric8.BundlesNode;
import org.fusesource.ide.jmx.karaf.navigator.fabric8.OsgiFacade;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.core.tree.Root;

public class Fabric8NodeContentProvider implements ITreeContentProvider {

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
				if (r.containsDomain("io.fabric8")) {
					try {
						// TODO: create a jmx facade and a root node to handle the facade
						JmxPluginJmxTemplate jmxTemplate = new JmxPluginJmxTemplate(r.getConnection());
						OsgiFacade facade = new OsgiFacade(jmxTemplate);
						BundlesNode bundles = new BundlesNode(r, facade);
						return new Object[]{bundles};
					} catch (MalformedObjectNameException ex) {
						Fabric8JMXPlugin.getLogger().error(ex);
					}
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

