/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core.io;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.junit.Test;

public class CamelIOHandlerForEmptyFileIT {
	
	@Test
	public void testEmptyCamelFileLoadReturnNull() throws Exception {
		File xmlFile = Files.createTempFile("empty",".xml").toFile();
		new CamelIOHandler().loadCamelModel(xmlFile , new NullProgressMonitor());
	}

}
