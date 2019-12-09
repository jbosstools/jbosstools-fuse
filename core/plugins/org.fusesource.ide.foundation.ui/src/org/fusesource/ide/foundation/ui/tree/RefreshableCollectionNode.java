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

package org.fusesource.ide.foundation.ui.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.foundation.ui.views.ViewPropertySheetPage;
import org.jboss.tools.jmx.core.tree.Node;

/**
 * Represents a refreshable collection which has no properties of itself but it just
 * renders a table in the PropertySheet of its children
 *
 */
public abstract class RefreshableCollectionNode extends RefreshableNode implements IAdaptable {

	public RefreshableCollectionNode(Node parent) {
		super(parent);
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return getPropertySourceTablePage();
		}
		return super.getAdapter(adapter);
	}

	public boolean requiresContentsPropertyPage() {
	    return true;
	}

	public ViewPropertySheetPage getPropertySourceTablePage() {
		List<IPropertySource> list = getPropertySourceList();
		if (!list.isEmpty()) {
			PropertySourceTableSheetPage view = createPropertySourceTableSheetPage();
			view.setPropertySources(list);
			return view;
		}
		return null;
	}

	protected PropertySourceTableSheetPage createPropertySourceTableSheetPage() {
		return new PropertySourceTableSheetPage(this, getClass().getName());
	}

	public List<IPropertySource> getPropertySourceList() {
		List<IPropertySource> list = new ArrayList<IPropertySource>();
		List<Node> children = getChildrenList();
		for (Node node : children) {
			IPropertySource propertySource = null;
			if (node instanceof IPropertySourceProvider) {
				IPropertySourceProvider provider = (IPropertySourceProvider) node;
				propertySource = provider.getPropertySource(node);
				if (propertySource != null) {
					list.add(propertySource);
				}
			}
			if (propertySource == null && node instanceof RefreshableCollectionNode) {
				RefreshableCollectionNode coll = (RefreshableCollectionNode) node;
				list.addAll(coll.getPropertySourceList());
			}
		}
		return list;
	}

	public int getChildCount() {
		Node[] children = getChildren();
		if (children != null) {
			return children.length;
		}
		return 0;
	}
}

