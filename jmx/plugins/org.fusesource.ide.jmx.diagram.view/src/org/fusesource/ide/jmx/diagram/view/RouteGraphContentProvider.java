/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.diagram.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

public class RouteGraphContentProvider implements IGraphContentProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getSource(java.lang.Object)
	 */
	@Override
	public Object getSource(Object rel) {
		if (rel instanceof CamelElementConnection) {
			CamelElementConnection flow = (CamelElementConnection) rel;
			return flow.getSource();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getDestination(java.lang.Object)
	 */
	@Override
	public Object getDestination(Object rel) {
		if (rel instanceof CamelElementConnection) {
			CamelElementConnection flow = (CamelElementConnection) rel;
			return flow.getTarget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		if (input instanceof Object[]) {
			return (Object[]) input;
		} else if (input instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement node = (AbstractCamelModelElement) input;

			Set<CamelElementConnection> set = new HashSet<>();
			CamelRouteElement parent;
			if (node instanceof CamelRouteElement) {
				parent = (CamelRouteElement) node;
			} else {
				parent = getRoute(node);
			}
			Set<AbstractCamelModelElement> descendents = new HashSet<>();
			if (parent == null) {
				getAllChildren(node.getChildElements(), descendents);
			} else {
				getAllChildren(parent.getChildElements(), descendents);
			}
			return set.toArray();
		}
		return null;
	}
	
	private void getAllOutputs(AbstractCamelModelElement elem, Set<AbstractCamelModelElement> set) {
		if (elem.getOutputElement() != null) {
			set.add(elem.getOutputElement());
		}
	}
	
	private void getAllChildren(List<AbstractCamelModelElement> elems, Set<AbstractCamelModelElement> set) {
		for (AbstractCamelModelElement e : elems) {
			set.add(e);
			getAllOutputs(e, set);
			if (!e.getChildElements().isEmpty()) {
				getAllChildren(e.getChildElements(), set);
			}
		}
	}
	
	private CamelRouteElement getRoute(AbstractCamelModelElement e) {
		AbstractCamelModelElement cme = e;
		while (cme != null && !(cme instanceof CamelRouteElement)) {
			cme = cme.getParent();
		}
		if (cme != null && cme instanceof CamelRouteElement) {
			return (CamelRouteElement)cme;
		}
		return null;
	}
}
