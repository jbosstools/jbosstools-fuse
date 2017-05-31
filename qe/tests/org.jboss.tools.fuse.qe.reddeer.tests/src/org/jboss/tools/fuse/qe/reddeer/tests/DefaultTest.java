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
package org.jboss.tools.fuse.qe.reddeer.tests;

import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.core.handler.ShellHandler;
import org.jboss.reddeer.core.matcher.WithTooltipTextMatcher;
import org.jboss.reddeer.eclipse.ui.console.ConsoleView;
import org.jboss.reddeer.eclipse.ui.views.log.LogMessage;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Prepares environment for tests
 * 
 * @author tsedmik
 */
public class DefaultTest {

	private static Logger log = Logger.getLogger(DefaultTest.class);

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void defaultClassSetup() {

		log.info("Maximizing workbench shell.");
		new WorkbenchShell().maximize();

		log.info("Disable showing Console view after standard output changes");
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);

		log.info("Disable showing Error Log view after changes");
		new ErrorLogView().selectActivateOnNewEvents(false);
	}

	/**
	 * Prepares test environment
	 */
	@Before
	public void defaultSetup() {

		new WorkbenchShell();

		log.info("Deleting Error Log.");
		new ErrorLogView().deleteLog();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void defaultClean() {

		new WorkbenchShell();

		log.info("Closing all non workbench shells.");
		ShellHandler.getInstance().closeAllNonWorbenchShells();

		log.info("Try to terminate a console.");
		ConsoleView console = new ConsoleView();
		console.open();
		try {
			console.terminateConsole();
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		} catch (CoreLayerException ex) {
			log.warn("Cannot terminate a console. Perhaps there is no active console.");
		}

		log.info("Save editor");
		try {
			new DefaultToolItem(new WorkbenchShell(), 0, new WithTooltipTextMatcher(new RegexMatcher("Save All.*")))
					.click();
		} catch (Exception e) {
			log.info("Nothing to save");
		}
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void defaultFinalClean() {

		new WorkbenchShell();

		log.info("Deleting all projects");
		ProjectFactory.deleteAllProjects();

		log.info("Stopping and deleting configured servers");
		FuseServerManipulator.deleteAllServers();
		FuseServerManipulator.deleteAllServerRuntimes();
	}

	/**
	 * Returns number of error messages in Error Log View originate from fuse plugins
	 * 
	 * @return number of error messages from fuse plugins
	 */
	protected int getErrorMessages() {

		log.info("Receiving count of errors from fuse plugins");
		int count = 0;
		ErrorLogView errorLog = new ErrorLogView();
		List<LogMessage> messages = errorLog.getErrorMessages();
		for (LogMessage message : messages) {
			if (message.getPlugin().toLowerCase().contains("fuse"))
				count++;
		}
		return count;
	}

	/**
	 * Deletes Error Log
	 */
	protected void deleteErrorLog() {

		log.info("Deleting error log");
		ErrorLogView errorLog = new ErrorLogView();
		errorLog.deleteLog();
	}
}
