/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.Messages;
import org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour;
import org.fusesource.ide.server.karaf.core.publish.jmx.KarafJMXPublisher;
import org.fusesource.ide.server.karaf.core.server.subsystems.publish.ModuleBundleVersionUtility;
import org.fusesource.ide.server.karaf.core.server.subsystems.publish.ModuleBundleVersionUtility.BundleDetails;
import org.jboss.ide.eclipse.as.core.server.IModulePathFilter;
import org.jboss.ide.eclipse.as.core.server.IModulePathFilterProvider;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.jboss.ide.eclipse.as.wtp.core.modules.filter.patterns.ComponentModuleInclusionFilterUtility;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPrimaryPublishController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishControllerDelegate;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.util.PublishControllerUtil;
import org.jboss.ide.eclipse.as.wtp.core.server.publish.LocalZippedModulePublishRunner;
import org.jboss.ide.eclipse.as.wtp.core.util.ServerModelUtilities;

/**
 * @author lhein
 */
public class Karaf2xPublishController extends AbstractSubsystemController implements IPublishController, IPrimaryPublishController  {
	
	protected IPublishBehaviour publisher2 = new KarafJMXPublisher();
	
	@Override
	public IStatus canPublish() {
		return Status.OK_STATUS;
	}

	protected IPublishBehaviour getJMXPublisher() {
		return publisher2;
	}
	
	protected IPublishBehaviour getPublisher(IModule[] module) {
		return getJMXPublisher();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#canPublishModule(org.eclipse.wst.server.core.IModule[])
	 */
	@Override
	public boolean canPublishModule(IModule[] module) {
		// This should be handled by plugin.xml really. 
//		for (IModule m : module) {
//			if (!m.getModuleType().getId().equals("fuse.camel") && !m.getModuleType().getVersion().equals("1.0")) {
//				return false;
//			}
//		}
		
		// Really should be checking stuff like server is started etc
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
	public int publishModule(int kind, int deltaKind, IModule[] modules, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		validate();
		
		// first see if we need to delegate to another custom publisher, such as bpel / osgi
		IPublishControllerDelegate delegate = PublishControllerUtil.findDelegatePublishController(getServer(),modules, true);
		if( delegate != null ) {
			return delegate.publishModule(kind, deltaKind, modules, subMonitor.split(2));
		}
		
		int publishType = PublishControllerUtil.getPublishType(getServer(), modules, kind, deltaKind);
		if( publishType == PublishControllerUtil.REMOVE_PUBLISH){
			return removeModule(modules, subMonitor.split(2));
		}

		if( ServerModelUtilities.isAnyDeleted(modules) ) {
			return IServer.PUBLISH_STATE_UNKNOWN;
		}
		
		boolean isBinaryObject = ServerModelUtilities.isBinaryModule(modules);
		if( !isBinaryObject ) {
		    // commented out after the updates to m2e-wtp which now handles
		    // cleanVersions as an option (FUSETOOLS-2018)
//			for (IModule module : modules) {
//				KarafUtils.runBuild(GOALS, module, subMonitor.split(1));
//			}
			return handleZippedPublish(modules, publishType, false, subMonitor.split(1));
		}
		
		return IServer.PUBLISH_STATE_UNKNOWN;
	}
	
	private int handleZippedPublish(IModule[] module, int publishType, boolean force, IProgressMonitor monitor) throws CoreException{
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Packaging Module", 3); //$NON-NLS-1$
		IPath tmpArchive = getTempBundlePath(module, subMonitor.split(1));
		
		LocalZippedModulePublishRunner runner = createZippedRunner(module, tmpArchive); 
		
		// Trimmed code from StandardFilesystemPublishController
		IStatus result = runner.fullPublishModule(subMonitor.split(1));
		if( result == null || result.isOK()) {
			if( tmpArchive.toFile().exists()) {
				return transferBuiltModule(module, tmpArchive, subMonitor.split(1));
			}
		}
		subMonitor.setWorkRemaining(0);
		return IServer.PUBLISH_STATE_UNKNOWN;
	}
	
	protected LocalZippedModulePublishRunner createZippedRunner(IModule[] m, IPath p) {
		return new LocalZippedModulePublishRunner(getServer(), m,p, 
				getModulePathFilterProvider());
	}

	protected IModulePathFilterProvider getModulePathFilterProvider() {
		return new IModulePathFilterProvider() {
			@Override
			public IModulePathFilter getFilter(IServer server, IModule[] module) {
				return ComponentModuleInclusionFilterUtility.findDefaultModuleFilter(module[module.length-1]);
			}
		};
	}
	
	private IPath getMetadataTemporaryLocation(IServer server) {
		IPath deployRoot = getServerStateLocation(server.getId()).
			append(IJBossToolingConstants.TEMP_REMOTE_DEPLOY).makeAbsolute();
		deployRoot.toFile().mkdirs();
		return deployRoot;
	}
	
	public static IPath getServerStateLocation(String serverID) {
		return serverID == null ? Activator.getDefault().getStateLocation() : 
			Activator.getDefault().getStateLocation()
			.append(serverID.replace(' ', '_'));
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
	
	/*
	 * The following two methods are used when delegating publishing to an existing publish controller,
	 * while still performing the final operation of transfer or remove. 
	 * 
	 * Basically, it allows for a delegate publish controller to build a module (such as an osgi project)
	 * even when it doesn't actually know how to transfer it to the server. 
	 */

	

	@Override
	public int transferBuiltModule(IModule[] module, IPath srcFile, IProgressMonitor monitor) throws CoreException {
		BundleDetails bd = new ModuleBundleVersionUtility().getBundleDetails(module, srcFile);
		int status;
		if( bd != null ) {
			status = getPublisher(module).publish(getServer(), module, bd.getSymbolicName(), bd.getVersion(), srcFile);
			((Server)getServer()).setModuleState(module, status);
			((Server)getServer()).setModulePublishState(module, IServer.PUBLISH_STATE_NONE);
			((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);			
			status = IServer.PUBLISH_STATE_NONE;
		} else {
			Activator.getLogger().error("Deployment problem: Binary archive " + srcFile.toOSString() + " is not valid or lacking a valid MANIFEST.MF!");
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DeployErrorMissingManifest));
		}
		return status;
	}

	@Override
	public int removeModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
		IPath tmpArchive = getTempBundlePath(module, monitor);
		BundleDetails bd = new ModuleBundleVersionUtility().getBundleDetails(module, tmpArchive);   
		if( bd != null ) {
			boolean removed = getPublisher(module).uninstall(getServer(), module, bd.getSymbolicName(), bd.getVersion());
			if( removed ) {
				File tmpDeployArtifact = tmpArchive.toFile();
				// remove the temp deploy file from file system once we undeploy or latest at shutdown of VM
				if (!tmpDeployArtifact.delete()) {
					tmpDeployArtifact.deleteOnExit();
				}
				return IServer.PUBLISH_STATE_NONE;
			}
		}
		return IServer.PUBLISH_STATE_FULL;
	}

    private IPath getTempBundlePath(IModule[] module, IProgressMonitor monitor) {
        IPath localTempLocation = getMetadataTemporaryLocation(getServer());
        String moduleVersion = getModuleVersion(module[0], monitor);
        String archiveName = module[0].getName() + "-" + moduleVersion + //$NON-NLS-1$
                ServerModelUtilities.getDefaultSuffixForModule(module[0]);
        return localTempLocation.append(archiveName);
    }

    private String getModuleVersion(IModule module, IProgressMonitor monitor) {
        IProject project = module.getProject();
        if (project != null) {
        	SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
        	IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project.getFile(IMavenConstants.POM_FILE_NAME), true, subMonitor.split(1));
        	try {
				MavenProject mavenProject = facade.getMavenProject(subMonitor.split(1));
				return mavenProject.getVersion();
			} catch (CoreException e) {
				Activator.getLogger().warning(e);
				return Long.toString(System.currentTimeMillis());
			}
        } else {
        	Activator.getLogger().warning("Cannot determine project associated with module: " + module.getName()); //$NON-NLS-1$
        	return Long.toString(System.currentTimeMillis());
        }
    }
}
