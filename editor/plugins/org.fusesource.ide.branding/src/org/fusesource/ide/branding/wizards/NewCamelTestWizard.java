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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.junit.wizards.JUnitWizard;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor.ClasspathFixProposal;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
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
	
	private static class ClasspathFixSelectionDialog extends MessageDialog implements SelectionListener,
	IDoubleClickListener {

		static class ClasspathFixLabelProvider extends LabelProvider {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
			 */
			@Override
			public Image getImage(Object element) {
				if (element instanceof ClasspathFixProposal) {
					ClasspathFixProposal classpathFixProposal = (ClasspathFixProposal) element;
					return classpathFixProposal.getImage();
				}
				return null;
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof ClasspathFixProposal) {
					ClasspathFixProposal classpathFixProposal = (ClasspathFixProposal) element;
					return classpathFixProposal.getDisplayString();
				}
				return null;
			}
		}

		private static final String BUILD_PATH_BLOCK = "block_until_buildpath_applied"; //$NON-NLS-1$
		private static final String BUILD_PATH_PAGE_ID = "org.eclipse.jdt.ui.propertyPages.BuildPathsPropertyPage"; //$NON-NLS-1$

		private static String getDialogMessage(boolean isJunit4) {
			return isJunit4 ? WizardMessages.NewTestCaseCreationWizard_fix_selection_junit4_description
					: WizardMessages.NewTestCaseCreationWizard_fix_selection_junit3_description;
		}

		private final ClasspathFixProposal[] fFixProposals;
		private TableViewer fFixSelectionTable;
		private Button fNoActionRadio;

		private Button fOpenBuildPathRadio;

		private Button fPerformFix;

		private final IJavaProject fProject;

		private ClasspathFixProposal fSelectedFix;

		public ClasspathFixSelectionDialog(Shell parent, boolean isJUnit4, IJavaProject project,
				ClasspathFixProposal[] fixProposals) {
			super(parent, WizardMessages.Wizard_title_new_testcase, null, getDialogMessage(isJUnit4),
					MessageDialog.QUESTION, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL },
					0);
			fProject = project;
			fFixProposals = fixProposals;
			fSelectedFix = null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.MessageDialog#buttonPressed(int)
		 */
		@Override
		protected void buttonPressed(int buttonId) {
			fSelectedFix = null;
			if (buttonId == 0) {
				if (fNoActionRadio.getSelection()) {
					// nothing to do
				} else if (fOpenBuildPathRadio.getSelection()) {
					String id = BUILD_PATH_PAGE_ID;
					Map<String, Boolean> input = new HashMap<String, Boolean>();
					input.put(BUILD_PATH_BLOCK, Boolean.TRUE);
					if (PreferencesUtil.createPropertyDialogOn(getShell(), fProject, id, new String[] { id }, input)
							.open() != Window.OK) {
						return;
					}
				} else if (fFixSelectionTable != null) {
					IStructuredSelection selection = (IStructuredSelection) fFixSelectionTable.getSelection();
					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof ClasspathFixProposal) {
						fSelectedFix = (ClasspathFixProposal) firstElement;
					}
				}
			}
			super.buttonPressed(buttonId);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createCustomArea(Composite composite) {
			fNoActionRadio = new Button(composite, SWT.RADIO);
			fNoActionRadio.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false));
			fNoActionRadio.setText(WizardMessages.NewTestCaseCreationWizard_fix_selection_not_now);
			fNoActionRadio.addSelectionListener(this);

			fOpenBuildPathRadio = new Button(composite, SWT.RADIO);
			fOpenBuildPathRadio.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false));
			fOpenBuildPathRadio.setText(WizardMessages.NewTestCaseCreationWizard_fix_selection_open_build_path_dialog);
			fOpenBuildPathRadio.addSelectionListener(this);

			if (fFixProposals.length > 0) {

				fPerformFix = new Button(composite, SWT.RADIO);
				fPerformFix.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false));
				fPerformFix.setText(WizardMessages.NewTestCaseCreationWizard_fix_selection_invoke_fix);
				fPerformFix.addSelectionListener(this);

				fFixSelectionTable = new TableViewer(composite, SWT.SINGLE | SWT.BORDER);
				fFixSelectionTable.setContentProvider(new ArrayContentProvider());
				fFixSelectionTable.setLabelProvider(new ClasspathFixLabelProvider());
				fFixSelectionTable.setComparator(new ViewerComparator());
				fFixSelectionTable.addDoubleClickListener(this);
				fFixSelectionTable.setInput(fFixProposals);
				fFixSelectionTable.setSelection(new StructuredSelection(fFixProposals[0]));

				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
				gridData.heightHint = convertHeightInCharsToPixels(4);
				gridData.horizontalIndent = convertWidthInCharsToPixels(2);

				fFixSelectionTable.getControl().setLayoutData(gridData);

				fNoActionRadio.setSelection(false);
				fOpenBuildPathRadio.setSelection(false);
				fPerformFix.setSelection(true);

			} else {
				fNoActionRadio.setSelection(true);
				fOpenBuildPathRadio.setSelection(false);
			}

			updateEnableStates();

			return composite;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
		 */
		@Override
		public void doubleClick(DoubleClickEvent event) {
			okPressed();

		}

		public ClasspathFixProposal getSelectedClasspathFix() {
			return fSelectedFix;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
		 */
		@Override
		protected boolean isResizable() {
			return true;
		}

		private void updateEnableStates() {
			if (fPerformFix != null) {
				fFixSelectionTable.getTable().setEnabled(fPerformFix.getSelection());
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			updateEnableStates();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateEnableStates();
		}
	}

	private NewCamelTestWizardPageOne fPage1;

	private NewCamelTestWizardPageTwo fPage2;

	public NewCamelTestWizard() {
		super();
		setWindowTitle(WizardMessages.Wizard_title_new_testcase);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/new_camel_test_case_wizard.png"));
		initDialogSettings();
	}

	private IRunnableWithProgress addJUnitToClasspath(IJavaProject project, final IRunnableWithProgress runnable,
			boolean isJUnit4) {
		String typeToLookup = isJUnit4 ? "org.junit.*" : "junit.awtui.*"; //$NON-NLS-1$//$NON-NLS-2$
		ClasspathFixProposal[] fixProposals = ClasspathFixProcessor.getContributedFixImportProposals(project,
				typeToLookup, null);
		String superClass = "org.apache.camel.test.junit4.*";
		// String superClass = fPage1.getSuperClass();
		ClasspathFixProposal[] fixProposals2 = ClasspathFixProcessor.getContributedFixImportProposals(project,
				superClass, null);

		List<ClasspathFixProposal> proposals = new ArrayList<ClasspathFixProposal>();
		proposals.addAll(Arrays.asList(fixProposals));
		proposals.addAll(Arrays.asList(fixProposals2));
		fixProposals = proposals.toArray(new ClasspathFixProposal[proposals.size()]);

		ClasspathFixSelectionDialog dialog = new ClasspathFixSelectionDialog(getShell(), isJUnit4, project,
				fixProposals);
		if (dialog.open() != 0) {
			throw new OperationCanceledException();
		}

		final ClasspathFixProposal fix = dialog.getSelectedClasspathFix();
		if (fix != null) {
			return new IRunnableWithProgress() {

				/*
				 * (non-Javadoc)
				 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					monitor.beginTask(WizardMessages.NewTestCaseCreationWizard_create_progress, 4);
					try {
						Change change = fix.createChange(new SubProgressMonitor(monitor, 1));
						new PerformChangeOperation(change).run(new SubProgressMonitor(monitor, 1));

						runnable.run(new SubProgressMonitor(monitor, 2));
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};
		}
		return runnable;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		fPage2 = new NewCamelTestWizardPageTwo();
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
				if (monitor == null) {
					monitor = new NullProgressMonitor();
				}
				monitor.beginTask(WizardMessages.NewTestCaseCreationWizard_create_progress, 2);
				
				fPage1.superClassChanged();
				monitor.worked(1);
				try {
					fPage1.createType(monitor);
					monitor.worked(1);
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				} finally {
					monitor.done();
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
	
	private IRunnableWithProgress addCamelTestToPomDeps(final IJavaProject project, final IRunnableWithProgress runnable) throws Exception {
		return new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (monitor == null) {
					monitor = new NullProgressMonitor();
				}
				monitor.beginTask(WizardMessages.NewTestCaseCreationWizard_create_progress, 4);

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
					
					if (changes) {
						try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile))) {
							MavenPlugin.getMaven().writeModel(model, os);
							IFile pomIFile = project.getProject().getFile(IMavenConstants.POM_FILE_NAME);
							if (pomIFile != null){
								pomIFile.refreshLocal(IResource.DEPTH_INFINITE, monitor);
							}
							runnable.run(new SubProgressMonitor(monitor, 2));
						} catch (Exception ex) {
							Activator.getLogger().error(ex);
						}
					}
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				} finally {
					monitor.done();
				}
			}
		};
	}
}
