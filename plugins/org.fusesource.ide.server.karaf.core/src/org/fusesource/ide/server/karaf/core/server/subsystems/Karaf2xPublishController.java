package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour;
import org.fusesource.ide.server.karaf.core.publish.jmx.KarafJMXPublisher;
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
		return Status.OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#canPublishModule(org.eclipse.wst.server.core.IModule[])
	 */
	@Override
	public boolean canPublishModule(IModule[] module) {
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
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
		
		int publishType = KarafUtils.getPublishType(getServer(), module, kind, deltaKind);
		switch (publishType) {
		case KarafUtils.FULL_PUBLISH:
			// do a build
			boolean built = runBuild(module[0], monitor);
			boolean done = this.publisher.publish(getServer(), module);
			if (done) {
				 ((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
				 ((Server)getServer()).setModuleState(module, IServer.PUBLISH_STATE_NONE);
			}
			break;
		case KarafUtils.INCREMENTAL_PUBLISH:
			// do a build
			built = runBuild(module[0], monitor);
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

	/**
	 * runs the maven build to create the jar file for the module
	 * @param module
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private boolean runBuild(IModule module, IProgressMonitor monitor)  throws CoreException {
		File pomFile = module.getProject().getLocation().append(IMavenConstants.POM_FILE_NAME).toFile();
		IMaven maven = MavenPlugin.getMaven();
		IMavenExecutionContext executionContext = maven.createExecutionContext();
		MavenExecutionRequest executionRequest = executionContext.getExecutionRequest();
		executionRequest.setPom(pomFile);
		executionRequest.setGoals(Arrays.asList("clean", "package"));
		MavenExecutionResult result = maven.execute(executionRequest, monitor);
		return !result.hasExceptions();
	}
}
