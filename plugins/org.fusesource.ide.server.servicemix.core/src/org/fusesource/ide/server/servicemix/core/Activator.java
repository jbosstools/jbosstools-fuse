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

package org.fusesource.ide.server.servicemix.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.preferences.PreferenceManager;

/**
 * @author lhein
 */
public class Activator extends Plugin {
	
	protected static Activator instance;
	private static IViewPart part;

	public static final String TERMINAL_VIEW_ID = "org.fusesource.ide.server.view.TerminalView";
	public static final String PLUGIN_ID = "org.fusesource.ide.server.servicemix.core";
		
	/**
	 * constructor
	 */
	public Activator() {
		super();
		instance = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * @return org.eclipse.jst.server.tomcat.internal.TomcatPlugin
	 */
	public static Activator getDefault() {
		return instance;
	}
	
	/**
	 * Return the install location preference.
	 * 
	 * @param id a runtime type id
	 * @return the install location
	 */
	public static String getPreference(String id) {
		return PreferenceManager.getInstance().loadPreferenceAsString(id);
	}
	
	/**
	 * Set the install location preference.
	 * 
	 * @param id the runtimt type id
	 * @param value the location
	 */
	public static void setPreference(String id, String value) {
		PreferenceManager.getInstance().savePreference(id, value);
	}
	
	/**
	 * opens the properties view if not already open
	 */
	public static IViewPart openTerminalView() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				if (wb != null) {
					IWorkbenchWindow activeWindow = wb.getActiveWorkbenchWindow();
					if (activeWindow != null) {
						IWorkbenchPage activePage = activeWindow.getActivePage();
						if (activePage != null) {
							try { 
								part = activePage.showView(TERMINAL_VIEW_ID);
							} catch (CoreException ex) {
								getLogger().error("Unable to create the terminal view!", ex);
							}
						}
					}
				}
			}
		});
		return part;
	}
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}