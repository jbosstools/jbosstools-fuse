/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher;

import org.eclipse.core.runtime.Plugin;
import org.fusesource.ide.commons.logging.RiderLogFacade;

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
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(instance.getLog());
	}
}
