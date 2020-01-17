/******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundleModel;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.Constants;

public class ImportExportPackageUpdaterForManifestIT {
	
	private static final String POM_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\"" +
			"		  xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
			"    	  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
			"  <modelVersion>4.0.0</modelVersion>\n" +
			"  <groupId>com.mycompany</groupId>\n" +
			"  <artifactId>testproject</artifactId>\n" +
			"  <version>1.0.0-SNAPSHOT</version>\n" +
			"  <packaging>bundle</packaging>\n" +
			"  <name>Some Dummy Project</name>\n" +
			"  <dependencies>\n" + 
			"	  <dependency>\n" + 
			"	    <groupId>org.apache.camel</groupId>\n" + 
			"	    <artifactId>camel-core</artifactId>\n" + 
			"	    <version>2.21.0</version>\n" + 
			"	  </dependency>\n" + 
			"  </dependencies>" +
			"  <build>\n" +
			"    <plugins>\n" +
			"      <plugin>\n"+
			"        <groupId>org.apache.felix</groupId>\n"+
			"        <artifactId>maven-bundle-plugin</artifactId>\n"+
			"        <version>4.2.0</version>\n"+
			"        <extensions>true</extensions>\n"+
			"      </plugin>";

	private static final String POM_END = 
			"    </plugins>\n" +
			"  </build>\n" +
			"</project>";
	

	@Rule
	public FuseProject fuseProject = new FuseProject(ImportExportPackageUpdaterForManifestIT.class.getName());
	
	private IProject project;
	private IFile pomIFile;
	private IFile manifestFile;

	/**
	 * Create a Project with a pom file and a MANIFEST file
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		project = fuseProject.getProject();
		pomIFile = project.getFile(IMavenConstants.POM_FILE_NAME);
		pomIFile.delete(IResource.FORCE, new NullProgressMonitor());
		pomIFile.create(new ByteArrayInputStream((POM_START + POM_END).getBytes(StandardCharsets.UTF_8)), true, new NullProgressMonitor());
		manifestFile = project.getFile("src/main/resources/META-INF/MANIFEST.MF");
		prepareFolder(project.getFolder("src/main/resources/META-INF/"));
		manifestFile.create(ImportExportPackageUpdaterForManifestIT.class.getResourceAsStream("/MANIFEST-Minimal.MF"), IResource.FORCE, new NullProgressMonitor());
	}
	
	@Test
	public void testImportPackageELAddedForFuseBetween63And70() throws CoreException {
		pomIFile.setContents(new ByteArrayInputStream((POM_START + POM_END).replaceAll("2.21.0", "2.17.0").getBytes(StandardCharsets.UTF_8)), IResource.FORCE, new NullProgressMonitor());
		new BuildAndRefreshJobWaiterUtil().waitJob(new NullProgressMonitor());
		
		new ImportExportPackageUpdater(project, null, null).updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String importPackage = bundleModel.getBundle().getHeader(Constants.IMPORT_PACKAGE);
		assertThat(normalize(importPackage)).isEqualTo(normalize("*,com.sun.el;version=\"[2,3)\""));
	}
	
	@Test
	public void testImportPackageELNotAddedForFuse70() {
		new ImportExportPackageUpdater(project, null, null).updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String importPackage = bundleModel.getBundle().getHeader(Constants.IMPORT_PACKAGE);
		assertThat(normalize(importPackage)).isEqualTo(normalize("*"));
	}
	
	@Test
	public void testExportPackageAdded() {
		new ImportExportPackageUpdater(project, "source.pack.MyClass", "target.pack.MyOtherClass").updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String exportPackage = bundleModel.getBundle().getHeader(Constants.EXPORT_PACKAGE);
		assertThat(normalize(exportPackage)).isEqualTo(normalize("source.pack,target.pack"));
	}
	
	@Test
	public void testExportDefaultPackageAdded() {
		new ImportExportPackageUpdater(project, "MyClass", "target.pack.MyOtherClass").updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String exportPackage = bundleModel.getBundle().getHeader(Constants.EXPORT_PACKAGE);
		assertThat(normalize(exportPackage)).isEqualTo(normalize(".,target.pack"));
	}
	
	@Test
	public void testExportPackageNotAddedForExternalClasses() throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
		description.setNatureIds(new String[]{JavaCore.NATURE_ID});
		project.setDescription(description, new NullProgressMonitor());
		
		JavaCore.create(project).setRawClasspath(new IClasspathEntry[]{JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER))}, new NullProgressMonitor());
		
		new ImportExportPackageUpdater(project, "java.util.List", "target.pack.MyOtherClass").updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String exportPackage = bundleModel.getBundle().getHeader(Constants.EXPORT_PACKAGE);
		assertThat(normalize(exportPackage)).isEqualTo(normalize("target.pack"));
	}
	
	@Test
	public void testExportPackageNotAddedTwice() {
		new ImportExportPackageUpdater(project, "source.pack.MyClass", "target.pack.MyOtherClass").updatePackageImports(new NullProgressMonitor());
		new ImportExportPackageUpdater(project, "source.pack.MyClass", "target.pack.MyOtherClass").updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String exportPackage = bundleModel.getBundle().getHeader(Constants.EXPORT_PACKAGE);
		assertThat(normalize(exportPackage)).isEqualTo(normalize("source.pack,target.pack"));
	}
	
	@Test
	public void testImportPackageNotAddedTwice() {
		new ImportExportPackageUpdater(project, null, null).updatePackageImports(new NullProgressMonitor());
		new ImportExportPackageUpdater(project, null, null).updatePackageImports(new NullProgressMonitor());
		
		WorkspaceBundleModel bundleModel = new WorkspaceBundleModel(manifestFile);
		String importPackage = bundleModel.getBundle().getHeader(Constants.IMPORT_PACKAGE);
		assertThat(normalize(importPackage)).isEqualTo(normalize("*"));
	}
	
	public void prepareFolder(IFolder folder) throws CoreException{
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			prepareFolder((IFolder) parent);
		}
		if (!folder.exists()) {
			folder.create(true, false, new NullProgressMonitor());
		}
	}
	
	private String normalize(String text) {
		StringBuilder builder = new StringBuilder();
		StringCharacterIterator iter = new StringCharacterIterator(text);
		for (char chr = iter.first(); chr != CharacterIterator.DONE; chr = iter.next()) {
			if (!Character.isWhitespace(chr)){
				builder.append(chr);
			}
		}
		return builder.toString();
	}
	
}
