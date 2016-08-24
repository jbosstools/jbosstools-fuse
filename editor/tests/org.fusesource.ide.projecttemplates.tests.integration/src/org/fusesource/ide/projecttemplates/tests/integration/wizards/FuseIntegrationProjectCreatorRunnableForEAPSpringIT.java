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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.EAPSpringTemplate;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.junit.Test;
import org.junit.Ignore;
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
	@Ignore("EAP test is nt working yet")
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
    }
}
