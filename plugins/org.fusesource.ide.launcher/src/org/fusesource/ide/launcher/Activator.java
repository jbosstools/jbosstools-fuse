package org.fusesource.ide.launcher;

import org.eclipse.core.runtime.Plugin;

/**
 * @author lhein
 */
public class Activator extends Plugin {

	private static Activator instance;
	
	/**
	 * 
	 */
	public Activator() {
		instance = this;
	}
	
	public static String getBundleID() {
		return instance.getBundle().getSymbolicName();
	}
	
	public static Activator getInstance() {
		return instance;
	}
}
