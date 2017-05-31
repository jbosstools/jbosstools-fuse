/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.view;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;

/**
 * Performs operations with the Fuse JMX Navigator View
 * 
 * @author tsedmik
 */
public class FuseJMXNavigator extends JMXNavigator {

	private static final Logger log = Logger.getLogger(FuseJMXNavigator.class);

	/**
	 * Tries to suspend Local Camel Context
	 * 
	 * @param path Path to Camel Context in JMX Navigator View
	 * @return true - given Camel Context was suspended, false - otherwise
	 */
	public boolean suspendCamelContext(String... path) {
		log.info("Trying to suspend Camel Context: " + path);
		activate();
		try {
			getNode(path).select();
			new ContextMenu("Suspend Camel Context").select();
		} catch (Exception e) {
			log.info("Camel Context was not suspended!");
			return false;
		}
		return true;
	}

	/**
	 * Tries to resume Local Camel Context
	 * 
	 * @param path Path to Camel Context in JMX Navigator View
	 * @return true - given Camel Context was resumed, false - otherwise
	 */
	public boolean resumeCamelContext(String... path) {
		log.info("Trying to resume Camel Context: " + path);
		activate();
		try {
			getNode(path).select();
			new ContextMenu("Resume Camel Context").select();
		} catch (Exception e) {
			log.info("Camel Context was not resumed!");
			return false;
		}
		return true;
	}
}
