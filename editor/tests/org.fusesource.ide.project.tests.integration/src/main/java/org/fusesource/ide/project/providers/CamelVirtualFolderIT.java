/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.project.providers;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CamelVirtualFolderIT {

	@Rule
	public FuseProject fuseProject = new FuseProject(CamelVirtualFolderIT.class.getName());
	
	@Rule
	public FuseProject fuseProject2 = new FuseProject(CamelVirtualFolderIT.class.getName()+"-secondProject");
	
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	@Test
	public void testCamelVirtualFolderFindCamelFile() throws Exception {
		fuseProject.createEmptyCamelFile();
		
		CamelVirtualFolder camelVirtualFolder = initializeCamelVirtualFolder(fuseProject.getProject());
		
		assertThat(camelVirtualFolder.getCamelFiles()).hasSize(1);
	}
	
	@Test
	public void testCamelVirtualFolderDetectRemovedCamelFile() throws Exception {
		CamelFile camelFile = fuseProject.createEmptyCamelFile();
		CamelVirtualFolder camelVirtualFolder = initializeCamelVirtualFolder(fuseProject.getProject());
		assertThat(camelVirtualFolder.getCamelFiles()).hasSize(1);
		
		camelFile.getResource().delete(true, new NullProgressMonitor());
		
		assertThat(camelVirtualFolder.getCamelFiles()).hasSize(0);
	}
	
	@Test
	public void testCamelVirtualFolderIgnoresCamelFilesInTargetFolder() throws Exception {
		IFolder targetFolder = fuseProject.getProject().getFolder("target");
		if(!targetFolder.exists()) {
			targetFolder.create(true, true, new NullProgressMonitor());
		}
		IFile camelFileInsideTargetFolder = targetFolder.getFile("emptyCamelFileInTargetFolder.xml");
		fuseProject.createEmptyCamelFile(camelFileInsideTargetFolder);
		
		CamelVirtualFolder camelVirtualFolder = initializeCamelVirtualFolder(fuseProject.getProject());
		
		assertThat(camelVirtualFolder.getCamelFiles()).hasSize(0);
	}
	
	@Test
	public void testUpdateInOtherProject() throws Exception {
		fuseProject.createEmptyCamelFile();
		CamelVirtualFolder camelVirtualFolder = initializeCamelVirtualFolder(fuseProject.getProject());
		
		fuseProject2.createEmptyCamelFile();
		CamelVirtualFolder camelVirtualFolder2 = initializeCamelVirtualFolder(fuseProject2.getProject());
		
		assertThat(camelVirtualFolder.getCamelFiles()).hasSize(1);
		assertThat(camelVirtualFolder2.getCamelFiles()).hasSize(1);
	}
	
	private CamelVirtualFolder initializeCamelVirtualFolder(IProject project) {
		CamelVirtualFolder camelVirtualFolder = new CamelVirtualFolder(project);
		camelVirtualFolder.populateChildren();
		return camelVirtualFolder;
	}
}
