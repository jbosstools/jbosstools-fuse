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

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.management.openmbean.TabularData;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.foundation.ui.drop.DropHandler;
import org.fusesource.ide.foundation.ui.drop.DropHandlerFactory;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableSheetPage;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.tree.Refreshables;
import org.fusesource.ide.foundation.ui.util.Workbenches;
import org.fusesource.ide.foundation.ui.views.ViewPropertySheetPage;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

public class BundlesNode extends RefreshableCollectionNode implements ImageProvider, /** ProjectDropTarget **/ DropHandlerFactory {
	private final OsgiFacade facade;
	private String bundlefilterText;

	public BundlesNode(Node parent, OsgiFacade facade) {
		super(parent);
		this.facade = facade;
	}

	@Override
	public String toString() {
		return "Bundles";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) return getPropertySourceTablePage();
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.tree.RefreshableCollectionNode#getPropertySourceTablePage()
	 */
	@Override
	public ViewPropertySheetPage getPropertySourceTablePage() {
		return new BundlesTableSheetPage(this);
	}

	@Override
	public Image getImage() {
		return KarafJMXPlugin.getDefault().getImage("bundle.png");
	}

	@Override
	protected void loadChildren() {
	}


	public OsgiFacade getFacade() {
		return facade;
	}

	@Override
    public boolean requiresContentsPropertyPage() {
        return false;
    }

	@Override
	protected PropertySourceTableSheetPage createPropertySourceTableSheetPage() {
		return new BundlesTableSheetPage(this);
	}


	@Override
	public List<IPropertySource> getPropertySourceList() {
		List<IPropertySource> answer = new ArrayList<>();
		try {
			final TabularData tabularData = facade.listBundles();
			return TabularDataHelper.toPropertySources(tabularData);
		} catch (Exception e) {
			KarafJMXPlugin.getLogger().error("Failed to fetch bundle state: " + e, e);
		}
		return answer;
	}

	@Override
	public DropHandler createDropHandler(DropTargetEvent event) {
		return null;
	}

	@Override
	public void refresh() {
		super.refresh();

		IPage currentPage = Workbenches.getPropertySheetPage();
		Refreshables.refresh(currentPage);
	}

	public String getBundlefilterText() {
		return bundlefilterText;
	}

	public void setBundleFilterText(String bundlefilterText) {
		this.bundlefilterText = bundlefilterText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof BundlesNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), "OSGiBundlesNode");
	}
}
