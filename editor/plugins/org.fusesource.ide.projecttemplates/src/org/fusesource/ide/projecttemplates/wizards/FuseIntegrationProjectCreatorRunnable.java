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
package org.fusesource.ide.projecttemplates.wizards;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.fusesource.ide.camel.model.service.core.util.JavaCamelFilesFinder;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplate;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreator;
import org.fusesource.ide.projecttemplates.util.JobWaiterUtil;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

/**
 * @author Aurelien Pupier
 *
 */
public final class FuseIntegrationProjectCreatorRunnable implements IRunnableWithProgress {

	public static final String FUSE_PERSPECTIVE_ID = "org.fusesource.ide.branding.perspective"; //$NON-NLS-1$

	private static final String ORG_APACHE_FELIX = "org.apache.felix";
	private static final String MAVEN_BUNDLE_PLUGIN = "maven-bundle-plugin";
	
	private final NewProjectMetaData metadata;
	
	private boolean isJavaEditorToOpen = false;

	/**
	 * @param metadata
	 */
	public FuseIntegrationProjectCreatorRunnable(NewProjectMetaData metadata) {
		this.metadata = metadata;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.FuseIntegrationProjectCreatorRunnable_CreatingTheProjectMonitorMessage, 7);
		// first create the project skeleton
		BasicProjectCreator c = new BasicProjectCreator(metadata);
		boolean ok = c.create(subMonitor.newChild(1));
		IProject prj = c.getProject();
		if (ok) {
			// then configure the project for the given template
			AbstractProjectTemplate template = metadata.getTemplate();
			if (metadata.isBlankProject()) {
				// we create a blank project
				template = new EmptyProjectTemplate();
			}
			// now execute the template
			try {
				template.create(prj, metadata, subMonitor.newChild(1));
			} catch (CoreException ex) {
				ProjectTemplatesActivator.pluginLog().logError("Unable to create project...", ex); //$NON-NLS-1$
			}
		}

		// switch perspective if needed
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPerspectiveDescriptor finalPersp = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(FUSE_PERSPECTIVE_ID);
		IPerspectiveDescriptor currentPersp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective();
		final boolean switchPerspective = currentPersp.getId().equals(finalPersp.getId()) ? false : confirmPerspectiveSwitch(workbenchWindow, finalPersp);
		if (switchPerspective) {
			// switch to Fuse perspective if necessary.
			switchToFusePerspective(workbenchWindow);
			subMonitor.worked(1);
		}
		
		// refresh
		try {
			prj.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.newChild(1));
			// update the pom maven bundle plugin config to reflect project name as Bundle-(Symbolic)Name
			updateBundlePluginConfiguration(prj, subMonitor.newChild(1));
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
		}
		// delete invalid MANIFEST files
		IResource rs = prj.findMember("src/META-INF/"); //$NON-NLS-1$
		if (rs != null && rs.exists()) {
			try {
				rs.delete(true, subMonitor.newChild(1));
			} catch (CoreException ex) {
				ProjectTemplatesActivator.pluginLog().logError(ex);
			}
		}
		// finally open the camel context file
		openCamelContextFile(prj, subMonitor.newChild(1));
		subMonitor.done();
	}

	/**
	 * responsible to update the manifest.mf to use the project name as Bundle-SymbolicName
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	protected void updateBundlePluginConfiguration(IProject project, IProgressMonitor monitor) throws CoreException {
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = MavenPlugin.getMaven().readModel(pomFile);

			customizeBundlePlugin(pomModel, project);

			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(pomModel, out);
				project.getFile(IMavenConstants.POM_FILE_NAME).refreshLocal(IResource.DEPTH_ZERO, monitor);
			}
		} catch (CoreException | XmlPullParserException | IOException e1) {
			ProjectTemplatesActivator.pluginLog().logError(e1);
		}
	}
	
	private void customizeBundlePlugin(Model pomModel, IProject project) throws XmlPullParserException, IOException {
		Build build = pomModel.getBuild();
		Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
		Plugin plugin = pluginsByName.get(ORG_APACHE_FELIX + ":" + MAVEN_BUNDLE_PLUGIN); //$NON-NLS-1$
		if (plugin != null) {
			manageConfigurations(plugin, project, pomModel);
		}
	}

	private void manageConfigurations(Plugin plugin, IProject project, Model pomModel) throws XmlPullParserException, IOException {
		Xpp3Dom config = (Xpp3Dom)plugin.getConfiguration();
		if (config == null) {
			config = Xpp3DomBuilder.build(new ByteArrayInputStream((
					"<configuration>" + //$NON-NLS-1$
					"</configuration>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			plugin.setConfiguration(config);
		}
		manageInstructions(config, project, pomModel);
	}

	private void manageInstructions(Xpp3Dom config, IProject project, Model pomModel) throws XmlPullParserException, IOException {
		Xpp3Dom instructions = config.getChild("instructions"); //$NON-NLS-1$
		if (instructions == null) {
			instructions = Xpp3DomBuilder.build(new ByteArrayInputStream(("<instructions>" + //$NON-NLS-1$
					"</instructions>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			config.addChild(instructions);
		}
		manageCustomInstructions(instructions, project, pomModel);
	}
	
	private void manageCustomInstructions(Xpp3Dom instructions, IProject project, Model pomModel) throws XmlPullParserException, IOException {
		Xpp3Dom bundleSymbolicName = instructions.getChild("Bundle-SymbolicName"); //$NON-NLS-1$
		if (bundleSymbolicName == null) {
			bundleSymbolicName = Xpp3DomBuilder.build(
					new ByteArrayInputStream(("<Bundle-SymbolicName>" + getBundleSymbolicNameForProjectName(project.getName()) + "</Bundle-SymbolicName>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			instructions.addChild(bundleSymbolicName);
		}
		String description = pomModel.getDescription();
		String desc = description != null && description.trim().length()>0 ? description : String.format("%s.%s", pomModel.getGroupId(), pomModel.getArtifactId()); 
		Xpp3Dom bundleName = instructions.getChild("Bundle-Name"); //$NON-NLS-1$
		if (bundleName == null) {
			bundleName = Xpp3DomBuilder.build(
					new ByteArrayInputStream(("<Bundle-Name>" + String.format("%s [%s]", desc, project.getName()) + "</Bundle-Name>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			instructions.addChild(bundleName);
		}
	}
	
	/**
	 * converts a project name into a bundle symbolic name
	 * 
	 * @param projectName
	 * @return
	 */
	public String getBundleSymbolicNameForProjectName(String projectName) {
		return projectName.replaceAll("[^a-zA-Z0-9-_]","");
	}

	/**
	 * Switches, if necessary, the perspective of active workbench window to
	 * Fuse perspective.
	 *
	 * @param workbenchWindow
	 */
	void switchToFusePerspective(final IWorkbenchWindow workbenchWindow) {
		IPerspectiveDescriptor activePerspective = workbenchWindow.getActivePage().getPerspective();
		if (activePerspective == null || !activePerspective.getId().equals(FUSE_PERSPECTIVE_ID)) {
			workbenchWindow.getShell().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						workbenchWindow.getWorkbench().showPerspective(FUSE_PERSPECTIVE_ID, workbenchWindow);
					} catch (WorkbenchException e) {
						ProjectTemplatesActivator.pluginLog().logError(e);
					}
				}
			});
		}
	}

	/**
	 * Prompts the user for whether to switch perspectives.
	 *
	 * @param window
	 *            The workbench window in which to switch perspectives; must not
	 *            be <code>null</code>
	 * @param finalPersp
	 *            The perspective to switch to; must not be <code>null</code>.
	 *
	 * @return <code>true</code> if it's OK to switch, <code>false</code>
	 *         otherwise
	 */
	private boolean confirmPerspectiveSwitch(IWorkbenchWindow window, IPerspectiveDescriptor finalPersp) {

		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		String pspm = store.getString(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
		if (!IDEInternalPreferences.PSPM_PROMPT.equals(pspm)) {
			// Return whether or not we should always switch
			return IDEInternalPreferences.PSPM_ALWAYS.equals(pspm);
		}

		String desc = finalPersp.getDescription();
		String message;
		if (desc == null || desc.length() == 0) {
			message = NLS.bind(ResourceMessages.NewProject_perspSwitchMessage, finalPersp.getLabel());
		} else {
			message = NLS.bind(ResourceMessages.NewProject_perspSwitchMessageWithDesc, new String[] { finalPersp.getLabel(), desc });
		}

		MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(window.getShell(), ResourceMessages.NewProject_perspSwitchTitle, message,
				null /* use the default message for the toggle */,
				false /* toggle is initially unchecked */, store, IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
		int result = dialog.getReturnCode();

		// If we are not going to prompt anymore propagate the choice.
		if (dialog.getToggleState()) {
			String preferenceValue;
			if (result == IDialogConstants.YES_ID) {
				// Doesn't matter if it is replace or new window
				// as we are going to use the open perspective setting
				preferenceValue = IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE;
			} else {
				preferenceValue = IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE;
			}

			// update PROJECT_OPEN_NEW_PERSPECTIVE to correspond
			PrefUtil.getAPIPreferenceStore().setValue(IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE, preferenceValue);
		}
		return result == IDialogConstants.YES_ID;
	}

	/**
	 * Open the first detected camel context file in the editor
	 *
	 * @param project
	 */
	private void openCamelContextFile(IProject project, IProgressMonitor monitor) {
		if (project != null) {
			final IFile[] holder = new IFile[1];
			searchCamelContextXMLFile(project, holder);
			ProjectTemplatesActivator.pluginLog().logWarning("xml file found? " + holder[0]); //$NON-NLS-1$
			try {
				if (holder[0] == null && project.hasNature(JavaCore.NATURE_ID)) {
					searchCamelContextJavaFile(project, monitor, holder);
					isJavaEditorToOpen = true;
				}
			} catch (CoreException e1) {
				ProjectTemplatesActivator.pluginLog().logError(e1);
			}

			if (holder[0] != null) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							if (!holder[0].exists()) {
								new JobWaiterUtil().waitBuildAndRefreshJob(monitor);
							}
							IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							if(isJavaEditorToOpen){
								IDE.openEditor(activePage, holder[0], OpenStrategy.activateOnOpen());
							} else {
								IDE.setDefaultEditor(holder[0], CamelUtils.CAMEL_EDITOR_ID);
								IDE.openEditor(activePage, holder[0], CamelUtils.CAMEL_EDITOR_ID, OpenStrategy.activateOnOpen());
							}
						} catch (PartInitException e) {
							ProjectTemplatesActivator.pluginLog().logError("Cannot open camel context file in editor", e); //$NON-NLS-1$
						}
					}
				});
			}
		}
	}

	/**
	 * @param project
	 * @param monitor
	 * @param holder
	 */
	private void searchCamelContextJavaFile(IProject project, IProgressMonitor monitor, final IFile[] holder) {
		IFile f = new JavaCamelFilesFinder().findJavaDSLRouteBuilderClass(project, monitor);
		if (f != null) {
			holder[0] = f;
		}
	}

	/**
	 * @param project
	 * @param holder
	 */
	private void searchCamelContextXMLFile(IProject project, final IFile[] holder) {
		Set<IFile> camelFiles = new CamelFilesFinder().findFiles(project);
		if(!camelFiles.isEmpty()){
			holder[0] = camelFiles.iterator().next();
		}
	}

}
