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

package org.fusesource.ide.fabric.views.logs.actions;

/**
 * @author S&eacute;bastien Pennec
 */
import org.eclipse.jdt.internal.debug.ui.console.JavaStackTraceConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.views.logs.LogEventBean;
import org.fusesource.ide.fabric.views.logs.Logs;
import org.fusesource.ide.fabric.views.logs.LogsView;


public class OpenStackTraceAction extends ActionSupport {

	private static final String NEW_LINE = System.getProperty("line.separator");  //$NON-NLS-1$

	LogsView view;
	JavaStackTraceConsole console;

	public OpenStackTraceAction(LogsView view, String text) {
		super(text, FabricPlugin.getDefault().getImageDescriptor("stckframe_obj.gif"));
		this.view = view;
		initConsole();
	}

	@Override
	public void run() {
		LogEventBean event = view.getSelectedEvent();
		if (event == null) {
			return;
		}
		StringBuffer buf = new StringBuffer();
		String[] lines = Logs.getThrowableRep(event);
		if (lines != null && lines.length > 0) {
			for (int i = 0; i < lines.length; i++) {
				buf.append(lines[i]).append(NEW_LINE);
			}
		} else {
			// lets use the location of the
			String className = event.getClassName();
			String methodName = event.getMethodName();
			String fileName = event.getFileName();
			String lineNumber = event.getLineNumber();
			if (Strings.isBlank(className) || Strings.isBlank(methodName) || Strings.isBlank(fileName) || Strings.isBlank(lineNumber)) {
				return;
			}
			buf.append(event.getMessage()).append(NEW_LINE);
			buf.append("\tat " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")").append(NEW_LINE);;
		}

		//check that the user did not remove our console from the ConsoleView
		boolean stillRegistered = checkRegistration();
		if (!stillRegistered) {
			registerConsole();
		}

		console.clearConsole();
		console.getDocument().set(buf.toString());
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
