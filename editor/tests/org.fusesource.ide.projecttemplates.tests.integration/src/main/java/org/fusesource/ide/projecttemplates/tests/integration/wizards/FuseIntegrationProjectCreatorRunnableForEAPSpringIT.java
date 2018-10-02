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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.EAPSpringTemplateForFuse6;
import org.fusesource.ide.projecttemplates.impl.simple.EAPSpringTemplateForFuse7;
import org.fusesource.ide.projecttemplates.impl.simple.EAPSpringTemplateForFuse71;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForEAPSpringIT extends FuseIntegrationProjectCreatorRunnableIT {
	

	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return CamelCatalogUtils.getCamelVersionsToTestWith();
	}
	
	public FuseIntegrationProjectCreatorRunnableForEAPSpringIT(String version) {
		super();
		camelVersion = version;
	}
	
	@Test
	public void testEAPSpringProjectCreation() throws Exception {
        testProjectCreation("-EAPSpringProject-"+camelVersion, CamelDSLType.SPRING, "src/main/webapp/META-INF/jboss-camel-context.xml", null);
	}
	
	@Override
	protected NewFuseIntegrationProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, String projectName) {
		NewFuseIntegrationProjectMetaData newProjectMetadata = super.createDefaultNewProjectMetadata(dsl, projectName);
		if (isOlderThan220()) {
			newProjectMetadata.setTemplate(new EAPSpringTemplateForFuse6());
		} else if(isNewerThan221()){
			newProjectMetadata.setTemplate(new EAPSpringTemplateForFuse71());
		} else {
			newProjectMetadata.setTemplate(new EAPSpringTemplateForFuse7());
		}
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
        
        assertThat(camelFacetFound).describedAs("The camel facet has not been found.").isTrue();
        assertThat(javaFacetFound).as("The java facet has not been found.").isTrue();
        assertThat(mavenFacetFound).as("The maven facet has not been found.").isTrue();
        assertThat(utilityFacetFound).as("The utility facet has been found but shouldn't be installed.").isFalse();
        assertThat(webFacetFound).as("The web facet has not been found.").isTrue();
        
        checkWARMappingCorrect(project);
        
        checkNoConflictingFacets(fproj);
    }
    
    private void checkWARMappingCorrect(IProject project) throws CoreException {
    	IProgressMonitor monitor = new NullProgressMonitor();
    	final IVirtualComponent c = ComponentCore.createComponent(project, false);
		c.create(IVirtualResource.NONE, monitor);
		final IVirtualFolder webroot = c.getRootFolder();
		final IVirtualFolder classesFolder = webroot.getFolder("/WEB-INF/classes"); //$NON-NLS-1$
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
		checkMappingsForSourcePathCorrect(m2prj.getCompileSourceLocations(), classesFolder);
    }
    
    private void checkMappingsForSourcePathCorrect(IPath[] paths, IVirtualFolder vFolder) throws CoreException {
    	for (IPath sourceLoc : paths) {
			IFolder srcFolder = project.getFolder(sourceLoc);
			IPath absSourcePath = srcFolder.getProjectRelativePath().makeAbsolute();
			assertThat(srcFolder.exists()).as("The folder "+ srcFolder + " does not exist!").isTrue();
			IVirtualResource[] mappings = ComponentCore.createResources(srcFolder);
			
			boolean found = false;
			for (IVirtualResource mapping : mappings) {
				if (mapping.getProjectRelativePath().equals(absSourcePath)) {
					assertThat(mapping.getRuntimePath()).isEqualTo(vFolder.getRuntimePath());
					found = true;
				}
			}
			if(!found){
				fail("Not found mappings for "+absSourcePath + " . The existing mappings are: "
						+ Arrays.stream(mappings)
						.map(mapping -> "(Project relative Path: "+ mapping.getProjectRelativePath()+ " , runtime path: "+mapping.getRuntimePath()+")")
						.reduce("", String::concat));
			}
    	}
    }
    
    @Override
    protected void launchDebug(IProject project)throws InterruptedException, IOException, MalformedObjectNameException, DebugException {
    	// Local launch is not configured for EAP projects
    }
    
    @Override
	protected EnvironmentData createEnvironmentData() {
		return new EnvironmentData(camelVersion, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.WILDFLY);
	}
    
}
