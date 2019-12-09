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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.List;

import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.jmx.camel.internal.JmxTemplateCamelFacade;
import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.jboss.tools.jmx.core.tree.NodeProvider;
import org.jboss.tools.jmx.core.tree.Root;


public class CamelNodeProvider implements NodeProvider {

	@Override
	public void provide(final Root root) {
		if (root.containsDomain("org.apache.camel")) {
			CamelJMXFacade facade = new JmxTemplateCamelFacade(new JmxPluginJmxTemplate(root.getConnection()));
			CamelContextsNode camel = new CamelContextsNode(root, facade);
			root.addChild(camel);
		}
	}

	@Override
	public void provideRootNodes(List<org.jboss.tools.jmx.core.tree.NodeProvider> list) {
	}
}
