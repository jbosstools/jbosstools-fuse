/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.rules.ExternalResource;

/**
 * @author Aurelien Pupier
 *
 */
public class FuseProject extends ExternalResource {

	private static final String DUMMY_SPRING_POM_CONTENT = getDummyPomContent("Spring");
	private static final String DUMMY_BLUEPRINT_POM_CONTENT = getDummyPomContent("Blueprint");

	private IProject project = null;
	private String projectName;
	private String camelVersion;
	private boolean isBlueprint = false;

	public FuseProject(String projectName) {
		this(projectName, CamelCatalogUtils.getLatestCamelVersion());
	}
	
	public FuseProject(String projectName, boolean isBlueprint) {
		this(projectName, CamelCatalogUtils.getLatestCamelVersion(), isBlueprint);
	}

	public FuseProject(String projectName, String camelVersion) {
		this.projectName = projectName;
		this.camelVersion = camelVersion;
		this.isBlueprint = false;
	}

	public FuseProject(String projectName, String camelVersion, boolean isBlueprint) {
		this(projectName, camelVersion);
		this.isBlueprint = isBlueprint;
	}

	@Override
	public void before() throws Throwable {
		super.before();
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		project = ws.getRoot().getProject(projectName);
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		// Create a fake pom.xml
		IFile pom = project.getFile(IMavenConstants.POM_FILE_NAME);
		String pomContent = DUMMY_SPRING_POM_CONTENT;
		if (isBlueprint) {
			pomContent = DUMMY_BLUEPRINT_POM_CONTENT;
		}
		pom.create(new ByteArrayInputStream(String.format(pomContent, this.camelVersion).getBytes(StandardCharsets.UTF_8)), true, new NullProgressMonitor());
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(IResource.FORCE, true, new NullProgressMonitor());
		IFolder srcMainFolder = srcFolder.getFolder("main");
		srcMainFolder.create(IResource.FORCE, true, new NullProgressMonitor());
		srcMainFolder.getFolder("java").create(IResource.FORCE, true, new NullProgressMonitor());
	}

	private static String getDummyPomContent(String type) {
		InputStream inputStream = FuseProject.class.getResourceAsStream("/dummy"+ type + "Pom.xml");
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
        	e.printStackTrace();
			return "";
		}
	}

	@Override
	public void after() {
		super.after();
		if (project != null && project.exists()) {
			try {
				project.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public IProject getProject() {
		return project;
	}

	public CamelFile createEmptyCamelFile() throws CoreException, IOException {
		IFile file = project.getFile("camel-context.xml");
		return createEmptyCamelFile(file);
	}

	public CamelFile createEmptyCamelFile(IFile file) throws CoreException, IOException {
		return createFileFromTemplate(file, "/empty-CamelFile.xml");
	}

	public CamelFile createEmptyBlueprintCamelFile() throws CoreException, IOException {
		IFile file = project.getFile("blueprint.xml");
		return createFileFromTemplate(file, "/empty-BlueprintCamelFile.xml");
	}

	public CamelFile createEmptyBlueprintCamelFile(IFile file) throws CoreException, IOException {
		return createFileFromTemplate(file, "/empty-BlueprintCamelFile.xml");
	}

	protected CamelFile createFileFromTemplate(IFile file, String nameTemplate) throws CoreException, IOException {
		try(InputStream source = FuseProject.class.getResourceAsStream(nameTemplate)){
			file.create(source, true, new NullProgressMonitor());
		}
		return new CamelIOHandler().loadCamelModel(file, new NullProgressMonitor());
	}

	public CamelFile createEmptyCamelFileWithRoutes() throws CoreException, IOException {
		IFile file = project.getFile("camel-context.xml");
		return createEmptyCamelFileWithRoutes(file);
	}

	private CamelFile createEmptyCamelFileWithRoutes(IFile file) throws CoreException, IOException {
		return createFileFromTemplate(file, "/empty-CamelFileWithRoutes.xml");
	}
}
