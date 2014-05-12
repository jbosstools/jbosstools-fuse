package org.fusesource.ide.server.karaf.core.server.subsystems;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour;
import org.fusesource.ide.server.karaf.core.publish.KarafJMXPublisher;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;

/**
 * @author lhein
 */
public class Karaf2xPublishController extends AbstractSubsystemController 
	implements IPublishController  {

	protected IPublishBehaviour publisher = new KarafJMXPublisher();
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#canPublish()
	 */
	@Override
	public IStatus canPublish() {
		// we only allow publish to started servers
		if (getServer().getServerState() == IServer.STATE_STARTED) {
			return Status.OK_STATUS;
		}
		// TODO: we should also check if the JMX port is already bound before using it
				
		// TODO: we should invoke a build so we have the artifact to deploy
		
		return Status.CANCEL_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#canPublishModule(org.eclipse.wst.server.core.IModule[])
	 */
	@Override
	public boolean canPublishModule(IModule[] module) {
		// TODO: check for a publish behaviour fitting the module and server
		
		for (IModule m : module) {
			if (!m.getModuleType().getId().equals("fuse.camel") && !m.getModuleType().getVersion().equals("1.0")) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishStart(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishStart(IProgressMonitor monitor) throws CoreException {
		// TODO: we need to create the connection and cache it
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
		// TODO: we need to cleanup the connection
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishModule(int, int, org.eclipse.wst.server.core.IModule[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public int publishModule(int kind, int deltaKind, IModule[] module,
			IProgressMonitor monitor) throws CoreException {
		monitor = monitor == null ? new NullProgressMonitor() : monitor; // nullsafe
		validate();
		
		// TODO: we somehow need to have some logic for determinng the correct IPublishBehaviour for the module and server selection
		// maybe that could be also checked in canPublish / canPublishModule if there is a fitting publishBehaviour		
		
		int publishType = KarafUtils.getPublishType(getServer(), module, kind, deltaKind);
		switch (publishType) {
		case KarafUtils.FULL_PUBLISH:
			boolean done = this.publisher.publish(getServer(), module);
			if (done) {
				 ((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
				 ((Server)getServer()).setModuleState(module, IServer.PUBLISH_STATE_NONE);
			}
			break;
		case KarafUtils.INCREMENTAL_PUBLISH:
			done = this.publisher.publish(getServer(), module);
			if (done) {
				 ((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
				 ((Server)getServer()).setModuleState(module, IServer.PUBLISH_STATE_NONE);
			}
			break;
		case KarafUtils.NO_PUBLISH:
			// we can skip this
			break;
		case KarafUtils.REMOVE_PUBLISH:
			done = this.publisher.uninstall(getServer(), module);
			if (done) {
				 ((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
				 ((Server)getServer()).setModuleState(module, IServer.PUBLISH_STATE_NONE);
			}
			break;
		default:
			Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Unknown publish type " + publishType));
		}
		
		return IServer.PUBLISH_STATE_NONE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishServer(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishServer(int kind, IProgressMonitor monitor)
			throws CoreException {
		validate();
	}
}
