package org.fusesource.ide.deployment;

import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.osgi.framework.BundleContext;


/**
 * @author lhein
 */
public class DeployPlugin extends ImagesActivatorSupport {

	public static final String DEPLOY_ICON = "deploy.png";
	private static DeployPlugin plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static DeployPlugin getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}
