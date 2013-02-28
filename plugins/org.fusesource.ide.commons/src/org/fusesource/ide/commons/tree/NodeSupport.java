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

package org.fusesource.ide.commons.tree;

import java.beans.IntrospectionException;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.ui.propsrc.BeanPropertySource;
import org.fusesource.ide.commons.util.Nodes;


public abstract class NodeSupport extends Node implements IPropertySourceProvider {

	private IPropertySource propertySource;
	private Object propertyBean;

	public NodeSupport(org.fusesource.ide.commons.tree.Node parent) {
		super(parent);
	}

	@Override
	public int compareTo(Object o) {
		String thatString = o.toString();

		/*
		if (o instanceof DomainNode) {
			DomainNode dn = (DomainNode) o;
			thatString = dn.getDomain();
		} else {
			thatString = o.toString();
		}
		 */
		return toString().compareTo(thatString);
	}

	@Override
	public IPropertySource getPropertySource(Object object) {
		// avoid infinite recursion!
		if (object != null && object != this) {
			//System.out.println("====== warning getPropertySource() asked for: " + object + " of class: "+ object.getClass());
			return null;
		}
		if (object == null) {
			return null;
		}
		if (propertySource == null) {
			try {
				propertySource = createPropertySource();
			} catch (Exception e) {
				Activator.getLogger().warning(e);
			}
		}
		return propertySource;
	}

	protected IPropertySource createPropertySource() throws IntrospectionException {
		Object localPropertyBean = getPropertyBean();
		if (localPropertyBean == null) {
			return null;
		}
		BeanPropertySource answer = new BeanPropertySource(localPropertyBean);
		answer.setOwner(this);
		return answer;
	}

	/**
	 * Returns the bean to use to reflect on to create an IPropertySource by default unless {@link #createPropertySource()} is overriden
	 */
	protected Object getPropertyBean() {
		if (propertyBean == null) {
			propertyBean = createPropertyBean();
		}
		return propertyBean;
	}

	/**
	 * Allows derived classes to lazily create a property bean
	 */
	protected Object createPropertyBean() {
		return null;
	}

	public IPropertySource getPropertySource() {
		if (propertySource == null) {
			try {
				propertySource = createPropertySource();
			} catch (IntrospectionException e) {
				Activator.getLogger().warning("Failed to create PropertySource for " + this + ". " + e, e);
			}
		}
		return propertySource;
	}

	public void setPropertySource(IPropertySource propertySource) {
		this.propertySource = propertySource;
	}

	public void setPropertyBean(Object propertyBean) {
		this.propertyBean = propertyBean;
	}

	protected void refreshParent() {
		Nodes.refreshParent(this);
	}
}
