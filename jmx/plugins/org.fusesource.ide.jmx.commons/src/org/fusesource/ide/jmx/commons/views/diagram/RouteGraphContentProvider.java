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

package org.fusesource.ide.jmx.commons.views.diagram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
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
		} else if (input instanceof CamelModelElement) {
			CamelModelElement node = (CamelModelElement) input;

			Set<CamelElementConnection> set = new HashSet<CamelElementConnection>();
			CamelRouteElement parent;
			if (node instanceof CamelRouteElement) {
				parent = (CamelRouteElement) node;
			} else {
				parent = getRoute(node);
			}
			Set<CamelModelElement> descendents = new HashSet<CamelModelElement>();
			if (parent == null) {
				getAllChildren(node.getChildElements(), descendents);
			} else {
				getAllChildren(parent.getChildElements(), descendents);
			}
			return set.toArray();
		}
		return null;
	}
	
	private void getAllOutputs(CamelModelElement elem, Set<CamelModelElement> set) {
		if (elem.getOutputElement() != null) set.add(elem.getOutputElement());
	}
	
	private void getAllChildren(List<CamelModelElement> elems, Set<CamelModelElement> set) {
		for (CamelModelElement e : elems) {
			set.add(e);
			getAllOutputs(e, set);
			if (e.getChildElements().isEmpty() == false) {
				getAllChildren(e.getChildElements(), set);
			}
		}
	}
	
	private CamelRouteElement getRoute(CamelModelElement e) {
		CamelModelElement cme = e;
		while (cme != null && cme instanceof CamelRouteElement == false) {
			cme = cme.getParent();
		}
		if (cme != null && cme instanceof CamelRouteElement) {
			return (CamelRouteElement)cme;
		}
		return null;
	}
}
