package org.fusesource.ide.fabric.camel;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		FabricCamelPlugin.registerPlugins();
	}

}
