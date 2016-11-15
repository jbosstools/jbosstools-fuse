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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.util.JobWaiterUtil;
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

	IProject project = null;

	@Before
	public void setup() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
	}

	@After
	public void tearDown() throws CoreException, OperationCanceledException, InterruptedException {
		if (project != null) {
			new JobWaiterUtil().waitBuildAndRefreshJob(new NullProgressMonitor());
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testEmptyButFacetedProjectCreation() throws Exception {
		final String projectName = CamelProjectConfiguratorIT.class.getSimpleName() + "-emptyNonCamel";
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(false, true,
				new EmptyProjectCreatorRunnable(this, projectName));

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		new JobWaiterUtil().waitBuildAndRefreshJob(new NullProgressMonitor());
		testFacetsAndNatures();
	}
	
	@Test
	public void testMavenNotFacetedProjectUpdate() throws Exception {
		File projectFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "simple-maven-project");
		projectFolder.mkdirs();
		final File file = new File(projectFolder, "pom.xml");
		Files.copy(CamelProjectConfiguratorIT.class.getResourceAsStream("pom.xml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		
		project = new MavenProjectHelper().importProjects(projectFolder, new String[]{"pom.xml"})[0];
		
		testFacetsAndNatures();
	}

	private void testFacetsAndNatures() throws Exception {
		assertThat(project.exists()).isTrue();
		final IFile pom = project.getFile("pom.xml");
		assertThat(pom.getLocation().toFile().exists()).isTrue();
		
		checkCamelFacetAndNatureNotAdded();

		modifyPOM(pom.getLocation().toFile());
		new JobWaiterUtil().waitBuildAndRefreshJob(new NullProgressMonitor());

		checkCamelFacetAndNatureNotAdded();
	}

	private void checkCamelFacetAndNatureNotAdded() throws CoreException {
		// test modification of the pom.xml is not resulting in project
		// getting a fuse project (facet and nature)
		assertThat(isCamelFacetEnabled(project)).isFalse();
		assertThat(isCamelNatureEnabled(project)).isFalse();
	}
	
	private void modifyPOM(File pomFile) throws CoreException {
		Model m2m = MavenPlugin.getMaven().readModel(pomFile);

		// some tiny change (including a dependency!)
		m2m.setDescription("New Description");
		final Dependency dependency = new Dependency();
		dependency.setGroupId("junit");
		dependency.setArtifactId("junit");
		dependency.setVersion("4.11");
		m2m.addDependency(dependency);
				
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile))){
		    MavenPlugin.getMaven().writeModel(m2m, os);
		} catch (Exception ex) {
			System.out.println(ex);
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
}
