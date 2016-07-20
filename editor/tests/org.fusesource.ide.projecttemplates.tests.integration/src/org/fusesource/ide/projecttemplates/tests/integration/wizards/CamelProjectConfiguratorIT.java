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

package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jst.common.project.facet.WtpUtils;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.fusesource.ide.foundation.core.util.JobHelpers;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author lhein
 *
 */
public class CamelProjectConfiguratorIT {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private IProject project = null;

	@Before
	public void setup() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
	}

	@After
	public void tearDown() throws CoreException, OperationCanceledException, InterruptedException {
		if (project != null) {
			waitJob();
			// project.delete(true, new NullProgressMonitor());
		}
	}

	private void waitJob() throws OperationCanceledException, InterruptedException {
		try {
			JobHelpers.waitForJobsToComplete(new NullProgressMonitor());
		} catch (CoreException ex) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			System.out.println("Have a trace in case of infinite loop in CamelProjectConfiguratorIT.waitJob()");
			waitJob();
		}
	}

	@Test
	public void testEmptyProjectCreation() throws Exception {
		testEmptyNonCamelProjectCreation("-emptyNonCamel");
	}

	private void testEmptyNonCamelProjectCreation(String projectNameSuffix) throws Exception {
		final String projectName = CamelProjectConfiguratorIT.class.getSimpleName() + projectNameSuffix;
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(false, true,
				new EmptyProjectCreatorRunnable(projectName));

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		waitJob();

		assertThat(project.exists()).isTrue();
		final IFile pom = project.getFile("pom.xml");
		assertThat(pom.getLocation().toFile().exists()).isTrue();

		// test modification of the pom.xml is not resulting in project
		// getting a fuse project (facet and nature)
		assertThat(isCamelFacetEnabled(project)).isFalse();
		assertThat(isCamelNatureEnabled(project)).isFalse();

		modifyPOM(pom.getLocation().toFile());
		waitJob();

		// test modification of the pom.xml is not resulting in project
		// getting a fuse project (facet and nature)
		assertThat(isCamelFacetEnabled(project)).isFalse();
		assertThat(isCamelNatureEnabled(project)).isFalse();
	}
	
	private void modifyPOM(File pomFile) throws CoreException {
		Model m2m = MavenPlugin.getMaven().readModel(pomFile);

		// some tiny change
		m2m.setDescription("New Description");
				
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile));
		    MavenPlugin.getMaven().writeModel(m2m, os);
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		} finally {
			IFile pomIFile2 = project.getProject().getFile("pom.xml");
			if (pomIFile2 != null) {
				pomIFile2.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		    }
			project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
		}
	}

	private boolean isCamelFacetEnabled(IProject project) throws CoreException {
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		if (fproj != null) {
			Set<IProjectFacetVersion> facets = fproj.getProjectFacets();
			Iterator<IProjectFacetVersion> itFacet = facets.iterator();
			while (itFacet.hasNext()) {
				IProjectFacetVersion f = itFacet.next();
				if (ICamelFacetDataModelProperties.CAMEL_PROJECT_FACET.equals(f.getProjectFacet().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCamelNatureEnabled(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] ids = projectDescription.getNatureIds();
		return Arrays.stream(ids).anyMatch(RiderProjectNature.NATURE_ID::equals);
	}

	private class EmptyProjectCreatorRunnable implements IRunnableWithProgress {

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

		public EmptyProjectCreatorRunnable(String projectName) {
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
			project = root.getProject(this.name);
			try {
				project.create(monitor);
				project.open(monitor);
				project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
				
				// now create some dummy pom.xml
				createPom(project);

				IFacetedProject fproj = ProjectFacetsManager.create(project);

				if (fproj == null) {
					// Add the modulecore nature
					WtpUtils.addNatures(project);
					addNature(project, FacetedProjectNature.NATURE_ID, monitor);
					fproj = ProjectFacetsManager.create(project);
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
			File pomFile = new File(project.getLocation().toOSString(), "pom.xml");
			try {
				pomFile.createNewFile();
				Files.write(pomFile.toPath(), DUMMY_POM_CONTENT.getBytes("UTF-8"), StandardOpenOption.WRITE);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
