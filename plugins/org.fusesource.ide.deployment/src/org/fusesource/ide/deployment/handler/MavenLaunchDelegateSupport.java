package org.fusesource.ide.deployment.handler;

import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.fusesource.ide.launcher.MavenLaunchDelegate;


public abstract class MavenLaunchDelegateSupport extends MavenLaunchDelegate {

	public MavenLaunchDelegateSupport(String mavenGoals) {
		super(mavenGoals);
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
	
		if (isNonJavaProject(configuration)) {
			// lets pretend to not be a JavaProject
			return preChecksNonJavaProject(configuration, mode, monitor);
		} else {
			return super.preLaunchCheck(configuration, mode, monitor);
		}
	}

	/**
	 * Perform the necessary pre checks when executed on a non Java Project...
	 */
	protected boolean preChecksNonJavaProject(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		if (!saveBeforeLaunch(configuration, mode, monitor)) {
			return false;
		}
		if (mode.equals(ILaunchManager.RUN_MODE) && configuration.supportsMode(ILaunchManager.DEBUG_MODE)) {
			IBreakpoint[] breakpoints= getBreakpoints(configuration);
	        if (breakpoints == null) {
	            return true;
	        }
			for (int i = 0; i < breakpoints.length; i++) {
				if (breakpoints[i].isEnabled()) {
					IStatusHandler prompter = DebugPlugin.getDefault().getStatusHandler(promptStatus);
					if (prompter != null) {
						boolean launchInDebugModeInstead = ((Boolean)prompter.handleStatus(switchToDebugPromptStatus, configuration)).booleanValue();
						if (launchInDebugModeInstead) { 
							return false; //kill this launch
						} 
					}
					// if no user prompt, or user says to continue (no need to check other breakpoints)
					return true;
				}
			}
		}	
		// no enabled breakpoints... continue launch
		return true;
	}

	@Override
	public IVMInstall getVMInstall(ILaunchConfiguration configuration) throws CoreException {
		if (isNonJavaProject(configuration)) {
			return getVMInstallNonJavaProject(configuration);
		} else {
			return super.getVMInstall(configuration);
		}
	}

	@Override
	public Map getVMSpecificAttributesMap(ILaunchConfiguration configuration) throws CoreException {
		if (isNonJavaProject(configuration)) {
			return null;
		} else {
			return super.getVMSpecificAttributesMap(configuration);
		}
	}

	@Override
	public String[] getBootpath(ILaunchConfiguration configuration) throws CoreException {
		if (isNonJavaProject(configuration)) {
			return null;
		} else {
			return super.getBootpath(configuration);
		}
	}

	protected IVMInstall getVMInstallNonJavaProject(ILaunchConfiguration configuration) {
		/*
		String jreAttr = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, (String)null);
		if (jreAttr != null) {
			IPath jrePath = Path.fromPortableString(jreAttr);
			IClasspathEntry entry = JavaCore.newContainerEntry(jrePath);
			IRuntimeClasspathEntryResolver2 resolver = JavaRuntime.getVariableResolver(jrePath.segment(0));
			if (resolver != null) {
				return resolver.resolveVMInstall(entry);
			} else {
				resolver = JavaRuntime.getContainerResolver(jrePath.segment(0));
				if (resolver != null) {
					return resolver.resolveVMInstall(entry);
				}
			}
		}
		*/
		return JavaRuntime.getDefaultVMInstall();	
	}

	protected boolean isNonJavaProject(ILaunchConfiguration configuration) throws CoreException {
		// Currently this only works on projects which have the Java nature
		// applied
		// so to be able to run this launch configuration on root pom projects
		// which don't have the Java nature, lets try add it
		IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
	
		boolean answer = false;
		String projectName = configuration.getAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				(String) null);
		if (projectName != null && projectName.trim().length() > 0) {
			IJavaProject javaProject = javaModel.getJavaProject(projectName);
			// IJavaProject javaProject =
			// JavaRuntime.getJavaProject(configuration);
			if (javaProject != null) {
				// lets see if it exists
				if (!javaProject.exists()) {
					answer = true;
				}
			}
		}
		return answer;
	}

}