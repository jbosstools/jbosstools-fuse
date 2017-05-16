/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author lheinema
 *
 */
public class CamelMavenUtilsTestIT {
	
	private static final String POM_NAME = "pomWithVariableNameInVersion.xml";
	
	@Rule
	public FuseProject fuseProject = new FuseProject("External Files");

	@Before
	public void setup() throws CoreException {
		InputStream inputStream = CamelMavenUtilsTestIT.class.getClassLoader().getResourceAsStream("/" + POM_NAME);
		IFile fileInProject = fuseProject.getProject().getFile("pom.xml");
		fileInProject.delete(true, null);
		fileInProject.create(inputStream, true, new NullProgressMonitor());
	}

	@Test
	public void testUpdatePathParams() {
		String version = new CamelMavenUtils().getCamelVersionFromMaven(fuseProject.getProject());
		assertNotNull("The retrieved camel version should not be null.", version);
		assertNotEquals("The retrieved camel version should not resolve to the variable name.", "${camel.version}", version);
		assertEquals("The retrieved version doesn't match the defined value in the pom file.", "2.19.0", version);
	}
}
