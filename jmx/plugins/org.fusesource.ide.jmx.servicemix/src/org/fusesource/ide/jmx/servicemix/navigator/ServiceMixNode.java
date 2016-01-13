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

package org.fusesource.ide.jmx.servicemix.navigator;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.jmx.servicemix.ServiceMixJMXPlugin;
import org.fusesource.ide.jmx.servicemix.internal.ServiceMixFacade;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

public class ServiceMixNode extends NodeSupport implements ImageProvider {

	private final ServiceMixFacade facade;

	public ServiceMixNode(Node parent, ServiceMixFacade facade) {
		super(parent);
		this.facade = facade;
		addChild(new EndpointsNode(this));
	}

	@Override
	public String toString() {
		return "ServiceMix";
	}

	public ServiceMixFacade getFacade() {
		return facade;
	}

	@Override
	public Image getImage() {
		// TODO replace with better ESB icon!!
		return ServiceMixJMXPlugin.getDefault().getImage("smx_server.png");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServiceMixNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if( getConnection() != null && getConnection().getProvider() != null ) {
			return ("ServiceMixNode" + getConnection().getProvider().getName(getConnection())).hashCode();
		}
		return super.hashCode();
	}
}
