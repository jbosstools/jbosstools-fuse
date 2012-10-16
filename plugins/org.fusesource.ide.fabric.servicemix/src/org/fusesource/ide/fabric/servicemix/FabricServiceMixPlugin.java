package org.fusesource.ide.fabric.servicemix;

import java.util.concurrent.atomic.AtomicBoolean;


import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.servicemix.navigator.ServiceMixNodeProvider;
import org.fusesource.ide.jmx.core.JMXActivator;
import org.osgi.framework.BundleContext;


/**
 * Represents a connector to Fuse Fabric for viewing the agents and features available in a fabric
 */
public class FabricServiceMixPlugin extends ImagesActivatorSupport {

	private static FabricServiceMixPlugin plugin;
	private static ServiceMixNodeProvider nodeProvider;
	private static AtomicBoolean started = new AtomicBoolean(false);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerPlugins();
	}	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		unregisterPlugins();
		plugin = null;
		super.stop(context);
	}
	
	public static void registerPlugins() {
		if (started.compareAndSet(false, true)) {
			nodeProvider = new ServiceMixNodeProvider();
			FabricPlugin.addNodeProvider(nodeProvider);
			JMXActivator.addNodeProvider(nodeProvider);
		}
	}

	public static void unregisterPlugins() {
		if (nodeProvider != null) {
			JMXActivator.removeNodeProvider(nodeProvider);
			FabricPlugin.removeNodeProvider(nodeProvider);
			nodeProvider = null;
		}
		started.set(false);
	}

	
	public static FabricServiceMixPlugin getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
	
}
