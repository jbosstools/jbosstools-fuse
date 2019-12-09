/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.tests.integration.contentype;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.junit.Test;

public class CamelContentTypeCheckIT {

	@Test
	public void testNonCamelBlueprintFileHasNotCamelContentType() throws Exception {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IProject project = ws.getRoot().getProject(CamelContentTypeCheckIT.class.getName());
		project.create(null);
		project.open(null);
		IFile file = project.getFile("blueprintButNotCamelFile.xml");
		file.create(new ByteArrayInputStream(("<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\"\r\n" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"    xsi:schemaLocation=\"\r\n" + 
				"      http://www.osgi.org/xmlns/blueprint/v1.0.0 https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd\r\n>\r\n" + 
				"</blueprint>").getBytes(StandardCharsets.UTF_8)), true, null);
		
		assertThat(new CamelFilesFinder().isFuseCamelContentType(file)).isFalse();
	}
	
	
}
