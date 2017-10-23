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
package org.jboss.tools.fuse.reddeer.view;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.matcher.WithTooltipTextMatcher;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;

/**
 * Performs operations with the Fuse JMX Navigator View
 * 
 * @author tsedmik
 * @author djelinek
 */
public class FuseJMXNavigator extends JMXNavigator {

	private static final Logger log = Logger.getLogger(FuseJMXNavigator.class);

	/**
	 * Tries to suspend Local Camel Context
	 * 
	 * @param path
	 *            Path to Camel Context in JMX Navigator View
	 * @return true - given Camel Context was suspended, false - otherwise
	 */
	public boolean suspendCamelContext(String... path) {
		log.info("Trying to suspend Camel Context: " + path);
		activate();
		try {
			getNode(path).select();
			new ContextMenuItem("Suspend Camel Context").select();
		} catch (Exception e) {
			log.info("Camel Context was not suspended!");
			return false;
		}
		return true;
	}

	/**
	 * Tries to resume Local Camel Context
	 * 
	 * @param path
	 *            Path to Camel Context in JMX Navigator View
	 * @return true - given Camel Context was resumed, false - otherwise
	 */
	public boolean resumeCamelContext(String... path) {
		log.info("Trying to resume Camel Context: " + path);
		activate();
		try {
			getNode(path).select();
			new ContextMenuItem("Resume Camel Context").select();
		} catch (Exception e) {
			log.info("Camel Context was not resumed!");
			return false;
		}
		return true;
	}

	/**
	 * Tries to open wizard 'Create JMX Connection'
	 *
	 * @author djelinek
	 * @throws Exception 
	 */
	public void clickNewConnection() throws Exception {
		log.info("Trying to open wizard 'Create JMX Connection'");
		activate();
		DefaultToolItem newConnection = new DefaultToolItem(this.cTabItem.getFolder(), "New Connection...");
		if (newConnection.isEnabled()) {
			newConnection.click();
			new WaitUntil(new ShellIsAvailable("Create JMX Connection"));
			log.info("Wizard 'Create JMX Connection' is open");
		} else {
			throw new Exception("Wizard 'Create JMX Connection' could not been open.");
		}	
	}

	/**
	 * Tries to collapse all nodes in JMX Navigator
	 *
	 * @author djelinek
	 * @throws Exception 
	 */
	public void collapseAll() throws Exception {
		log.info("Trying to collapse all nodes in JMX Navigator");
		activate();
		DefaultToolItem collapse = new DefaultToolItem(new WithTooltipTextMatcher(new RegexMatcher("Collapse All.*"))); 
		if (collapse.isEnabled()) {
			collapse.click();
			log.info("Nodes were collapsed");
		} else {
			throw new Exception("Nodes in JMX Navigator were not collapsed properly.");
		}
	}

}
