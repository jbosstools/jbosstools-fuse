/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.navigator.properties.actions;

import io.fabric8.insight.log.LogEvent;

import org.eclipse.jdt.internal.debug.ui.console.JavaStackTraceConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.navigator.properties.LogViewTabSection;

public class OpenStackTraceAction extends ActionSupport {
	private static final String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$
	
	private JavaStackTraceConsole console;
	private LogViewTabSection view;
	
	public OpenStackTraceAction(LogViewTabSection view, String text) {
		super(text, Fabric8JMXPlugin.getDefault().getImageDescriptor("stckframe_obj.gif"));
		this.view = view;
	}

	@Override
	public void run() {
		LogEvent event = view.getSelectedEvent();
		if (event == null) {
			return;
		}
		StringBuffer buf = new StringBuffer();
		String[] lines = event.getException();
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
			if (Strings.isBlank(className) || Strings.isBlank(methodName)
					|| Strings.isBlank(fileName) || Strings.isBlank(lineNumber)) {
				return;
			}
			buf.append(event.getMessage()).append(NEW_LINE);
			buf.append("\tat " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")").append(NEW_LINE);
			;
		}
		// check that the user did not remove our console from the ConsoleView
		boolean stillRegistered = checkRegistration();
		if (!stillRegistered) {
			registerConsole();
		}
		console.clearConsole();
		console.getDocument().set(buf.toString());
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}

	private boolean checkRegistration() {
		if (this.console == null) initConsole();
		IConsole[] consoleArray = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (int i = 0; i < consoleArray.length; i++) {
			IConsole c = consoleArray[i];
			if (c == console) {
				return true;
			}
		}
		return false;
	}

	private void registerConsole() {
		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { console });
	}

	private void initConsole() {
		console = new JavaStackTraceConsole();
		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { console });
	}
}