/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * @author lhein
 */
public class RiderLogFacade {
	
	private static Map<String, RiderLogFacade> openLogs = new ConcurrentHashMap<String, RiderLogFacade>();
	
	private ILog log;
	
	private boolean isDebugEnabled = System.getProperty("fusesource.debugEnabled") == null ? true : Boolean.valueOf(System.getProperty("fusesource.debugEnabled"));
		
	/**
	 * creates the log facade for the given plugin log
	 * 
	 * @param log	the plugins log object
	 */
	private RiderLogFacade(ILog log) {
		this.log = log;
	}
	
	/**
	 * Returns <code>true</code> if logging debug messages; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if logging debug messages; <code>false</code> otherwise.
	 */
	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	/**
	 * Set logging of debug messages.
	 * 
	 * @param isDebug - if <code>true</code> log debug messages. 
	 */
	public void setDebugEnabled(boolean isDebug) {
		this.isDebugEnabled = isDebug;
	}

	/**
	 * creates a new log facade or returns it if its already opened
	 * 
	 * @param pluginLogger	the plugins logger
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static RiderLogFacade getLog(ILog pluginLogger) throws IllegalArgumentException {
		if (pluginLogger == null) {
			throw new IllegalArgumentException("Log parameter must not be null!");
		}
		if (openLogs == null || pluginLogger.getBundle() == null) {
			// being called from outside of OSGi
			return new RiderLogFacade(pluginLogger);
		}
		if (!openLogs.containsKey(pluginLogger.getBundle().getSymbolicName())) {
			openLogs.put(pluginLogger.getBundle().getSymbolicName(), new RiderLogFacade(pluginLogger));
		}
		return openLogs.get(pluginLogger.getBundle().getSymbolicName());
	}
	
	/**
	 * logs an information
	 * 
	 * @param message	the message to log
	 */
	public void info(String message) {
		info(message, null);
	}
	
	/**
	 * logs an exception as information
	 * 
	 * @param ex	the exception to log
	 */
	public void info(Throwable ex) {
		info(null, ex);
	}
	
	/**
	 * logs an information with exception
	 * 
	 * @param message	the message to log
	 * @param ex		the exception to log
	 */
	public void info(String message, Throwable ex) {
		log(IStatus.INFO, IStatus.INFO, message, ex);
	}
	
	/**
	 * logs a warning
	 * 
	 * @param message	the message to log
	 */
	public void warning(String message) {
		warning(message, null);
	}
	
	/**
	 * logs an exception as warning
	 * 
	 * @param ex	the exception to log
	 */
	public void warning(Throwable ex) {
		warning(null, ex);
	}
	
	/**
	 * logs a warning with exception
	 * 
	 * @param message	the message to log
	 * @param ex		the exception to log
	 */
	public void warning(String message, Throwable ex) {
		log(IStatus.WARNING, IStatus.WARNING, message, ex);
	}
	
	/**
	 * logs an error
	 * 
	 * @param message	the message to log
	 */
	public void error(String message) {
		error(message, null);
	}
	
	/**
	 * logs an exception as error
	 * 
	 * @param ex	the exception to log
	 */
	public void error(Throwable ex) {
		error(ex == null ? "null" : ex.getMessage(), ex);
	}
	
	/**
	 * logs an error with exception
	 * 
	 * @param message	the message to log
	 * @param ex		the exception to log
	 */
	public void error(String message, Throwable ex) {
		log(IStatus.ERROR, IStatus.ERROR, message, ex);
	}
	
	/**
	 * logs a message and exception
	 * 
	 * @param severity	the severity code
	 * @param code		the error code
	 * @param message	the message
	 * @param ex		the exception
	 */
	public void log(int severity, int code, String message, Throwable ex) {
		log(createStatus(severity, code, message, ex));
	}
	
	/**
	 * creates a Status object out of the given parameters
	 * 
	 * @param severity	the severity of the message
	 * @param code		the code
	 * @param message	the message to log
	 * @param ex		the exception to log
	 * @return	a status object
	 */
	public IStatus createStatus(int severity, int code, String message, Throwable ex) {
		Bundle bundle = this.log.getBundle();
		// lets deal with non OSGi...
		String symbolicName = bundle != null ? bundle.getSymbolicName() : "nonOSGi";
		return new Status(severity, symbolicName, code, message, ex);
	}
	
	/**
	 * logs the given status object to the plugins default log
	 * 
	 * @param status	the status to log
	 */
	public void log(IStatus status) {
		if (this.isDebugEnabled) {
			this.log.log(status);
		}
	}

	public void debug(String message) {
		if (this.isDebugEnabled) {
			this.log(createStatus(IStatus.INFO, 0, message, null));
		}
	}

	public void debug(String message, Throwable e) {
		if (this.isDebugEnabled) {
			this.log(createStatus(IStatus.ERROR, 0, message, e));
		}
	}
}
