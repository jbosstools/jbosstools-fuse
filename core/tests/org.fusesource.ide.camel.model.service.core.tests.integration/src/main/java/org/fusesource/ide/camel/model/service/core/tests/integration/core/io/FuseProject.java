/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.project.registry.MavenProjectFacade;
import org.eclipse.m2e.core.internal.project.registry.StaleMutableProjectRegistryException;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.CamelModelServiceIntegrationTestActivator;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Aurelien Pupier
 *
 */
public class FuseProject extends ExternalResource {

	private static final int TIMEOUT_M2E_REGISTRY_REMOVAL = 50000;
	private static final String DUMMY_SPRING_POM_CONTENT = getDummyPomContent("Spring");
	private static final String DUMMY_BLUEPRINT_POM_CONTENT = getDummyPomContent("Blueprint");

	private IProject project = null;
	private String projectName;
	private String camelVersion;
	private boolean isBlueprint = false;

	public FuseProject() {
		this(null, CamelCatalogUtils.getLatestCamelVersion());
	}
	
	public FuseProject(String projectName) {
		this(projectName, CamelCatalogUtils.getLatestCamelVersion());
	}
	
	public FuseProject(String projectName, boolean isBlueprint) {
		this(projectName, CamelCatalogUtils.getLatestCamelVersion(), isBlueprint);
	}
	
	public FuseProject(boolean isBlueprint) {
		this(null, CamelCatalogUtils.getLatestCamelVersion(), isBlueprint);
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
	public Statement apply(Statement base, Description description) {
		if(projectName == null) {
			projectName = description.getClassName() + "." + description.getMethodName();
		}
		return super.apply(base, description);
	}

	@Override
	public void before() throws Throwable {
		super.before();
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		project = ws.getRoot().getProject(projectName);
		if (!project.exists()) {
			logInfo("Creating project "+projectName);
			project.create(null);
		}
		if (!project.isOpen()) {
			logInfo("Opening project "+projectName);
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
		
		IFolder srcTestFolder = srcFolder.getFolder("test");
		srcTestFolder.create(IResource.FORCE, true, new NullProgressMonitor());
		srcTestFolder.getFolder("java").create(IResource.FORCE, true, new NullProgressMonitor());
		
		try {
			enableMavenNature();
		} catch (StaleMutableProjectRegistryException ex) {
			logError(ex, "Exception why enabling Maven Nature of " + projectName);
			// second attempt to enable Maven nature
			enableMavenNature();
		}
		logInfo("End of FuseProject.before() for project " + projectName);
	}

	private void logError(StaleMutableProjectRegistryException ex, String message) {
		CamelModelServiceIntegrationTestActivator bundle = CamelModelServiceIntegrationTestActivator.getDefault();
		if(bundle != null) {
			bundle.getLog().error(message, ex);
		} else {
			// When launching in Eclipse as JUnit Plugin, the bundle is not initialized. So here is a workaround
			ex.printStackTrace();
			System.err.println(message);
		}
	}

	private void logInfo(String message) {
		CamelModelServiceIntegrationTestActivator bundle = CamelModelServiceIntegrationTestActivator.getDefault();
		if(bundle != null) {
			bundle.getLog().info(message);
		} else {
			// When launching in Eclipse as JUnit Plugin, the bundle is not initialized. So here is a workaround
			System.out.println(message);
		}
	}

	private void enableMavenNature() throws CoreException {
		ResolverConfiguration configuration = new ResolverConfiguration();
		configuration.setResolveWorkspaceProjects(true);
		configuration.setSelectedProfiles(""); //$NON-NLS-1$
		new BuildAndRefreshJobWaiterUtil().waitJob(new NullProgressMonitor());
		IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();
		logInfo("Enabling Maven nature to " + projectName);
		configurationManager.enableMavenNature(project, configuration, new NullProgressMonitor());
		logInfo("Updating Maven project configuration of " + projectName);
		configurationManager.updateProjectConfiguration(project, new NullProgressMonitor());
		new BuildAndRefreshJobWaiterUtil().waitJob(new NullProgressMonitor());
	}

	private static String getDummyPomContent(String type) {
		InputStream inputStream = FuseProject.class.getResourceAsStream("/dummy"+ type + "Pom.xml");
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
        	CamelModelServiceIntegrationTestActivator.getDefault().getLog().log(new Status(IStatus.ERROR, CamelModelServiceIntegrationTestActivator.ID, "Error retrievving dummy pom content for test", e));
			return "";
		}
	}

	@Override
	public void after() {
		super.after();
		if (project != null && project.exists()) {
			try {
				project.delete(true, new NullProgressMonitor());
				awaitProjectIsRemovedFromM2ERegistry();
			} catch (CoreException | InterruptedException e) {
				CamelModelServiceIntegrationTestActivator.getDefault().getLog().log(new Status(IStatus.ERROR, CamelModelServiceIntegrationTestActivator.ID, "Cannot delete project used during test", e));
			}
		}
	}

	/**
	 * The m2e registry is updated asynchronously.
	 * When deleting a project, and recreating one with same name just after, it can cause StaleMutableRegistryException.
	 * Awaiting that the m2e registry is updated with project removal avoids this issue.
	 * 
	 * @throws InterruptedException
	 */
	private void awaitProjectIsRemovedFromM2ERegistry() throws InterruptedException {
		int time = 0;
		boolean mavenProjectInRegistry = true;
		while(mavenProjectInRegistry && time < TIMEOUT_M2E_REGISTRY_REMOVAL ) {
			List<MavenProjectFacade> projectsInRegistry = MavenPluginActivator.getDefault().getMavenProjectManagerImpl().getProjects();
			mavenProjectInRegistry = projectsInRegistry.stream()
					.map(IMavenProjectFacade::getProject)
					.map(IProject::getName)
					.filter(projectInRegistryName -> projectInRegistryName.equals(projectName))
					.count() > 0;
					time += 500;
					Thread.sleep(500);
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
