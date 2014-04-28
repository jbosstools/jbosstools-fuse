package org.fusesource.ide.server.karaf.core.server.subsystems;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;

// Temporary class, to be removed later
public class NullPublishController extends AbstractSubsystemController 
	implements IPublishController  {

	public IStatus canPublish() {
		return Status.OK_STATUS;
	}

	@Override
	public boolean canPublishModule(IModule[] module) {
		return true;
	}

	@Override
	public void publishStart(IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public int publishModule(int kind, int deltaKind, IModule[] module,
			IProgressMonitor monitor) throws CoreException {
		return IServer.PUBLISH_STATE_NONE;
	}

	@Override
	public void publishServer(int kind, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

}
