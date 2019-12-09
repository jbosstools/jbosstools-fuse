/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;

class EmptyProjectCreatorRunnable implements IRunnableWithProgress {

	private final CamelProjectConfiguratorIT camelProjectConfiguratorIT;

	private static final String DUMMY_POM_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
			+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
			+ "  <modelVersion>4.0.0</modelVersion>\n" + "  <groupId>com.mycompany</groupId>\n"
			+ "  <artifactId>testproject</artifactId>\n" + "  <version>1.0.0-SNAPSHOT</version>\n"
			+ "  <packaging>bundle</packaging>\n" + "  <name>Some Dummy Project</name>\n" + "  <build>\n"
			+ "    <defaultGoal>install</defaultGoal>\n" + "    <plugins>\n" + "      <plugin>\n"
			+ "        <artifactId>maven-compiler-plugin</artifactId>\n" + "        <version>2.5.1</version>\n"
			+ "        <configuration>\n" + "          <source>1.7</source>\n" + "          <target>1.7</target>\n"
			+ "        </configuration>\n" + "      </plugin>\n" + "      <plugin>\n"
			+ "        <artifactId>maven-resources-plugin</artifactId>\n" + "        <version>2.6</version>\n"
			+ "        <configuration>\n" + "          <encoding>UTF-8</encoding>\n" + "        </configuration>\n"
			+ "      </plugin>\n" + "    </plugins>\n" + "  </build>\n" + "</project>";

	private String name;

	public EmptyProjectCreatorRunnable(CamelProjectConfiguratorIT camelProjectConfiguratorIT, String projectName) {
		this.camelProjectConfiguratorIT = camelProjectConfiguratorIT;
		this.name = projectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.
	 * core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		// first create the project skeleton
		monitor.beginTask("Creating the project...", IProgressMonitor.UNKNOWN);

		// first create the project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		this.camelProjectConfiguratorIT.project = root.getProject(this.name);
		try {
			this.camelProjectConfiguratorIT.project.create(monitor);
			this.camelProjectConfiguratorIT.project.open(monitor);
			this.camelProjectConfiguratorIT.project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			
			// now create some dummy pom.xml
			createPom(this.camelProjectConfiguratorIT.project);

			IFacetedProject fproj = ProjectFacetsManager.create(this.camelProjectConfiguratorIT.project);

			if (fproj == null) {
				// Add the modulecore nature
				WtpUtils.addNatures(this.camelProjectConfiguratorIT.project);
				addNature(this.camelProjectConfiguratorIT.project, FacetedProjectNature.NATURE_ID, monitor);
				fproj = ProjectFacetsManager.create(this.camelProjectConfiguratorIT.project);
				IFacetedProjectWorkingCopy fpwc = fproj.createWorkingCopy();
				
				IProjectFacet m2eFacet 	= ProjectFacetsManager.getProjectFacet("jboss.m2");
				installFacet(fproj, fpwc, m2eFacet, m2eFacet.getLatestVersion(), monitor);
			}
		} catch (CoreException ex) {
			ex.printStackTrace();
		} finally {
			monitor.done();
		}
	}

	private void installFacet(IFacetedProject fproj, IFacetedProjectWorkingCopy fpwc, IProjectFacet facet, IProjectFacetVersion facetVersion, IProgressMonitor mon) throws CoreException {
		if (facet != null && !fproj.hasProjectFacet(facet)) {
			fpwc.addProjectFacet(facetVersion);
		} else {
			IProjectFacetVersion f = fproj.getProjectFacetVersion(facet);
			if (!f.getVersionString().equals(facetVersion.getVersionString())) {
				// version change
				fpwc.changeProjectFacetVersion(facetVersion);
			}
		}
	}
	
	private void addNature(IProject project, String natureId, IProgressMonitor monitor)
			throws CoreException {
		if (!project.hasNature(natureId)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
			newNatures[0] = natureId;
			description.setNatureIds(newNatures);
			project.setDescription(description, IResource.KEEP_HISTORY, monitor);
		}
	}

	private void createPom(IProject project) {
		File pomFile = new File(project.getLocation().toOSString(), IMavenConstants.POM_FILE_NAME);
		try {
			pomFile.createNewFile();
			Files.write(pomFile.toPath(), DUMMY_POM_CONTENT.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}