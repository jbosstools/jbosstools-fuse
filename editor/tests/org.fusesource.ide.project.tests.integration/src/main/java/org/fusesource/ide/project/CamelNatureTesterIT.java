/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.project.providers.CamelVirtualFolderIT;
import org.junit.Rule;
import org.junit.Test;

public class CamelNatureTesterIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject(CamelVirtualFolderIT.class.getName());
	
	@Test
	public void testDetectRemovalOfFileForHasChildren() throws Exception {
		CamelFile camelFile = fuseProject.createEmptyCamelFile();
		CamelNatureTester camelNatureTester = new CamelNatureTester();
		assertThat(camelNatureTester.test(fuseProject.getProject(), "hasChildren", null, null)).isTrue();
		
		camelFile.getResource().delete(true, new NullProgressMonitor());
		
		assertThat(camelNatureTester.test(fuseProject.getProject(), "hasChildren", null, null)).isFalse();
	}

}
