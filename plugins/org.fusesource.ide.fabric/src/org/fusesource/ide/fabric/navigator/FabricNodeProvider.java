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

package org.fusesource.ide.fabric.navigator;

import java.util.Arrays;
import java.util.List;

import javax.management.MalformedObjectNameException;

//import org.fusesource.fabric.monitor.api.FetchMonitoredViewDTO;
import org.fusesource.fabric.service.JmxTemplateSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.JmxPluginJmxTemplate;
import org.fusesource.ide.fabric.navigator.osgi.BundlesNode;
import org.fusesource.ide.fabric.navigator.osgi.OsgiFacade;
import org.fusesource.ide.jmx.core.tree.Root;


public class FabricNodeProvider implements NodeProvider, org.fusesource.ide.jmx.core.tree.NodeProvider {

	@Override
	public void provide(ContainerNode agentNode) {
		/*
		 * try { CamelFacade facade = new
		 * JmxTemplateCamelFacade(agentNode.getJmxTemplate()); CamelContextsNode
		 * camel = new CamelContextsNode(agentNode, facade);
		 * agentNode.addChild(camel); } catch (Exception e) {
		 * FabricCamelPlugin.getLogger().warning("Could not connect to Camel: "
		 * + e, e); }
		 */
	}

	@Override
	public void provide(final Root root) {
		if (root.containsDomain("org.fusesource.fabric")) {
			JmxTemplateSupport jmxTemplate = new JmxPluginJmxTemplate(root.getConnection());
			//Object list = MonitorFacade.list(jmxTemplate);

//			FetchMonitoredViewDTO view = new FetchMonitoredViewDTO();
//
//			view.data_sources.addAll(Arrays.asList(new String[]{
//					"java.lang:name=CMS Old Gen,type=MemoryPool@Usage@used",
//					"java.lang:name=Par Survivor Space,type=MemoryPool@Usage@used",
//					"java.lang:name=Par Eden Space,type=MemoryPool@Usage@used",
//					"java.lang:type=Memory@HeapMemoryUsage@committed"
//			}));
//			view.monitored_set = "jvm-default";

			//System.out.println("======== doing Fabric Monitor call with: " + view);
			//MonitoredViewDTO answer = MonitorFacade.fetch(jmxTemplate, view);
			//System.out.println("Got: " + answer);
		}

		if (root.containsDomain("osgi.core")) {
			try {
				JmxPluginJmxTemplate jmxTemplate = new JmxPluginJmxTemplate(root.getConnection());
				OsgiFacade facade = new OsgiFacade(jmxTemplate);
				root.addChild(new BundlesNode(root, facade));

				// lets not add a node for services; it looks a bit icky
				// lets just add another tab for the bundle/service view later
				//root.addChild(new ServicesNode(root, facade));

			} catch (MalformedObjectNameException e) {
				FabricPlugin.getLogger().error("Failed to create OSGi facade: " + e, e);
			}
		}
	}

	@Override
	public void provideRootNodes(List<org.fusesource.ide.jmx.core.tree.NodeProvider> list) {
	}

}
