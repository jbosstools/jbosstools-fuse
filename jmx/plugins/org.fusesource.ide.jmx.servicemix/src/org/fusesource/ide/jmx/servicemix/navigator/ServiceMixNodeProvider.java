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

import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.fusesource.ide.jmx.servicemix.internal.JmxTemplateServiceMixFacade;
import org.fusesource.ide.jmx.servicemix.internal.ServiceMixFacade;
import org.jboss.tools.jmx.core.tree.NodeProvider;
import org.jboss.tools.jmx.core.tree.Root;

public class ServiceMixNodeProvider implements NodeProvider {

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
