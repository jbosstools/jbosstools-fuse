/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.File;

import org.fusesource.ide.commons.camel.tools.RouteXml;
import org.fusesource.ide.commons.camel.tools.XmlModel;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.camel.CamelSpringBuilder;

public final class CamelConfigurationHelper {

	private RouteXml routeXml;
	private XmlModel camelModel;
	private CamelConfigBuilder configBuilder;
	
	private CamelConfigurationHelper(RouteXml routeXml, XmlModel camelModel) {
		this.routeXml = routeXml;
		this.camelModel = camelModel;
		configBuilder = new CamelSpringBuilder(camelModel.getContextElement());
	}
	
	public static CamelConfigurationHelper load(File contextFile) throws Exception {
		RouteXml routeXml = new RouteXml();
		XmlModel camelModel = routeXml.unmarshal(contextFile);
		return camelModel != null ? new CamelConfigurationHelper(routeXml, camelModel) : null;
	}

	public CamelConfigBuilder getConfigBuilder() {
		return configBuilder;
	}
	
	public void save(File file) throws Exception {
		routeXml.marshal(file, camelModel);
	}
}
