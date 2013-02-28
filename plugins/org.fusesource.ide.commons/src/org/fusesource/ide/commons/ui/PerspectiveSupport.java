/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.commons.ui;

import org.eclipse.ui.IPerspectiveFactory;

public abstract class PerspectiveSupport implements IPerspectiveFactory {

	public static final String ID_FABRIC_EXPORER = "org.fusesource.ide.fabric.navigator";
	public static final String ID_JMX_EXPORER = "org.fusesource.ide.jmx.ui.internal.views.navigator.MBeanExplorer";
	public static final String ID_ZK_EXPORER = "org.fusesource.ide.zk.zookeeper.views.explorer.ZooKeeperExplorerView";
	public static final String ID_DIAGRAM_VIEW = "org.fusesource.ide.camel.editor.views.DiagramView";
	public static final String ID_TERMINAL_VIEW = "org.fusesource.ide.server.karaf.view.TerminalView";
	public static final String ID_MESSAGE_TABLE = "org.fusesource.ide.fabric.views.MessageView";
	public static final String ID_SERVERS_VIEW = "org.eclipse.wst.server.ui.ServersView";
	public static final String ID_CONSOLE_VIEW = "org.eclipse.ui.console.ConsoleView";
	public static final String ID_LOGS_VIEW = "org.fusesource.ide.fabric.views.logs.LogsView";

	public PerspectiveSupport() {
		super();
	}

}