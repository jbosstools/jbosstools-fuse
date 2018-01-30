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

package org.fusesource.ide.launcher.ui.launch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.fusesource.ide.launcher.run.util.MavenLaunchUtils;
import org.fusesource.ide.launcher.ui.Activator;
import org.fusesource.ide.launcher.ui.Messages;

public abstract class ExecutePomActionSupport implements ILaunchShortcut, IExecutableExtension {

	private final String launchConfigurationId;
	private final String launchConfigTypeId;

	private final String defaultMavenGoals;

	private String goalName;
	private boolean showDialog = false;
	private ExecutePomActionPostProcessor postProcessor;
	private ILaunch launch;

	public ExecutePomActionSupport(String launchConfigurationId, String launchConfigTypeId, String defaultMavenGoals) {
		this.launchConfigurationId = launchConfigurationId;
		this.launchConfigTypeId = launchConfigTypeId;
		this.defaultMavenGoals = defaultMavenGoals;
		this.goalName = defaultMavenGoals;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
		if ("WITH_DIALOG".equals(data)) { //$NON-NLS-1$
			this.showDialog = true;
		} else {
			this.goalName = (String) data;
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		if (editor != null) {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				launchCamelContext(((IFileEditorInput) editorInput).getFile(), mode);
			} else if(editorInput instanceof CamelXMLEditorInput){
				launchCamelContext(((CamelXMLEditorInput) editorInput).getCamelContextFile(), mode);
			} else {
				IFile targetedFile = editorInput.getAdapter(IFile.class);
				if (isCamelContentType(targetedFile)) {
					launchCamelContext(targetedFile, mode);
				}
			}
		}
	}
	
	boolean isCamelContentType(IFile file){
		if(file != null){
			try {
				IContentDescription contentDescription = file.getContentDescription();
				return contentDescription != null
						&& CamelUtils.FUSE_CAMEL_CONTENT_TYPE.equals(contentDescription.getContentType().getId());
			} catch (CoreException e) {
				Activator.getLogger().error(e);
			}
		}
		return false;
	}

	public void setPostProcessor(ExecutePomActionPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}

	/**
	 * @return the postProcessor
	 */
	public ExecutePomActionPostProcessor getPostProcessor() {
		return this.postProcessor;
	}

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();

			if (object instanceof IFile) {
				launch = launchCamelContext((IFile)object, mode);
			} else if (object instanceof IProject) {
				launch = launchCamelContextOnProject((IProject)object, mode);					
			}
		}
	}

	ILaunch launchCamelContext(IFile camelFile, String mode) {
		if (camelFile == null) {
			return null;
		}

		IContainer basedir = findPomXmlBasedir(camelFile.getParent());

		ILaunchConfiguration launchConfiguration;
		try {
			launchConfiguration = getLaunchConfiguration(camelFile, basedir, mode);
			if (launchConfiguration == null) {
				launchConfiguration = createLaunchConfiguration(basedir, defaultMavenGoals, camelFile);
				if (launchConfiguration == null) {
					return null;
				}
			}
		} catch (InvalidConfigurationException e) {
			return null;
		}

		boolean isWARPackaging = false;
		try {
			isWARPackaging = MavenLaunchUtils.isPackagingTypeWAR(basedir.getFile(Path.fromOSString(IMavenConstants.POM_FILE_NAME))); //$NON-NLS-1$
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
		}
		
		ILaunchConfigurationWorkingCopy lc = null;
		try {
			lc = launchConfiguration.getWorkingCopy();
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
			MessageDialog.openError(getShell(), Messages.ExecutePomActionSupport_UnableToLaunchTitle, Messages.ExecutePomActionSupport_UnableToLaunchMessage + ex.getMessage());
			return null;
		}
		boolean openDialog = showDialog;
		if (!openDialog) {
			try {
				// if no goals specified
				String goals = setGoals(isWARPackaging, lc);
				// no rider file selection
				if (Strings.isBlank(lc.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, ""))) { //$NON-NLS-1$
					lc.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, getAttributeURIFormatStorage(camelFile));
				}
				openDialog = Strings.isBlank(goals) || Strings.isBlank(lc.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, "")); //$NON-NLS-1$
			} catch (CoreException ex) {
				Activator.getLogger().error("Error getting the maven goals from the configuration.", ex); //$NON-NLS-1$
			}
		}

		if (openDialog) {
			String category = "org.fusesource.ide.launcher.ui.runCamelLaunchGroup"; //$NON-NLS-1$
			if (mode == ILaunchManager.DEBUG_MODE) {
				category = "org.fusesource.ide.launcher.ui.debugCamelLaunchGroup"; //$NON-NLS-1$
			}
			if (DebugUITools.openLaunchConfigurationPropertiesDialog(getShell(), lc, category, null) == Window.CANCEL) {
				return null;
			}
		} 
		
		try {
			final ILaunch launch = lc.doSave().launch(mode, new NullProgressMonitor());
			handlePostLaunch(launch);
			return launch;
		} catch (CoreException ex) {
			DebugUITools.launch(lc, mode);
		}
		return null;
	}

	protected String setGoals(boolean isWARPackaging, ILaunchConfigurationWorkingCopy lc) throws CoreException {
		String goals = lc.getAttribute(MavenLaunchConstants.ATTR_GOALS, (String) null);
		if (Strings.isBlank(goals)) {
			goals = isWARPackaging ? CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR : CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR;
		} else {
			if (goals.contains(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL)) {
				// replace
				if(isWARPackaging){
					goals = goals.replaceAll(CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_JAR, CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_WAR);
					if(!goals.contains(CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_WAR)){
						goals += " " + CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_WAR;
					}
				} else {
					goals = goals.replaceAll(CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_WAR, CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_JAR);
					if(!goals.contains(CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_JAR)){
						goals += " " + CamelContextLaunchConfigConstants.SPECIFIC_MAVEN_GOAL_JAR;
					}
				}
			} else {
				// add
				goals = isWARPackaging ? CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR : CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR;
			}
		}
		lc.setAttribute(MavenLaunchConstants.ATTR_GOALS, goals);
		return goals;
	}

	/**
	 * @param launch
	 */
	private void handlePostLaunch(final ILaunch launch) {
		if (postProcessor != null) {

			// TODO would be nice to avoid the thread here but I guess there's no other way?
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!launch.isTerminated()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}

					IProcess[] processes = launch.getProcesses();
					boolean failed = false;
					for (IProcess process : processes) {
						try {
							if (process.getExitValue() != 0) {
								failed = true;
								break;
							}
						} catch (DebugException e) {
							Activator.getLogger().error("Failed to get exit code of build process", e); //$NON-NLS-1$
						}
					}
					// only invoke post processor on success
					if (!failed) {
						postProcessor.executeOnSuccess();
					} else {
						postProcessor.executeOnFailure();
					}
				}
			});
			t.setDaemon(false);
			t.start();
		}
	}

	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	private IContainer findPomXmlBasedir(IContainer dir) {
		if (dir == null) {
			return null;
		}

		try {
			// loop upwards through the parents as long as we do not cross the
			// project boundary
			while (dir.exists() && dir.getProject() != null
					&& dir.getProject() != dir) {
				// see if pom.xml exists
				if (dir.getType() == IResource.FOLDER) {
					IFolder folder = (IFolder) dir;
					if (folder.findMember(IMavenConstants.POM_FILE_NAME) != null) {
						return folder;
					}
				} else if (dir.getType() == IResource.FILE) {
					if (((IFile) dir).getName().equals(IMavenConstants.POM_FILE_NAME)) {
						return dir.getParent();
					}
				}
				dir = dir.getParent();
			}
		} catch (Exception e) {
			return dir;
		}
		return dir;
	}

	private ILaunchConfiguration createLaunchConfiguration(IContainer basedir, String goal, IFile camelFile) {
		try {
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(launchConfigTypeId);
			String launchConfigName = launchManager.generateLaunchConfigurationName(getBasicLaunchConfigurationName(camelFile));
			ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, launchConfigName);
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_POM_DIR, basedir.getLocation().toOSString());
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_GOALS, goal);
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE, "${project}"); //$NON-NLS-1$
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, basedir.getProject().getName());

			appendAttributes(basedir, workingCopy, goal);

			setProjectConfiguration(workingCopy, basedir);

			IPath path = getJREContainerPath(basedir);
			if (path != null) {
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, path.toPortableString());
			}

			// TODO when launching Maven with debugger consider to add the
			// following property
			// -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"

			// CONTEXTLAUNCHING
			workingCopy.setMappedResources(new IResource[] { basedir, camelFile });
			return workingCopy.doSave();
		} catch (CoreException ex) {
			Activator.getLogger().error("Unable to create launch configuration", ex); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * @param basedir
	 * @param camelFile
	 * @param launchSafeGoalName
	 * @return
	 */
	protected String getBasicLaunchConfigurationName(IFile camelFile) {
		return NLS.bind(Messages.launchConfigurationName, camelFile.getProject().getName(), camelFile.getName());
	}

	/**
	 * Appends attributes to the working copy
	 * 
	 * @throws InvalidConfigurationException
	 *             if the configuration cannot be used due to missing
	 *             configuration
	 */
	protected abstract void appendAttributes(IContainer basedir, ILaunchConfigurationWorkingCopy workingCopy, String goal);

	private void setProjectConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IContainer basedir) {
		IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		IFile pomFile = basedir.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		IMavenProjectFacade projectFacade = projectRegistry.create(pomFile, false, new NullProgressMonitor());
		if (projectFacade != null) {
			ResolverConfiguration configuration = projectFacade.getResolverConfiguration();

			String activeProfiles = configuration.getSelectedProfiles();
			if (activeProfiles != null && activeProfiles.length() > 0) {
				workingCopy.setAttribute(MavenLaunchConstants.ATTR_PROFILES, activeProfiles);
			}
		}
	}

	private IPath getJREContainerPath(IContainer basedir) throws CoreException {
		IProject project = basedir.getProject();
		if (project != null && project.hasNature(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] entries = javaProject.getRawClasspath();
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (JavaRuntime.JRE_CONTAINER.equals(entry.getPath().segment(0))) {
					return entry.getPath();
				}
			}
		}
		return null;
	}

	private ILaunchConfiguration getLaunchConfiguration(IFile camelFile, IContainer basedir, String mode) {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(launchConfigTypeId);

		// scan existing launch configurations
		IPath basedirLocation = basedir.getLocation();
		if (!showDialog) {
			try {
				List<ILaunchConfiguration> matchingConfigs = findMatchingLaunchConfiguration(camelFile, launchManager, launchConfigurationType, basedirLocation);

				if (matchingConfigs.size() == 1) {
					Activator.getLogger().info("Using existing launch configuration"); //$NON-NLS-1$
					return matchingConfigs.get(0);
				} else if (matchingConfigs.size() > 1) {
					final IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(
							getShell(), 
							new CustomLaunchConfigurationLabelProvider(labelProvider));
					dialog.setElements(matchingConfigs.toArray(new ILaunchConfiguration[matchingConfigs.size()]));
					dialog.setTitle(Messages.ExecutePomActionSupport_SelectConfigurationDialogTitle);
					if (mode.equals(ILaunchManager.DEBUG_MODE)) {
						dialog.setMessage(Messages.ExecutePomActionSupport_SelectDebugConfigurationmessage);
					} else {
						dialog.setMessage(Messages.ExecutePomActionSupport_SelectRunConfigurationmessage);
					}
					dialog.setMultipleSelection(false);
					int result = dialog.open();
					labelProvider.dispose();
					return result == Window.OK ? (ILaunchConfiguration) dialog.getFirstResult() : null;
				}

			} catch (CoreException ex) {
				Activator.getLogger().error("Unable to get the launch configuration.", ex); //$NON-NLS-1$
			}
		}

		Activator.getLogger().info("Creating new launch configuration"); //$NON-NLS-1$

		return createLaunchConfiguration(basedir, goalName, camelFile);
	}

	/**
	 * @param camelFile
	 * @param launchManager
	 * @param launchConfigurationType
	 * @param basedirLocation
	 * @return
	 * @throws CoreException
	 */
	private List<ILaunchConfiguration> findMatchingLaunchConfiguration(IFile camelFile, ILaunchManager launchManager, ILaunchConfigurationType launchConfigurationType,
			IPath basedirLocation) throws CoreException {
		ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations(launchConfigurationType);
		return Stream.of(launchConfigurations)
				.filter(configuration -> isLaunchConfigurationMatching(camelFile, basedirLocation, configuration))
				.collect(Collectors.toList());
	}

	/**
	 * @param camelFile
	 * @param basedirLocation
	 * @param configuration
	 * @return
	 * @throws CoreException
	 */
	private boolean isLaunchConfigurationMatching(IFile camelFile, IPath basedirLocation, ILaunchConfiguration configuration) {
		return isSameBaseDir(basedirLocation, configuration) && isSameCamelFile(camelFile, configuration) && isTestStrategyMatching(configuration);
	}

	/**
	 * @param configuration
	 * @return
	 */

	protected abstract boolean isTestStrategyMatching(ILaunchConfiguration configuration);

	protected boolean isSkipTest(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(MavenLaunchConstants.ATTR_SKIP_TESTS, false);
		} catch (CoreException e) {
			Activator.getLogger().error(e);
		}
		return false;
	}

	/**
	 * @param basedirLocation
	 * @param configuration
	 * @throws CoreException
	 */
	private boolean isSameBaseDir(IPath basedirLocation, ILaunchConfiguration configuration) {
		try {
			String workDir = MavenLaunchUtils.substituteVar(configuration.getAttribute(MavenLaunchConstants.ATTR_POM_DIR, (String) null));
			if (workDir == null) {
				return false;
			}
			IPath workPath = new Path(workDir);
			return basedirLocation.equals(workPath);
		} catch (CoreException e) {
			return false;
		}
	}

	/**
	 * @param camelFile
	 * @param configuration
	 * @return
	 * @throws CoreException
	 */
	private boolean isSameCamelFile(IFile camelFile, ILaunchConfiguration configuration) {
		try {
			String camelFileFromLaunchConfig = configuration.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, (String) null);
			return camelFile.getLocationURI().compareTo(new URI(camelFileFromLaunchConfig)) == 0;
		} catch (CoreException | URISyntaxException e) {
			Activator.getLogger().warning(e);
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	protected String getSelectedFilePath() {
		ISelectionService selService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection isel = selService.getSelection();
		if (isel != null && isel instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection)isel;
			Object elem = ssel.getFirstElement();
			if (elem != null && elem instanceof IFile) {
				return getAttributeURIFormatStorage((IFile)elem);
			}
		}
		return null;
	}

	private String getAttributeURIFormatStorage(IFile file) {
		return file.getLocationURI().toString();
	}

	/**
	 * 
	 * @param basedir
	 * @return
	 */
	protected IFile getPomFile(IContainer basedir) {
		return basedir.getFile(Path.fromOSString(IMavenConstants.POM_FILE_NAME));		 //$NON-NLS-1$
	}
	
	private ILaunch launchCamelContextOnProject(IProject project, String mode) {
		try{
			final List<IFile> files = CamelUtils.getFilesWithCamelContentType(project);
			if (files.isEmpty()) {
				MessageDialog.openInformation(getShell(), Messages.ExecutePomActionSupport_NoCamelXMLFileFoundTitle, Messages.ExecutePomActionSupport_NoCamelXMLFileFoundMessage);
				return null;
			} else if (files.size() == 1) {
				return launchCamelContext(files.get(0), mode);
			} else if (files.size() > 1) {
				//org.jboss.tools.fuse.transformation.editor.internal.util.Util.selectCamelResourceFromWorkspace(Shell, IProject)
				FilteredItemsSelectionDialog selector = new FilteredResourcesSelectionDialog(getShell(), false, project, IResource.FILE){
							@Override
							protected void fillContentProvider(final AbstractContentProvider contentProvider,
									ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
								super.fillContentProvider(new AbstractContentProvider(){
									@Override
									public void add(Object item, ItemsFilter itemsFilter) {
										if(files.contains(item)){
											contentProvider.add(item, itemsFilter);
										}												
									}}, itemsFilter, progressMonitor);
							}					
				};
				selector.setTitle(Messages.xmlSelectionDialogOnRunAndDebugTitle);
				selector.setInitialPattern("*.xml"); //$NON-NLS-1$
				if (selector.open() == Window.OK) {
					Object[] resultArray = selector.getResult();
					if (resultArray != null && resultArray.length > 0 && resultArray[0] instanceof IFile) {
						return launchCamelContext((IFile) resultArray[0],mode);
					}
				}
			}
		} catch (CoreException e) {
			Activator.getLogger().error("Failed to launch camel context: " + e, e); //$NON-NLS-1$
		}
		return  null;
	}

	public ILaunch getLaunch() {
		return launch;
	}

}