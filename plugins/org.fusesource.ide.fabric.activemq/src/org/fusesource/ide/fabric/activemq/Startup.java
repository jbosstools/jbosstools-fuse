package org.fusesource.ide.fabric.activemq;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		FabricActiveMQPlugin.registerNodeProviders();
	}

}
