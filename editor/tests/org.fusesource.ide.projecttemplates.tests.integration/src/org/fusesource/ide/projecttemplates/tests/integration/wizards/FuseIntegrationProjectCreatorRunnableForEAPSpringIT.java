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
import static org.junit.Assume.assumeFalse;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.EAPSpringTemplate;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForEAPSpringIT extends FuseIntegrationProjectCreatorRunnableIT {

	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return CamelModelFactory.getSupportedCamelVersions(); 
	}
	
	public FuseIntegrationProjectCreatorRunnableForEAPSpringIT(String version) {
		super();
		camelVersion = version;
	}
	
	@Test
	@Ignore("EAP test is not working yet")
	public void testEAPSpringProjectCreation() throws Exception {
        assumeFalse("Spring with 2.15 redhat version is not working", camelVersion.startsWith("2.15"));

        testProjectCreation("-EAPSpringProject", CamelDSLType.SPRING, "src/main/webapp/META-INF/jboss-camel-context.xml", null);
	}
	
	@Override
	protected NewProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		newProjectMetadata.setTemplate(new EAPSpringTemplate());
		newProjectMetadata.setBlankProject(false);
		return newProjectMetadata;
	}
	
    @Override
    protected void checkCorrectFacetsEnabled(IProject project) throws CoreException {
        IFacetedProject fproj = ProjectFacetsManager.create(project);
        readAndDispatch(0);
        
        boolean camelFacetFound = fproj.hasProjectFacet(camelFacet);
        boolean javaFacetFound = fproj.hasProjectFacet(javaFacet);
        boolean mavenFacetFound = fproj.hasProjectFacet(m2eFacet);
        boolean utilityFacetFound = fproj.hasProjectFacet(utilFacet);
        boolean webFacetFound = fproj.hasProjectFacet(webFacet);

        assertThat(camelFacetFound).isTrue();
        assertThat(javaFacetFound).isTrue();
        assertThat(mavenFacetFound).isTrue();
        assertThat(utilityFacetFound).isFalse();
        assertThat(webFacetFound).isTrue();
        
        checkWARMappingCorrect(project);
    }
    
    private void checkWARMappingCorrect(IProject project) throws CoreException {
    	assertThat(project).isNotNull();
    	IProgressMonitor monitor = new NullProgressMonitor();
    	final IVirtualComponent c = ComponentCore.createComponent(project, false);
		c.create(IVirtualResource.NONE, monitor);
		final IVirtualFolder webroot = c.getRootFolder();
		final IVirtualFolder classesFolder = webroot.getFolder("/WEB-INF/classes"); //$NON-NLS-1$
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
		checkMappingsForSourcePathCorrect(m2prj.getCompileSourceLocations(), classesFolder, monitor);
		checkMappingsForSourcePathCorrect(m2prj.getTestCompileSourceLocations(), classesFolder, monitor);
    }
    
    private void checkMappingsForSourcePathCorrect(IPath[] paths, IVirtualFolder vFolder, IProgressMonitor monitor) throws CoreException {
    	for (IPath sourceLoc : paths) {
			IFolder srcFolder = project.getFolder(sourceLoc);
			IPath absSourcePath = srcFolder.getProjectRelativePath().makeAbsolute();
			IVirtualResource[] mappings = ComponentCore.createResources(srcFolder);
			for (IVirtualResource mapping : mappings) {
				if (mapping.getProjectRelativePath().equals(absSourcePath)) {
					assertThat(mapping.getRuntimePath()).isEqualTo(vFolder.getRuntimePath());
				}
			}
		}
    }
}
