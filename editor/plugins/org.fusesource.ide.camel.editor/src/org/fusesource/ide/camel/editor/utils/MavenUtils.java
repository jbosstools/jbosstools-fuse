/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;

/**
 * @author lhein
 */
public class MavenUtils {

	private static final String CAMEL_GROUP_ID = "org.apache.camel";
	private static final String CAMEL_CORE_ARTIFACT_ID = "camel-core";
	private static final String SCOPE_PROVIDED = "provided";

	private static final String MAIN_PATH = "src/main/"; //$NON-NLS-1$

	public static final String RESOURCES_PATH = MAIN_PATH + "resources/"; //$NON-NLS-1$

	private static final String JAVA_PATH = MAIN_PATH + "java/"; //$NON-NLS-1$
	
	/**
	 * @return the Java source folder for the project containing the Camel file
	 *         currently being edited
	 * @throws CoreException
	 *             if the project's POM file could not be read
	 */
	public String javaSourceFolder() throws CoreException {
		String name = readMavenModel(getPomFile(CamelUtils.project())).getBuild().getSourceDirectory();
		if (name == null)
			return JAVA_PATH;
		return name.endsWith("/") ? name : name + "/";
	}

	/**
	 * checks if we need to add a maven dependency for the chosen component and
	 * inserts it into the pom.xml if needed
	 *
	 * @param compDeps
	 *            the Maven dependencies to be updated
	 * @throws CoreException
	 * 
	 * @Deprecated Use {@link #updateMavenDependencies(`List<Dependency>, IProject)} instead to avoid relying upon external system configuration.
	 */
	@Deprecated
	public void updateMavenDependencies(final List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps) throws CoreException {
		final IProject project = CamelUtils.project();
		if (project == null) {
			CamelEditorUIActivator.pluginLog().logWarning(
					"Unable to add component dependencies because selected project can't be determined. Maybe this is a remote camel context.");
			return;
		}
		updateMavenDependencies(compDeps, project);
	}
	
	public void updateMavenDependencies(final List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps, IProject project) throws CoreException {
		if (compDeps == null || compDeps.isEmpty()) {
			CamelEditorUIActivator.pluginLog()
					.logWarning("Unable to add component dependencies because no dependencies were specified.");
			return;
		}
		// show progress dialog to user to signal ongoing changes to the pom
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(UIMessages.updatePomDependenciesProgressDialogLabel, 100);
					try {
						internalUpdateMavenDependencies(compDeps, project);
					} catch (CoreException ex) {
						CamelEditorUIActivator.pluginLog().logError(ex);
					}
					monitor.done();
				}
			});
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
	}

	/**
	 * @param compDeps
	 * @param project
	 * @throws CoreException
	 */
	protected void internalUpdateMavenDependencies(List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps, IProject project) throws CoreException {
		final File pomFile = getPomFile(project);

		final Model model = readMavenModel(pomFile);
		List<Dependency> deps = CamelMavenUtils.getDependencies(project, model);

		// then check if component dependency is already a dep
		List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> missingDeps = new ArrayList<>();
		String scope = null;
		for (org.fusesource.ide.camel.model.service.core.catalog.Dependency conDep : compDeps) {
			boolean found = false;
			for (Dependency pomDep : deps) {
				if (scope == null &&
						CAMEL_GROUP_ID.equalsIgnoreCase(pomDep.getGroupId()) &&
						CAMEL_CORE_ARTIFACT_ID.equalsIgnoreCase(pomDep.getArtifactId()) &&
						SCOPE_PROVIDED.equalsIgnoreCase(pomDep.getScope())) {
					scope = pomDep.getScope();
				}
				if (pomDep.getGroupId().equalsIgnoreCase(conDep.getGroupId())
						&& pomDep.getArtifactId().equalsIgnoreCase(conDep.getArtifactId())) {
					// check for correct version
					if (pomDep.getVersion() == null || !pomDep.getVersion().equalsIgnoreCase(conDep.getVersion())) {
						// not the correct version - change it to fit
						pomDep.setVersion(conDep.getVersion());
					}
					found = true;
					break;
				}
			}
			if (!found) {
				missingDeps.add(conDep);
			}
		}

		addDependency(model, missingDeps, scope);

		if (!missingDeps.isEmpty()) {
			writeNewPomFile(project, pomFile, model);
		}
	}

	/**
	 * @param pomFile
	 * @return the Maven model for the supplied POM file
	 * @throws CoreException
	 */
	Model readMavenModel(final File pomFile) throws CoreException {
		return MavenPlugin.getMaven().readModel(pomFile);
	}

	/**
	 * @param project
	 * @return the POM file for the supplied project
	 */
	File getPomFile(IProject project) {
		IPath pomPathValue = project.getProject().getRawLocation() != null
				? project.getProject().getRawLocation().append(IMavenConstants.POM_FILE_NAME)
				: ResourcesPlugin.getWorkspace().getRoot().getLocation()
						.append(project.getFullPath().append(IMavenConstants.POM_FILE_NAME));
		String pomPath = pomPathValue.toOSString();
		return new File(pomPath);
	}

	/**
	 * @param project
	 * @param pomFile
	 * @param model
	 */
	public void writeNewPomFile(IProject project, final File pomFile, final Model model) {
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile))) {
			MavenPlugin.getMaven().writeModel(model, os);
			IFile pomIFile2 = project.getProject().getFile(IMavenConstants.POM_FILE_NAME);
			if (pomIFile2 != null) {
				pomIFile2.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
	}

	/**
	 * @param model
	 * @param missingDeps
	 * @param scope
	 */
	void addDependency(final Model model,
			List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> missingDeps, String scope) {
		for (org.fusesource.ide.camel.model.service.core.catalog.Dependency missDep : missingDeps) {
			Dependency dep = new Dependency();
			dep.setGroupId(missDep.getGroupId());
			dep.setArtifactId(missDep.getArtifactId());
			dep.setVersion(missDep.getVersion());
			if (scope != null) {
				dep.setScope(scope);
			}
			model.addDependency(dep);
		}
	}

	/**
	 * adds a resource folder to the maven pom file if not yet there
	 *
	 * @param project
	 *            the eclipse project
	 * @param pomFile
	 *            the pom.xml file
	 * @param resourceFolderName
	 *            the name of the new resource folder
	 * @throws CoreException
	 *             on any errors
	 */
	public void addResourceFolder(IProject project, File pomFile, String resourceFolderName) throws CoreException {
		final Model model = readMavenModel(pomFile);
		List<Resource> resources = model.getBuild().getResources();

		boolean exists = false;
		for (Resource resource : resources) {
			if (resource.getDirectory().equals(resourceFolderName)) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			Resource resource = new Resource();
			resource.setDirectory(resourceFolderName);
			model.getBuild().addResource(resource);

			try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(model, os);
				IFile pomIFile = project.getFile("pom.xml");
				if (pomIFile != null) {
					pomIFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
			} catch (Exception ex) {
				CamelEditorUIActivator.pluginLog().logError(ex);
			}
		}
	}
}
