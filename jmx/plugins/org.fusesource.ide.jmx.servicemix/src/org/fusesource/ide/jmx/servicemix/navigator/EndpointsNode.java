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

import java.util.List;

import org.apache.servicemix.nmr.management.ManagedEndpointMBean;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.jmx.servicemix.ServiceMixJMXPlugin;
import org.fusesource.ide.jmx.servicemix.internal.ServiceMixFacade;
import org.jboss.tools.jmx.ui.ImageProvider;


public class EndpointsNode extends RefreshableCollectionNode implements ImageProvider {

	private final ServiceMixNode serviceMixNode;

	public EndpointsNode(ServiceMixNode serviceMixNode) {
		super(serviceMixNode);
		this.serviceMixNode = serviceMixNode;
	}
	@Override
	public String toString() {
		return "Endpoints";
	}
	
	@Override
	protected void loadChildren() {
		try {
			List<ManagedEndpointMBean> endpoints = getFacade().getEndpoints();
			for (ManagedEndpointMBean endpointMBean : endpoints) {
				EndpointNode endpoint = new EndpointNode(this, endpointMBean);
				addChild(endpoint);
			}
		} catch (Exception e) {
			ServiceMixJMXPlugin.getLogger().warning("Failed to load endpoints for "
					+ this + ". " + e, e);
		}
	}

	public ServiceMixFacade getFacade() {
		return serviceMixNode.getFacade();
	}


	@Override
	public Image getImage() {
		return ServiceMixJMXPlugin.getDefault().getImage("queue_folder.png");
	}
}
