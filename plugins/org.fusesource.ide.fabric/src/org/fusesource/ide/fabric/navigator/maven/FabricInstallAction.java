package org.fusesource.ide.fabric.navigator.maven;


import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;


/**
 * Does a local build and installs the build locally
 */
public class FabricInstallAction extends ExecutePomActionSupport {

	public static final String CONFIG_TAB_GROUP = "org.fusesource.fabric.build.tabGroup";
	public static final String CONFIG_TYPE_ID = "org.fusesource.fabric.build";

	public FabricInstallAction() {
		super(CONFIG_TAB_GROUP, CONFIG_TYPE_ID, "install");
	}
}