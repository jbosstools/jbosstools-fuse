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

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.fusesource.ide.launcher.run.util.MavenLaunchUtils;
import org.fusesource.ide.launcher.ui.Activator;

public class ExecutePomActionSupport implements ILaunchShortcut, IExecutableExtension {

	private final String launchConfigurationId;
	private final String launchConfigTypeId;

	private final String defaultMavenGoals;

	private String goalName;
	private boolean showDialog = false;
	private ExecutePomActionPostProcessor postProcessor;

	public ExecutePomActionSupport(String launchConfigurationId,
			String launchConfigTypeId, String defaultMavenGoals) {
		this.launchConfigurationId = launchConfigurationId;
		this.launchConfigTypeId = launchConfigTypeId;
		this.defaultMavenGoals = defaultMavenGoals;
		this.goalName = defaultMavenGoals;
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) {
		if ("WITH_DIALOG".equals(data)) {
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
			}
		}
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
				launchCamelContext((IFile)object, mode);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void launchCamelContext(IFile file, String mode) {
		if (file == null) {
			return;
		}

		IContainer basedir = findPomXmlBasedir(file.getParent());

		ILaunchConfiguration launchConfiguration;
		try {
			launchConfiguration = getLaunchConfiguration(basedir, mode);
			if (launchConfiguration == null) {
				launchConfiguration = createLaunchConfiguration(basedir,
						defaultMavenGoals);
				if (launchConfiguration == null) {
					return;
				}
			}
		} catch (InvalidConfigurationException e) {
			return;
		}

		boolean isWARPackaging = false;
		try {
			isWARPackaging = MavenLaunchUtils.isPackagingTypeWAR(basedir.getFile(Path.fromOSString("pom.xml")));
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
		}
		
		ILaunchConfigurationWorkingCopy lc = null;
		try {
			lc = launchConfiguration.getWorkingCopy();
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
			MessageDialog.openError(getShell(), "Unable to launch...", "An error occured when trying to launch the project. Message: " + ex.getMessage());
			return;
		}
		boolean openDialog = showDialog;
		if (!openDialog) {
			try {
				// if no goals specified
				String goals = lc.getAttribute(MavenLaunchConstants.ATTR_GOALS, (String) null);
				if (Strings.isBlank(goals)) {
					goals = isWARPackaging ? CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR : CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR;
				} else {
					if (goals.indexOf(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL) != -1) {
						// replace
						goals = goals.replaceAll(CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL, isWARPackaging ? CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR : CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR);
					} else {
						// add
						goals = isWARPackaging ? CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_WAR : CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_JAR;
					}
				}
				// no rider file selection
				if (Strings.isBlank(lc.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, ""))) {
					lc.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, file.getLocation().toOSString());
				}
				openDialog = Strings.isBlank(goals) || Strings.isBlank(lc.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, ""));
				lc.setAttribute(MavenLaunchConstants.ATTR_GOALS, goals);
			} catch (CoreException ex) {
				Activator.getLogger().error("Error getting the maven goals from the configuration.", ex);
			}
		}

		if (openDialog) {
			String category = "org.fusesource.ide.launcher.ui.runCamelLaunchGroup";
			if (mode == ILaunchManager.DEBUG_MODE) {
				category = "org.fusesource.ide.launcher.ui.debugCamelLaunchGroup";
			}
			if (DebugUITools.openLaunchConfigurationPropertiesDialog(getShell(), lc, category, null) == Window.CANCEL) {
				return;
			}
		} 
		
		try {
			final ILaunch launch = lc.launch(mode, new NullProgressMonitor());
			if (postProcessor != null) {

				// TODO would be nice to avoid the thread here but I guess there's no other way?
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						while (!launch.isTerminated()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
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
								Activator.getLogger().error("Failed to get exit code of build process", e);
							}
						}
						// only invoke post processor on sucess
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
		} catch (CoreException ex) {
			DebugUITools.launch(lc, mode);
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
					if (((IFile) dir).getName().equals(
							IMavenConstants.POM_FILE_NAME)) {
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

	private ILaunchConfiguration createLaunchConfiguration(IContainer basedir,
			String goal) {
		ILaunchConfiguration config = null;
		try {
			ILaunchManager launchManager = DebugPlugin.getDefault()
					.getLaunchManager();
			/*
			ILaunchConfigurationType launchConfigurationType = launchManager
					.getLaunchConfigurationType(MavenLaunchConstants.LAUNCH_CONFIGURATION_TYPE_ID);
			 */
			ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(launchConfigTypeId);

			String launchSafeGoalName = goal.replace(':', '-');

			ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType
					.newInstance(
							null, //
							"Executing "
							+ launchSafeGoalName
							+ " in "
							+ basedir.getLocation().toString()
							.replace('/', '-'));
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_POM_DIR, basedir
					.getLocation().toOSString());
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_GOALS, goal);
			workingCopy.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE,
					"${project}");
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					basedir.getProject().getName());

			appendAttributes(basedir, workingCopy, goal);

			setProjectConfiguration(workingCopy, basedir);

			IPath path = getJREContainerPath(basedir);
			if (path != null) {
				workingCopy
				.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
						path.toPortableString());
			}

			// TODO when launching Maven with debugger consider to add the
			// following property
			// -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"

			// CONTEXTLAUNCHING
			workingCopy.setMappedResources(new IResource[] { basedir });
			config = workingCopy.doSave();

			return config;
		} catch (CoreException ex) {
			Activator.getLogger().error("Unable to create launch configuration", ex);
		}
		return null;
	}

	/**
	 * Appends attributes to the working copy
	 * 
	 * @throws InvalidConfigurationException if the configuration cannot be used due to missing configuration
	 */
	protected void appendAttributes(IContainer basedir,
			ILaunchConfigurationWorkingCopy workingCopy, String goal) {
	}

	private void setProjectConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy, IContainer basedir) {
		IMavenProjectRegistry projectRegistry = MavenPlugin.getMavenProjectRegistry();
		IFile pomFile = basedir
				.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		IMavenProjectFacade projectFacade = projectRegistry.create(pomFile,
				false, new NullProgressMonitor());
		if (projectFacade != null) {
			ResolverConfiguration configuration = projectFacade
					.getResolverConfiguration();

			String activeProfiles = configuration.getActiveProfiles();
			if (activeProfiles != null && activeProfiles.length() > 0) {
				workingCopy.setAttribute(MavenLaunchConstants.ATTR_PROFILES,
						activeProfiles);
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
				if (JavaRuntime.JRE_CONTAINER
						.equals(entry.getPath().segment(0))) {
					return entry.getPath();
				}
			}
		}
		return null;
	}

	private ILaunchConfiguration getLaunchConfiguration(IContainer basedir,
			String mode) {
		if (goalName != null) {
			return createLaunchConfiguration(basedir, goalName);
		}

		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager
				.getLaunchConfigurationType(launchConfigTypeId);

		// scan existing launch configurations
		IPath basedirLocation = basedir.getLocation();
		if (!showDialog) {
			try {
				// ILaunch[] launches = launchManager.getLaunches();
				// ILaunchConfiguration[] launchConfigurations = null;
				// if(launches.length > 0) {
				// for(int i = 0; i < launches.length; i++ ) {
				// ILaunchConfiguration config =
				// launches[i].getLaunchConfiguration();
				// if(config != null &&
				// launchConfigurationType.equals(config.getType())) {
				// launchConfigurations = new ILaunchConfiguration[] {config};
				// }
				// }
				// }
				// if(launchConfigurations == null) {
				// launchConfigurations =
				// launchManager.getLaunchConfigurations(launchConfigurationType);
				// }

				ILaunchConfiguration[] launchConfigurations = launchManager
						.getLaunchConfigurations(launchConfigurationType);
				ArrayList<ILaunchConfiguration> matchingConfigs = new ArrayList<ILaunchConfiguration>();
				for (ILaunchConfiguration configuration : launchConfigurations) {
					// substitute variables
					String workDir = MavenLaunchUtils.substituteVar(configuration
							.getAttribute(MavenLaunchConstants.ATTR_POM_DIR,
									(String) null));
					if (workDir == null) {
						continue;
					}
					IPath workPath = new Path(workDir);
					if (basedirLocation.equals(workPath)) {
						matchingConfigs.add(configuration);
					}
				}

				if (matchingConfigs.size() == 1) {
					Activator.getLogger().info("Using existing launch configuration");
					return matchingConfigs.get(0);
				} else if (matchingConfigs.size() > 1) {
					final IDebugModelPresentation labelProvider = DebugUITools
							.newDebugModelPresentation();
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(
							getShell(), //
							new ILabelProvider() {
								@Override
								public Image getImage(Object element) {
									return labelProvider.getImage(element);
								}

								@Override
								public String getText(Object element) {
									if (element instanceof ILaunchConfiguration) {
										ILaunchConfiguration configuration = (ILaunchConfiguration) element;
										try {
											return labelProvider
													.getText(element)
													+ " : "
													+ configuration
													.getAttribute(
															MavenLaunchConstants.ATTR_GOALS,
															"");
										} catch (CoreException ex) {
											// ignore
										}
									}
									return labelProvider.getText(element);
								}

								@Override
								public boolean isLabelProperty(Object element,
										String property) {
									return labelProvider.isLabelProperty(
											element, property);
								}

								@Override
								public void addListener(
										ILabelProviderListener listener) {
									labelProvider.addListener(listener);
								}

								@Override
								public void removeListener(
										ILabelProviderListener listener) {
									labelProvider.removeListener(listener);
								}

								@Override
								public void dispose() {
									labelProvider.dispose();
								}
							});
					dialog.setElements(matchingConfigs
							.toArray(new ILaunchConfiguration[matchingConfigs
							                                  .size()]));
					dialog.setTitle("Select Configuration");
					if (mode.equals(ILaunchManager.DEBUG_MODE)) {
						dialog.setMessage("Select a launch configuration to debug:");
					} else {
						dialog.setMessage("Select a launch configuration to run:");
					}
					dialog.setMultipleSelection(false);
					int result = dialog.open();
					labelProvider.dispose();
					return result == Window.OK ? (ILaunchConfiguration) dialog
							.getFirstResult() : null;
				}

			} catch (CoreException ex) {
				Activator.getLogger().error("Unable to get the launch configuration.", ex);
			}
		}

		Activator.getLogger().info("Creating new launch configuration");

		// need to do it that way as Eclipse 3.6 differs from 3.5 so we need
		// to
		// introspect to find the correct method to call
		String newName = null;
		try {
			Method m = DebugPlugin.getDefault().getLaunchManager().getClass()
					.getMethod("generateLaunchConfigurationName", String.class);
			newName = (String) m.invoke(DebugPlugin.getDefault()
					.getLaunchManager(), basedir.getName());
		} catch (NoSuchMethodError nsm) {
			newName = DebugPlugin.getDefault().getLaunchManager()
					.generateLaunchConfigurationName(basedir.getName());
		} catch (Exception ex) {
			// ignore
		} finally {
			if (newName == null) {
				newName = basedir.getName();
			}
		}

		try {
			ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType
					.newInstance(null, newName);
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_POM_DIR,
					basedirLocation.toString());
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					basedir.getProject().getName());

			appendAttributes(basedir, workingCopy, goalName);

			setProjectConfiguration(workingCopy, basedir);

			// set other defaults if needed
			// MavenLaunchMainTab maintab = new MavenLaunchMainTab();
			// maintab.setDefaults(workingCopy);
			// maintab.dispose();

			return workingCopy.doSave();
		} catch (Exception ex) {
			Activator.getLogger().error("Error creating new launch configuration", ex);
		}
		return null;
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
				IFile f = (IFile)elem;
				return f.getLocationURI().toString();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param basedir
	 * @return
	 */
	protected IFile getPomFile(IContainer basedir) {
		return basedir.getFile(Path.fromOSString("pom.xml"));		
	}
}