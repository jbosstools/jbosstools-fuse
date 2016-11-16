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
package org.fusesource.ide.project.camel.integration.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class CamelProjectWizardIT {

	private String projectName = this.getClass().getName() + "-testProjectCreation";
	boolean deploymentFinished = false;
	boolean isDeploymentOk = false;

	@Before
	public void setup() throws CoreException {
		clean();
	}

	@After
	public void tearDown() throws CoreException {
		clean();
	}

	private void clean() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project.exists()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	/**
	 * commented out for now because that wizard is going to disappear and
	 * the test is therefore obsolete (at least here)
	 * 
	 * @throws InterruptedException
	 */
	@Test
	@Ignore("CamelProjectWizard is no more used. New test need to be written")
	public void testCreateDefaultBlueprintCamelProject() throws InterruptedException {
//		CamelProjectWizard wizard = createAndInitializeWizard();

//		assertThat(wizard.performFinish()).isTrue();

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		assertThat(project.exists()).isTrue();
		assertThat(project.getFile("pom.xml").exists()).isTrue();
		assertThat(project.getFile(new Path("camelcontent/OSGI-INF/blueprint/camel-context.xml")).exists()).isTrue();
		// TODO: check that the project has the right Camel/Fuse nature/facet
		// assertThat(project.hasNature(RiderProjectNature.NATURE_ID)).isTrue();
		// TODO: check that there is no compilation error
		// TODO: check that there is no validation error
		// TODO: check that we can run as camel route without error
		// launchDebug(project); Currently this provided code shows that it
		// doesn't work. Uncomment when the implementation will be done.
	}

	/**
	 * @param project
	 * @throws InterruptedException
	 */
	private void launchDebug(IProject project) throws InterruptedException {
		final ExecutePomAction executePomAction = new ExecutePomAction();


		executePomAction.setPostProcessor(new ExecutePomActionPostProcessor() {

			@Override
			public void executeOnSuccess() {
				// TODO: shutdown
				deploymentFinished = true;
				isDeploymentOk = true;
				// fail("deployment success");
			}

			@Override
			public void executeOnFailure() {
				// TODO fail the test
				deploymentFinished = true;
				isDeploymentOk = false;
				// fail("Deployment failed");
			}
		});
		executePomAction.launch(new StructuredSelection(project), ILaunchManager.DEBUG_MODE);
		while (!deploymentFinished) {
			Thread.sleep(100);
		}
		assertThat(isDeploymentOk).isTrue();
	}

//	private CamelProjectWizard createAndInitializeWizard() {
//		CamelProjectWizard wizard = new CamelProjectWizard();
//		final WizardDialog wizardContainer = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
//		wizardContainer.create();
//		wizard.setContainer(wizardContainer);
//		wizard.init(PlatformUI.getWorkbench(), null);
//		wizard.addPages();
//		wizard.createPageControls(new Composite(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE));
//		final IDataModel model = wizard.getDataModel();
//		model.setStringProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME, projectName);
//		return wizard;
//	}
}
