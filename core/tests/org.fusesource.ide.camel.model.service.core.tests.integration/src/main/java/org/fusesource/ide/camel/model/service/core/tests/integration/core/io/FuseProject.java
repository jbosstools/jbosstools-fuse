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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.junit.rules.ExternalResource;

/**
 * @author Aurelien Pupier
 *
 */
public class FuseProject extends ExternalResource {

	private static final String DUMMY_POM_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
			+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
			+ "  <modelVersion>4.0.0</modelVersion>\n"
			+ "  <groupId>com.mycompany</groupId>\n"
			+ "  <artifactId>testproject</artifactId>\n"
			+ "  <version>1.0.0-SNAPSHOT</version>\n"
			+ "  <packaging>bundle</packaging>\n"
			+ "  <name>Some Dummy Project</name>\n"
			+ "  <build>\n"
			+ "    <defaultGoal>install</defaultGoal>\n"
			+ "    <plugins>\n"
			+ "      <plugin>\n"
			+ "        <artifactId>maven-compiler-plugin</artifactId>\n"
			+ "        <version>3.5.1</version>\n"
			+ "        <configuration>\n"
			+ "          <source>1.7</source>\n"
			+ "          <target>1.7</target>\n"
			+ "        </configuration>\n"
			+ "      </plugin>\n"
			+ "      <plugin>\n"
			+ "        <artifactId>maven-resources-plugin</artifactId>\n"
			+ "        <version>2.6</version>\n"
			+ "        <configuration>\n"
			+ "          <encoding>UTF-8</encoding>\n"
			+ "        </configuration>\n"
			+ "      </plugin>\n"
			+ "    </plugins>\n"
			+ "  </build>\n"
			+ "</project>";

	private IProject project = null;
	private String projectName;

	public FuseProject(String projectName) {
		this.projectName = projectName;
	}

	@Override
	protected void before() throws Throwable {
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
		IFile pom = project.getFile("pom.xml");
		pom.create(new ByteArrayInputStream(DUMMY_POM_CONTENT.getBytes()), true, new NullProgressMonitor());
	}

	@Override
	protected void after() {
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
		try(InputStream source = FuseProject.class.getResourceAsStream("/empty-CamelFile.xml")){
			file.create(source, true, new NullProgressMonitor());
		}
		return new CamelIOHandler().loadCamelModel(file, new NullProgressMonitor());
	}

	public CamelFile createEmptyBlueprintCamelFile(IFile file) throws CoreException, IOException {
		try(InputStream source = FuseProject.class.getResourceAsStream("/empty-BlueprintCamelFile.xml")){
			file.create(source, true, new NullProgressMonitor());
		}
		return new CamelIOHandler().loadCamelModel(file, new NullProgressMonitor());
	}
}
