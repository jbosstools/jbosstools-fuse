/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
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
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse6;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse7;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lheinema
 */
public abstract class BasicProjectCreatorRunnable implements IRunnableWithProgress {
	
	protected CommonNewProjectMetaData metadata;
	
	public BasicProjectCreatorRunnable(CommonNewProjectMetaData metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * returns true if the catalog should be preloaded
	 * 
	 * @return
	 */
	public boolean shouldPreloadCatalog() {
		return true;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		boolean oldValueForValidation = disableGlobalValidationDuringProjectCreation();
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.basicProjectCreatorRunnableCreatingTheProjectMonitorMessage, 9);
			CamelModelServiceCoreActivator.getProjectClasspathChangeListener().deactivate();

			// first create the project skeleton
			BasicProjectCreator c = new BasicProjectCreator(metadata);
			boolean ok = c.create(subMonitor.split(1));
			IProject prj = c.getProject();
						
			if (ok) {
				// then configure the project for the given template
				AbstractProjectTemplate template = retrieveTemplate();
				// now execute the template
				try {
					template.create(prj, metadata, subMonitor.split(1));
				} catch (CoreException ex) {
					ProjectTemplatesActivator.pluginLog().logError("Unable to create project...", ex); //$NON-NLS-1$
				}
				checkJRECompatibility(template, subMonitor.split(1));
			}

			// switch perspective if needed
			if (requiresFuseIntegrationPerspective()) {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IPerspectiveDescriptor finalPersp = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(FusePerspective.ID);
				IPerspectiveDescriptor currentPersp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective();
				final boolean switchPerspective = currentPersp.getId().equals(finalPersp.getId()) ? false : confirmPerspectiveSwitch(workbenchWindow, finalPersp);
				if (switchPerspective) {
					switchToFusePerspective(workbenchWindow);
				}
			}
			subMonitor.setWorkRemaining(5);
				
			// refresh
			try {
				prj.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
			} catch (CoreException ex) {
				ProjectTemplatesActivator.pluginLog().logError(ex);
			}

			// user hook for doing additional project configuration work
			doAdditionalProjectConfiguration(prj, subMonitor.split(1));

			// preload camel catalog for the given version
			if (shouldPreloadCatalog()) {
				CamelCatalogCacheManager.getInstance().getCamelModelForProject(prj, subMonitor.split(1, SubMonitor.SUPPRESS_NONE));
			}
			subMonitor.setWorkRemaining(2);
			
			// finally open any editors required
			openRequiredFilesInEditor(prj, subMonitor.split(1));

			// a final refresh
			new BuildAndRefreshJobWaiterUtil().waitJob(subMonitor.split(1));
			
			
		} finally {
			setbackValidationValueAfterProjectCreation(oldValueForValidation);
			CamelModelServiceCoreActivator.getProjectClasspathChangeListener().activate();	
		}
	}
	
	private void checkJRECompatibility(AbstractProjectTemplate template, IProgressMonitor monitor) {
		String javaExecutionEnvironment = template.getJavaExecutionEnvironment();
		if (!hasStrictlyCompatibleVM(javaExecutionEnvironment)) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.noStrictlyCompliantJREWarningTitle,
					NLS.bind(Messages.noStrictlyCompliantJREWarningMessage, javaExecutionEnvironment));
		}
	}

	private boolean hasStrictlyCompatibleVM(String environmentId) {
		IExecutionEnvironment executionEnvironment = JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(environmentId);
		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
		for (IVMInstallType vmInstallType : vmInstallTypes) {
			for (IVMInstall vmInstall : vmInstallType.getVMInstalls()) {
				if (executionEnvironment.isStrictlyCompatible(vmInstall)) {
					return true;
				}
			}
		}
		return false;
	}

	protected void openRequiredFilesInEditor(IProject prj, IProgressMonitor monitor) {
		// to be implemented by a subclass if required
	}
	
	protected void doAdditionalProjectConfiguration(IProject prj, IProgressMonitor monitor) {
		// delete invalid MANIFEST files
		IResource rs = prj.findMember("src/META-INF/"); //$NON-NLS-1$
		if (rs != null && rs.exists()) {
			try {
				rs.delete(true, monitor);
			} catch (CoreException ex) {
				ProjectTemplatesActivator.pluginLog().logError(ex);
			}
		}		
	}

	protected boolean requiresFuseIntegrationPerspective() {
		return true;
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
		if (activePerspective == null || !FusePerspective.ID.equals(activePerspective.getId())) {
			workbenchWindow.getShell().getDisplay().syncExec( () -> {
				try {
					workbenchWindow.getWorkbench().showPerspective(FusePerspective.ID, workbenchWindow);
				} catch (WorkbenchException e) {
					ProjectTemplatesActivator.pluginLog().logError(e);
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
	
	protected AbstractProjectTemplate retrieveTemplate() {
		if (metadata.getTemplate() == null) {
			if(new VersionUtil().isStrictlyLowerThan2200(metadata.getCamelVersion())){
				return new EmptyProjectTemplateForFuse6();
			} else {
				return new EmptyProjectTemplateForFuse7();
			}
		} else {
			return metadata.getTemplate();
		}
	}
}
