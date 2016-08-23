/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.commons;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Aurelien Pupier
 *
 */
// @RunWith(MockitoJUnitRunner.class)
public class ValidateSyntaxInCatalogIT {

	// @Mock(answer = Answers.RETURNS_DEEP_STUBS)
	// CamelFile camelFile;
	// private IProject project;
	// private IFile pom;

	// @Before
	// public void setup() throws CoreException {
	// IWorkspace ws = ResourcesPlugin.getWorkspace();
	// project = ws.getRoot().getProject("External Files");
	// if (!project.exists()) {
	// project.create(null);
	// }
	// if (!project.isOpen()) {
	// project.open(null);
	// }
	// // Create a fake pom.xml
	// pom = project.getFile("pom.xml");
	//
	// }
	//
	// @After
	// public void tearDown() throws CoreException {
	// if (project.exists()) {
	// project.delete(true, new NullProgressMonitor());
	// }
	// }

	@Test
	public void checkComponentSyntaxAreValid() throws Exception {
		StringBuilder sb = new StringBuilder();
		List<String> supportedCamelVersions = CamelModelFactory.getSupportedCamelVersions();
		// TODO: update when a new version is added
		assertThat(supportedCamelVersions).containsExactly("2.15.1.redhat-621084", "2.17.0.redhat-630077");
		for (String camelVersion : supportedCamelVersions) {
			CamelModel camelModel = CamelModelFactory.getModelForVersion(camelVersion);
			for (Component component : camelModel.getComponentModel().getSupportedComponents()) {
				for (Parameter param : new ArrayList<>(component.getUriParameters())) {
					AbstractCamelModelElement selectedEP = new CamelEndpoint(component.getSyntax());
					// doReturn(camelFile).when(selectedEP).getCamelFile();
					// when(camelFile.getResource().getProject()).thenReturn(project);
					try {
						PropertiesUtils.getPropertyFromUri(selectedEP, param, component);
					} catch (Exception e) {
						sb.append(camelVersion + " " + component.getName() + " " + param.getName() + " " + component.getSyntax() + "\n");
					}
				}
			}
		}
		if (sb.length() != 0) {
			fail(sb.toString());
		}

	}

}
