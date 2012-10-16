package org.fusesource.ide.branding;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.osgi.framework.BundleContext;


/**
 * @author lhein
 */
public class Activator extends AbstractUIPlugin {
	
	// The shared instance
	private static Activator plugin;
	private BundleContext bundleContext;
	
	public static final String PLUGIN_ID = "org.fusesource.ide.plugin.wizards";
	public static final String CAMEL_NATURE_ID = "org.fusesource.ide.project.RiderProjectNature";
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		this.bundleContext = context;
		super.stop(context);
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}
