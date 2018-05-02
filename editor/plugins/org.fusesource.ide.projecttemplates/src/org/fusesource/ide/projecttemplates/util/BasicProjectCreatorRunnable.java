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
package org.fusesource.ide.projecttemplates.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.foundation.core.util.BuildAndRefreshJobWaiterUtil;
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
	
	private int result;
	private IWorkbenchPage activePage;
	
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
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.basicProjectCreatorRunnableCreatingTheProjectMonitorMessage, 1);
		// first create the project skeleton
		BasicProjectCreator c = new BasicProjectCreator(metadata);
		final boolean ok = c.create(subMonitor.split(1));
		final IProject prj = c.getProject();

		if (ok) {
			ProjectSetupJob job = new ProjectSetupJob(Messages.basicProjectCreatorRunnableCreatingTheProjectMonitorMessage, prj, metadata);
			job.schedule();
		}
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

		Display.getDefault().syncExec( () -> {
			MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(window.getShell(), ResourceMessages.NewProject_perspSwitchTitle, message,
					null /* use the default message for the toggle */,
					false /* toggle is initially unchecked */, store, IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE);
			result = dialog.getReturnCode();

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
		});
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
	
	
	public class ProjectSetupJob extends Job {
		
		private IProject project;
		private CommonNewProjectMetaData metadata;
		
		public ProjectSetupJob(final String name, final IProject project, CommonNewProjectMetaData metadata) {
			super(name);
			this.project = project;
			this.metadata = metadata;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.basicProjectCreatorRunnableCreatingTheProjectMonitorMessage, 7);

			boolean oldValueForValidation = disableGlobalValidationDuringProjectCreation();
			CamelModelServiceCoreActivator.getProjectClasspathChangeListener().deactivate();
			
			try {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IPerspectiveDescriptor finalPersp = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(FusePerspective.ID);
				if (workbenchWindow == null) {
					for (IWorkbenchWindow w : PlatformUI.getWorkbench().getWorkbenchWindows()) {
						if (w != null && w.getActivePage() != null) {
							workbenchWindow = w;
							activePage = w.getActivePage();
							break;
						}
					}
				}

				ensureProgressViewVisible();
								
				// prepare the template inside the project
				createAndConfigureTemplate(project, metadata, subMonitor.split(1));

				// switch perspective if needed
				if (requiresFuseIntegrationPerspective() && workbenchWindow != null && activePage != null) {
					IPerspectiveDescriptor currentPersp = workbenchWindow.getActivePage().getPerspective();

					final boolean switchPerspective = currentPersp.getId().equals(finalPersp.getId()) ? false : confirmPerspectiveSwitch(workbenchWindow, finalPersp);
					if (switchPerspective) {
						switchToFusePerspective(workbenchWindow);
						ensureProgressViewVisible();
					}
				}
				subMonitor.setWorkRemaining(5);

				// refresh
				try {
					project.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
				} catch (CoreException ex) {
					ProjectTemplatesActivator.pluginLog().logError(ex);
				}
				
				// user hook for doing additional project configuration work
				doAdditionalProjectConfiguration(project, subMonitor.split(1));

				// preload camel catalog for the given version
				if (shouldPreloadCatalog()) {
					CamelCatalogCacheManager.getInstance().getCamelModelForProject(project, subMonitor.split(1, SubMonitor.SUPPRESS_NONE));
				}
				
				subMonitor.setWorkRemaining(2);
				
				// finally open any editors required
				openRequiredFilesInEditor(project, subMonitor.split(1));
				
				// a final refresh
				new BuildAndRefreshJobWaiterUtil().waitJob(subMonitor.split(1));
			} finally {
				setbackValidationValueAfterProjectCreation(oldValueForValidation);
				CamelModelServiceCoreActivator.getProjectClasspathChangeListener().activate();
			}

			return Status.OK_STATUS;
		}
		
		private void ensureProgressViewVisible() {
			Display.getDefault().syncExec( () -> {
				try {
					activePage.showView("org.eclipse.ui.views.ProgressView");
				} catch (PartInitException ex) {
					ProjectTemplatesActivator.pluginLog().logError(ex);
				}		
			});
		}
		
		private void createAndConfigureTemplate(final IProject prj, final CommonNewProjectMetaData meta, final IProgressMonitor monitor) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
			// then configure the project for the given template
			AbstractProjectTemplate template = retrieveTemplate();
			subMonitor.setWorkRemaining(1);
			// now execute the template
			try {
				template.create(prj, meta, subMonitor.split(1));
			} catch (CoreException ex) {
				ProjectTemplatesActivator.pluginLog().logError("Unable to create project...", ex); //$NON-NLS-1$
			} finally {
				subMonitor.setWorkRemaining(0);
			}
		}
	}
}
