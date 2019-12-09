/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests.suite;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.ui.bot.tests.CamelEditorRoutesTest;
import org.jboss.tools.fuse.ui.bot.tests.CamelEditorTest;
import org.jboss.tools.fuse.ui.bot.tests.ComponentTest;
import org.jboss.tools.fuse.ui.bot.tests.ProblemsViewTest;
import org.jboss.tools.fuse.ui.bot.tests.ProjectLocalRunTest;
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
	ProjectLocalRunTest.class,
	ComponentTest.class,
	CamelEditorRoutesTest.class,
	ProblemsViewTest.class
	})
@RunWith(RedDeerSuite.class)
public class SmokeTests extends TestSuite {

}
