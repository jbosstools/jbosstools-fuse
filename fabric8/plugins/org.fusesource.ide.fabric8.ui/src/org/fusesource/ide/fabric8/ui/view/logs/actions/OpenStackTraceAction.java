/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * 
 * Copyright (C) 1999-2007, QOS.ch
 * 
 * This library is free software, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation.
 */
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
package org.fusesource.ide.fabric8.ui.view.logs.actions;

/**
 * @author S&eacute;bastien Pennec
 */
import org.eclipse.jdt.internal.debug.ui.console.JavaStackTraceConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.fabric8.core.dto.LogEventDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.view.logs.LogsView;

public class OpenStackTraceAction extends ActionSupport {

	private LogsView view;
	private JavaStackTraceConsole console;

	public OpenStackTraceAction(LogsView view, String text) {
		super(text, FabricPlugin.getDefault().getImageDescriptor("stckframe_obj.gif"));
		this.view = view;
		initConsole();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public void run() {
		LogEventDTO event = view.getSelectedEvent();
		if (event == null) {
			return;
		}

		//check that the user did not remove our console from the ConsoleView
		boolean stillRegistered = checkRegistration();
		if (!stillRegistered) {
			registerConsole();
		}

		console.clearConsole();
		console.getDocument().set(event.toString());
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}

	private boolean checkRegistration() {
		IConsole[]consoleArray = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (int i = 0; i < consoleArray.length; i++) {
			IConsole c = consoleArray[i];
			if (c == console) {
				return true;
			}
		}
		return false;
	}

	private void registerConsole() {
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
	}

	private void initConsole() {
		console = new JavaStackTraceConsole();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
	}
}
