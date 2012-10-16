package org.fusesource.ide.fabric.camel.navigator;

import java.util.List;


import org.fusesource.fabric.camel.facade.CamelFacade;
import org.fusesource.fabric.camel.facade.JmxTemplateCamelFacade;
import org.fusesource.ide.fabric.JmxPluginJmxTemplate;
import org.fusesource.ide.fabric.camel.FabricCamelPlugin;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.NodeProvider;
import org.fusesource.ide.jmx.core.tree.Root;


public class CamelNodeProvider implements NodeProvider, org.fusesource.ide.jmx.core.tree.NodeProvider {

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
	public void provideRootNodes(List<org.fusesource.ide.jmx.core.tree.NodeProvider> list) {
	}
	
}
