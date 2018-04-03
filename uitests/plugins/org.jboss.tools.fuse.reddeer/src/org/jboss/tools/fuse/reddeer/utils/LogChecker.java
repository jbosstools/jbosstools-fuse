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
package org.jboss.tools.fuse.reddeer.utils;

import static org.junit.Assert.assertTrue;

import org.jboss.tools.fuse.reddeer.LogGrapper;

/**
 * Checks 'Fuse' errors in Error Log View
 * 
 * @author djelinek
 */
public class LogChecker {
	
	public static boolean noFuseError() {
		return LogGrapper.getPluginErrors("fuse").isEmpty();
	}
	
	public static void assertNoFuseError() {
		assertTrue("Console contains 'fuse' errors", noFuseError());
	}
	
}
