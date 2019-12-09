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

package org.fusesource.ide.launcher.ui;

import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.foundation.ui.util.ImagesActivatorSupport;


/**
 * @author lhein
 */
public class Activator extends ImagesActivatorSupport {

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
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(instance.getLog());
	}
}
