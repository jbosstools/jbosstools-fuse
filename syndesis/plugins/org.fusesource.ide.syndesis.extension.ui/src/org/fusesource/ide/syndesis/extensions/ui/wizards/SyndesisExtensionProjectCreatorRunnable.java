/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.wizards;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
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
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.model.GlobalPreferences;
import org.eclipse.wst.validation.internal.model.GlobalPreferencesValues;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;
import org.fusesource.ide.syndesis.extensions.ui.templates.BasicSyndesisExtensionXmlProjectTemplate;

/**
 * @author lhein
 */
public final class SyndesisExtensionProjectCreatorRunnable implements IRunnableWithProgress {

	private static final String SYNDESIS_PLUGIN_GROUPID = "io.syndesis";
	private static final String SYNDESIS_PLUGIN_ARTIFACTID = "syndesis-maven-plugin";
	
	private NewProjectMetaData metadata;
	private SyndesisExtension extension;
	
	public SyndesisExtensionProjectCreatorRunnable(String projectName, IPath location, boolean isInWorkspace, SyndesisExtension extension) {
		this.extension = extension;
		this.metadata = new NewProjectMetaData();
		metadata.setProjectName(projectName);
		if (extension != null) this.metadata.setCamelVersion(extension.getCamelVersion());
		if (!isInWorkspace) {
			metadata.setLocationPath(location);
		}
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		boolean oldValueForValidation = disableGlobalValidationDuringProjectCreation();
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.syndesisExtensionProjectCreatorRunnableCreatingTheProjectMonitorMessage, 7);
			CamelModelServiceCoreActivator.getProjectClasspathChangeListener().deactivate();
			
			// first create the project skeleton
			BasicProjectCreator c = new BasicProjectCreator(metadata);
			boolean ok = c.create(subMonitor.split(1));
			IProject prj = c.getProject();
						
			if (ok) {
				// then configure the project for the given template
				AbstractProjectTemplate template = new BasicSyndesisExtensionXmlProjectTemplate();
				// now execute the template
				try {
					template.create(prj, metadata, subMonitor.split(1));
				} catch (CoreException ex) {
					SyndesisExtensionsUIActivator.pluginLog().logError("Unable to create project...", ex); //$NON-NLS-1$
				}
			}
			
			// now configure pom.xml with values from SyndesisExtension instance
			// refresh
			try {
				prj.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
				updateSyndesisConfiguration(prj, subMonitor.split(1));
			} catch (CoreException ex) {
				SyndesisExtensionsUIActivator.pluginLog().logError(ex);
			}

			// switch perspective if needed
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IPerspectiveDescriptor finalPersp = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(FuseIntegrationProjectCreatorRunnable.FUSE_PERSPECTIVE_ID);
			IPerspectiveDescriptor currentPersp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective();
			final boolean switchPerspective = currentPersp.getId().equals(finalPersp.getId()) ? false : confirmPerspectiveSwitch(workbenchWindow, finalPersp);
			if (switchPerspective) {
				// switch to Fuse perspective if necessary.
				switchToFusePerspective(workbenchWindow);
			}
			subMonitor.setWorkRemaining(6);

			// refresh
			try {
				prj.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
			} catch (CoreException ex) {
				SyndesisExtensionsUIActivator.pluginLog().logError(ex);
			}

			subMonitor.setWorkRemaining(3);
			
			CamelCatalogCacheManager.getInstance().getCamelModelForProject(prj, subMonitor.split(1, SubMonitor.SUPPRESS_NONE));
			
			// finally open the camel context file
			openCamelContextFile(prj, subMonitor.split(1));
			new BuildAndRefreshJobWaiterUtil().waitJob(subMonitor.split(1));
		} finally {
			setbackValidationValueAfterProjectCreation(oldValueForValidation);
			CamelModelServiceCoreActivator.getProjectClasspathChangeListener().activate();
		}
	}

	/**
	 * responsible to update the manifest.mf to use the project name as Bundle-SymbolicName
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	protected void updateSyndesisConfiguration(IProject project, IProgressMonitor monitor) throws CoreException {
		try {
			File pomFile = project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toFile();
			Model pomModel = new CamelMavenUtils().getMavenModel(project);

			configureProjectVersions(pomModel);
			customizeSyndesisPlugin(pomModel);

			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pomFile))) {
				MavenPlugin.getMaven().writeModel(pomModel, out);
				project.getFile(IMavenConstants.POM_FILE_NAME).refreshLocal(IResource.DEPTH_ZERO, monitor);
			}
		} catch (CoreException | XmlPullParserException | IOException e1) {
			SyndesisExtensionsUIActivator.pluginLog().logError(e1);
		}
	}
	
	private void customizeSyndesisPlugin(Model pomModel) throws XmlPullParserException, IOException {
		Build build = pomModel.getBuild();
		Map<String, Plugin> pluginsByName = build.getPluginsAsMap();
		Plugin plugin = pluginsByName.get(SYNDESIS_PLUGIN_GROUPID + ":" + SYNDESIS_PLUGIN_ARTIFACTID); //$NON-NLS-1$
		if (plugin != null) {
			manageConfiguration(plugin);
		}
	}

	private void manageConfiguration(Plugin plugin) throws XmlPullParserException, IOException {
		Xpp3Dom config = (Xpp3Dom)plugin.getConfiguration();
		if (config == null) {
			config = Xpp3DomBuilder.build(new ByteArrayInputStream((
					"<configuration>" + //$NON-NLS-1$
					"</configuration>").getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			plugin.setConfiguration(config);
		}
		manageInstructions(config);
	}

	private void manageInstructions(Xpp3Dom config) throws XmlPullParserException, IOException {
		setOrChangeConfigValue(config, "extensionId", extension.getExtensionId()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "name", extension.getName()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "description", extension.getDescription()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "version", extension.getVersion()); //$NON-NLS-1$
		setOrChangeConfigValue(config, "tags", extension.getTags().stream().collect(Collectors.joining(","))); //$NON-NLS-1$
	}
	
	private void setOrChangeConfigValue(Xpp3Dom config, String keyName, String newValue) throws XmlPullParserException, IOException {
		Xpp3Dom elem = config.getChild(keyName); 
		if (elem == null) {
			elem = Xpp3DomBuilder.build(
					new ByteArrayInputStream((String.format("<%s>%s</%s>", keyName, newValue, keyName)).getBytes(StandardCharsets.UTF_8)), //$NON-NLS-1$
					StandardCharsets.UTF_8.name());
			config.addChild(elem);
		} else {
			elem.setValue(newValue);
		}
	}
	
	private void configureProjectVersions(Model pomModel) {
		Properties props = pomModel.getProperties();
		props.setProperty("spring.boot.version", extension.getSpringBootVersion());
		props.setProperty("camel.version", extension.getCamelVersion());
		props.setProperty("syndesis.version", extension.getSyndesisVersion());
		pomModel.setProperties(props);
	}
	
	protected void setbackValidationValueAfterProjectCreation(boolean oldValueForValidation) {
		GlobalPreferencesValues globalPreferencesAsValues = ValManager.getDefault().getGlobalPreferences().asValues();
		globalPreferencesAsValues.disableAllValidation = oldValueForValidation;
		ValManager.getDefault().replace(globalPreferencesAsValues);
	}

	protected boolean disableGlobalValidationDuringProjectCreation() {
		GlobalPreferences globalPreferences = ValManager.getDefault().getGlobalPreferences();
		GlobalPreferencesValues globalPreferencesAsValues = globalPreferences.asValues();
		boolean oldValueForValidation = globalPreferencesAsValues.disableAllValidation;
		globalPreferencesAsValues.disableAllValidation = true;
		ValManager.getDefault().replace(globalPreferencesAsValues);
		return oldValueForValidation;
	}

	/**
	 * Switches, if necessary, the perspective of active workbench window to
	 * Fuse perspective.
	 *
	 * @param workbenchWindow
	 */
	void switchToFusePerspective(final IWorkbenchWindow workbenchWindow) {
		IPerspectiveDescriptor activePerspective = workbenchWindow.getActivePage().getPerspective();
		if (activePerspective == null || !FuseIntegrationProjectCreatorRunnable.FUSE_PERSPECTIVE_ID.equals(activePerspective.getId())) {
			workbenchWindow.getShell().getDisplay().syncExec( () -> {
				try {
					workbenchWindow.getWorkbench().showPerspective(FuseIntegrationProjectCreatorRunnable.FUSE_PERSPECTIVE_ID, workbenchWindow);
				} catch (WorkbenchException e) {
					SyndesisExtensionsUIActivator.pluginLog().logError(e);
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
			if (holder[0] != null) {
				Display.getDefault().asyncExec( () -> {
					try {
						if (!holder[0].exists()) {
							new BuildAndRefreshJobWaiterUtil().waitJob(monitor);
						}
						IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IDE.setDefaultEditor(holder[0], CamelUtils.CAMEL_EDITOR_ID);
						IDE.openEditor(activePage, holder[0], CamelUtils.CAMEL_EDITOR_ID, OpenStrategy.activateOnOpen());
					} catch (PartInitException e) {
						SyndesisExtensionsUIActivator.pluginLog().logError("Cannot open camel context file in editor", e); //$NON-NLS-1$
					}
				});
			}
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
