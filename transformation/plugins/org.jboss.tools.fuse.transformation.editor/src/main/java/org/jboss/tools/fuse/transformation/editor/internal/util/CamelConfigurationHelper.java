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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder;

public final class CamelConfigurationHelper {

    private CamelFile camelModel;
    private CamelConfigBuilder configBuilder;
    private CamelIOHandler handler;
    
    private CamelConfigurationHelper(CamelFile camelModel, CamelIOHandler handler) {
    	this.camelModel = camelModel;
    	this.handler = handler;
    }

    public static CamelConfigurationHelper load(File contextFile) throws Exception {
    	CamelIOHandler handler = new CamelIOHandler();
    	CamelFile camelModel = handler.loadCamelModel(contextFile, new NullProgressMonitor());
        return camelModel != null ? new CamelConfigurationHelper(camelModel, handler) : null;
    }

    public static CamelConfigBuilder getConfigBuilder(File contextFile) {
        CamelConfigBuilder builder = new CamelConfigBuilder();
        return builder;
    }
    
    public CamelConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    public void save() throws Exception {
        handler.saveCamelModel(camelModel, camelModel.getResource(), new NullProgressMonitor());
    }
}
