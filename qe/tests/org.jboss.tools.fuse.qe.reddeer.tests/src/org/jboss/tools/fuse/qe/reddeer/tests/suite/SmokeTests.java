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
package org.jboss.tools.fuse.qe.reddeer.tests.suite;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.qe.reddeer.tests.CamelEditorTest;
import org.jboss.tools.fuse.qe.reddeer.tests.JMXNavigatorTest;
import org.jboss.tools.fuse.qe.reddeer.tests.ProjectLocalRunTest;
import org.jboss.tools.fuse.qe.reddeer.tests.SimpleTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestSuite;

/**
 * Runs smoke tests on Fuse Tooling
 * 
 * @author tsedmik
 */
@SuiteClasses({
	CamelEditorTest.class,
	JMXNavigatorTest.class,
	ProjectLocalRunTest.class,
	SimpleTest.class,
	})
@RunWith(RedDeerSuite.class)
public class SmokeTests extends TestSuite {

}
