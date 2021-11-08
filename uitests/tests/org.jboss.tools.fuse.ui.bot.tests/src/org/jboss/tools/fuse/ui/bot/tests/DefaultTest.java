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
package org.jboss.tools.fuse.ui.bot.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.core.matcher.WithTooltipTextMatcher;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.views.log.LogMessage;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

/**
 * Prepares environment for tests
 * 
 * @author tsedmik
 */
public class DefaultTest {

	private static Logger log = Logger.getLogger(DefaultTest.class);

	@Rule
	public final TestWatcher watchman = new TestWatcher() {

		@Override
		protected void failed(Throwable e, org.junit.runner.Description description) {
			Path logFile = Platform.getLogFileLocation().toFile().toPath();
			try {
				Files.copy(logFile, logFile
						.resolveSibling(description.getClassName() + "." + description.getMethodName() + ".log"));
			} catch (IOException ex) {
				log.error("Cannot backup workspace log file", ex);
			}
		}
	};

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
		new LogView().open();
		new LogView().setActivateOnNewEvents(false);
	}

	/**
	 * Prepares test environment
	 */
	@Before
	public void defaultSetup() {

		log.info("Deleting Error Log.");
		new LogView().deleteLog();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void defaultClean() {

		log.info("Closing all non workbench shells.");
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();

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
		LogView errorLog = new LogView();
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
		LogView errorLog = new LogView();
		errorLog.deleteLog();
	}
}
