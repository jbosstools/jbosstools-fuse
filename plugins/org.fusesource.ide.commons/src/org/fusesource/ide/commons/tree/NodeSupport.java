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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.ui.UIHelper;
import org.fusesource.ide.commons.ui.propsrc.BeanPropertySource;
import org.fusesource.ide.commons.util.Nodes;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.internal.actions.RefreshAction;


public abstract class NodeSupport extends Node implements IAdaptable, RefreshableUI, HasRefreshableUI, IPropertySourceProvider, ITabbedPropertySheetPageContributor {

	private IPropertySource propertySource;
	private Object propertyBean;

	public NodeSupport(Node parent) {
		super(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return "org.jboss.tools.jmx.ui.internal.views.navigator.MBeanExplorer";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		 if (adapter == IPropertySheetPage.class) return new TabbedPropertySheetPage(this);
		if (this instanceof NodeSupport) return Platform.getAdapterManager().getAdapter(this, adapter);
		return null;
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
			Activator.getLogger().debug("====== warning getPropertySource() asked for: " + object + " of class: "+ object.getClass());
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
	
	@Override
	public RefreshableUI getRefreshableUI() {
		if (this instanceof RefreshableUI) {
			return (RefreshableUI) this;
		} else if (parent != null) {
			// return parent.getRefreshableUI();
// TODO: reworkd refresh logic -->			System.err.println("refreshing node?!?");
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.tree.RefreshableUI#fireRefresh()
	 */
	@Override
	public void fireRefresh() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				RefreshAction ra = new RefreshAction(UIHelper.ID_JMX_EXPORER);
				ra.run();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.tree.RefreshableUI#fireRefresh(java.lang.Object, boolean)
	 */
	@Override
	public void fireRefresh(Object node, boolean full) {
		if (node instanceof Node) {
			((Node) node).getParent().clearChildren();
			((Node) node).getParent().getChildren();
			fireRefresh();
		}
	}
}
