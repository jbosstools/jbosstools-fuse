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

package org.fusesource.ide.fabric.camel.navigator;

import io.fabric8.camel.facade.CamelFacade;
import io.fabric8.camel.facade.JmxTemplateCamelFacade;

import java.util.List;

import org.fusesource.ide.fabric.JmxPluginJmxTemplate;
import org.fusesource.ide.fabric.camel.FabricCamelPlugin;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.NodeProvider;
import org.jboss.tools.jmx.core.tree.Root;


public class CamelNodeProvider implements NodeProvider, org.jboss.tools.jmx.core.tree.NodeProvider {

	public void provide(ContainerNode agentNode) {
		try {
			CamelFacade facade = new JmxTemplateCamelFacade(agentNode.getJmxTemplate());
			CamelContextsNode camel = new CamelContextsNode(agentNode, facade);
			agentNode.addChild(camel);
		} catch (Exception e) {
			FabricCamelPlugin.getLogger().warning("Could not connect to Camel: " + e, e);
		}
	}

	@Override
	public void provide(final Root root) {
		if (root.containsDomain("org.apache.camel")) {
			CamelFacade facade = new JmxTemplateCamelFacade(new JmxPluginJmxTemplate(root.getConnection()));
			CamelContextsNode camel = new CamelContextsNode(root, facade);
			root.addChild(camel);
		}
	}

	@Override
	public void provideRootNodes(List<org.jboss.tools.jmx.core.tree.NodeProvider> list) {
	}
	
}
