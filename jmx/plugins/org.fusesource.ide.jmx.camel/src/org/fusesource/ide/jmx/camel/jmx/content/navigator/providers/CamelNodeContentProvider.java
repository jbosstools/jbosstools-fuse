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
package org.fusesource.ide.jmx.camel.jmx.content.navigator.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.jmx.camel.internal.JmxTemplateCamelFacade;
import org.fusesource.ide.jmx.camel.navigator.CamelContextsNode;
import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.core.tree.Root;

public class CamelNodeContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
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
			if (r != null && r.containsDomain("org.apache.camel")) {
				CamelJMXFacade facade = new JmxTemplateCamelFacade(new JmxPluginJmxTemplate(r.getConnection()));
				CamelContextsNode camel = new CamelContextsNode(r, facade);
				return new Object[]{camel};
			}
		} else if (parentElement instanceof NodeSupport) {
			NodeSupport contexts = (NodeSupport)parentElement;
			return contexts.getChildren();
		} 
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if( element instanceof Node ) {
			return !((Node)element).getChildrenList().isEmpty();
		}
		return false;
	}
}

