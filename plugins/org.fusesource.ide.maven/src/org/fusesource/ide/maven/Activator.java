package org.fusesource.ide.maven;

import org.eclipse.core.runtime.Plugin;

/**
 * @author lhein
 */
public class Activator extends Plugin {
	
	private static Activator instance;
	
	/**
	 * default constructor
	 */
	public Activator() {
		instance = this;
	}
	
	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static Activator getDefault() {
		return instance;
	}
}
