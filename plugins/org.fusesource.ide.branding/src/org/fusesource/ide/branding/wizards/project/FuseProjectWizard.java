/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.branding.wizards.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.INewWizard;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.branding.wizards.WizardMessages;
import org.fusesource.ide.maven.MavenFacade;

/**
 * Simple project wizard for creating a new Fuse Project
 * <p>
 * The wizard provides the following functionality to the user:
 * <ul>
 * <li>Create the project in the workspace or at some external location.</li>
 * <li>Provide information about the Maven2 artifact to create.</li>
 * <li>Choose directories of the default Maven2 directory structure to create.</li>
 * </ul>
 * </p>
 * <p>
 * Once the wizard has finished, the following resources are created and
 * configured:
 * <ul>
 * <li>A POM file containing the given artifact information and the chosen
 * dependencies.</li>
 * <li>The chosen Maven2 directories.</li>
 * <li>The .classpath file is configured to hold appropriate entries for the
 * Maven2 directories created as well as the Java and Maven2 classpath
 * containers.</li>
 * </ul>
 * </p>
 */
public class FuseProjectWizard extends AbstractFuseProjectWizard implements
		INewWizard {

	/** The wizard page for gathering general project information. */
	protected FuseProjectWizardLocationPage locationPage;
	private FuseProjectWizardArchetypePage archetypePage;

	/**
	 * Default constructor. Sets the title and image of the wizard.
	 */
	public FuseProjectWizard() {
		super();
		setWindowTitle(WizardMessages.wizardProjectTitle);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(
				this.getClass(), "/icons/new_fuse_project_wizard.png"));
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		locationPage = new FuseProjectWizardLocationPage(
				WizardMessages.wizardProjectPageProjectTitle,
				WizardMessages.wizardProjectPageProjectDescription, workingSets) { //

			protected void createAdditionalControls(Composite container) {
				/*
				 * simpleProject = new Button(container, SWT.CHECK);
				 * simpleProject
				 * .setText(WizardMessages.wizardProjectPageProjectSimpleProject
				 * ); simpleProject.setLayoutData(new GridData(SWT.FILL,
				 * SWT.TOP, false, false, 3, 1));
				 * simpleProject.addSelectionListener(new SelectionAdapter() {
				 * public void widgetSelected(SelectionEvent e) { validate(); }
				 * });
				 */
				Label label = new Label(container, SWT.NONE);
				GridData labelData = new GridData(SWT.FILL, SWT.TOP, false,
						false, 3, 1);
				labelData.heightHint = 10;
				label.setLayoutData(labelData);
			}

			/**
			 * Skips the archetype selection page if the user chooses a simple
			 * project.
			 */
			public IWizardPage getNextPage() {
				return getPage("MavenProjectWizardArchetypePage"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		};
		locationPage.setLocationPath(SelectionUtil
				.getSelectedLocation(selection));

		archetypePage = new FuseProjectWizardArchetypePage();
		// TODO
		/*
		 * parametersPage = new
		 * MavenProjectWizardArchetypeParametersPage(importConfiguration);
		 * artifactPage = new
		 * MavenProjectWizardArtifactPage(importConfiguration);
		 */

		addPage(locationPage);
		addPage(archetypePage);
		/*
		 * addPage(parametersPage); addPage(artifactPage);
		 */
	}

	/** Adds the listeners after the page controls are created. */
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);

		/*
		 * archetypePage.addArchetypeSelectionListener(new
		 * ISelectionChangedListener() { public void
		 * selectionChanged(SelectionChangedEvent selectionchangedevent) {
		 * parametersPage.setArchetype(archetypePage.getArchetype());
		 * getContainer().updateButtons(); } });
		 */

		// locationPage.addProjectNameListener(new ModifyListener() {
		// public void modifyText(ModifyEvent e) {
		// parametersPage.setProjectName(locationPage.getProjectName());
		// artifactPage.setProjectName(locationPage.getProjectName());
		// }
		// });
	}

	/** Returns the model. */
	/*
	 * public Model getModel() { if(simpleProject.getSelection()) { return
	 * artifactPage.getModel(); } return parametersPage.getModel(); }
	 */

	/**
	 * To perform the actual project creation, an operation is created and run
	 * using this wizard as execution context. That way, messages about the
	 * progress of the project creation are displayed inside the wizard.
	 */
	public boolean performFinish() {
		// First of all, we extract all the information from the wizard pages.
		// Note that this should not be done inside the operation we will run
		// since many of the wizard pages' methods can only be invoked from
		// within
		// the SWT event dispatcher thread. However, the operation spawns a new
		// separate thread to perform the actual work, i.e. accessing SWT
		// elements
		// from within that thread would lead to an exception.

		// Get the location where to create the project. For some reason, when
		// using
		// the default workspace location for a project, we have to pass null
		// instead of the actual location.

		/*
		 * final Model model = getModel();
		 */
		final String projectName = getProjectName();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = locationPage.isInWorkspace() ? null : locationPage.getLocationPath();
		final IWorkspaceRoot root = workspace.getRoot();
		final IPath rootPath = locationPage.isInWorkspace() ? root.getLocation().append(projectName) : location;
		final File pomFile = rootPath.append("pom.xml").toFile();
		boolean pomExists = pomFile.exists();

		if (pomExists) {
			MessageDialog.openError(getShell(), NLS.bind(
					WizardMessages.wizardProjectJobFailed, projectName),
					WizardMessages.wizardProjectErrorPomAlreadyExists);
			return false;
		}

		final String groupId = archetypePage.getGroupId();
		final String artifactId = archetypePage.getArtifactId();
		final String version = archetypePage.getVersion();

		final ArchetypeDetails archetype = archetypePage.getArchetype();

		final String javaPackage = archetypePage.getJavaPackage();
		final Properties properties = archetypePage.getProperties();

		Activator.getLogger().debug(
				"About to create project: " + projectName + " from archetype: "
						+ archetype + " for " + groupId + ": " + artifactId
						+ ":" + version + " at " + rootPath);

		String jobName = NLS.bind(WizardMessages.wizardProjectJobCreating, projectName);
		final Job job = new AbstractCreateProjectJob(jobName, workingSets) {
			@Override
			protected List<IProject> doCreateMavenProjects(
					IProgressMonitor monitor) throws CoreException {

				try {
					URL resource = archetype.getResource();
					InputStream in = resource.openStream();
					File outputDir = rootPath.toFile();
					/*
					 * IFile projectDir = project.getFile("/"); File outputDir =
					 * IFiles.toFile(projectDir);
					 */

					createProject(in, outputDir, groupId, artifactId, version,
							javaPackage);

					final IProject project = root.getProject(projectName);

					MavenFacade facade = new MavenFacade();
					facade.importProjects(monitor, pomFile, projectName,
							groupId, artifactId, version);

					return Arrays.asList(project);
				} catch (IOException e) {
					Status status = new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, 0, NLS.bind(
									WizardMessages.failedToOpenArchetype,
									e.getMessage()), e); //$NON-NLS-1$
					throw new CoreException(status);
				}
			}
		};

		job.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				final IStatus result = event.getResult();
				if (!result.isOK()) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(
									getShell(), //
									NLS.bind(
											WizardMessages.wizardProjectJobFailed,
											projectName), result.getMessage());
						}
					});
				} else {
					final IProject project = root.getProject(projectName);
					try {
						enforceNatures(project, new NullProgressMonitor());
					} catch (CoreException ex) {
						Activator.getLogger().error(ex);
					}
				}
			}
		});

		ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRuleFactory()
				.buildRule();
		job.setRule(rule);
		job.schedule();

		return true;
	}

	/**
	 * adds the Camel nature to the project and also a maybe missing java nature
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	private void enforceNatures(final IProject project, final IProgressMonitor monitor)
			throws CoreException {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					IProjectDescription projectDescription = project.getDescription();
					String[] ids = projectDescription.getNatureIds();
					boolean camelNatureFound = false;
					boolean javaNatureFound = false;
					for (String id : ids) {
						if (id.equals(JavaCore.NATURE_ID)) {
							javaNatureFound = true;
						} else if (id.equals(Activator.CAMEL_NATURE_ID)) {
							camelNatureFound = true;
						}
					}
					int toAdd = 0;
					if (!camelNatureFound) {
						toAdd++;
					}
					if (!javaNatureFound) {
						toAdd++;
					}
					String[] newIds = new String[ids.length + toAdd];
					System.arraycopy(ids, 0, newIds, 0, ids.length);
					if (!camelNatureFound && !javaNatureFound) {
						newIds[ids.length] = Activator.CAMEL_NATURE_ID;
						newIds[newIds.length - 1] = JavaCore.NATURE_ID;
					} else if (!camelNatureFound) {
						newIds[ids.length] = Activator.CAMEL_NATURE_ID;
					} else if (!javaNatureFound) {
						newIds[ids.length] = JavaCore.NATURE_ID;
					}
					projectDescription.setNatureIds(newIds);
					project.setDescription(projectDescription, monitor);
					
					IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();
				    IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
					MavenUpdateRequest request = new MavenUpdateRequest(false, true);
					request.addPomFile(project.getFile("pom.xml"));
					configurationManager.updateProjectConfiguration(request, monitor);
				} catch (CoreException ex) {
					Activator.getLogger().error(ex);
				}
			}
		});
	}
	
	public String getProjectName() {
		return locationPage != null ? locationPage.getProjectName() : null;
	}
}
