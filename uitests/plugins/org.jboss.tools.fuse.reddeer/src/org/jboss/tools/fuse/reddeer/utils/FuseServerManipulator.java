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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.exception.EclipseLayerException;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.wst.server.ui.Runtime;
import org.eclipse.reddeer.eclipse.wst.server.ui.RuntimePreferencePage;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.Server;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServerLabel;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServerModule;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersView2;
import org.eclipse.reddeer.eclipse.wst.server.ui.wizard.ModifyModulesDialog;
import org.eclipse.reddeer.swt.api.Tree;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.reddeer.wizard.FuseModifyModulesPage;
import org.jboss.tools.fuse.reddeer.wizard.FuseServerWizard;

/**
 * Performs operation with a Fuse server
 * 
 * @author tsedmik
 */
public class FuseServerManipulator {

	private static final Logger log = Logger.getLogger(FuseServerManipulator.class);

	public static void addServerRuntime(String type, String path) {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);
		serverRuntime.addServerRuntime(type, path);
		dialog.ok();
	}

	public static void editServerRuntime(String name, String path) {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);
		serverRuntime.editServerRuntime(name, path);
		dialog.ok();
	}

	public static void removeServerRuntime(String name) {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);
		try {
			serverRuntime.removeRuntime(new Runtime(name, ""));
		} catch (CoreLayerException ex) {
			log.warn("Cannot remove '" + name + "' server runtime. It is not listed in Server Runtimes!");
		}
		dialog.ok();
	}

	public static void deleteAllServerRuntimes() {

		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		RuntimePreferencePage page = new RuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(page);
		page.removeAllRuntimes();
		dialog.ok();
	}

	public static List<Runtime> getServerRuntimes() {
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage(dialog);
		dialog.open();
		dialog.select(serverRuntime);
		List<Runtime> temp = serverRuntime.getServerRuntimes();
		dialog.cancel();
		return temp;
	}

	public static void deleteAllServers() {

		ServersView2 view = new ServersView2();
		view.open();
		for (Server server : view.getServers()) {
			server.delete(true);
		}
	}

	public static void addServer(String type, String hostname, String name, String portNumber, String userName,
			String password, String... projects) {

		FuseServerWizard serverWizard = new FuseServerWizard();
		serverWizard.setType(type);
		serverWizard.setHostName(hostname);
		serverWizard.setName(name);
		serverWizard.setPortNumber(portNumber);
		serverWizard.setUserName(userName);
		serverWizard.setPassword(password);
		serverWizard.setProjects(projects);
		serverWizard.execute();
	}

	public static void removeServer(String name) {

		try {
			new ServersView2().getServer(name).delete();
		} catch (EclipseLayerException ex) {
		}
	}

	public static void startServer(String name) {

		doOperation("Start", name);
	}

	public static void debugServer(String name) {

		doOperation("Debug", name);
	}

	public static void restartInDebug(String name) {

		doOperation("Restart in Debug", name);
	}

	private static void doOperation(String operation, String name) {

		new ServersView2().open();
		for (TreeItem item : new DefaultTree().getItems()) {
			if (item.getText().startsWith(name)) {
				item.select();
				new ContextMenuItem(operation).select();

				for (int i = 0; i < 10; i++) {
					AbstractWait.sleep(TimePeriod.SHORT);
					if (new ShellIsAvailable("Password Required").test()) {
						new DefaultShell("Password Required");
						new LabeledText("Password:").setText("admin");
						new PushButton("OK").click();
						AbstractWait.sleep(TimePeriod.SHORT);
						new WorkbenchShell();
					}
				}

				new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
				if (name.toLowerCase().contains("fuse")) {
					new ConsoleView().open();
					new WaitUntil(new ConsoleHasText("100%"), TimePeriod.VERY_LONG);
				}
				AbstractWait.sleep(TimePeriod.DEFAULT);
				break;
			}
		}
		new WorkbenchShell().setFocus();
	}

	public static void stopServer(String name) {

		try {
			new FuseShellSSH().execute("log:clear");
			ServersView2 serversView = new ServersView2();
			serversView.open();
			Server server = serversView.getServer(name);
			server.stop();
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
			hack_General_CloseServerTerminateWindow();
		} catch (Exception ex) {
		}
	}

	public static List<String> getServers() {

		ServersView2 serversView = new ServersView2();
		serversView.open();
		List<String> temp = new ArrayList<String>();
		try {
			Tree tree = new DefaultTree();
			for (TreeItem item : tree.getItems()) {
				temp.add(new ServerLabel(item).getName());
			}
		} catch (CoreLayerException e) {
			return temp;
		}

		return temp;
	}

	/**
	 * Checks if the Fuse server is started
	 * 
	 * @param name
	 *            Name of the server
	 * @return true - a Fuse server is started, false - otherwise
	 */
	public static boolean isServerStarted(String name) {

		return new ServersView2().getServer(name).getLabel().getState().isRunningState();
	}

	/**
	 * Checks if the Fuse server with given name exists
	 * 
	 * @param name
	 *            Name of the Server in Servers View
	 * @return true - a Fuse server is present, false - otherwise
	 */
	public static boolean isServerPresent(String name) {

		try {
			new ServersView2().getServer(name);
		} catch (EclipseLayerException ex) {
			return false;
		}

		return true;
	}

	/**
	 * Removes server and removes server's runtime
	 * 
	 * @param name
	 *            Name of the Server in Servers View
	 */
	public static void clean(String name) {

		stopServer(name);
		removeAllModules(name);
		removeServer(name);
		removeServerRuntime(name + " Runtime");
	}

	/**
	 * Checks whether a defined server has added a given module (project)
	 * 
	 * @param server
	 *            name of the server in the Servers view
	 * @param module
	 *            name of the module
	 * @return true - the server has added a given module, false - otherwise
	 */
	public static boolean hasServerModule(String server, String module) {

		try {
			new ServersView2().getServer(server).getModule(new RegexMatcher(module + ".*"));
			AbstractWait.sleep(TimePeriod.SHORT);
		} catch (EclipseLayerException ex) {
			return false;
		}
		return true;
	}

	/**
	 * Removes all assigned modules to a server
	 * 
	 * @param name
	 *            name of the server in the Servers view
	 */
	public static void removeAllModules(String name) {

		ServersView2 view = new ServersView2();
		view.open();
		for (ServerModule module : view.getServer(name).getModules()) {
			module.remove();
		}
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new WorkbenchShell();
	}

	/**
	 * Adds a given project to the server
	 * 
	 * @param server
	 *            name of the server in the Servers view
	 * @param project
	 *            name of the project in Project Explorer view
	 */
	public static void addModule(String server, String project) {

		ServersView2 view = new ServersView2();
		view.open();
		ModifyModulesDialog dialog = view.getServer(server).addAndRemoveModules();
		FuseModifyModulesPage page = new FuseModifyModulesPage(dialog);
		for (String module : page.getAvailableModules()) {
			if (module.startsWith(project)) {
				page.add(module);
				break;
			}
		}
		dialog.finish();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		AbstractWait.sleep(TimePeriod.DEFAULT);
	}

	/**
	 * Publishes the given server
	 *
	 * @param name
	 *            name of the server in the Servers view
	 */
	public static void publish(String name) {

		ServersView2 view = new ServersView2();
		view.open();
		view.getServer(name).publish();
	}

	/**
	 * If stopping a server takes a long time, <i>Terminate Server</i> window is appeared. This method tries to close
	 * the window.
	 */
	private static void hack_General_CloseServerTerminateWindow() {

		try {
			new DefaultShell("Terminate Server").setFocus();
			new PushButton("OK").click();
		} catch (Exception e) {
			log.info("Window 'Terminate Server' didn't appeared.");
		}
	}
}
