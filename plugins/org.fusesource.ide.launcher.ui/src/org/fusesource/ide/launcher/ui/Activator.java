package org.fusesource.ide.launcher.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fusesource.ide.commons.logging.RiderLogFacade;


/**
 * @author lhein
 */
public class Activator extends AbstractUIPlugin {

	private static Activator instance;
	
	/**
	 * 
	 */
	public Activator() {
		instance = this;
	}
	
	public static Activator getDefault() {
		return instance;
	}
	
	public static void error(String message, Exception ex)  {
		RiderLogFacade.getLog(instance.getLog()).log(getStatus(message, ex));
	}
	
	public static void error(Exception ex) {
		error(null, ex);
	}
	
	private static IStatus getStatus(String msg, Exception ex) {
		return new Status(IStatus.ERROR, instance.getBundle().getSymbolicName(), msg == null ? ex.getMessage() : msg, ex);
	}
}
