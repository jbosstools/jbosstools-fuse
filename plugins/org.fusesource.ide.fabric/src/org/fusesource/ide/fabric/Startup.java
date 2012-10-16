package org.fusesource.ide.fabric;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		FabricPlugin.registerPlugins();
	}

}
