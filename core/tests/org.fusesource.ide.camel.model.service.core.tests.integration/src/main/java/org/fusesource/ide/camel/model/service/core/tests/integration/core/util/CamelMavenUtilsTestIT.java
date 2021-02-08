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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Repository;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
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
	public FuseProject fuseProject = new FuseProject();

	private IFile pomFileInProject;

	@Before
	public void setup() {
		pomFileInProject = fuseProject.getProject().getFile("pom.xml");
	}

	@Test
	public void testRetrieveCamelVersionFromMaven() throws CoreException {
		InputStream inputStream = CamelMavenUtilsTestIT.class.getClassLoader().getResourceAsStream("/" + POM_NAME);
		pomFileInProject.setContents(inputStream, true, true, new NullProgressMonitor());
		
		new JobWaiterUtil(Arrays.asList(ResourcesPlugin.FAMILY_AUTO_BUILD)).waitJob(new NullProgressMonitor());
		
		String version = new CamelMavenUtils().getCamelVersionFromMaven(fuseProject.getProject(), false);
		assertNotNull("The retrieved camel version should not be null.", version);
		assertNotEquals("The retrieved camel version should not resolve to the variable name.", "${camel.version}", version);
		assertEquals("The retrieved version doesn't match the defined value in the pom file.", "2.19.0", version);
	}
	
	@Test
	public void testEmptyRepositoriesWhenPomMissing() throws CoreException {
		pomFileInProject.delete(true, null);
		List<Repository> repositories = new CamelMavenUtils().getRepositories(fuseProject.getProject(), new NullProgressMonitor());
		assertThat(repositories).isEmpty();
	}
}
