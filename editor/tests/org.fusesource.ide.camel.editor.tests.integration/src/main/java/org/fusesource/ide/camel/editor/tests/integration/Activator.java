package org.fusesource.ide.camel.editor.tests.integration;

import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends BaseUIPlugin {
	
	private static Activator instance;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setInstance(this);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		setInstance(null);
		super.stop(context);
	}

	private static synchronized void setInstance(Activator activator) {
		instance = activator;
	}
	
	public static Activator getDefault() {
		return instance;
	}
	
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}
}
