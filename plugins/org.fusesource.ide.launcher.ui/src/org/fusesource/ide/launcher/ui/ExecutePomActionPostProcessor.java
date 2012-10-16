package org.fusesource.ide.launcher.ui;

/**
 * @author lhein
 */
public interface ExecutePomActionPostProcessor {
	
	/**
	 * call back method to be run after launch is finished successfully
	 */
	void executeOnSuccess();
	
	/**
	 * call back method to be run after launch failed
	 */
	void executeOnFailure();
}
