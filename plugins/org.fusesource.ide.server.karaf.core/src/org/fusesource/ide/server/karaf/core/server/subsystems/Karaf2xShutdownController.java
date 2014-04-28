package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.ide.eclipse.as.core.util.LaunchCommandPreferences;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IServerShutdownController;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.AbstractStartJavaServerLaunchDelegate;

public class Karaf2xShutdownController extends AbstractSubsystemController
		implements IServerShutdownController {

	@Override
	public IStatus canStop() {
		if( getServer().getServerState() == IServer.STATE_STARTED) {
			return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public void stop(boolean force) {
		boolean ignoreLaunch = false;
		try {
			ILaunchConfiguration config = getServer().getLaunchConfiguration(true, new NullProgressMonitor());
			ignoreLaunch = LaunchCommandPreferences.isIgnoreLaunchCommand(config);
		} catch(CoreException ce) {
			Activator.getDefault().getLog().log(ce.getStatus());
		}
		
		if(ignoreLaunch) {
			((ControllableServerBehavior)getControllableBehavior()).setServerStopped();
			return;
		}
		stopImpl(force);

	}
	
	public void stopImpl(boolean force) {
		int state = getServer().getServerState();
		if (force || shouldUseForce()) {
			forceStop();
		} else if (state == IServer.STATE_STARTING
				|| state == IServer.STATE_STOPPING) {
			// if we're starting up or shutting down and they've tried again,
			// then force it to stop.
			//cancelPolling(null);   No polling yet
			forceStop();
		} else {
			((ControllableServerBehavior)getControllableBehavior()).setServerStopping();
			IStatus result = gracefullStop();
			if (!result.isOK()) {
				setNextStopRequiresForce(true);
				((ControllableServerBehavior)getControllableBehavior()).setServerStarted();
			}
		}
	}
	
	protected IStatus gracefullStop() {
		// TODO um... refactor this... a lot
		// This should ideally be done via another java launch config, 
		
		String workingDir = getServer().getRuntime().getLocation().toOSString();
		File workDirFile = new File(workingDir + File.separator + "bin");
		ProcessBuilder pb = new ProcessBuilder();
		boolean isWindows = System.getProperty("os.name" ).toLowerCase().indexOf("windows") != -1;
		if (isWindows) {
			pb.command("cmd", "/C", "stop.bat");
		} else {
			pb.command("./stop");
		}
		pb.directory(workDirFile);
		try {
			Process karafStopProcess = pb.start();
			while (true) {
				try {
					karafStopProcess.exitValue();
					break;
				} catch (IllegalThreadStateException ex) {
					// still running
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						
					}
				}
			}
		} catch(Exception e) {
			Status fail = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to stop server", e);
			Activator.getDefault().getLog().log(fail);
			return fail;
		}
		return Status.OK_STATUS;
	}
	
	

	protected boolean shouldUseForce() {
		int state = getServer().getServerState();
		boolean useForce = !isProcessRunning() || state == IServer.STATE_STOPPED || getRequiresForce(); 
		return useForce;
	}
	
	protected void forceStop() {
		// Only synchronize on this for fast methods blocking on the process
		// Calls to parent should not be synchronized for fear of deadlock
		synchronized(this) {
			// just terminate the process.
			if( isProcessRunning()) {
				try {
					getProcess().terminate();
				} catch( DebugException e ) {
				}
			}
			clearProcess();
			setNextStopRequiresForce(false);
		}
		((ControllableServerBehavior)getControllableBehavior()).setServerStopped();
	}
	
	
	protected IProcess getProcess() {
		IProcess existing = (IProcess)getControllableBehavior().getSharedData(AbstractStartJavaServerLaunchDelegate.PROCESS);
		return existing;
	}

	protected boolean getRequiresForce() {
		Object o = getControllableBehavior().getSharedData(AbstractStartJavaServerLaunchDelegate.NEXT_STOP_REQUIRES_FORCE);
		return o == null ? false : ((Boolean)o).booleanValue();
	}
	
	protected boolean isProcessRunning() {
		boolean isProcessRunning = getProcess() != null && !getProcess().isTerminated();
		return isProcessRunning;
	}
	
	protected void clearProcess() {
		getControllableBehavior().putSharedData(AbstractStartJavaServerLaunchDelegate.PROCESS, null);
	}
	

	protected void setNextStopRequiresForce(boolean val) {
		getControllableBehavior().putSharedData(AbstractStartJavaServerLaunchDelegate.NEXT_STOP_REQUIRES_FORCE, val);
	}
}
