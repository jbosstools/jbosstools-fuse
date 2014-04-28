package org.fusesource.ide.server.karaf.core.server.subsystems;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IModuleStateController;

public class NullStateController extends AbstractSubsystemController implements IModuleStateController{

	@Override
	public boolean canRestartModule(IModule[] module) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int startModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int stopModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int restartModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getModuleState(IModule[] module, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isModuleStarted(IModule[] module, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void waitModuleStarted(IModule[] module, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waitModuleStarted(IModule[] module, int maxDelay) {
		// TODO Auto-generated method stub
		
	}

}
