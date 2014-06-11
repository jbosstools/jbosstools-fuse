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

package org.fusesource.ide.fabric.servicemix.navigator;

import io.fabric8.servicemix.facade.JmxTemplateServiceMixFacade;
import io.fabric8.servicemix.facade.ServiceMixFacade;

import java.util.List;

import org.fusesource.ide.fabric.JmxPluginJmxTemplate;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.NodeProvider;
import org.fusesource.ide.fabric.servicemix.FabricServiceMixPlugin;
import org.jboss.tools.jmx.core.tree.Root;


public class ServiceMixNodeProvider implements NodeProvider, org.jboss.tools.jmx.core.tree.NodeProvider {

	public void provide(ContainerNode agentNode) {
		try {
			ServiceMixFacade facade = new JmxTemplateServiceMixFacade(agentNode.getJmxTemplate());
			ServiceMixNode camel = new ServiceMixNode(agentNode, facade);
			agentNode.addChild(camel);
		} catch (Exception e) {
			FabricServiceMixPlugin.getLogger().warning("Could not connect to ServiceMix: " + e, e);
		}
	}

	@Override
	public void provide(final Root root) {
		if (root.containsDomain("org.apache.servicemix")) {
			ServiceMixFacade facade = new JmxTemplateServiceMixFacade(new JmxPluginJmxTemplate(root.getConnection()));
			ServiceMixNode camel = new ServiceMixNode(root, facade);
			root.addChild(camel);
		}
	}

	public void provideRootNodes(List<org.jboss.tools.jmx.core.tree.NodeProvider> list) {
	}
	
}
