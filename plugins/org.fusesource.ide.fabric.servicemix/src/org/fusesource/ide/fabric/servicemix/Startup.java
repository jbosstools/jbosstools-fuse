package org.fusesource.ide.fabric.servicemix;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		FabricServiceMixPlugin.registerPlugins();
	}

}
