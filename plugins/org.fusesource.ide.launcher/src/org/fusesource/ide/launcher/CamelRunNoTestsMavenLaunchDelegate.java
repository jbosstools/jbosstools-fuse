package org.fusesource.ide.launcher;


/**
 * @author lhein
 */
public class CamelRunNoTestsMavenLaunchDelegate extends CamelRunMavenLaunchDelegate {
	
	public CamelRunNoTestsMavenLaunchDelegate() {
		super();
		setSkipTests(true);
	}
}
