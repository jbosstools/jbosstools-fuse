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
package org.fusesource.ide.wsdl2rest.ui.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestOptions;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestWizard;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test to ensure we can run the wsdl2rest wizard.
 * 
 * @author brianf
 */
public class Wsdl2RestWizardIT {

	static final String WSDL_LOCATION = "src/test/resources/wsdl/Address.wsdl"; //$NON-NLS-1$
	static final String OUTPUT_PATH = "src/main/java"; //$NON-NLS-1$
	static final String SPRING_CAMEL_PATH = "/src/main/resources/META-INF/spring"; //$NON-NLS-1$
	static final String BLUEPRINT_CAMEL_PATH = "/src/main/resources/OSGI-INF/blueprint"; //$NON-NLS-1$

	@Rule
	public FuseProject fuseProject = new FuseProject(Wsdl2RestWizardIT.class.getName());

	@Rule
	public FuseProject fuseProject2 = new FuseProject(Wsdl2RestWizardIT.class.getName() + '2');

	@Rule
	public FuseProject fuseProject3 = new FuseProject(Wsdl2RestWizardIT.class.getName() + '3', true);
	
	@Rule
	public FuseProject fuseProject4 = new FuseProject(Wsdl2RestWizardIT.class.getName() + '4', true);

	@Rule
	public FuseProject fuseProject5 = new FuseProject(Wsdl2RestWizardIT.class.getName() + '5', true);

	private WizardDialog wizardDialog;

	@Before
	public void setup() throws CoreException, IOException {
		fuseProject.createEmptyCamelFile();
		fuseProject2.createEmptyCamelFile();
		fuseProject3.createEmptyBlueprintCamelFile();
		fuseProject4.createEmptyBlueprintCamelFile();
		fuseProject5.createEmptyCamelFile();
		waitJob();
	}
	
	@After
	public void tearDown() {
		if (wizardDialog != null) {
			wizardDialog.close();
		}
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

	private void processContainer(IContainer container, String extension, List<IFile> fileList) throws CoreException {
		IResource [] members = container.members();
		for (IResource member : members) {
			if (member instanceof IContainer) {
				processContainer((IContainer)member, extension, fileList);
			} else if (member instanceof IFile && extension.equalsIgnoreCase(member.getFileExtension())) {
				fileList.add((IFile) member);
			}
		}
	} 

	private void runWsdl2RestWizard(String camelPath, FuseProject project, String camelFileName) throws MalformedURLException, CoreException {
		File wsdlFile = new File(WSDL_LOCATION);
		Path outpath = new File(OUTPUT_PATH).toPath();
		Wsdl2RestOptions options = new Wsdl2RestOptions();
		options.setWsdlURL(wsdlFile.toURI().toURL().toExternalForm());
		options.setProjectName(project.getProject().getName());
		options.setDestinationJava(outpath.toString());
		options.setTargetRestServiceAddress(new URL("http://localhost:8083/myjaxrs").toExternalForm()); //$NON-NLS-1$
		options.setTargetServiceAddress(new URL("http://localhost:8080/doclit").toExternalForm()); //$NON-NLS-1$
		
		Wsdl2RestWizard wizard = new Wsdl2RestWizard(options);
		wizard.setInTest(true);
		wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		wizardDialog.setBlockOnOpen(false);
		wizardDialog.open();
		/* Update the destination Camel only here otherwise it is overridden when UI is initializing with the project selected
		 * As it is used only in test, applying just a workaround.
		 * */
		options.setDestinationCamel(camelPath);
		
		assertThat(wizard.performFinish()).isTrue();

		IProject pr = project.getProject();
		pr.refreshLocal(IResource.DEPTH_INFINITE, null);

		List<IFile> xmlFiles = new ArrayList<>();
		processContainer(pr, "xml", xmlFiles);
		IResource camelFile = findFileWithNameInList(camelFileName, xmlFiles);
		Assert.assertTrue("Generated Camel file not found", camelFile != null && camelFile.exists());

		List<IFile> javaFiles = new ArrayList<>();
		processContainer(pr, "java", javaFiles);
		IResource addAddressJavaFile = findFileWithNameInList("AddAddress.java", javaFiles); //$NON-NLS-1$
		Assert.assertTrue("Generated AddAddress class not found",  //$NON-NLS-1$
				addAddressJavaFile != null && addAddressJavaFile.exists());
	}

	@Test
	public void testWsdl2RestWizardWithNoProjectInCamelPath() throws MalformedURLException, CoreException {
		runWsdl2RestWizard(SPRING_CAMEL_PATH, fuseProject, Wsdl2RestWizard.DEFAULT_CONFIG_NAME);
	}

	@Test
	public void testWsdl2RestWizardWithProjectInCamelPath() throws MalformedURLException, CoreException {
		String camelPath = '/' + fuseProject2.getProject().getName() + '/' + SPRING_CAMEL_PATH;
		runWsdl2RestWizard(camelPath, fuseProject2, Wsdl2RestWizard.DEFAULT_CONFIG_NAME);

		// make sure that the jetty dependency was added as part of the wizard
		// TODO: This will change when we update which Rest Configuration component we're using for generated Spring Rest DSL
		Assert.assertTrue(hasDependency(fuseProject2.getProject(), "camel-jetty"));
	}
	
	private static boolean hasDependency(IProject project, String artifactIdToCheck) {
		CamelMavenUtils cmu = new CamelMavenUtils();
		List<Dependency> projectDependencies = cmu.getDependencyList(project);
		return projectDependencies != null
				&& projectDependencies.stream()
					.anyMatch(dependency -> artifactIdToCheck.equals(dependency.getArtifactId()));
	}

	@Test
	public void testWsdl2RestWizardWithBlueprintNoProjectInCamelPath() throws MalformedURLException, CoreException {
		runWsdl2RestWizard(BLUEPRINT_CAMEL_PATH, fuseProject3, Wsdl2RestWizard.DEFAULT_BLUEPRINT_CONFIG_NAME);
		
		// make sure that the servlet dependency was added as part of the wizard
		Assert.assertTrue(hasDependency(fuseProject3.getProject(), "camel-servlet"));
	}
	
	@Test
	public void testWsdl2RestWizardWithBlueprintProjectInCamelPath() throws MalformedURLException, CoreException {
		String camelPath = '/' + fuseProject4.getProject().getName() + '/' + BLUEPRINT_CAMEL_PATH;
		runWsdl2RestWizard(camelPath, fuseProject4, Wsdl2RestWizard.DEFAULT_BLUEPRINT_CONFIG_NAME);
	}

	@Test
	public void testWsdl2RestWizardWithCustomFileInCamelPath() throws MalformedURLException, CoreException {
		String camelPath = SPRING_CAMEL_PATH + '/' + "customconfig.xml";
		runWsdl2RestWizard(camelPath, fuseProject5, "customconfig.xml");
	}

	private IFile findFileWithNameInList(String name, List<IFile> files) {
		Iterator<IFile> fileIter = files.iterator();
		while (fileIter.hasNext()) {
			IFile tempFile = fileIter.next();
			if (tempFile.getName().equals(name)) {
				return tempFile;
			}
		}
		return null;
	}
	
}
