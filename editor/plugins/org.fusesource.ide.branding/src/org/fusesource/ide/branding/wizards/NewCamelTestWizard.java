/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     James Strachan <jstracha@redhat.com> - Camel specific updates
 *******************************************************************************/
package org.fusesource.ide.branding.wizards;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.junit.wizards.JUnitWizard;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.contenttype.BlueprintXmlMatchingStrategy;
import org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport;

/**
 * A wizard for creating test cases.
 */
@SuppressWarnings("restriction")
public class NewCamelTestWizard extends JUnitWizard {

	private static final String CAMEL_GROUP_ID = "org.apache.camel";
	private static final String CAMEL_ARTIFACT_ID_WILDCARD = "camel-";
	private static final String CAMEL_SPRING_TEST_ARTIFACT_ID = "camel-test-spring";
	private static final String CAMEL_BP_TEST_ARTIFACT_ID = "camel-test-blueprint";
	private static final String CAMEL_TEST_SCOPE = "test";

	private String camelVersion = null;
	private XmlMatchingStrategySupport blueprintXmlMatcher = new BlueprintXmlMatchingStrategy();
	
	private NewCamelTestWizardPageOne fPage1;

	public NewCamelTestWizard() {
		super();
		setWindowTitle(WizardMessages.Wizard_title_new_testcase);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/new_camel_test_case_wizard.png"));
		initDialogSettings();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		NewCamelTestWizardPageTwo fPage2 = new NewCamelTestWizardPageTwo();
		fPage1 = new NewCamelTestWizardPageOne(this, fPage2);
		addPage(fPage1);
		addPage(fPage2);
		fPage1.init(getSelection());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.internal.junit.wizards.JUnitWizard#initializeDefaultPageImageDescriptor()
	 */
	@Override
	protected void initializeDefaultPageImageDescriptor() {
		setDefaultPageImageDescriptor(JUnitPlugin.getImageDescriptor("wizban/newtest_wiz.png")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.internal.junit.wizards.JUnitWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
				SubMonitor subMonitor = SubMonitor.convert(monitor, WizardMessages.NewTestCaseCreationWizard_create_progress, 2);
				
				fPage1.superClassChanged();
				subMonitor.setWorkRemaining(1);
				try {
					fPage1.createType(subMonitor.split(1));
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				}
			}
		};
		// this will create the test class
		finishPage(runnable);
		
		IJavaProject project = fPage1.getJavaProject();
		runnable = fPage1.getRunnable();
		try {
			runnable = addCamelTestToPomDeps(project, runnable);
			//			runnable = addJUnitToClasspath(project, runnable, fPage1.isJUnit4());
		} catch (Exception e) {
			return false;
		}

		if (finishPage(runnable)) {
			IType newClass = fPage1.getCreatedType();
			if (newClass == null) {
				return false;
			}
			IResource resource = newClass.getCompilationUnit().getResource();
			if (resource != null) {
				selectAndReveal(resource);
				openResource(resource);
			}
			return true;
		}
		return false;
	}

	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public boolean isBlueprintFile(String filePath) {
		boolean isBlueprint = false;
		
		if (filePath != null && filePath.trim().length()>0) {
			String rawPath;
			if (filePath.startsWith("file:")) {
				rawPath = filePath.substring(5);
			} else {
				rawPath = filePath;
			}
			Path f = new Path(rawPath);
			java.io.File nf = new java.io.File(f.toOSString());
			if (nf.exists() && nf.isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(f.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
				isBlueprint = blueprintXmlMatcher.matches(file);
			}
		}
		
		return isBlueprint;
	}
	
	private IRunnableWithProgress addCamelTestToPomDeps(final IJavaProject project, final IRunnableWithProgress runnable) {
		return new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				SubMonitor subMonitor = SubMonitor.convert(monitor, WizardMessages.NewTestCaseCreationWizard_create_progress, 4);

				// first load the pom file into some model
				IPath pomPathValue = project.getProject().getRawLocation() != null ? project.getProject().getRawLocation().append(IMavenConstants.POM_FILE_NAME) : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getPath().append(IMavenConstants.POM_FILE_NAME));
				String pomPath = pomPathValue.toOSString();
				final File pomFile = new File(pomPath);
				try {
					final Model model = new CamelMavenUtils().getMavenModel(project.getProject());
	
					// then check if camel-test is already a dep
					boolean isBlueprint = isBlueprintFile(fPage1.getXmlFileUnderTest().getLocationURI().toString());
					boolean hasCamelSpringTestDep = false;
					boolean hasCamelBPTestDep = false;
					List<Dependency> deps = model.getDependencies();
					for (Dependency dep : deps) {
						if (dep.getArtifactId().startsWith(CAMEL_ARTIFACT_ID_WILDCARD) && 
							dep.getGroupId().equalsIgnoreCase(CAMEL_GROUP_ID) &&
							camelVersion == null) {
							camelVersion = dep.getVersion();
						}
						if (!isBlueprint && dep.getArtifactId().equalsIgnoreCase(CAMEL_SPRING_TEST_ARTIFACT_ID)) {
							hasCamelSpringTestDep = true;
							break;
						}
						if (isBlueprint && dep.getArtifactId().equalsIgnoreCase(CAMEL_BP_TEST_ARTIFACT_ID)) {
							hasCamelBPTestDep = true;
							break;
						}
					}
					
					boolean changes = false;
					
					if (!isBlueprint && !hasCamelSpringTestDep) {
						Dependency dep = new Dependency();
						dep.setGroupId(CAMEL_GROUP_ID);
						dep.setArtifactId(CAMEL_SPRING_TEST_ARTIFACT_ID);
						dep.setVersion(camelVersion);
						dep.setScope(CAMEL_TEST_SCOPE);
						model.addDependency(dep);
						changes = true;
					}
					
					if (isBlueprint && !hasCamelBPTestDep) {
						Dependency dep = new Dependency();
						dep.setGroupId(CAMEL_GROUP_ID);
						dep.setArtifactId(CAMEL_BP_TEST_ARTIFACT_ID);
						dep.setVersion(camelVersion);
						dep.setScope(CAMEL_TEST_SCOPE);
						model.addDependency(dep);
						changes = true;
					}
					subMonitor.setWorkRemaining(3);
					
					if (changes) {
						try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile))) {
							MavenPlugin.getMaven().writeModel(model, os);
							IFile pomIFile = project.getProject().getFile(IMavenConstants.POM_FILE_NAME);
							if (pomIFile != null){
								pomIFile.refreshLocal(IResource.DEPTH_INFINITE, subMonitor.split(1));
							}
							subMonitor.setWorkRemaining(2);
							runnable.run(subMonitor.split(2));
						} catch (Exception ex) {
							Activator.getLogger().error(ex);
						}
					}
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				} finally {
					subMonitor.setWorkRemaining(0);
				}
			}
		};
	}
}
