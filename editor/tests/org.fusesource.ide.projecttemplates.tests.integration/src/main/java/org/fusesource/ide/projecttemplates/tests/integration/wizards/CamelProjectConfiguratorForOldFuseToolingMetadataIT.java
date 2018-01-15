/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.internal.wizards.datatransfer.SmartImportJob;
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.tests.util.MavenProjectHelper;
import org.fusesource.ide.projecttemplates.maven.CamelProjectConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CamelProjectConfiguratorForOldFuseToolingMetadataIT {
	
	private File projectDirectory;
	
	@Before
	public void setup() throws Exception {
		clearAllProjects();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		projectDirectory = root.getLocation().append("test-old-metadata").toFile().getCanonicalFile();
		ZipInputStream zipInputStream = new ZipInputStream(CamelProjectConfiguratorForOldFuseToolingMetadataIT.class.getResourceAsStream("/test-old-metadata.zip"));
		unzip(zipInputStream, projectDirectory.toPath());
	}
	
	@After
	public void tearDown() throws CoreException {
		clearAllProjects();
	}

	protected void clearAllProjects() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : root.getProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	@Ignore("when importing as Eclipse projects, the Camel facet version is not updated. See https://issues.jboss.org/browse/FUSETOOLS-2756")
	public void testFacetUpgradedUsingSmartImport() throws Exception {
		SmartImportJob job = new SmartImportJob(projectDirectory, Collections.EMPTY_SET, true, true);
		Map<File, List<ProjectConfigurator>> proposals = job.getImportProposals(new NullProgressMonitor());
		job.setDirectoriesToImport(proposals.keySet());
	    job.run(new NullProgressMonitor());
	    job.join();
	    
	    checkImportedProject();
	}

	@Test
	public void testFacetUpgradedUsingMavenImport() throws Exception {
		new MavenProjectHelper().importProjects(projectDirectory, new String[] {"pom.xml"});
		
		checkImportedProject();
	}
	
	protected void checkImportedProject() throws CoreException {
	    new BuildAndRefreshJobWaiterUtil().waitJob(new NullProgressMonitor());
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	    IProject project = root.getProject("test-old-metadata");
	    
	    IFacetedProject fproj = ProjectFacetsManager.create(project);
	    IProjectFacetVersion installedVersion = fproj.getInstalledVersion(CamelProjectConfigurator.camelFacet);
	    assertThat(installedVersion.getVersionString()).isEqualTo(CamelProjectConfigurator.DEFAULT_CAMEL_FACET_VERSION);
	}
	
	public void unzip(final ZipInputStream zipInputStream, final Path unzipLocation) throws IOException {
		if (!(Files.exists(unzipLocation))) {
			Files.createDirectories(unzipLocation);
		}
		ZipEntry entry = zipInputStream.getNextEntry();
		while (entry != null) {
			Path filePath = unzipLocation.resolve(entry.getName());
			if (!entry.isDirectory()) {
				unzipFiles(zipInputStream, filePath);
			} else {
				Files.createDirectories(filePath);
			}

			zipInputStream.closeEntry();
			entry = zipInputStream.getNextEntry();
		}
	}

    public void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {
    	if(!unzipFilePath.getParent().toAbsolutePath().toFile().mkdirs()) {
    		System.out.println("argh "+unzipFilePath.toAbsolutePath().toFile());
    	}
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = zipInputStream.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
	
}
