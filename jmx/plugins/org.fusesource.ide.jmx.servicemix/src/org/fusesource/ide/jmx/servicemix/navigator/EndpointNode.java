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


import org.apache.servicemix.nmr.management.ManagedEndpointMBean;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.jmx.servicemix.ServiceMixJMXPlugin;
import org.jboss.tools.jmx.ui.ImageProvider;


public class EndpointNode extends NodeSupport implements ImageProvider {
	private final ManagedEndpointMBean mbean;

	public EndpointNode(EndpointsNode endpointsNode, ManagedEndpointMBean mbean) {
		super(endpointsNode);
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mbean.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jmx.ui.ImageProvider#getImage()
	 */
	@Override
	public Image getImage() {
		return ServiceMixJMXPlugin.getDefault().getImage("endpoint_node.png");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EndpointNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if( getConnection() != null && getConnection().getProvider() != null ) {
			return ("ServiceMixEndpointNode-" + toString() + "-" + getConnection().getProvider().getName(getConnection())).hashCode();
		}
		return super.hashCode();
	}
}
