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

package org.fusesource.ide.jmx.activemq.navigator;

import java.util.List;

import javax.management.MBeanServerConnection;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.activemq.internal.BrokerFacade;
import org.fusesource.ide.jmx.activemq.internal.RemoteBrokerFacade;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;
import org.jboss.tools.jmx.core.tree.NodeProvider;
import org.jboss.tools.jmx.core.tree.Root;


public class ActiveMQNodeProvider implements NodeProvider {

	@Override
	public void provide(final Root root) {
		if (root.containsDomain("org.apache.activemq")) {
			
			try {
				// TODO replace with better JmxTemplate reusing the Connection!!!
				root.getConnection().run(new IJMXRunnable() {
					
					@Override
					public void run(MBeanServerConnection connection) throws JMXException {
						// TODO REPLACE WITH BETTER JmxTemplateImpl...
						BrokerFacade facade = new RemoteBrokerFacade(connection);
						BrokerNode broker = new BrokerNode(root, facade, "Broker");
						root.addChild(broker);
					}
				});
			} catch (CoreException e) {
				ActiveMQJMXPlugin.getLogger().warning("Failed to connect to JMX: " + e, e);
			}
		}
		
	}

	@Override
	public void provideRootNodes(List<org.jboss.tools.jmx.core.tree.NodeProvider> list) {
	}	
}
