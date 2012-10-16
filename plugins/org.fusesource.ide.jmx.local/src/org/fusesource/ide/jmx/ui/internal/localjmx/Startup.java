package org.fusesource.ide.jmx.ui.internal.localjmx;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	public void earlyStartup() {
		Activator.registerNodeProviders();
	}

}
