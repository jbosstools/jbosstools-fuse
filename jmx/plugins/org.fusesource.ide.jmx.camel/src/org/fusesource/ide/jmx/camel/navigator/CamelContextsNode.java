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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;


public class CamelContextsNode extends RefreshableCollectionNode implements ImageProvider {
	private final CamelJMXFacade facade;

	public CamelContextsNode(Node parent, CamelJMXFacade facade) {
		super(parent);
		this.facade = facade;
	}

	@Override
	public String toString() {
		return "Camel";
	}

	public CamelJMXFacade getFacade() {
		return facade;
	}

	@Override
	protected void loadChildren() {
		try {
			List<CamelContextMBean> camelContexts = facade.getCamelContexts();
			if (camelContexts != null) {
				for (CamelContextMBean camelContextMBean : camelContexts) {
					CamelContextNode child = new CamelContextNode(this, facade, camelContextMBean);
					addChild(child);
				}
			}
		} catch (Exception e) {
			CamelJMXPlugin.getLogger().warning("Failed to connect to JMX: " + e, e);
		}
	}

	public void addChildren(CamelJMXFacade facade) throws Exception {
	}


	@Override
	public Image getImage() {
		return CamelJMXPlugin.getDefault().getImage("camel.png");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CamelContextsNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if(isConnectionAvailable()) {
			return ("CamelContextsNode-" + toString() + "-" + getConnection().getProvider().getName(getConnection())).hashCode();
		}
		return super.hashCode();
	}
}
