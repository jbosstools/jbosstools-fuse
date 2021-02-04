/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.project.providers;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Rule;
import org.junit.Test;

public class CamelVirtualFolderWithTwoProjectIT extends AbstractCamelVirtualFolderIT {

	@Rule
	public FuseProject fuseProject = new FuseProject(CamelVirtualFolderWithTwoProjectIT.class.getName());
	
	@Rule
	public FuseProject fuseProject2 = new FuseProject(CamelVirtualFolderWithTwoProjectIT.class.getName()+"-secondProject");
	
	@Test
	public void testUpdateInOtherProject() throws Exception {
		fuseProject.createEmptyCamelFile();
		CamelVirtualFolder camelVirtualFolder = initializeCamelVirtualFolder(fuseProject.getProject());
		
		fuseProject2.createEmptyCamelFile();
		CamelVirtualFolder camelVirtualFolder2 = initializeCamelVirtualFolder(fuseProject2.getProject());
		
		assertThat(camelVirtualFolder.getCamelFiles()).hasSize(1);
		assertThat(camelVirtualFolder2.getCamelFiles()).hasSize(1);
	}

}
