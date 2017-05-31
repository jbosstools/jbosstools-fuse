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
import org.jboss.tools.fuse.qe.reddeer.tests.ComponentTest;
import org.jboss.tools.fuse.qe.reddeer.tests.DataTransformationDeploymentTest;
import org.jboss.tools.fuse.qe.reddeer.tests.DataTransformationTest;
import org.jboss.tools.fuse.qe.reddeer.tests.DebuggerTest;
import org.jboss.tools.fuse.qe.reddeer.tests.DeploymentEAPTest;
import org.jboss.tools.fuse.qe.reddeer.tests.DeploymentTest;
import org.jboss.tools.fuse.qe.reddeer.tests.DownloadServerTest;
import org.jboss.tools.fuse.qe.reddeer.tests.FeaturesTest;
import org.jboss.tools.fuse.qe.reddeer.tests.FuseProjectTest;
import org.jboss.tools.fuse.qe.reddeer.tests.JMXNavigatorServerTest;
import org.jboss.tools.fuse.qe.reddeer.tests.JMXNavigatorTest;
import org.jboss.tools.fuse.qe.reddeer.tests.LicenseTest;
import org.jboss.tools.fuse.qe.reddeer.tests.NewFuseProjectWizardTest;
import org.jboss.tools.fuse.qe.reddeer.tests.ProjectLocalRunTest;
import org.jboss.tools.fuse.qe.reddeer.tests.QuickStartsTest;
import org.jboss.tools.fuse.qe.reddeer.tests.RouteManipulationTest;
import org.jboss.tools.fuse.qe.reddeer.tests.ServerJRETest;
import org.jboss.tools.fuse.qe.reddeer.tests.ServerTest;
import org.jboss.tools.fuse.qe.reddeer.tests.SimpleTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestSuite;

/**
 * Runs all tests
 * 
 * @author tsedmik
 */
@SuiteClasses({
	CamelEditorTest.class,
	ComponentTest.class,
	DataTransformationTest.class,
	DataTransformationDeploymentTest.class,
	DebuggerTest.class,
	DeploymentEAPTest.class,
	DeploymentTest.class,
	DownloadServerTest.class,
	FeaturesTest.class,
	FuseProjectTest.class,
	JMXNavigatorServerTest.class,
	JMXNavigatorTest.class,
	LicenseTest.class,
	NewFuseProjectWizardTest.class,
	ProjectLocalRunTest.class,
	QuickStartsTest.class,
	RouteManipulationTest.class,
	ServerTest.class,
	SimpleTest.class,
	ServerJRETest.class })
@RunWith(RedDeerSuite.class)
public class AllTests extends TestSuite {

}
