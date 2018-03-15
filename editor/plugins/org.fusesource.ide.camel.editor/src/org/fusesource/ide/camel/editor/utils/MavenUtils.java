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
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.ui.util.Shells;

/**
 * @author lhein
 */
public class MavenUtils {

	private static final String SYNDESIS_PLUGIN_GROUPID = "io.syndesis";
	private static final String SYNDESIS_PLUGIN_ARTIFACTID = "syndesis-maven-plugin";

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
	public String javaSourceFolder() {
		String name = new CamelMavenUtils().getMavenModel(CamelUtils.project()).getBuild().getSourceDirectory();
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
	 * @deprecated Use {@link #updateMavenDependencies(`List<Dependency>, IProject)} instead to avoid relying upon external system configuration.
	 */
	@Deprecated
	public void updateMavenDependencies(final List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps) {
		final IProject project = CamelUtils.project();
		if (project == null) {
			CamelEditorUIActivator.pluginLog().logWarning(
					"Unable to add component dependencies because selected project can't be determined. Maybe this is a remote camel context.");
			return;
		}
		updateMavenDependencies(compDeps, project);
	}
	
	public void updateMavenDependencies(final List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps, IProject project) {
		if (compDeps == null || compDeps.isEmpty()) {
			CamelEditorUIActivator.pluginLog()
					.logWarning("Unable to add component dependencies because no dependencies were specified.");
			return;
		}
		// show progress dialog to user to signal ongoing changes to the pom
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Shells.getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					SubMonitor subMonitor = SubMonitor.convert(monitor, UIMessages.updatePomDependenciesProgressDialogLabel, 1);
					updateMavenDependencies(compDeps, project, subMonitor.split(1));
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
	public void updateMavenDependencies(List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps, IProject project, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
		CamelMavenUtils cmu = new CamelMavenUtils();
		final File pomFile = getPomFile(project);
		final Model model = cmu.getMavenModel(project);

		List<Dependency> projectDependencies = cmu.getDependencyList(project);

		// then check if component dependency is already a dep
		List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> missingDependencies = new ArrayList<>();
		String scope = determineScopeOfCamelCoreDependency(projectDependencies);
		determineMissingDependencies(compDeps, projectDependencies, missingDependencies);
		subMonitor.setWorkRemaining(9);

		addDependency(model, missingDependencies, scope);
		subMonitor.setWorkRemaining(8);

		if (!missingDependencies.isEmpty()) {
			writeNewPomFile(project, pomFile, model, subMonitor.split(8));
		}
		subMonitor.setWorkRemaining(0);
	}

	private void determineMissingDependencies(List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps, List<Dependency> projectDependencies, List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> missingDependencies) {
		for (org.fusesource.ide.camel.model.service.core.catalog.Dependency catalogConnectorDependency : compDeps) {
			boolean found = false;
			for (Dependency pomDep : projectDependencies) {
				if (pomDep.getGroupId().equalsIgnoreCase(catalogConnectorDependency.getGroupId()) && 
					pomDep.getArtifactId().equalsIgnoreCase(catalogConnectorDependency.getArtifactId())) {
					// check for correct version
					if (pomDep.getVersion() == null || !pomDep.getVersion().equalsIgnoreCase(catalogConnectorDependency.getVersion())) {
						// not the correct version - change it to fit
						pomDep.setVersion(catalogConnectorDependency.getVersion());
					}
					found = true;
					break;
				}
			}
			if (!found) {
				missingDependencies.add(catalogConnectorDependency);
			}
		}
	}
	
	private String determineScopeOfCamelCoreDependency(List<Dependency> projectDependencies) {
		String scope = null;
		for (Dependency pomDep : projectDependencies) {
			if (CAMEL_GROUP_ID.equalsIgnoreCase(pomDep.getGroupId()) &&
				CAMEL_CORE_ARTIFACT_ID.equalsIgnoreCase(pomDep.getArtifactId()) &&
				SCOPE_PROVIDED.equalsIgnoreCase(pomDep.getScope())) {
				scope = pomDep.getScope();
				break;
			}
		}
		return scope;
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
	 * @param monitor 
	 */
	public void writeNewPomFile(IProject project, final File pomFile, final Model model, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(pomFile))) {
			MavenPlugin.getMaven().writeModel(model, os);
			subMonitor.worked(1);
			IFile pomIFile2 = project.getProject().getFile(IMavenConstants.POM_FILE_NAME);
			if (pomIFile2 != null) {
				pomIFile2.refreshLocal(IResource.DEPTH_ONE, subMonitor.split(1));
				new BuildAndRefreshJobWaiterUtil().waitJob(subMonitor.split(1));
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
		final Model model = new CamelMavenUtils().getMavenModel(project);
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
				IFile pomIFile = project.getFile(IMavenConstants.POM_FILE_NAME);
				if (pomIFile != null) {
					pomIFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
			} catch (Exception ex) {
				CamelEditorUIActivator.pluginLog().logError(ex);
			}
		}
	}
	
	public boolean isSyndesisExtensionProject(IProject project) {
		Model model = new CamelMavenUtils().getMavenModel(project);
		if (model != null) {
			boolean pluginFound = isSyndesisPluginDefined(model.getBuild().getPlugins());
			if (!pluginFound && model.getBuild().getPluginManagement() != null) { 
				pluginFound = isSyndesisPluginDefined(model.getBuild().getPluginManagement().getPlugins());
			}
			if (pluginFound) {
				return true;
			}
		}
		return false;
	}	
	
	public boolean isSyndesisPluginDefined(List<Plugin> plugins) {
		if (plugins != null) {
			for (Plugin p : plugins) {
				if (SYNDESIS_PLUGIN_GROUPID.equalsIgnoreCase(p.getGroupId()) && 
					SYNDESIS_PLUGIN_ARTIFACTID.equalsIgnoreCase(p.getArtifactId()) ) {
					return true;
				}
			}
		}
		return false;
	}
}
